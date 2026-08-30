package com.holybuckets.relicfuse.mixin;

import com.holybuckets.relicfuse.client.FusionBolt;
import com.holybuckets.relicfuse.client.FusionClientManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(
        method = "submitEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/state/level/LevelRenderState;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V",
        at = @At("TAIL")
    )
    private void relicfuse$submitFusionBolts(PoseStack poseStack, LevelRenderState levelRenderState,
                                             SubmitNodeCollector collector, CallbackInfo ci) {
        FusionBolt.submitBolts(poseStack, collector, levelRenderState.cameraRenderState.pos);
        FusionClientManager.submitAnimation(poseStack, collector, levelRenderState.cameraRenderState.pos);
    }
}
