# hbs_relicfuse:entrance_shaft
# OPTIONAL. Run from the SAME origin as :catacombs.
# Digs a 2x2 lined shaft with ladders from the catacomb ceiling up 40 blocks.
# This is the only piece that breaks the 6-block height rule -- it is separate on purpose.

fill ~14 ~5 ~1 ~17 ~45 ~4 deepslate_bricks
fill ~15 ~5 ~2 ~16 ~45 ~3 air
fill ~15 ~5 ~2 ~15 ~44 ~2 ladder[facing=south]
setblock ~15 ~4 ~2 ladder[facing=south]
setblock ~16 ~10 ~3 soul_lantern[hanging=true]
setblock ~16 ~20 ~3 soul_lantern[hanging=true]
setblock ~16 ~30 ~3 soul_lantern[hanging=true]
say [hbs_relicfuse] Entrance shaft placed. Break through the top block to surface.
