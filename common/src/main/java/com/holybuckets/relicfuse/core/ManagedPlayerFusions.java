package com.holybuckets.relicfuse.core;

import com.holybuckets.foundation.GeneralConfig;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.balm.BreakBlockEvent;
import com.holybuckets.foundation.event.balm.DigSpeedEvent;
import com.holybuckets.foundation.event.balm.LivingDamageEvent;
import com.holybuckets.foundation.event.balm.LivingDeathEvent;
import com.holybuckets.foundation.event.balm.LivingFallEvent;
import com.holybuckets.foundation.event.balm.LivingHealEvent;
import com.holybuckets.foundation.event.balm.PlayerAttackEvent;
import com.holybuckets.foundation.event.balm.PlayerRespawnEvent;
import com.holybuckets.foundation.event.balm.TossItemEvent;
import com.holybuckets.foundation.event.balm.UseBlockEvent;
import com.holybuckets.foundation.event.custom.PlayerHasItemEvent;
import com.holybuckets.foundation.event.custom.PlayerInteractEvent;
import com.holybuckets.foundation.event.custom.ServerTickEvent;
import com.holybuckets.foundation.event.custom.TickType;
import com.holybuckets.foundation.modelInterface.IManagedPlayer;
import com.holybuckets.foundation.player.ManagedPlayer;
import com.holybuckets.foundation.util.DeferredObject;
import com.holybuckets.relicfuse.effect.ModEffects;
import com.holybuckets.relicfuse.item.ModItems;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ManagedPlayerFusions implements IManagedPlayer {

    private static GeneralConfig CONFIG = GeneralConfig.getInstance();

    private String id;
    private Player player;
    private ManagedPlayer parent;
    private static LocalPlayer localPlayer;
    public static ManagedPlayerFusions localFusions;

    static final Map<String, ManagedPlayerFusions> PLAYER_FUSIONS = new ConcurrentHashMap<>();


    public static void init(EventRegistrar registrar) {
        ManagedPlayer.registerManagedPlayerData(ManagedPlayerFusions.class, () -> new ManagedPlayerFusions());
        CONFIG = GeneralConfig.getInstance();
        initEvents(registrar);
    }

    private static String getId(Player player) {
        if(player instanceof LocalPlayer lp && CONFIG.isIntegrated())
            player = HBUtil.PlayerUtil.getServerPlayer(lp);
        return HBUtil.PlayerUtil.getId(player);
    }

    //** Overrides

    @Override
    public boolean isInit(String subclass) {
        return false;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Nullable
    public static ManagedPlayerFusions getManagedFusions(Player player) {
        if (player == null) return null;
        return PLAYER_FUSIONS.get(getId(player));
    }

    //** IMANAGED_PLAYER INTERFACE IMPLEMENTATION

    @Override
    public void setPlayer(Player player)
    {
        if(player == null) return;
        if(player == this.player) return;

        if(player instanceof ServerPlayer)
        {
            if (this.player != null)
                PLAYER_FUSIONS.remove(getId(this.player));
            this.player = player;
            this.parent = ManagedPlayer.getManagedPlayer(player);
            if(CONFIG.isIntegrated())
                localFusions = this;

            PLAYER_FUSIONS.put(getId(player), this);
        }
        else    //clientPlayer client side only
        {
            localPlayer = (LocalPlayer) player;
            if(!CONFIG.isServerSide())
                localFusions = this;
        }
    }


    @Override
    public IManagedPlayer getStaticInstance(Player player, String id) {
        return null;
    }

    @Override
    public void handlePlayerJoin(Player player) {}

    //** CORE **//

    private void initFusedWeapon(PlayerHasItemEvent event)
    {
        Item fusedItem = FusionManager.getFusedItem(event.getItemStack());
        if(fusedItem == null) return;
        Hooks h = routes().get(fusedItem);
        if(h == null) return;

        ServerPlayer sp = server(event.getPlayer());
        h.initFusedTool.run(sp, tool(sp));
    }


    //** SOULBOUND **//

    /** Held between death and respawn; dumped on logout so nothing is lost to a disconnect. */
    private final List<ItemStack> soulbound = new ArrayList<>();
    private Vec3 lastKnownPos = Vec3.ZERO;
    private ServerLevel lastKnownLevel;

    /**
     * Pulls every ender bone fused item out of the inventory before vanilla drops it, so the stacks
     * are never on the ground and cannot be duplicated regardless of the keep inventory rule.
     */
    private void stashSoulbound() {
        if (!(this.player instanceof ServerPlayer serverPlayer)) return;

        Item enderBone = ModItems.enderBone == null ? null : ModItems.enderBone.get();
        if (enderBone == null) return;

        rememberLocation(serverPlayer);
        Inventory inventory = serverPlayer.getInventory();

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.isEmpty()) continue;
            if (!enderBone.equals(FusionManager.getFusedItem(stack))) continue;

            soulbound.add(stack.copy());
            inventory.setItem(i, ItemStack.EMPTY);
        }
    }

    private void restoreSoulbound(ServerPlayer respawned) {
        if (soulbound.isEmpty()) return;
        soulbound.forEach(stack -> respawned.getInventory().placeItemBackInInventory(stack));
        soulbound.clear();
    }

    /** A disconnect never reaches a respawn, so anything still held is dropped where they stood. */
    private void dumpSoulbound() {
        if (soulbound.isEmpty() || lastKnownLevel == null) return;

        for (ItemStack stack : soulbound) {
            if (stack.isEmpty()) continue;
            lastKnownLevel.addFreshEntity(new ItemEntity(
                lastKnownLevel, lastKnownPos.x, lastKnownPos.y, lastKnownPos.z, stack));
        }
        soulbound.clear();
    }

    private void rememberLocation(ServerPlayer serverPlayer) {
        this.lastKnownPos = serverPlayer.position();
        this.lastKnownLevel = serverPlayer.level();
    }


    //** PLAYER TICKS ***//

    private void onPlayer20Ticks(ServerTickEvent event) {

    }





    /* STATIC CORE */

    private static final Map<Item, Hooks> ROUTES = new LinkedHashMap<>();
    private static final Map<Entity, ServerPlayer> lastAttackerMap = new HashMap<>();

    private static void initEvents(EventRegistrar registrar) {
        registrar.registerOnPlayerAttack(ManagedPlayerFusions::onPlayerAttack);
        registrar.registerOnPlayerDeath(ManagedPlayerFusions::onLivingEntityDeath);
        registrar.registerOnPlayerDamage(ManagedPlayerFusions::onPlayerDamage);
        registrar.registerOnPlayerFall(ManagedPlayerFusions::onPlayerFall);
        registrar.registerOnPlayerHeal(ManagedPlayerFusions::onPlayerHeal);
        registrar.registerOnPlayerRespawn(ManagedPlayerFusions::onPlayerRespawn);

        registrar.registerOnBreakBlock(ManagedPlayerFusions::onBreakBlock);
        registrar.registerOnDigSpeedEvent(ManagedPlayerFusions::onDigSpeed);
        registrar.registerOnUseBlock(ManagedPlayerFusions::onUseBlock);

        registrar.registerOnPlayerInteract(PlayerInteractEvent.LeftClickInteraction.class, ManagedPlayerFusions::onLeftClick);
        registrar.registerOnPlayerInteract(PlayerInteractEvent.RightClickInteraction.class, ManagedPlayerFusions::onRightClick);
        registrar.registerOnPlayerInteract(PlayerInteractEvent.EntityInteract.class, ManagedPlayerFusions::onEntityInteract);

        registrar.registerOnTossItem(ManagedPlayerFusions::onTossItem);

        registrar.registerOnServerTick(TickType.ON_SINGLE_TICK, ManagedPlayerFusions::onServerTick);
        registrar.registerOnServerTick(TickType.ON_20_TICKS, ManagedPlayerFusions::on20Ticks);
        registrar.registerOnServerTick(TickType.ON_120_TICKS, ManagedPlayerFusions::on120Ticks);

    }

    private static ServerPlayer server(Player p) {
        return p instanceof ServerPlayer sp ? sp : null;
    }

    private static ItemStack tool(Player p) {
        return p.getMainHandItem();
    }

    public static boolean readyToFuse(ServerPlayer serverPlayer) {
        if(serverPlayer == null) return false;
        //check if player has ancient power effect
        if(!serverPlayer.hasEffect(ModEffects.ancientPower)) return false;
        ItemStack tool = serverPlayer.getMainHandItem();
        ItemStack fusable = serverPlayer.getOffhandItem();
        return FusionManager.isFusableTool(tool) && FusionManager.isFusableItem(fusable);
    }


    /* COMBAT */

    private static void onPlayerAttack(PlayerAttackEvent event) {
        Hooks h = hooks(event.getPlayer());
        lastAttackerMap.put(event.getTarget(), server(event.getPlayer()));
        if (h == null) return;
        ServerPlayer sp = server(event.getPlayer());
        h.weaponOnHurt.run(sp, tool(sp), event.getTarget());
    }

    /**
     * Fires for any living entity. A player dying routes to onPlayerDeath; anything else routes
     * to swordOnDeath against whoever last struck it.
     */
    private static void onLivingEntityDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();

        if (victim instanceof ServerPlayer dead) {
            ManagedPlayerFusions fusions = getManagedFusions(dead);
            if (fusions != null) fusions.stashSoulbound();

            Hooks own = hooks(dead);
            if (own != null) own.onPlayerDeath.run(dead, tool(dead), event.getDamageSource());
        }

        ServerPlayer killer = lastAttackerMap.remove(victim);
        if (killer == null) return;
        Hooks h = hooks(killer);
        if (h == null) return;
        h.swordOnDeath.run(killer, tool(killer), event.getDamageSource(), victim);
    }

    private static void onPlayerDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        Hooks h = hooks(sp);
        if (h == null) return;
        h.onPlayerHurt.run(sp, tool(sp), event.getDamageSource(), event.getDamageAmount());
    }

    private static void onPlayerFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        Hooks h = hooks(sp);
        if (h == null) return;
        h.onPlayerFall.run(sp, tool(sp), event.getFallDamage());
    }

    private static void onPlayerHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        Hooks h = hooks(sp);
        if (h == null) return;
        h.onPlayerHeal.run(sp, tool(sp), event.getAmount());
    }


    private static void onPlayerRespawn(PlayerRespawnEvent event) {
        FusionAbilities.EnderBone.restoreOnRespawn(event.getNewPlayer());
    }


    /* BLOCKS */

    private static void onBreakBlock(BreakBlockEvent event) {
        Hooks h = hooks(event.getPlayer());
        if (h == null) return;
        ServerPlayer sp = server(event.getPlayer());
        h.toolOnBreakBlock.run(sp, tool(sp), event.getPos(), event.getState());
    }

    private static void onDigSpeed(DigSpeedEvent event) {
        Hooks h = hooks(event.getPlayer());
        if (h == null) return;
        ServerPlayer sp = server(event.getPlayer());
        h.toolOnMineBlock.run(sp, tool(sp), event.getState());
    }

    private static void onUseBlock(UseBlockEvent event) {
        Hooks h = hooks(event.getPlayer());
        if (h == null) return;
        ServerPlayer sp = server(event.getPlayer());
        h.toolOnUseBlock.run(sp, tool(sp), event.getHitResult().getBlockPos());
    }


    /* INTERACTION */

    private static void onLeftClick(PlayerInteractEvent.LeftClickInteraction event) {
        Hooks h = hooks(event.getPlayer());
        if (h == null) return;
        ServerPlayer sp = server(event.getPlayer());
        h.onSwing.run(sp, tool(sp));
    }

    private static void onRightClick(PlayerInteractEvent.RightClickInteraction event) {
        Hooks h = hooks(event.getPlayer());
        if (h == null) return;
        ServerPlayer sp = server(event.getPlayer());
        h.toolOnRightClick.run(sp, tool(sp));
    }

    private static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Hooks h = hooks(event.getPlayer());
        if (h == null) return;
        ServerPlayer sp = server(event.getPlayer());
        h.toolOnEntityInteract.run(sp, tool(sp), event.getTarget());
    }


    /* INVENTORY AND LIFECYCLE */

    private static void onTossItem(TossItemEvent event) {
        Hooks h = hooks(event.getPlayer());
        if (h == null) return;
        ServerPlayer sp = server(event.getPlayer());
        h.onTossItem.run(sp, tool(sp), event.getItemStack());
    }


    //** TICK EVENTS **//

    private static void onServerTick(ServerTickEvent event) {
        CONFIG.getLevels().values().forEach(level -> {
            if(level instanceof ServerLevel sl) {
                FusionAbilities.ToxicCrystal.onTick(sl);
                FusionAbilities.ElectricCrystal.onTick(sl);
            }
        });
    }

    private static void on20Ticks(ServerTickEvent event) {
        CONFIG.getLevels().values().forEach(level -> {
            if(level instanceof ServerLevel sl)
                FusionAbilities.ToxicCrystal.dissolveQueued20Ticks(sl);
        });

        PLAYER_FUSIONS.values().forEach(mf -> {
            mf.onPlayer20Ticks(event);
        });
    }

    private static void on120Ticks(ServerTickEvent event) {

    }


    //Serializeres

    @Override
    public CompoundTag serializeNBT() {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }




    /* HOOK SHAPES */

    @FunctionalInterface public interface EntityHook { void run(ServerPlayer p, ItemStack tool, Entity target); }
    @FunctionalInterface public interface DeathHook { void run(ServerPlayer p, ItemStack tool, DamageSource src, LivingEntity target); }
    @FunctionalInterface public interface PlainHook { void run(ServerPlayer p, ItemStack tool); }
    @FunctionalInterface public interface PosHook { void run(ServerPlayer p, ItemStack tool, BlockPos pos); }
    @FunctionalInterface public interface StateHook { void run(ServerPlayer p, ItemStack tool, BlockState state); }
    @FunctionalInterface public interface BreakHook { void run(ServerPlayer p, ItemStack tool, BlockPos pos, BlockState state); }
    @FunctionalInterface public interface DamageHook { void run(ServerPlayer p, ItemStack tool, DamageSource src, float amount); }
    @FunctionalInterface public interface AmountHook { void run(ServerPlayer p, ItemStack tool, float amount); }
    @FunctionalInterface public interface SourceHook { void run(ServerPlayer p, ItemStack tool, DamageSource src); }
    @FunctionalInterface public interface StackHook { void run(ServerPlayer p, ItemStack tool, ItemStack other); }

    /**
     * Hooks a modifier chooses to answer. Every slot defaults to a no-op, so a modifier only
     * names the hooks it needs
     */
    public static final class Hooks {

        EntityHook weaponOnHurt = (p, t, e) -> {};
        DeathHook swordOnDeath = (p, t, s, e) -> {};
        PlainHook onSwing = (p, t) -> {};
        PlainHook toolOnRightClick = (p, t) -> {};
        EntityHook toolOnEntityInteract = (p, t, e) -> {};
        PosHook toolOnUseBlock = (p, t, pos) -> {};
        StateHook toolOnMineBlock = (p, t, st) -> {};
        BreakHook toolOnBreakBlock = (p, t, pos, st) -> {};
        DamageHook onPlayerHurt = (p, t, src, amt) -> {};
        AmountHook onPlayerFall = (p, t, amt) -> {};
        AmountHook onPlayerHeal = (p, t, amt) -> {};
        SourceHook onPlayerDeath = (p, t, src) -> {};
        StackHook onTossItem = (p, t, other) -> {};
        PlainHook initFusedTool = (p, t) -> {};

        public static Hooks of() { return new Hooks(); }

        public Hooks swordOnHurt(EntityHook h) { if (h != null) weaponOnHurt = h; return this; }
        public Hooks swordOnDeath(DeathHook h) { if (h != null) swordOnDeath = h; return this; }
        public Hooks onSwing(PlainHook h) { if (h != null) onSwing = h; return this; }
        public Hooks toolOnRightClick(PlainHook h) { if (h != null) toolOnRightClick = h; return this; }
        public Hooks toolOnEntityInteract(EntityHook h) { if (h != null) toolOnEntityInteract = h; return this; }
        public Hooks toolOnUseBlock(PosHook h) { if (h != null) toolOnUseBlock = h; return this; }
        public Hooks toolOnMineBlock(StateHook h) { if (h != null) toolOnMineBlock = h; return this; }
        public Hooks toolOnBreakBlock(BreakHook h) { if (h != null) toolOnBreakBlock = h; return this; }
        public Hooks onPlayerHurt(DamageHook h) { if (h != null) onPlayerHurt = h; return this; }
        public Hooks onPlayerFall(AmountHook h) { if (h != null) onPlayerFall = h; return this; }
        public Hooks onPlayerHeal(AmountHook h) { if (h != null) onPlayerHeal = h; return this; }
        public Hooks onPlayerDeath(SourceHook h) { if (h != null) onPlayerDeath = h; return this; }
        public Hooks onTossItem(StackHook h) { if (h != null) onTossItem = h; return this; }
        public Hooks initFusedTool(PlainHook h) { if (h != null) initFusedTool = h; return this; }
    }



    private static Map<Item, Hooks> routes() {
        if (!ROUTES.isEmpty()) return ROUTES;

        route(ModItems.blessedCrystal, Hooks.of()
            .swordOnDeath(FusionAbilities.BlessedCrystal::swordOnDeath)
            .toolOnBreakBlock(FusionAbilities.BlessedCrystal::toolOnBreakBlock));

        route(ModItems.demonicCrystal, Hooks.of()
            .swordOnDeath(FusionAbilities.DemonicCrystal::swordOnDeath)
            .toolOnBreakBlock(FusionAbilities.DemonicCrystal::toolOnBreakBlock));

        route(ModItems.earthCrystal, Hooks.of()
            .swordOnDeath(FusionAbilities.EarthCrystal::swordOnDeath)
            .toolOnBreakBlock(FusionAbilities.EarthCrystal::toolOnBreakBlock)
            .toolOnUseBlock(FusionAbilities.EarthCrystal::toolOnUseBlock));


        route(ModItems.electricCrystal, Hooks.of()
            .swordOnHurt(FusionAbilities.ElectricCrystal::swordOnHurt)
            .toolOnBreakBlock(FusionAbilities.ElectricCrystal::toolOnBreakBlock)
            .toolOnUseBlock(FusionAbilities.ElectricCrystal::toolOnUseBlock));

        route(ModItems.toxicCrystal, Hooks.of()
            .swordOnHurt(FusionAbilities.ToxicCrystal::weapOnHurt)
            .toolOnBreakBlock(FusionAbilities.ToxicCrystal::toolOnBreakBlock));

        route(ModItems.encasedBone, Hooks.of());

        route(ModItems.overgrownBone, Hooks.of()
            .swordOnHurt(FusionAbilities.OvergrownBone::swordOnHurt)
            .toolOnUseBlock(FusionAbilities.OvergrownBone::toolOnUseBlock)
            .toolOnBreakBlock(FusionAbilities.OvergrownBone::toolOnBreakBlock));

        route(ModItems.spiritedBone, Hooks.of()
            .onSwing(FusionAbilities.SpiritedBone::onSwing)
            .toolOnBreakBlock(FusionAbilities.SpiritedBone::toolOnBreakBlock));

        route(ModItems.toxicBone, Hooks.of()
            .swordOnHurt(FusionAbilities.ToxicBone::swordOnHurt)
            .onSwing(FusionAbilities.ToxicBone::onSwing));

        route(ModItems.enderBone, Hooks.of());

        return ROUTES;
    }

    private static void route(DeferredObject<Item> item, Hooks hooks) {
        if (item == null || item.get() == null) return;
        ROUTES.put(item.get(), hooks);
    }

    /**
     * Resolves the held tool and its fused modifier, or null when the player is not holding one.
     */
    private static Hooks hooks(Player p) {
        if (!(p instanceof ServerPlayer sp)) return null;
        ItemStack tool = sp.getMainHandItem();
        if (!FusionManager.isFused(tool)) return null;
        Item fusedItem = FusionManager.getFusedItem(tool);
        return fusedItem == null ? null : routes().get(fusedItem);
    }


}
