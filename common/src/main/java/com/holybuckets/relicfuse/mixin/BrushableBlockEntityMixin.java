package com.holybuckets.relicfuse.mixin;

import com.holybuckets.relicfuse.core.FusionItemWeights;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrushableBlockEntity.class)
public abstract class BrushableBlockEntityMixin {

    @Shadow private ItemStack item;

    @Shadow private ResourceKey<LootTable> lootTable;

    @Unique private boolean relicfuse$hadLootTable;

    @Inject(
        method = "unpackLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemInstance;)V",
        at = @At("HEAD")
    )
    private void relicfuse$captureLootTable(ServerLevel level, LivingEntity digger, ItemInstance tool, CallbackInfo ci) {
        this.relicfuse$hadLootTable = this.lootTable != null;
    }

    @Inject(
        method = "unpackLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemInstance;)V",
        at = @At("TAIL")
    )
    private void relicfuse$substituteRelic(ServerLevel level, LivingEntity digger, ItemInstance tool, CallbackInfo ci) {
        if (!this.relicfuse$hadLootTable) return;

        Item relic = FusionItemWeights.tryRelic(level, digger, tool);
        if (relic == null) return;

        this.item = new ItemStack(relic);
        ((BlockEntity) (Object) this).setChanged();
    }
}
