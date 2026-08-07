package com.holybuckets.relicfuse;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class RelicFuseMainForge {

    public RelicFuseMainForge(ModContainer modContainer, IEventBus modEventBus) {
        super();
        final var context = new NeoForgeLoadContext(modContainer, modEventBus);
        Balm.initializeMod(Constants.MOD_ID, context, CommonClass::init);
    }

}
