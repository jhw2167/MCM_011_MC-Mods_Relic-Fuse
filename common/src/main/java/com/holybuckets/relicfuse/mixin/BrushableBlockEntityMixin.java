package com.holybuckets.relicfuse.mixin;

import com.holybuckets.relicfuse.core.FusionItemWeights;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Substitutes the rolled archaeology item before the block entity syncs it, so the relic is what
 * the player watches emerge rather than appearing at the end of the brush.
 */
@Mixin(BrushableBlockEntity.class)
public abstract class BrushableBlockEntityMixin {

    @Shadow private ItemStack item;

    @Inject(
        method = "unpackLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemInstance;)V",
        at = @At("TAIL")
    )
    private void relicfuse$substituteRelic(ServerLevel level, LivingEntity digger, ItemInstance tool, CallbackInfo ci) {
        if (this.item == null || this.item.isEmpty()) return;

        Item relic = FusionItemWeights.tryRelic(level, digger, tool);
        if (relic == null) return;

        this.item = new ItemStack(relic);
        ((BlockEntity) (Object) this).setChanged();
    }
}
