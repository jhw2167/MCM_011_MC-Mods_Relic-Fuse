package com.holybuckets.relicfuse;

import com.holybuckets.relicfuse.client.CommonClassClient;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.blay09.mods.balm.api.client.BalmClient;
import net.fabricmc.api.ClientModInitializer;


public class RelicFuseMainFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BalmClient.initialize(Constants.MOD_ID, EmptyLoadContext.INSTANCE, CommonClassClient::initClient);
    }

}
