package com.holybuckets.relicfuse;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.fabricmc.api.ModInitializer;

//YOU NEED TO UPDATE NAME OF MAIN CLASS IN fabric.mod.json
//Use mod_id of other mods to add them in depends section, ensures they are loaded first
public class RelicFuseMainFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Balm.initializeMod(Constants.MOD_ID, FabricLoadContext.INSTANCE, CommonClass::init);
    }
}
