package com.holybuckets.relicfuse.item.tool;

import com.holybuckets.foundation.networking.SimpleStringMessage;
import com.holybuckets.relicfuse.core.FusionManager;
import com.holybuckets.relicfuse.effect.ModEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AncientTotemItem extends Item {

    public static int EFFECT_DURATION = 1200;
    public static int EFFECT_AMPLIFIER = 0;

    public AncientTotemItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (player.hasEffect(ModEffects.ancientPower)) {
            return InteractionResult.PASS;
        }

        player.addEffect(new MobEffectInstance(
            ModEffects.ancientPower, EFFECT_DURATION, EFFECT_AMPLIFIER, false, true, true));

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            SimpleStringMessage.createAndFire(serverPlayer, FusionManager.TOTEM_USED, "");
        }

        return InteractionResult.SUCCESS;
    }
}
