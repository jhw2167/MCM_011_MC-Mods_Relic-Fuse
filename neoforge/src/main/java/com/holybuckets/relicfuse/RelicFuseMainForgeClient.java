package com.holybuckets.relicfuse;

import com.holybuckets.relicfuse.client.CommonClassClient;
import com.holybuckets.relicfuse.client.IBewlrRenderer;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.neoforge.NeoForgeLoadContext;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class RelicFuseMainForgeClient {

    public RelicFuseMainForgeClient(IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modEventBus);
        BalmClient.initialize(Constants.MOD_ID, context, CommonClassClient::initClient);
        //Item challengeChest = ModBlocks.challengeChest.asItem();
        // setBlockEntityRender( challengeChest, ChallengeItemBlockRenderer.CHEST_RENDERER);
    }

    private static void setBlockEntityRender(Object item, BlockEntityWithoutLevelRenderer renderer) {
        ((IBewlrRenderer) item).setBlockEntityWithoutLevelRenderer(renderer);
    }

}
