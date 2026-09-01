package com.holybuckets.relicfuse;


import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.balm.server.ServerStartingEvent;
import com.holybuckets.relicfuse.command.CommandList;
import com.holybuckets.relicfuse.config.TemplateConfig;
import com.holybuckets.relicfuse.effect.FusionColors;
import com.holybuckets.relicfuse.effect.FusionGlow;
import com.holybuckets.relicfuse.core.FusionManager;
import com.holybuckets.relicfuse.core.ManagedPlayerFusions;

/**
 * Main instance of the mod, initialize this class statically via commonClass
 * This class will init all major Manager instances and events for the mod
 */
public class RelicFuseMain {
    private static boolean DEV_MODE = false;
    private static TemplateConfig CONFIG;
    public static RelicFuseMain INSTANCE;


    public RelicFuseMain()
    {
        super();
        INSTANCE = this;
        init();
    }

    private void init()
    {
        EventRegistrar registrar = EventRegistrar.getInstance();
        registrar.registerOnBeforeServerStarted(this::onServerStarting);
        FusionManager.init(registrar);
        CommandList.init(registrar);
        ManagedPlayerFusions.init(registrar);
        FusionColors.init(registrar);
        FusionGlow.init(registrar);
    }

    private void onServerStarting(ServerStartingEvent e) {
        this.DEV_MODE = false;
    }


}
