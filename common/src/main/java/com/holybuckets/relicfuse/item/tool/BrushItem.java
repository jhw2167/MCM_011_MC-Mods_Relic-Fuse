package com.holybuckets.relicfuse.item.tool;

import com.holybuckets.relicfuse.item.IFusedTool;

/**
 * All brushing behaviour lives in the vanilla BrushItem (useOn, getUseDuration, onUseTick), so the
 * upgrade brushes must extend it rather than the plain FusedToolItem base.
 */
public class BrushItem extends net.minecraft.world.item.BrushItem implements IFusedTool {

    public BrushItem(Properties properties) {
        super(properties);
    }
}
