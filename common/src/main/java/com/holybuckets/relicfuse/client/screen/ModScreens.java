package com.holybuckets.relicfuse.client.screen;

import com.holybuckets.relicfuse.menu.ModMenus;
import net.blay09.mods.balm.api.client.screen.BalmScreens;

public class ModScreens {
    public static void clientInitialize(BalmScreens screens) {
        screens.registerScreen(
            ModMenus.countingChestMenu::get,
            CountingChestScreen::new
        );
    }

}
