# ==========================================================================
# Ancient Catacombs  --  hbs_relicfuse:catacombs   (v4)
#
# Tunnels bored after the Ancient Crystals, widened into workings where the
# first fusions were attempted. Built the way a mineshaft is built: nothing
# is placed except where the tunnel would otherwise open into nothing.
#
# Buried in stone it leaves no outline. Cutting through a cave, it seals
# itself with a one-block skin and keeps going.
#
# Three levels at ~0 / ~-6 / ~-12. No hall longer than 32.
# Origin (~ ~ ~) = FLOOR of the upper level, northwest end.
# Needs ~14 blocks of headroom below the origin.
# ==========================================================================

scoreboard objectives add relic_rng dummy

# ======================================================================
# PASS 1 -- skin. Only fills where there is nothing: open air, cave air,
# water, lava. Inside solid rock every one of these is a no-op.
# ======================================================================
fill ~3 ~-1 ~17 ~36 ~3 ~21 deepslate replace air
fill ~3 ~-1 ~17 ~36 ~3 ~21 deepslate replace cave_air
fill ~3 ~-1 ~17 ~36 ~3 ~21 deepslate replace water
fill ~3 ~-1 ~17 ~36 ~3 ~21 deepslate replace lava
fill ~9 ~-1 ~3 ~13 ~3 ~19 deepslate replace air
fill ~9 ~-1 ~3 ~13 ~3 ~19 deepslate replace cave_air
fill ~9 ~-1 ~3 ~13 ~3 ~19 deepslate replace water
fill ~9 ~-1 ~3 ~13 ~3 ~19 deepslate replace lava
fill ~25 ~-1 ~19 ~29 ~3 ~35 deepslate replace air
fill ~25 ~-1 ~19 ~29 ~3 ~35 deepslate replace cave_air
fill ~25 ~-1 ~19 ~29 ~3 ~35 deepslate replace water
fill ~25 ~-1 ~19 ~29 ~3 ~35 deepslate replace lava
fill ~31 ~-1 ~8 ~35 ~3 ~19 deepslate replace air
fill ~31 ~-1 ~8 ~35 ~3 ~19 deepslate replace cave_air
fill ~31 ~-1 ~8 ~35 ~3 ~19 deepslate replace water
fill ~31 ~-1 ~8 ~35 ~3 ~19 deepslate replace lava
fill ~19 ~-1 ~11 ~23 ~3 ~19 deepslate replace air
fill ~19 ~-1 ~11 ~23 ~3 ~19 deepslate replace cave_air
fill ~19 ~-1 ~11 ~23 ~3 ~19 deepslate replace water
fill ~19 ~-1 ~11 ~23 ~3 ~19 deepslate replace lava
fill ~4 ~-1 ~19 ~10 ~3 ~23 deepslate replace air
fill ~4 ~-1 ~19 ~10 ~3 ~23 deepslate replace cave_air
fill ~4 ~-1 ~19 ~10 ~3 ~23 deepslate replace water
fill ~4 ~-1 ~19 ~10 ~3 ~23 deepslate replace lava
fill ~12 ~-1 ~29 ~19 ~3 ~33 deepslate replace air
fill ~12 ~-1 ~29 ~19 ~3 ~33 deepslate replace cave_air
fill ~12 ~-1 ~29 ~19 ~3 ~33 deepslate replace water
fill ~12 ~-1 ~29 ~19 ~3 ~33 deepslate replace lava
fill ~29 ~-1 ~3 ~36 ~4 ~10 deepslate replace air
fill ~29 ~-1 ~3 ~36 ~4 ~10 deepslate replace cave_air
fill ~29 ~-1 ~3 ~36 ~4 ~10 deepslate replace water
fill ~29 ~-1 ~3 ~36 ~4 ~10 deepslate replace lava
fill ~19 ~-7 ~5 ~23 ~-3 ~38 deepslate replace air
fill ~19 ~-7 ~5 ~23 ~-3 ~38 deepslate replace cave_air
fill ~19 ~-7 ~5 ~23 ~-3 ~38 deepslate replace water
fill ~19 ~-7 ~5 ~23 ~-3 ~38 deepslate replace lava
fill ~5 ~-7 ~13 ~21 ~-3 ~17 deepslate replace air
fill ~5 ~-7 ~13 ~21 ~-3 ~17 deepslate replace cave_air
fill ~5 ~-7 ~13 ~21 ~-3 ~17 deepslate replace water
fill ~5 ~-7 ~13 ~21 ~-3 ~17 deepslate replace lava
fill ~21 ~-7 ~25 ~37 ~-3 ~29 deepslate replace air
fill ~21 ~-7 ~25 ~37 ~-3 ~29 deepslate replace cave_air
fill ~21 ~-7 ~25 ~37 ~-3 ~29 deepslate replace water
fill ~21 ~-7 ~25 ~37 ~-3 ~29 deepslate replace lava
fill ~6 ~-7 ~15 ~10 ~-3 ~29 deepslate replace air
fill ~6 ~-7 ~15 ~10 ~-3 ~29 deepslate replace cave_air
fill ~6 ~-7 ~15 ~10 ~-3 ~29 deepslate replace water
fill ~6 ~-7 ~15 ~10 ~-3 ~29 deepslate replace lava
fill ~22 ~-7 ~9 ~30 ~-3 ~13 deepslate replace air
fill ~22 ~-7 ~9 ~30 ~-3 ~13 deepslate replace cave_air
fill ~22 ~-7 ~9 ~30 ~-3 ~13 deepslate replace water
fill ~22 ~-7 ~9 ~30 ~-3 ~13 deepslate replace lava
fill ~13 ~-7 ~31 ~20 ~-3 ~35 deepslate replace air
fill ~13 ~-7 ~31 ~20 ~-3 ~35 deepslate replace cave_air
fill ~13 ~-7 ~31 ~20 ~-3 ~35 deepslate replace water
fill ~13 ~-7 ~31 ~20 ~-3 ~35 deepslate replace lava
fill ~32 ~-7 ~15 ~36 ~-3 ~27 deepslate replace air
fill ~32 ~-7 ~15 ~36 ~-3 ~27 deepslate replace cave_air
fill ~32 ~-7 ~15 ~36 ~-3 ~27 deepslate replace water
fill ~32 ~-7 ~15 ~36 ~-3 ~27 deepslate replace lava
fill ~3 ~-7 ~27 ~11 ~-2 ~35 deepslate replace air
fill ~3 ~-7 ~27 ~11 ~-2 ~35 deepslate replace cave_air
fill ~3 ~-7 ~27 ~11 ~-2 ~35 deepslate replace water
fill ~3 ~-7 ~27 ~11 ~-2 ~35 deepslate replace lava
fill ~7 ~-13 ~7 ~40 ~-9 ~11 deepslate replace air
fill ~7 ~-13 ~7 ~40 ~-9 ~11 deepslate replace cave_air
fill ~7 ~-13 ~7 ~40 ~-9 ~11 deepslate replace water
fill ~7 ~-13 ~7 ~40 ~-9 ~11 deepslate replace lava
fill ~13 ~-13 ~9 ~17 ~-9 ~25 deepslate replace air
fill ~13 ~-13 ~9 ~17 ~-9 ~25 deepslate replace cave_air
fill ~13 ~-13 ~9 ~17 ~-9 ~25 deepslate replace water
fill ~13 ~-13 ~9 ~17 ~-9 ~25 deepslate replace lava
fill ~29 ~-13 ~9 ~33 ~-9 ~23 deepslate replace air
fill ~29 ~-13 ~9 ~33 ~-9 ~23 deepslate replace cave_air
fill ~29 ~-13 ~9 ~33 ~-9 ~23 deepslate replace water
fill ~29 ~-13 ~9 ~33 ~-9 ~23 deepslate replace lava
fill ~27 ~-13 ~21 ~31 ~-9 ~25 deepslate replace air
fill ~27 ~-13 ~21 ~31 ~-9 ~25 deepslate replace cave_air
fill ~27 ~-13 ~21 ~31 ~-9 ~25 deepslate replace water
fill ~27 ~-13 ~21 ~31 ~-9 ~25 deepslate replace lava
fill ~19 ~-13 ~13 ~27 ~-9 ~17 deepslate replace air
fill ~19 ~-13 ~13 ~27 ~-9 ~17 deepslate replace cave_air
fill ~19 ~-13 ~13 ~27 ~-9 ~17 deepslate replace water
fill ~19 ~-13 ~13 ~27 ~-9 ~17 deepslate replace lava
fill ~8 ~-13 ~9 ~12 ~-9 ~21 deepslate replace air
fill ~8 ~-13 ~9 ~12 ~-9 ~21 deepslate replace cave_air
fill ~8 ~-13 ~9 ~12 ~-9 ~21 deepslate replace water
fill ~8 ~-13 ~9 ~12 ~-9 ~21 deepslate replace lava
fill ~33 ~-13 ~21 ~37 ~-9 ~31 deepslate replace air
fill ~33 ~-13 ~21 ~37 ~-9 ~31 deepslate replace cave_air
fill ~33 ~-13 ~21 ~37 ~-9 ~31 deepslate replace water
fill ~33 ~-13 ~21 ~37 ~-9 ~31 deepslate replace lava
fill ~23 ~-13 ~23 ~31 ~-8 ~31 deepslate replace air
fill ~23 ~-13 ~23 ~31 ~-8 ~31 deepslate replace cave_air
fill ~23 ~-13 ~23 ~31 ~-8 ~31 deepslate replace water
fill ~23 ~-13 ~23 ~31 ~-8 ~31 deepslate replace lava
fill ~10 ~-7 ~13 ~13 ~3 ~16 deepslate replace air
fill ~10 ~-7 ~13 ~13 ~3 ~16 deepslate replace cave_air
fill ~10 ~-7 ~13 ~13 ~3 ~16 deepslate replace water
fill ~10 ~-7 ~13 ~13 ~3 ~16 deepslate replace lava
fill ~20 ~-13 ~8 ~23 ~-3 ~11 deepslate replace air
fill ~20 ~-13 ~8 ~23 ~-3 ~11 deepslate replace cave_air
fill ~20 ~-13 ~8 ~23 ~-3 ~11 deepslate replace water
fill ~20 ~-13 ~8 ~23 ~-3 ~11 deepslate replace lava

# ======================================================================
# PASS 2 -- bore the tunnels
# ======================================================================
fill ~4 ~0 ~18 ~35 ~2 ~20 air
fill ~10 ~0 ~4 ~12 ~2 ~18 air
fill ~26 ~0 ~20 ~28 ~2 ~34 air
fill ~32 ~0 ~9 ~34 ~2 ~18 air
fill ~20 ~0 ~12 ~22 ~2 ~18 air
fill ~5 ~0 ~20 ~9 ~2 ~22 air
fill ~13 ~0 ~30 ~18 ~2 ~32 air
fill ~30 ~0 ~4 ~35 ~3 ~9 air
fill ~20 ~-6 ~6 ~22 ~-4 ~37 air
fill ~6 ~-6 ~14 ~20 ~-4 ~16 air
fill ~22 ~-6 ~26 ~36 ~-4 ~28 air
fill ~7 ~-6 ~16 ~9 ~-4 ~28 air
fill ~23 ~-6 ~10 ~29 ~-4 ~12 air
fill ~14 ~-6 ~32 ~19 ~-4 ~34 air
fill ~33 ~-6 ~16 ~35 ~-4 ~26 air
fill ~4 ~-6 ~28 ~10 ~-3 ~34 air
fill ~8 ~-12 ~8 ~39 ~-10 ~10 air
fill ~14 ~-12 ~10 ~16 ~-10 ~24 air
fill ~30 ~-12 ~10 ~32 ~-10 ~22 air
fill ~28 ~-12 ~22 ~30 ~-10 ~24 air
fill ~20 ~-12 ~14 ~26 ~-10 ~16 air
fill ~9 ~-12 ~10 ~11 ~-10 ~20 air
fill ~34 ~-12 ~22 ~36 ~-10 ~30 air
fill ~24 ~-12 ~24 ~30 ~-9 ~30 air
fill ~11 ~-6 ~14 ~12 ~2 ~15 air
fill ~21 ~-12 ~9 ~22 ~-4 ~10 air

# --- ladders in the shafts ---
setblock ~11 ~-6 ~15 ladder[facing=south]
setblock ~11 ~-5 ~15 ladder[facing=south]
setblock ~11 ~-4 ~15 ladder[facing=south]
setblock ~11 ~-3 ~15 ladder[facing=south]
setblock ~11 ~-2 ~15 ladder[facing=south]
setblock ~11 ~-1 ~15 ladder[facing=south]
setblock ~11 ~0 ~15 ladder[facing=south]
setblock ~11 ~1 ~15 ladder[facing=south]
setblock ~11 ~2 ~15 ladder[facing=south]
setblock ~12 ~-6 ~15 gravel
setblock ~12 ~-5 ~15 gravel
setblock ~21 ~-12 ~10 ladder[facing=south]
setblock ~21 ~-11 ~10 ladder[facing=south]
setblock ~21 ~-10 ~10 ladder[facing=south]
setblock ~21 ~-9 ~10 ladder[facing=south]
setblock ~21 ~-8 ~10 ladder[facing=south]
setblock ~21 ~-7 ~10 ladder[facing=south]
setblock ~21 ~-6 ~10 ladder[facing=south]
setblock ~21 ~-5 ~10 ladder[facing=south]
setblock ~21 ~-4 ~10 ladder[facing=south]
setblock ~22 ~-12 ~10 gravel
setblock ~22 ~-11 ~10 gravel

# ====================== UPPER LEVEL ======================
# --- the frames that are still standing ---
fill ~6 ~0 ~18 ~6 ~1 ~18 deepslate_bricks
fill ~6 ~0 ~20 ~6 ~1 ~20 deepslate_bricks
fill ~6 ~2 ~18 ~6 ~2 ~20 deepslate_bricks
fill ~11 ~0 ~18 ~11 ~1 ~18 deepslate_bricks
fill ~11 ~0 ~20 ~11 ~1 ~20 deepslate_bricks
fill ~11 ~2 ~18 ~11 ~2 ~20 cracked_deepslate_bricks
fill ~16 ~0 ~18 ~16 ~1 ~18 cracked_deepslate_bricks
fill ~16 ~0 ~20 ~16 ~1 ~20 cracked_deepslate_bricks
fill ~16 ~2 ~18 ~16 ~2 ~20 cracked_deepslate_bricks
fill ~21 ~0 ~18 ~21 ~1 ~18 cracked_deepslate_bricks
fill ~21 ~0 ~20 ~21 ~1 ~20 cracked_deepslate_bricks
fill ~21 ~2 ~18 ~21 ~2 ~20 deepslate_tiles
fill ~26 ~0 ~18 ~26 ~1 ~18 deepslate_bricks
setblock ~26 ~0 ~20 deepslate_brick_wall
setblock ~26 ~2 ~18 cracked_deepslate_bricks
fill ~31 ~0 ~18 ~31 ~1 ~18 polished_deepslate
setblock ~31 ~0 ~20 deepslate_brick_wall
setblock ~31 ~2 ~18 cracked_deepslate_bricks
fill ~10 ~0 ~6 ~10 ~1 ~6 deepslate_bricks
fill ~12 ~0 ~6 ~12 ~1 ~6 deepslate_bricks
fill ~10 ~2 ~6 ~12 ~2 ~6 deepslate_tiles
fill ~10 ~0 ~11 ~10 ~1 ~11 deepslate_bricks
setblock ~12 ~0 ~11 deepslate_brick_wall
setblock ~10 ~2 ~11 cracked_deepslate_bricks
fill ~10 ~0 ~16 ~10 ~1 ~16 cracked_deepslate_bricks
setblock ~12 ~0 ~16 deepslate_brick_wall
setblock ~10 ~2 ~16 cracked_deepslate_bricks
fill ~26 ~0 ~22 ~26 ~1 ~22 deepslate_bricks
setblock ~28 ~0 ~22 deepslate_brick_wall
setblock ~26 ~2 ~22 cracked_deepslate_bricks
fill ~26 ~0 ~27 ~26 ~1 ~27 cracked_deepslate_bricks
fill ~28 ~0 ~27 ~28 ~1 ~27 cracked_deepslate_bricks
fill ~26 ~2 ~27 ~28 ~2 ~27 deepslate_bricks
fill ~26 ~0 ~32 ~26 ~1 ~32 polished_deepslate
setblock ~28 ~0 ~32 deepslate_brick_wall
setblock ~26 ~2 ~32 cracked_deepslate_bricks
fill ~32 ~0 ~11 ~32 ~1 ~11 cracked_deepslate_bricks
fill ~34 ~0 ~11 ~34 ~1 ~11 cracked_deepslate_bricks
fill ~32 ~2 ~11 ~34 ~2 ~11 deepslate_tiles
fill ~32 ~0 ~16 ~32 ~1 ~16 deepslate_bricks
fill ~34 ~0 ~16 ~34 ~1 ~16 deepslate_bricks
fill ~32 ~2 ~16 ~34 ~2 ~16 deepslate_tiles
fill ~20 ~0 ~14 ~20 ~1 ~14 polished_deepslate
fill ~22 ~0 ~14 ~22 ~1 ~14 polished_deepslate
fill ~20 ~2 ~14 ~22 ~2 ~14 deepslate_bricks
fill ~7 ~0 ~20 ~7 ~1 ~20 deepslate_bricks
fill ~7 ~0 ~22 ~7 ~1 ~22 deepslate_bricks
fill ~7 ~2 ~20 ~7 ~2 ~22 deepslate_bricks
fill ~15 ~0 ~30 ~15 ~1 ~30 deepslate_bricks
fill ~15 ~0 ~32 ~15 ~1 ~32 deepslate_bricks
fill ~15 ~2 ~30 ~15 ~2 ~32 cracked_deepslate_bricks

# --- blotches of masonry in the raw rock ---
fill ~5 ~-1 ~21 ~5 ~-1 ~21 cobbled_deepslate replace deepslate
fill ~5 ~-1 ~22 ~5 ~-1 ~22 polished_deepslate replace deepslate
fill ~7 ~-1 ~19 ~7 ~-1 ~19 deepslate_tiles replace deepslate
fill ~7 ~-1 ~20 ~7 ~-1 ~20 cracked_deepslate_tiles replace deepslate
fill ~8 ~-1 ~18 ~8 ~-1 ~18 cobbled_deepslate replace deepslate
fill ~10 ~-1 ~10 ~10 ~-1 ~10 polished_deepslate replace deepslate
fill ~10 ~-1 ~11 ~10 ~-1 ~11 deepslate_tiles replace deepslate
fill ~10 ~-1 ~17 ~10 ~-1 ~17 cobbled_deepslate replace deepslate
fill ~10 ~-1 ~19 ~10 ~-1 ~19 cracked_deepslate_tiles replace deepslate
fill ~11 ~-1 ~5 ~11 ~-1 ~5 deepslate_bricks replace deepslate
fill ~11 ~-1 ~20 ~11 ~-1 ~20 cracked_deepslate_tiles replace deepslate
fill ~12 ~-1 ~6 ~12 ~-1 ~6 cobbled_deepslate replace deepslate
fill ~14 ~-1 ~31 ~14 ~-1 ~31 deepslate_tiles replace deepslate
fill ~15 ~-1 ~31 ~15 ~-1 ~31 cobbled_deepslate replace deepslate
fill ~16 ~-1 ~19 ~16 ~-1 ~19 cracked_deepslate_tiles replace deepslate
fill ~16 ~-1 ~32 ~16 ~-1 ~32 deepslate_tiles replace deepslate
fill ~18 ~-1 ~32 ~18 ~-1 ~32 deepslate_tiles replace deepslate
fill ~19 ~-1 ~19 ~19 ~-1 ~19 cobbled_deepslate replace deepslate
fill ~20 ~-1 ~12 ~20 ~-1 ~12 cracked_deepslate_tiles replace deepslate
fill ~20 ~-1 ~15 ~20 ~-1 ~15 cracked_deepslate_bricks replace deepslate
fill ~21 ~-1 ~12 ~21 ~-1 ~12 deepslate_tiles replace deepslate
fill ~21 ~-1 ~13 ~21 ~-1 ~13 cobbled_deepslate replace deepslate
fill ~21 ~-1 ~15 ~21 ~-1 ~15 cracked_deepslate_tiles replace deepslate
fill ~22 ~-1 ~12 ~22 ~-1 ~12 cracked_deepslate_bricks replace deepslate
fill ~22 ~-1 ~19 ~22 ~-1 ~19 polished_deepslate replace deepslate
fill ~23 ~-1 ~20 ~23 ~-1 ~20 cracked_deepslate_bricks replace deepslate
fill ~26 ~-1 ~23 ~26 ~-1 ~23 cracked_deepslate_bricks replace deepslate
fill ~26 ~-1 ~29 ~26 ~-1 ~29 polished_deepslate replace deepslate
fill ~27 ~-1 ~19 ~27 ~-1 ~19 deepslate_tiles replace deepslate
fill ~27 ~-1 ~25 ~27 ~-1 ~25 polished_deepslate replace deepslate
fill ~27 ~-1 ~29 ~27 ~-1 ~29 cobbled_deepslate replace deepslate
fill ~27 ~-1 ~31 ~27 ~-1 ~31 cobbled_deepslate replace deepslate
fill ~28 ~-1 ~24 ~28 ~-1 ~24 deepslate_bricks replace deepslate
fill ~28 ~-1 ~31 ~28 ~-1 ~31 polished_deepslate replace deepslate
fill ~31 ~-1 ~18 ~31 ~-1 ~18 deepslate_bricks replace deepslate
fill ~31 ~-1 ~19 ~31 ~-1 ~19 deepslate_bricks replace deepslate
fill ~31 ~-1 ~20 ~31 ~-1 ~20 cobbled_deepslate replace deepslate
fill ~32 ~-1 ~13 ~32 ~-1 ~13 polished_deepslate replace deepslate
fill ~33 ~-1 ~4 ~33 ~-1 ~4 cracked_deepslate_bricks replace deepslate
fill ~33 ~-1 ~13 ~33 ~-1 ~13 cobbled_deepslate replace deepslate
fill ~33 ~-1 ~19 ~33 ~-1 ~19 cracked_deepslate_bricks replace deepslate
fill ~33 ~-1 ~20 ~33 ~-1 ~20 cracked_deepslate_bricks replace deepslate
fill ~34 ~-1 ~5 ~34 ~-1 ~5 cracked_deepslate_bricks replace deepslate
fill ~34 ~-1 ~6 ~34 ~-1 ~6 deepslate_bricks replace deepslate
fill ~34 ~-1 ~18 ~34 ~-1 ~18 deepslate_tiles replace deepslate
fill ~34 ~-1 ~19 ~34 ~-1 ~19 polished_deepslate replace deepslate
fill ~35 ~-1 ~5 ~35 ~-1 ~5 deepslate_tiles replace deepslate
fill ~35 ~-1 ~7 ~35 ~-1 ~7 cobbled_deepslate replace deepslate
fill ~35 ~-1 ~9 ~35 ~-1 ~9 cracked_deepslate_tiles replace deepslate
fill ~35 ~-1 ~18 ~35 ~-1 ~18 deepslate_bricks replace deepslate
fill ~4 ~1 ~18 ~4 ~1 ~18 cobbled_deepslate replace deepslate
fill ~5 ~2 ~18 ~5 ~2 ~18 cobbled_deepslate replace deepslate
fill ~6 ~1 ~21 ~6 ~1 ~21 deepslate_bricks replace deepslate
fill ~6 ~2 ~22 ~6 ~2 ~22 deepslate_tiles replace deepslate
fill ~7 ~2 ~21 ~7 ~2 ~21 cracked_deepslate_bricks replace deepslate
fill ~8 ~1 ~21 ~8 ~1 ~21 cracked_deepslate_tiles replace deepslate
fill ~10 ~2 ~12 ~10 ~2 ~12 deepslate_bricks replace deepslate
fill ~10 ~1 ~14 ~10 ~1 ~14 cracked_deepslate_tiles replace deepslate
fill ~10 ~1 ~16 ~10 ~1 ~16 cracked_deepslate_tiles replace deepslate
fill ~11 ~1 ~13 ~11 ~1 ~13 cracked_deepslate_bricks replace deepslate
fill ~11 ~1 ~16 ~11 ~1 ~16 cobbled_deepslate replace deepslate
fill ~11 ~0 ~17 ~11 ~0 ~17 deepslate_bricks replace deepslate
fill ~11 ~1 ~19 ~11 ~1 ~19 cracked_deepslate_tiles replace deepslate
fill ~12 ~2 ~15 ~12 ~2 ~15 deepslate_bricks replace deepslate
fill ~12 ~2 ~16 ~12 ~2 ~16 cobbled_deepslate replace deepslate
fill ~12 ~2 ~19 ~12 ~2 ~19 deepslate_bricks replace deepslate
fill ~12 ~1 ~20 ~12 ~1 ~20 cracked_deepslate_bricks replace deepslate
fill ~13 ~2 ~18 ~13 ~2 ~18 cracked_deepslate_tiles replace deepslate
fill ~13 ~1 ~20 ~13 ~1 ~20 deepslate_tiles replace deepslate
fill ~13 ~2 ~32 ~13 ~2 ~32 cracked_deepslate_bricks replace deepslate
fill ~16 ~1 ~18 ~16 ~1 ~18 cracked_deepslate_bricks replace deepslate
fill ~20 ~1 ~16 ~20 ~1 ~16 deepslate_tiles replace deepslate
fill ~21 ~0 ~17 ~21 ~0 ~17 deepslate_tiles replace deepslate
fill ~22 ~2 ~15 ~22 ~2 ~15 cobbled_deepslate replace deepslate
fill ~24 ~1 ~18 ~24 ~1 ~18 deepslate_bricks replace deepslate
fill ~25 ~0 ~19 ~25 ~0 ~19 polished_deepslate replace deepslate
fill ~26 ~1 ~21 ~26 ~1 ~21 cobbled_deepslate replace deepslate
fill ~26 ~2 ~22 ~26 ~2 ~22 cracked_deepslate_tiles replace deepslate
fill ~26 ~0 ~24 ~26 ~0 ~24 cracked_deepslate_bricks replace deepslate
fill ~26 ~0 ~28 ~26 ~0 ~28 deepslate_tiles replace deepslate
fill ~27 ~0 ~23 ~27 ~0 ~23 polished_deepslate replace deepslate
fill ~28 ~2 ~20 ~28 ~2 ~20 cracked_deepslate_tiles replace deepslate
fill ~28 ~1 ~23 ~28 ~1 ~23 cobbled_deepslate replace deepslate
fill ~30 ~2 ~4 ~30 ~2 ~4 cobbled_deepslate replace deepslate
fill ~30 ~0 ~8 ~30 ~0 ~8 cracked_deepslate_tiles replace deepslate
fill ~30 ~0 ~9 ~30 ~0 ~9 deepslate_tiles replace deepslate
fill ~31 ~2 ~5 ~31 ~2 ~5 polished_deepslate replace deepslate
fill ~31 ~1 ~6 ~31 ~1 ~6 cracked_deepslate_bricks replace deepslate
fill ~32 ~1 ~5 ~32 ~1 ~5 deepslate_tiles replace deepslate
fill ~32 ~0 ~11 ~32 ~0 ~11 cracked_deepslate_tiles replace deepslate
fill ~32 ~1 ~17 ~32 ~1 ~17 cracked_deepslate_tiles replace deepslate
fill ~33 ~0 ~7 ~33 ~0 ~7 deepslate_bricks replace deepslate
fill ~33 ~0 ~8 ~33 ~0 ~8 deepslate_tiles replace deepslate
fill ~33 ~2 ~12 ~33 ~2 ~12 cracked_deepslate_tiles replace deepslate
fill ~34 ~0 ~20 ~34 ~0 ~20 cracked_deepslate_tiles replace deepslate

# --- fallen rock ---
setblock ~4 ~0 ~20 deepslate_brick_wall
setblock ~5 ~0 ~20 gravel
setblock ~6 ~0 ~20 deepslate_tile_slab[type=bottom]
setblock ~10 ~0 ~15 gravel
setblock ~11 ~0 ~4 deepslate_brick_wall
setblock ~11 ~0 ~6 deepslate_brick_slab[type=bottom]
setblock ~11 ~0 ~15 gravel
setblock ~12 ~0 ~11 deepslate_brick_slab[type=bottom]
setblock ~14 ~0 ~18 cobbled_deepslate_wall
setblock ~14 ~0 ~20 gravel
setblock ~15 ~0 ~18 gravel
setblock ~15 ~0 ~19 cobbled_deepslate
setblock ~15 ~0 ~20 deepslate_brick_wall
setblock ~16 ~0 ~30 cobbled_deepslate
setblock ~17 ~0 ~19 deepslate_brick_slab[type=bottom]
setblock ~17 ~0 ~30 gravel
setblock ~17 ~0 ~31 deepslate_brick_slab[type=bottom]
setblock ~18 ~0 ~31 deepslate_brick_slab[type=bottom]
setblock ~19 ~0 ~18 cobbled_deepslate
setblock ~20 ~0 ~17 gravel
setblock ~20 ~0 ~19 gravel
setblock ~22 ~0 ~14 deepslate_brick_slab[type=bottom]
setblock ~22 ~0 ~17 cobbled_deepslate
setblock ~24 ~0 ~20 deepslate_brick_slab[type=bottom]
setblock ~26 ~0 ~26 cobbled_deepslate_wall
setblock ~26 ~0 ~32 deepslate_brick_slab[type=bottom]
setblock ~26 ~0 ~33 gravel
setblock ~27 ~0 ~18 deepslate_brick_wall
setblock ~27 ~0 ~20 deepslate_brick_wall
setblock ~27 ~0 ~21 deepslate_tile_slab[type=bottom]
setblock ~27 ~0 ~27 deepslate_brick_slab[type=bottom]
setblock ~28 ~0 ~25 deepslate_tile_slab[type=bottom]
setblock ~28 ~0 ~26 deepslate_brick_slab[type=bottom]
setblock ~28 ~0 ~27 deepslate_brick_wall
setblock ~29 ~0 ~18 deepslate_tile_slab[type=bottom]
setblock ~29 ~0 ~20 cobbled_deepslate
setblock ~30 ~0 ~18 deepslate_tile_slab[type=bottom]
setblock ~32 ~0 ~4 cobbled_deepslate_wall
setblock ~32 ~0 ~12 deepslate_tile_slab[type=bottom]
setblock ~32 ~0 ~14 cobbled_deepslate_wall
setblock ~32 ~0 ~18 deepslate_brick_wall
setblock ~32 ~0 ~19 deepslate_brick_wall
setblock ~33 ~0 ~5 gravel
setblock ~33 ~0 ~11 deepslate_tile_slab[type=bottom]
setblock ~34 ~0 ~10 deepslate_brick_wall
setblock ~5 ~2 ~19 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~7 ~2 ~18 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~11 ~2 ~11 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~11 ~2 ~14 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~11 ~2 ~18 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~14 ~2 ~30 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~18 ~2 ~30 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~20 ~2 ~20 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~21 ~2 ~20 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~27 ~2 ~32 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~28 ~2 ~33 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~28 ~2 ~34 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~30 ~2 ~5 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~35 ~2 ~4 pointed_dripstone[vertical_direction=down,thickness=tip]

# --- cave-ins ---
fill ~9 ~0 ~19 ~9 ~1 ~19 gravel
setblock ~9 ~2 ~19 cobbled_deepslate
fill ~12 ~0 ~8 ~12 ~1 ~8 gravel
setblock ~12 ~2 ~8 cobbled_deepslate
fill ~12 ~0 ~18 ~12 ~1 ~18 gravel
setblock ~12 ~2 ~18 cobbled_deepslate
fill ~31 ~0 ~8 ~31 ~1 ~8 gravel
setblock ~31 ~2 ~8 cobbled_deepslate
fill ~32 ~0 ~8 ~32 ~1 ~8 gravel
setblock ~32 ~2 ~8 cobbled_deepslate
fill ~33 ~0 ~10 ~33 ~1 ~10 gravel
setblock ~33 ~2 ~10 cobbled_deepslate

# --- cobwebs in the still air ---
setblock ~8 ~1 ~22 cobweb
setblock ~10 ~2 ~13 cobweb
setblock ~12 ~1 ~7 cobweb
setblock ~15 ~2 ~30 cobweb
setblock ~16 ~2 ~20 cobweb
setblock ~19 ~2 ~20 cobweb
setblock ~21 ~1 ~18 cobweb
setblock ~27 ~2 ~30 cobweb
setblock ~29 ~2 ~19 cobweb
setblock ~30 ~1 ~7 cobweb
setblock ~33 ~2 ~14 cobweb
setblock ~35 ~1 ~8 cobweb

# --- sculk ---
fill ~7 ~-1 ~22 ~7 ~-1 ~22 sculk replace deepslate
fill ~9 ~-1 ~20 ~9 ~-1 ~20 sculk replace deepslate
fill ~9 ~-1 ~22 ~9 ~-1 ~22 sculk replace deepslate
fill ~11 ~-1 ~7 ~11 ~-1 ~7 sculk replace deepslate
fill ~13 ~-1 ~19 ~13 ~-1 ~19 sculk replace deepslate
fill ~18 ~-1 ~18 ~18 ~-1 ~18 sculk replace deepslate
fill ~23 ~-1 ~18 ~23 ~-1 ~18 sculk replace deepslate
fill ~24 ~-1 ~19 ~24 ~-1 ~19 sculk replace deepslate
fill ~26 ~-1 ~20 ~26 ~-1 ~20 sculk replace deepslate
fill ~26 ~-1 ~25 ~26 ~-1 ~25 sculk replace deepslate
fill ~26 ~-1 ~31 ~26 ~-1 ~31 sculk replace deepslate
fill ~33 ~-1 ~16 ~33 ~-1 ~16 sculk replace deepslate
fill ~33 ~-1 ~17 ~33 ~-1 ~17 sculk replace deepslate
fill ~34 ~-1 ~7 ~34 ~-1 ~7 sculk replace deepslate
fill ~34 ~-1 ~13 ~34 ~-1 ~13 sculk replace deepslate
fill ~35 ~-1 ~6 ~35 ~-1 ~6 sculk replace deepslate
fill ~35 ~-1 ~19 ~35 ~-1 ~19 sculk replace deepslate
fill ~35 ~-1 ~20 ~35 ~-1 ~20 sculk replace deepslate
setblock ~11 ~0 ~7 sculk_sensor
setblock ~9 ~0 ~20 sculk_shrieker[can_summon=true,shrieking=false]

# --- what light survives ---
setblock ~12 ~2 ~9 soul_lantern[hanging=true]
setblock ~12 ~2 ~14 soul_lantern[hanging=true]
setblock ~17 ~2 ~32 soul_lantern[hanging=true]
setblock ~21 ~2 ~19 soul_lantern[hanging=true]
setblock ~26 ~2 ~19 soul_lantern[hanging=true]
setblock ~30 ~2 ~19 soul_lantern[hanging=true]
setblock ~31 ~2 ~4 soul_lantern[hanging=true]
setblock ~32 ~2 ~6 soul_lantern[hanging=true]
setblock ~12 ~2 ~4 iron_chain[axis=y]
setblock ~12 ~2 ~17 iron_chain[axis=y]
setblock ~26 ~2 ~27 iron_chain[axis=y]
setblock ~28 ~2 ~21 iron_chain[axis=y]
setblock ~34 ~2 ~14 iron_chain[axis=y]

# ====================== MIDDLE LEVEL ======================
# --- the frames that are still standing ---
fill ~20 ~-6 ~8 ~20 ~-5 ~8 deepslate_bricks
fill ~22 ~-6 ~8 ~22 ~-5 ~8 deepslate_bricks
fill ~20 ~-4 ~8 ~22 ~-4 ~8 deepslate_tiles
fill ~20 ~-6 ~13 ~20 ~-5 ~13 deepslate_bricks
fill ~22 ~-6 ~13 ~22 ~-5 ~13 deepslate_bricks
fill ~20 ~-4 ~13 ~22 ~-4 ~13 deepslate_bricks
fill ~20 ~-6 ~18 ~20 ~-5 ~18 polished_deepslate
fill ~22 ~-6 ~18 ~22 ~-5 ~18 polished_deepslate
fill ~20 ~-4 ~18 ~22 ~-4 ~18 deepslate_tiles
fill ~20 ~-6 ~23 ~20 ~-5 ~23 cracked_deepslate_bricks
fill ~22 ~-6 ~23 ~22 ~-5 ~23 cracked_deepslate_bricks
fill ~20 ~-4 ~23 ~22 ~-4 ~23 deepslate_bricks
fill ~20 ~-6 ~28 ~20 ~-5 ~28 deepslate_bricks
fill ~22 ~-6 ~28 ~22 ~-5 ~28 deepslate_bricks
fill ~20 ~-4 ~28 ~22 ~-4 ~28 cracked_deepslate_bricks
fill ~20 ~-6 ~33 ~20 ~-5 ~33 cracked_deepslate_bricks
fill ~22 ~-6 ~33 ~22 ~-5 ~33 cracked_deepslate_bricks
fill ~20 ~-4 ~33 ~22 ~-4 ~33 deepslate_bricks
fill ~8 ~-6 ~14 ~8 ~-5 ~14 cracked_deepslate_bricks
setblock ~8 ~-6 ~16 deepslate_brick_wall
setblock ~8 ~-4 ~14 cracked_deepslate_bricks
fill ~13 ~-6 ~14 ~13 ~-5 ~14 polished_deepslate
setblock ~13 ~-6 ~16 deepslate_brick_wall
setblock ~13 ~-4 ~14 cracked_deepslate_bricks
fill ~18 ~-6 ~14 ~18 ~-5 ~14 deepslate_bricks
fill ~18 ~-6 ~16 ~18 ~-5 ~16 deepslate_bricks
fill ~18 ~-4 ~14 ~18 ~-4 ~16 deepslate_bricks
fill ~24 ~-6 ~26 ~24 ~-5 ~26 deepslate_bricks
fill ~24 ~-6 ~28 ~24 ~-5 ~28 deepslate_bricks
fill ~24 ~-4 ~26 ~24 ~-4 ~28 cracked_deepslate_bricks
fill ~29 ~-6 ~26 ~29 ~-5 ~26 cracked_deepslate_bricks
setblock ~29 ~-6 ~28 deepslate_brick_wall
setblock ~29 ~-4 ~26 cracked_deepslate_bricks
fill ~34 ~-6 ~26 ~34 ~-5 ~26 polished_deepslate
fill ~34 ~-6 ~28 ~34 ~-5 ~28 polished_deepslate
fill ~34 ~-4 ~26 ~34 ~-4 ~28 deepslate_bricks
fill ~7 ~-6 ~18 ~7 ~-5 ~18 polished_deepslate
fill ~9 ~-6 ~18 ~9 ~-5 ~18 polished_deepslate
fill ~7 ~-4 ~18 ~9 ~-4 ~18 cracked_deepslate_bricks
fill ~7 ~-6 ~23 ~7 ~-5 ~23 cracked_deepslate_bricks
setblock ~9 ~-6 ~23 deepslate_brick_wall
setblock ~7 ~-4 ~23 cracked_deepslate_bricks
fill ~25 ~-6 ~10 ~25 ~-5 ~10 cracked_deepslate_bricks
setblock ~25 ~-6 ~12 deepslate_brick_wall
setblock ~25 ~-4 ~10 cracked_deepslate_bricks
fill ~16 ~-6 ~32 ~16 ~-5 ~32 deepslate_bricks
setblock ~16 ~-6 ~34 deepslate_brick_wall
setblock ~16 ~-4 ~32 cracked_deepslate_bricks
fill ~33 ~-6 ~18 ~33 ~-5 ~18 deepslate_bricks
fill ~35 ~-6 ~18 ~35 ~-5 ~18 deepslate_bricks
fill ~33 ~-4 ~18 ~35 ~-4 ~18 cracked_deepslate_bricks
fill ~33 ~-6 ~23 ~33 ~-5 ~23 cracked_deepslate_bricks
fill ~35 ~-6 ~23 ~35 ~-5 ~23 cracked_deepslate_bricks
fill ~33 ~-4 ~23 ~35 ~-4 ~23 deepslate_tiles

# --- blotches of masonry in the raw rock ---
fill ~4 ~-7 ~34 ~4 ~-7 ~34 deepslate_tiles replace deepslate
fill ~5 ~-7 ~29 ~5 ~-7 ~29 polished_deepslate replace deepslate
fill ~6 ~-7 ~28 ~6 ~-7 ~28 cracked_deepslate_bricks replace deepslate
fill ~6 ~-7 ~31 ~6 ~-7 ~31 cobbled_deepslate replace deepslate
fill ~6 ~-7 ~32 ~6 ~-7 ~32 cracked_deepslate_bricks replace deepslate
fill ~7 ~-7 ~15 ~7 ~-7 ~15 cobbled_deepslate replace deepslate
fill ~7 ~-7 ~18 ~7 ~-7 ~18 polished_deepslate replace deepslate
fill ~7 ~-7 ~25 ~7 ~-7 ~25 cracked_deepslate_bricks replace deepslate
fill ~7 ~-7 ~26 ~7 ~-7 ~26 deepslate_bricks replace deepslate
fill ~7 ~-7 ~28 ~7 ~-7 ~28 cracked_deepslate_tiles replace deepslate
fill ~7 ~-7 ~29 ~7 ~-7 ~29 polished_deepslate replace deepslate
fill ~8 ~-7 ~22 ~8 ~-7 ~22 deepslate_bricks replace deepslate
fill ~9 ~-7 ~22 ~9 ~-7 ~22 deepslate_bricks replace deepslate
fill ~9 ~-7 ~25 ~9 ~-7 ~25 cobbled_deepslate replace deepslate
fill ~11 ~-7 ~15 ~11 ~-7 ~15 cobbled_deepslate replace deepslate
fill ~12 ~-7 ~14 ~12 ~-7 ~14 deepslate_tiles replace deepslate
fill ~15 ~-7 ~16 ~15 ~-7 ~16 cracked_deepslate_bricks replace deepslate
fill ~17 ~-7 ~15 ~17 ~-7 ~15 polished_deepslate replace deepslate
fill ~18 ~-7 ~32 ~18 ~-7 ~32 deepslate_bricks replace deepslate
fill ~19 ~-7 ~14 ~19 ~-7 ~14 cracked_deepslate_bricks replace deepslate
fill ~19 ~-7 ~33 ~19 ~-7 ~33 polished_deepslate replace deepslate
fill ~19 ~-7 ~34 ~19 ~-7 ~34 polished_deepslate replace deepslate
fill ~20 ~-7 ~7 ~20 ~-7 ~7 cracked_deepslate_tiles replace deepslate
fill ~20 ~-7 ~22 ~20 ~-7 ~22 cracked_deepslate_tiles replace deepslate
fill ~21 ~-7 ~10 ~21 ~-7 ~10 deepslate_tiles replace deepslate
fill ~21 ~-7 ~12 ~21 ~-7 ~12 deepslate_tiles replace deepslate
fill ~21 ~-7 ~31 ~21 ~-7 ~31 polished_deepslate replace deepslate
fill ~21 ~-7 ~32 ~21 ~-7 ~32 polished_deepslate replace deepslate
fill ~21 ~-7 ~36 ~21 ~-7 ~36 cracked_deepslate_bricks replace deepslate
fill ~22 ~-7 ~8 ~22 ~-7 ~8 deepslate_tiles replace deepslate
fill ~22 ~-7 ~12 ~22 ~-7 ~12 deepslate_bricks replace deepslate
fill ~22 ~-7 ~13 ~22 ~-7 ~13 cracked_deepslate_tiles replace deepslate
fill ~22 ~-7 ~17 ~22 ~-7 ~17 cracked_deepslate_bricks replace deepslate
fill ~22 ~-7 ~23 ~22 ~-7 ~23 cracked_deepslate_bricks replace deepslate
fill ~22 ~-7 ~31 ~22 ~-7 ~31 cracked_deepslate_bricks replace deepslate
fill ~22 ~-7 ~34 ~22 ~-7 ~34 cracked_deepslate_bricks replace deepslate
fill ~22 ~-7 ~35 ~22 ~-7 ~35 deepslate_tiles replace deepslate
fill ~23 ~-7 ~10 ~23 ~-7 ~10 cracked_deepslate_tiles replace deepslate
fill ~23 ~-7 ~26 ~23 ~-7 ~26 polished_deepslate replace deepslate
fill ~24 ~-7 ~28 ~24 ~-7 ~28 polished_deepslate replace deepslate
fill ~27 ~-7 ~26 ~27 ~-7 ~26 deepslate_bricks replace deepslate
fill ~28 ~-7 ~11 ~28 ~-7 ~11 deepslate_tiles replace deepslate
fill ~31 ~-7 ~26 ~31 ~-7 ~26 deepslate_tiles replace deepslate
fill ~34 ~-7 ~19 ~34 ~-7 ~19 cracked_deepslate_tiles replace deepslate
fill ~34 ~-7 ~21 ~34 ~-7 ~21 cracked_deepslate_bricks replace deepslate
fill ~34 ~-7 ~22 ~34 ~-7 ~22 polished_deepslate replace deepslate
fill ~34 ~-7 ~23 ~34 ~-7 ~23 cobbled_deepslate replace deepslate
fill ~35 ~-7 ~18 ~35 ~-7 ~18 cracked_deepslate_bricks replace deepslate
fill ~35 ~-7 ~21 ~35 ~-7 ~21 polished_deepslate replace deepslate
fill ~35 ~-7 ~23 ~35 ~-7 ~23 deepslate_tiles replace deepslate
fill ~4 ~-5 ~28 ~4 ~-5 ~28 polished_deepslate replace deepslate
fill ~5 ~-6 ~28 ~5 ~-6 ~28 deepslate_bricks replace deepslate
fill ~5 ~-4 ~34 ~5 ~-4 ~34 cobbled_deepslate replace deepslate
fill ~6 ~-4 ~14 ~6 ~-4 ~14 deepslate_bricks replace deepslate
fill ~6 ~-5 ~15 ~6 ~-5 ~15 polished_deepslate replace deepslate
fill ~7 ~-5 ~14 ~7 ~-5 ~14 polished_deepslate replace deepslate
fill ~7 ~-5 ~16 ~7 ~-5 ~16 deepslate_bricks replace deepslate
fill ~7 ~-4 ~21 ~7 ~-4 ~21 cracked_deepslate_tiles replace deepslate
fill ~7 ~-4 ~32 ~7 ~-4 ~32 deepslate_tiles replace deepslate
fill ~8 ~-4 ~16 ~8 ~-4 ~16 cobbled_deepslate replace deepslate
fill ~8 ~-6 ~26 ~8 ~-6 ~26 cracked_deepslate_tiles replace deepslate
fill ~9 ~-4 ~24 ~9 ~-4 ~24 cracked_deepslate_bricks replace deepslate
fill ~9 ~-6 ~26 ~9 ~-6 ~26 cobbled_deepslate replace deepslate
fill ~9 ~-6 ~29 ~9 ~-6 ~29 cracked_deepslate_tiles replace deepslate
fill ~10 ~-4 ~28 ~10 ~-4 ~28 cobbled_deepslate replace deepslate
fill ~11 ~-6 ~16 ~11 ~-6 ~16 cracked_deepslate_tiles replace deepslate
fill ~14 ~-4 ~14 ~14 ~-4 ~14 cobbled_deepslate replace deepslate
fill ~14 ~-4 ~16 ~14 ~-4 ~16 deepslate_tiles replace deepslate
fill ~14 ~-5 ~33 ~14 ~-5 ~33 polished_deepslate replace deepslate
fill ~16 ~-6 ~32 ~16 ~-6 ~32 deepslate_bricks replace deepslate
fill ~17 ~-4 ~14 ~17 ~-4 ~14 cobbled_deepslate replace deepslate
fill ~18 ~-5 ~15 ~18 ~-5 ~15 polished_deepslate replace deepslate
fill ~20 ~-6 ~20 ~20 ~-6 ~20 cracked_deepslate_tiles replace deepslate
fill ~20 ~-5 ~23 ~20 ~-5 ~23 deepslate_bricks replace deepslate
fill ~20 ~-6 ~24 ~20 ~-6 ~24 cracked_deepslate_bricks replace deepslate
fill ~20 ~-6 ~35 ~20 ~-6 ~35 cobbled_deepslate replace deepslate
fill ~21 ~-6 ~14 ~21 ~-6 ~14 cracked_deepslate_tiles replace deepslate
fill ~21 ~-4 ~15 ~21 ~-4 ~15 polished_deepslate replace deepslate
fill ~21 ~-5 ~21 ~21 ~-5 ~21 cracked_deepslate_tiles replace deepslate
fill ~22 ~-6 ~6 ~22 ~-6 ~6 cracked_deepslate_tiles replace deepslate
fill ~22 ~-4 ~9 ~22 ~-4 ~9 deepslate_tiles replace deepslate
fill ~22 ~-5 ~36 ~22 ~-5 ~36 deepslate_bricks replace deepslate
fill ~27 ~-6 ~11 ~27 ~-6 ~11 cracked_deepslate_bricks replace deepslate
fill ~29 ~-5 ~10 ~29 ~-5 ~10 polished_deepslate replace deepslate
fill ~29 ~-5 ~28 ~29 ~-5 ~28 polished_deepslate replace deepslate
fill ~30 ~-5 ~26 ~30 ~-5 ~26 cracked_deepslate_bricks replace deepslate
fill ~30 ~-5 ~27 ~30 ~-5 ~27 polished_deepslate replace deepslate
fill ~31 ~-5 ~28 ~31 ~-5 ~28 polished_deepslate replace deepslate
fill ~33 ~-5 ~23 ~33 ~-5 ~23 polished_deepslate replace deepslate
fill ~34 ~-5 ~25 ~34 ~-5 ~25 polished_deepslate replace deepslate
fill ~35 ~-4 ~17 ~35 ~-4 ~17 deepslate_bricks replace deepslate
fill ~35 ~-6 ~22 ~35 ~-6 ~22 deepslate_tiles replace deepslate
fill ~35 ~-6 ~25 ~35 ~-6 ~25 cobbled_deepslate replace deepslate
fill ~35 ~-5 ~27 ~35 ~-5 ~27 deepslate_tiles replace deepslate
fill ~36 ~-6 ~28 ~36 ~-6 ~28 deepslate_bricks replace deepslate

# --- fallen rock ---
setblock ~4 ~-6 ~30 cobbled_deepslate
setblock ~6 ~-6 ~33 gravel
setblock ~7 ~-6 ~19 deepslate_brick_wall
setblock ~8 ~-6 ~20 cobbled_deepslate
setblock ~8 ~-6 ~34 deepslate_brick_slab[type=bottom]
setblock ~9 ~-6 ~14 deepslate_tile_slab[type=bottom]
setblock ~9 ~-6 ~20 deepslate_tile_slab[type=bottom]
setblock ~9 ~-6 ~30 cobbled_deepslate
setblock ~12 ~-6 ~16 cobbled_deepslate_wall
setblock ~17 ~-6 ~33 cobbled_deepslate
setblock ~18 ~-6 ~33 cobbled_deepslate_wall
setblock ~20 ~-6 ~18 deepslate_brick_slab[type=bottom]
setblock ~20 ~-6 ~19 cobbled_deepslate_wall
setblock ~20 ~-6 ~21 gravel
setblock ~20 ~-6 ~29 deepslate_brick_slab[type=bottom]
setblock ~20 ~-6 ~30 cobbled_deepslate_wall
setblock ~20 ~-6 ~31 deepslate_tile_slab[type=bottom]
setblock ~20 ~-6 ~37 deepslate_brick_wall
setblock ~21 ~-6 ~7 deepslate_brick_slab[type=bottom]
setblock ~21 ~-6 ~11 cobbled_deepslate
setblock ~21 ~-6 ~25 gravel
setblock ~21 ~-6 ~28 deepslate_tile_slab[type=bottom]
setblock ~21 ~-6 ~29 deepslate_brick_slab[type=bottom]
setblock ~21 ~-6 ~34 cobbled_deepslate_wall
setblock ~21 ~-6 ~37 deepslate_brick_wall
setblock ~22 ~-6 ~11 cobbled_deepslate_wall
setblock ~22 ~-6 ~21 deepslate_brick_slab[type=bottom]
setblock ~22 ~-6 ~25 cobbled_deepslate
setblock ~22 ~-6 ~29 deepslate_brick_slab[type=bottom]
setblock ~23 ~-6 ~12 deepslate_tile_slab[type=bottom]
setblock ~24 ~-6 ~11 deepslate_brick_wall
setblock ~24 ~-6 ~12 cobbled_deepslate
setblock ~25 ~-6 ~10 deepslate_brick_wall
setblock ~25 ~-6 ~26 cobbled_deepslate_wall
setblock ~25 ~-6 ~27 cobbled_deepslate
setblock ~27 ~-6 ~28 deepslate_tile_slab[type=bottom]
setblock ~29 ~-6 ~26 deepslate_brick_slab[type=bottom]
setblock ~32 ~-6 ~26 deepslate_brick_slab[type=bottom]
setblock ~33 ~-6 ~25 cobbled_deepslate
setblock ~33 ~-6 ~26 deepslate_brick_slab[type=bottom]
setblock ~33 ~-6 ~27 deepslate_brick_slab[type=bottom]
setblock ~33 ~-6 ~28 cobbled_deepslate_wall
setblock ~35 ~-6 ~16 deepslate_brick_slab[type=bottom]
setblock ~35 ~-6 ~20 cobbled_deepslate_wall
setblock ~36 ~-6 ~27 deepslate_tile_slab[type=bottom]
setblock ~5 ~-4 ~33 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~7 ~-4 ~20 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~7 ~-4 ~24 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~8 ~-4 ~25 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~8 ~-4 ~28 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~9 ~-4 ~21 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~15 ~-4 ~15 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~20 ~-4 ~9 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~22 ~-4 ~7 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~22 ~-4 ~24 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~22 ~-4 ~28 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~27 ~-4 ~10 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~32 ~-4 ~28 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~34 ~-4 ~24 pointed_dripstone[vertical_direction=down,thickness=tip]

# --- cave-ins ---
fill ~4 ~-6 ~29 ~4 ~-5 ~29 gravel
setblock ~4 ~-4 ~29 cobbled_deepslate
fill ~5 ~-6 ~30 ~5 ~-5 ~30 gravel
setblock ~5 ~-4 ~30 cobbled_deepslate
fill ~8 ~-6 ~15 ~8 ~-5 ~15 gravel
setblock ~8 ~-4 ~15 cobbled_deepslate
fill ~22 ~-6 ~26 ~22 ~-5 ~26 gravel
setblock ~22 ~-4 ~26 cobbled_deepslate
fill ~23 ~-6 ~27 ~23 ~-5 ~27 gravel
setblock ~23 ~-4 ~27 cobbled_deepslate
fill ~34 ~-6 ~28 ~34 ~-5 ~28 gravel
setblock ~34 ~-4 ~28 cobbled_deepslate

# --- cobwebs in the still air ---
setblock ~6 ~-5 ~16 cobweb
setblock ~7 ~-4 ~17 cobweb
setblock ~7 ~-5 ~31 cobweb
setblock ~8 ~-4 ~29 cobweb
setblock ~8 ~-4 ~31 cobweb
setblock ~10 ~-5 ~16 cobweb
setblock ~15 ~-5 ~14 cobweb
setblock ~16 ~-4 ~16 cobweb
setblock ~16 ~-4 ~34 cobweb
setblock ~18 ~-4 ~16 cobweb
setblock ~20 ~-5 ~28 cobweb
setblock ~22 ~-4 ~22 cobweb

# --- sculk ---
fill ~7 ~-7 ~33 ~7 ~-7 ~33 sculk replace deepslate
fill ~8 ~-7 ~19 ~8 ~-7 ~19 sculk replace deepslate
fill ~10 ~-7 ~33 ~10 ~-7 ~33 sculk replace deepslate
fill ~13 ~-7 ~14 ~13 ~-7 ~14 sculk replace deepslate
fill ~14 ~-7 ~34 ~14 ~-7 ~34 sculk replace deepslate
fill ~19 ~-7 ~16 ~19 ~-7 ~16 sculk replace deepslate
fill ~20 ~-7 ~6 ~20 ~-7 ~6 sculk replace deepslate
fill ~20 ~-7 ~27 ~20 ~-7 ~27 sculk replace deepslate
fill ~20 ~-7 ~33 ~20 ~-7 ~33 sculk replace deepslate
fill ~21 ~-7 ~19 ~21 ~-7 ~19 sculk replace deepslate
fill ~21 ~-7 ~23 ~21 ~-7 ~23 sculk replace deepslate
fill ~21 ~-7 ~27 ~21 ~-7 ~27 sculk replace deepslate
fill ~22 ~-7 ~18 ~22 ~-7 ~18 sculk replace deepslate
fill ~22 ~-7 ~27 ~22 ~-7 ~27 sculk replace deepslate
fill ~24 ~-7 ~27 ~24 ~-7 ~27 sculk replace deepslate
fill ~26 ~-7 ~10 ~26 ~-7 ~10 sculk replace deepslate
fill ~29 ~-7 ~27 ~29 ~-7 ~27 sculk replace deepslate
fill ~33 ~-7 ~20 ~33 ~-7 ~20 sculk replace deepslate
setblock ~29 ~-6 ~27 sculk_sensor
setblock ~7 ~-6 ~33 sculk_shrieker[can_summon=true,shrieking=false]

# --- what light survives ---
setblock ~6 ~-4 ~30 soul_lantern[hanging=true]
setblock ~7 ~-4 ~30 soul_lantern[hanging=true]
setblock ~7 ~-4 ~34 soul_lantern[hanging=true]
setblock ~9 ~-4 ~19 soul_lantern[hanging=true]
setblock ~9 ~-4 ~34 soul_lantern[hanging=true]
setblock ~16 ~-4 ~14 soul_lantern[hanging=true]
setblock ~26 ~-4 ~26 soul_lantern[hanging=true]
setblock ~27 ~-4 ~27 soul_lantern[hanging=true]
setblock ~8 ~-4 ~14 iron_chain[axis=y]
setblock ~18 ~-4 ~14 iron_chain[axis=y]
setblock ~19 ~-4 ~15 iron_chain[axis=y]
setblock ~20 ~-4 ~14 iron_chain[axis=y]
setblock ~22 ~-4 ~32 iron_chain[axis=y]

# ====================== LOWER LEVEL ======================
# --- the frames that are still standing ---
fill ~10 ~-12 ~8 ~10 ~-11 ~8 cracked_deepslate_bricks
setblock ~10 ~-12 ~10 deepslate_brick_wall
setblock ~10 ~-10 ~8 cracked_deepslate_bricks
fill ~15 ~-12 ~8 ~15 ~-11 ~8 polished_deepslate
fill ~15 ~-12 ~10 ~15 ~-11 ~10 polished_deepslate
fill ~15 ~-10 ~8 ~15 ~-10 ~10 deepslate_bricks
fill ~20 ~-12 ~8 ~20 ~-11 ~8 deepslate_bricks
setblock ~20 ~-12 ~10 deepslate_brick_wall
setblock ~20 ~-10 ~8 cracked_deepslate_bricks
fill ~25 ~-12 ~8 ~25 ~-11 ~8 cracked_deepslate_bricks
fill ~25 ~-12 ~10 ~25 ~-11 ~10 cracked_deepslate_bricks
fill ~25 ~-10 ~8 ~25 ~-10 ~10 cracked_deepslate_bricks
fill ~30 ~-12 ~8 ~30 ~-11 ~8 cracked_deepslate_bricks
setblock ~30 ~-12 ~10 deepslate_brick_wall
setblock ~30 ~-10 ~8 cracked_deepslate_bricks
fill ~35 ~-12 ~8 ~35 ~-11 ~8 cracked_deepslate_bricks
fill ~35 ~-12 ~10 ~35 ~-11 ~10 cracked_deepslate_bricks
fill ~35 ~-10 ~8 ~35 ~-10 ~10 deepslate_tiles
fill ~14 ~-12 ~12 ~14 ~-11 ~12 deepslate_bricks
setblock ~16 ~-12 ~12 deepslate_brick_wall
setblock ~14 ~-10 ~12 cracked_deepslate_bricks
fill ~14 ~-12 ~17 ~14 ~-11 ~17 deepslate_bricks
fill ~16 ~-12 ~17 ~16 ~-11 ~17 deepslate_bricks
fill ~14 ~-10 ~17 ~16 ~-10 ~17 cracked_deepslate_bricks
fill ~14 ~-12 ~22 ~14 ~-11 ~22 cracked_deepslate_bricks
fill ~16 ~-12 ~22 ~16 ~-11 ~22 cracked_deepslate_bricks
fill ~14 ~-10 ~22 ~16 ~-10 ~22 deepslate_bricks
fill ~30 ~-12 ~12 ~30 ~-11 ~12 polished_deepslate
fill ~32 ~-12 ~12 ~32 ~-11 ~12 polished_deepslate
fill ~30 ~-10 ~12 ~32 ~-10 ~12 deepslate_tiles
fill ~30 ~-12 ~17 ~30 ~-11 ~17 deepslate_bricks
setblock ~32 ~-12 ~17 deepslate_brick_wall
setblock ~30 ~-10 ~17 cracked_deepslate_bricks
fill ~22 ~-12 ~14 ~22 ~-11 ~14 polished_deepslate
fill ~22 ~-12 ~16 ~22 ~-11 ~16 polished_deepslate
fill ~22 ~-10 ~14 ~22 ~-10 ~16 deepslate_bricks
fill ~9 ~-12 ~12 ~9 ~-11 ~12 cracked_deepslate_bricks
fill ~11 ~-12 ~12 ~11 ~-11 ~12 cracked_deepslate_bricks
fill ~9 ~-10 ~12 ~11 ~-10 ~12 deepslate_tiles
fill ~9 ~-12 ~17 ~9 ~-11 ~17 cracked_deepslate_bricks
setblock ~11 ~-12 ~17 deepslate_brick_wall
setblock ~9 ~-10 ~17 cracked_deepslate_bricks
fill ~34 ~-12 ~24 ~34 ~-11 ~24 polished_deepslate
fill ~36 ~-12 ~24 ~36 ~-11 ~24 polished_deepslate
fill ~34 ~-10 ~24 ~36 ~-10 ~24 cracked_deepslate_bricks

# --- blotches of masonry in the raw rock ---
fill ~9 ~-13 ~9 ~9 ~-13 ~9 deepslate_bricks replace deepslate
fill ~9 ~-13 ~14 ~9 ~-13 ~14 cracked_deepslate_tiles replace deepslate
fill ~9 ~-13 ~17 ~9 ~-13 ~17 cracked_deepslate_bricks replace deepslate
fill ~10 ~-13 ~12 ~10 ~-13 ~12 polished_deepslate replace deepslate
fill ~10 ~-13 ~13 ~10 ~-13 ~13 cracked_deepslate_bricks replace deepslate
fill ~10 ~-13 ~20 ~10 ~-13 ~20 deepslate_tiles replace deepslate
fill ~11 ~-13 ~13 ~11 ~-13 ~13 cracked_deepslate_tiles replace deepslate
fill ~11 ~-13 ~14 ~11 ~-13 ~14 cracked_deepslate_tiles replace deepslate
fill ~11 ~-13 ~17 ~11 ~-13 ~17 deepslate_bricks replace deepslate
fill ~11 ~-13 ~19 ~11 ~-13 ~19 cobbled_deepslate replace deepslate
fill ~14 ~-13 ~9 ~14 ~-13 ~9 deepslate_bricks replace deepslate
fill ~14 ~-13 ~12 ~14 ~-13 ~12 cracked_deepslate_tiles replace deepslate
fill ~14 ~-13 ~15 ~14 ~-13 ~15 deepslate_bricks replace deepslate
fill ~14 ~-13 ~16 ~14 ~-13 ~16 deepslate_bricks replace deepslate
fill ~15 ~-13 ~11 ~15 ~-13 ~11 cobbled_deepslate replace deepslate
fill ~15 ~-13 ~14 ~15 ~-13 ~14 cracked_deepslate_bricks replace deepslate
fill ~15 ~-13 ~19 ~15 ~-13 ~19 cracked_deepslate_tiles replace deepslate
fill ~15 ~-13 ~23 ~15 ~-13 ~23 cracked_deepslate_tiles replace deepslate
fill ~16 ~-13 ~9 ~16 ~-13 ~9 cobbled_deepslate replace deepslate
fill ~16 ~-13 ~12 ~16 ~-13 ~12 polished_deepslate replace deepslate
fill ~16 ~-13 ~13 ~16 ~-13 ~13 cracked_deepslate_bricks replace deepslate
fill ~16 ~-13 ~17 ~16 ~-13 ~17 deepslate_bricks replace deepslate
fill ~17 ~-13 ~10 ~17 ~-13 ~10 cobbled_deepslate replace deepslate
fill ~18 ~-13 ~8 ~18 ~-13 ~8 cracked_deepslate_bricks replace deepslate
fill ~20 ~-13 ~9 ~20 ~-13 ~9 deepslate_bricks replace deepslate
fill ~20 ~-13 ~16 ~20 ~-13 ~16 cracked_deepslate_bricks replace deepslate
fill ~21 ~-13 ~9 ~21 ~-13 ~9 cracked_deepslate_bricks replace deepslate
fill ~22 ~-13 ~9 ~22 ~-13 ~9 deepslate_bricks replace deepslate
fill ~23 ~-13 ~16 ~23 ~-13 ~16 cracked_deepslate_tiles replace deepslate
fill ~24 ~-13 ~9 ~24 ~-13 ~9 deepslate_tiles replace deepslate
fill ~24 ~-13 ~14 ~24 ~-13 ~14 deepslate_bricks replace deepslate
fill ~24 ~-13 ~15 ~24 ~-13 ~15 cracked_deepslate_bricks replace deepslate
fill ~26 ~-13 ~9 ~26 ~-13 ~9 deepslate_bricks replace deepslate
fill ~27 ~-13 ~30 ~27 ~-13 ~30 cracked_deepslate_tiles replace deepslate
fill ~29 ~-13 ~8 ~29 ~-13 ~8 cobbled_deepslate replace deepslate
fill ~29 ~-13 ~29 ~29 ~-13 ~29 cracked_deepslate_bricks replace deepslate
fill ~29 ~-13 ~30 ~29 ~-13 ~30 polished_deepslate replace deepslate
fill ~30 ~-13 ~10 ~30 ~-13 ~10 cracked_deepslate_tiles replace deepslate
fill ~30 ~-13 ~18 ~30 ~-13 ~18 deepslate_tiles replace deepslate
fill ~31 ~-13 ~15 ~31 ~-13 ~15 deepslate_bricks replace deepslate
fill ~31 ~-13 ~19 ~31 ~-13 ~19 cracked_deepslate_tiles replace deepslate
fill ~32 ~-13 ~14 ~32 ~-13 ~14 cobbled_deepslate replace deepslate
fill ~32 ~-13 ~16 ~32 ~-13 ~16 deepslate_tiles replace deepslate
fill ~32 ~-13 ~17 ~32 ~-13 ~17 cobbled_deepslate replace deepslate
fill ~32 ~-13 ~19 ~32 ~-13 ~19 cracked_deepslate_bricks replace deepslate
fill ~32 ~-13 ~22 ~32 ~-13 ~22 cracked_deepslate_tiles replace deepslate
fill ~34 ~-13 ~9 ~34 ~-13 ~9 cobbled_deepslate replace deepslate
fill ~36 ~-13 ~9 ~36 ~-13 ~9 cracked_deepslate_tiles replace deepslate
fill ~36 ~-13 ~10 ~36 ~-13 ~10 cobbled_deepslate replace deepslate
fill ~39 ~-13 ~10 ~39 ~-13 ~10 polished_deepslate replace deepslate
fill ~9 ~-12 ~12 ~9 ~-12 ~12 polished_deepslate replace deepslate
fill ~9 ~-10 ~20 ~9 ~-10 ~20 polished_deepslate replace deepslate
fill ~10 ~-12 ~15 ~10 ~-12 ~15 deepslate_tiles replace deepslate
fill ~10 ~-11 ~17 ~10 ~-11 ~17 cracked_deepslate_bricks replace deepslate
fill ~11 ~-11 ~12 ~11 ~-11 ~12 polished_deepslate replace deepslate
fill ~11 ~-12 ~16 ~11 ~-12 ~16 deepslate_tiles replace deepslate
fill ~12 ~-11 ~9 ~12 ~-11 ~9 cracked_deepslate_tiles replace deepslate
fill ~13 ~-10 ~8 ~13 ~-10 ~8 deepslate_bricks replace deepslate
fill ~14 ~-10 ~11 ~14 ~-10 ~11 polished_deepslate replace deepslate
fill ~14 ~-10 ~14 ~14 ~-10 ~14 polished_deepslate replace deepslate
fill ~14 ~-10 ~18 ~14 ~-10 ~18 cracked_deepslate_bricks replace deepslate
fill ~15 ~-10 ~8 ~15 ~-10 ~8 cobbled_deepslate replace deepslate
fill ~15 ~-12 ~9 ~15 ~-12 ~9 cobbled_deepslate replace deepslate
fill ~15 ~-11 ~16 ~15 ~-11 ~16 cracked_deepslate_tiles replace deepslate
fill ~15 ~-11 ~17 ~15 ~-11 ~17 cracked_deepslate_bricks replace deepslate
fill ~15 ~-11 ~18 ~15 ~-11 ~18 cobbled_deepslate replace deepslate
fill ~15 ~-11 ~21 ~15 ~-11 ~21 cracked_deepslate_tiles replace deepslate
fill ~16 ~-12 ~8 ~16 ~-12 ~8 polished_deepslate replace deepslate
fill ~16 ~-10 ~10 ~16 ~-10 ~10 deepslate_bricks replace deepslate
fill ~16 ~-11 ~11 ~16 ~-11 ~11 deepslate_bricks replace deepslate
fill ~16 ~-11 ~18 ~16 ~-11 ~18 deepslate_bricks replace deepslate
fill ~16 ~-11 ~21 ~16 ~-11 ~21 cracked_deepslate_tiles replace deepslate
fill ~16 ~-11 ~22 ~16 ~-11 ~22 cobbled_deepslate replace deepslate
fill ~20 ~-10 ~14 ~20 ~-10 ~14 cobbled_deepslate replace deepslate
fill ~20 ~-12 ~15 ~20 ~-12 ~15 deepslate_bricks replace deepslate
fill ~22 ~-10 ~10 ~22 ~-10 ~10 cracked_deepslate_bricks replace deepslate
fill ~23 ~-11 ~8 ~23 ~-11 ~8 deepslate_bricks replace deepslate
fill ~24 ~-11 ~8 ~24 ~-11 ~8 cobbled_deepslate replace deepslate
fill ~24 ~-10 ~16 ~24 ~-10 ~16 deepslate_bricks replace deepslate
fill ~24 ~-12 ~25 ~24 ~-12 ~25 cobbled_deepslate replace deepslate
fill ~24 ~-12 ~27 ~24 ~-12 ~27 cracked_deepslate_bricks replace deepslate
fill ~24 ~-10 ~29 ~24 ~-10 ~29 cobbled_deepslate replace deepslate
fill ~25 ~-10 ~16 ~25 ~-10 ~16 deepslate_bricks replace deepslate
fill ~25 ~-12 ~24 ~25 ~-12 ~24 cracked_deepslate_bricks replace deepslate
fill ~25 ~-11 ~25 ~25 ~-11 ~25 cobbled_deepslate replace deepslate
fill ~26 ~-11 ~30 ~26 ~-11 ~30 cracked_deepslate_bricks replace deepslate
fill ~27 ~-11 ~25 ~27 ~-11 ~25 deepslate_tiles replace deepslate
fill ~28 ~-10 ~8 ~28 ~-10 ~8 polished_deepslate replace deepslate
fill ~28 ~-10 ~24 ~28 ~-10 ~24 deepslate_tiles replace deepslate
fill ~28 ~-11 ~27 ~28 ~-11 ~27 cracked_deepslate_tiles replace deepslate
fill ~28 ~-10 ~29 ~28 ~-10 ~29 deepslate_tiles replace deepslate
fill ~30 ~-11 ~16 ~30 ~-11 ~16 deepslate_tiles replace deepslate
fill ~31 ~-10 ~18 ~31 ~-10 ~18 deepslate_tiles replace deepslate
fill ~36 ~-10 ~23 ~36 ~-10 ~23 cracked_deepslate_bricks replace deepslate
fill ~39 ~-12 ~9 ~39 ~-12 ~9 cobbled_deepslate replace deepslate

# --- fallen rock ---
setblock ~8 ~-12 ~8 deepslate_brick_slab[type=bottom]
setblock ~10 ~-12 ~10 cobbled_deepslate
setblock ~11 ~-12 ~10 cobbled_deepslate
setblock ~11 ~-12 ~15 deepslate_brick_wall
setblock ~11 ~-12 ~20 deepslate_brick_wall
setblock ~14 ~-12 ~20 deepslate_tile_slab[type=bottom]
setblock ~14 ~-12 ~21 cobbled_deepslate
setblock ~14 ~-12 ~22 deepslate_tile_slab[type=bottom]
setblock ~15 ~-12 ~13 deepslate_tile_slab[type=bottom]
setblock ~15 ~-12 ~24 deepslate_brick_wall
setblock ~16 ~-12 ~15 deepslate_brick_wall
setblock ~16 ~-12 ~23 cobbled_deepslate_wall
setblock ~19 ~-12 ~8 deepslate_tile_slab[type=bottom]
setblock ~20 ~-12 ~8 deepslate_brick_slab[type=bottom]
setblock ~21 ~-12 ~16 cobbled_deepslate
setblock ~23 ~-12 ~10 cobbled_deepslate
setblock ~23 ~-12 ~15 deepslate_brick_slab[type=bottom]
setblock ~24 ~-12 ~10 deepslate_brick_slab[type=bottom]
setblock ~24 ~-12 ~26 cobbled_deepslate_wall
setblock ~24 ~-12 ~28 deepslate_brick_slab[type=bottom]
setblock ~25 ~-12 ~27 cobbled_deepslate_wall
setblock ~27 ~-12 ~9 deepslate_tile_slab[type=bottom]
setblock ~27 ~-12 ~28 deepslate_brick_wall
setblock ~29 ~-12 ~9 deepslate_brick_wall
setblock ~29 ~-12 ~26 deepslate_brick_slab[type=bottom]
setblock ~30 ~-12 ~20 cobbled_deepslate_wall
setblock ~30 ~-12 ~25 deepslate_tile_slab[type=bottom]
setblock ~31 ~-12 ~10 deepslate_brick_slab[type=bottom]
setblock ~31 ~-12 ~14 deepslate_brick_slab[type=bottom]
setblock ~31 ~-12 ~17 gravel
setblock ~33 ~-12 ~8 deepslate_tile_slab[type=bottom]
setblock ~34 ~-12 ~24 cobbled_deepslate_wall
setblock ~34 ~-12 ~27 gravel
setblock ~34 ~-12 ~28 cobbled_deepslate_wall
setblock ~34 ~-12 ~29 gravel
setblock ~35 ~-12 ~8 deepslate_brick_slab[type=bottom]
setblock ~35 ~-12 ~10 cobbled_deepslate_wall
setblock ~35 ~-12 ~22 deepslate_brick_wall
setblock ~35 ~-12 ~23 deepslate_brick_wall
setblock ~35 ~-12 ~25 deepslate_tile_slab[type=bottom]
setblock ~36 ~-12 ~22 gravel
setblock ~36 ~-12 ~26 cobbled_deepslate
setblock ~37 ~-12 ~8 cobbled_deepslate_wall
setblock ~37 ~-12 ~9 gravel
setblock ~39 ~-12 ~8 deepslate_brick_slab[type=bottom]
setblock ~10 ~-10 ~9 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~13 ~-10 ~10 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~14 ~-10 ~10 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~22 ~-10 ~8 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~25 ~-10 ~8 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~26 ~-10 ~28 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~26 ~-10 ~29 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~29 ~-10 ~23 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~30 ~-10 ~11 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~30 ~-10 ~21 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~32 ~-10 ~8 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~34 ~-10 ~22 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~35 ~-10 ~26 pointed_dripstone[vertical_direction=down,thickness=tip]
setblock ~37 ~-10 ~10 pointed_dripstone[vertical_direction=down,thickness=tip]

# --- cave-ins ---
fill ~15 ~-12 ~10 ~15 ~-11 ~10 gravel
setblock ~15 ~-10 ~10 cobbled_deepslate
fill ~22 ~-12 ~14 ~22 ~-11 ~14 gravel
setblock ~22 ~-10 ~14 cobbled_deepslate
fill ~30 ~-12 ~17 ~30 ~-11 ~17 gravel
setblock ~30 ~-10 ~17 cobbled_deepslate
fill ~30 ~-12 ~26 ~30 ~-11 ~26 gravel
setblock ~30 ~-10 ~26 cobbled_deepslate
fill ~35 ~-12 ~9 ~35 ~-11 ~9 gravel
setblock ~35 ~-10 ~9 cobbled_deepslate
fill ~38 ~-12 ~8 ~38 ~-11 ~8 gravel
setblock ~38 ~-10 ~8 cobbled_deepslate

# --- cobwebs in the still air ---
setblock ~9 ~-10 ~19 cobweb
setblock ~11 ~-11 ~11 cobweb
setblock ~13 ~-11 ~9 cobweb
setblock ~23 ~-11 ~9 cobweb
setblock ~25 ~-10 ~26 cobweb
setblock ~26 ~-11 ~16 cobweb
setblock ~27 ~-11 ~26 cobweb
setblock ~27 ~-11 ~29 cobweb
setblock ~30 ~-11 ~12 cobweb
setblock ~30 ~-11 ~30 cobweb
setblock ~32 ~-10 ~12 cobweb
setblock ~35 ~-10 ~28 cobweb

# --- sculk ---
fill ~9 ~-13 ~16 ~9 ~-13 ~16 sculk replace deepslate
fill ~10 ~-13 ~19 ~10 ~-13 ~19 sculk replace deepslate
fill ~14 ~-13 ~19 ~14 ~-13 ~19 sculk replace deepslate
fill ~16 ~-13 ~16 ~16 ~-13 ~16 sculk replace deepslate
fill ~25 ~-13 ~10 ~25 ~-13 ~10 sculk replace deepslate
fill ~25 ~-13 ~28 ~25 ~-13 ~28 sculk replace deepslate
fill ~25 ~-13 ~29 ~25 ~-13 ~29 sculk replace deepslate
fill ~27 ~-13 ~8 ~27 ~-13 ~8 sculk replace deepslate
fill ~27 ~-13 ~10 ~27 ~-13 ~10 sculk replace deepslate
fill ~28 ~-13 ~9 ~28 ~-13 ~9 sculk replace deepslate
fill ~29 ~-13 ~25 ~29 ~-13 ~25 sculk replace deepslate
fill ~30 ~-13 ~8 ~30 ~-13 ~8 sculk replace deepslate
fill ~31 ~-13 ~9 ~31 ~-13 ~9 sculk replace deepslate
fill ~31 ~-13 ~13 ~31 ~-13 ~13 sculk replace deepslate
fill ~31 ~-13 ~21 ~31 ~-13 ~21 sculk replace deepslate
fill ~32 ~-13 ~21 ~32 ~-13 ~21 sculk replace deepslate
fill ~35 ~-13 ~30 ~35 ~-13 ~30 sculk replace deepslate
fill ~36 ~-13 ~24 ~36 ~-13 ~24 sculk replace deepslate
setblock ~32 ~-12 ~21 sculk_sensor
setblock ~31 ~-12 ~9 sculk_shrieker[can_summon=true,shrieking=false]

# --- what light survives ---
setblock ~10 ~-10 ~18 soul_lantern[hanging=true]
setblock ~15 ~-10 ~15 soul_lantern[hanging=true]
setblock ~24 ~-10 ~24 soul_lantern[hanging=true]
setblock ~26 ~-10 ~26 soul_lantern[hanging=true]
setblock ~30 ~-10 ~9 soul_lantern[hanging=true]
setblock ~32 ~-10 ~9 soul_lantern[hanging=true]
setblock ~33 ~-10 ~9 soul_lantern[hanging=true]
setblock ~36 ~-10 ~28 soul_lantern[hanging=true]
setblock ~21 ~-10 ~15 iron_chain[axis=y]
setblock ~29 ~-10 ~10 iron_chain[axis=y]
setblock ~29 ~-10 ~28 iron_chain[axis=y]
setblock ~32 ~-10 ~11 iron_chain[axis=y]
setblock ~36 ~-10 ~30 iron_chain[axis=y]

# ====================== UPPER WORKING -- THE CAMP ======================
fill ~30 ~-1 ~4 ~35 ~-1 ~9 deepslate_tiles
fill ~32 ~-1 ~6 ~33 ~-1 ~7 cracked_deepslate_tiles
setblock ~31 ~0 ~5 chest[facing=south]
data merge block ~31 ~0 ~5 {LootTable:"minecraft:chests/ancient_city"}
setblock ~32 ~0 ~5 deepslate_tile_slab[type=top]
setblock ~32 ~1 ~5 brewing_stand
setblock ~33 ~0 ~5 water_cauldron[level=2]
setblock ~34 ~0 ~8 barrel[facing=up]
fill ~30 ~0 ~9 ~30 ~1 ~9 polished_deepslate
setblock ~34 ~0 ~5 gravel
setblock ~33 ~3 ~7 soul_lantern[hanging=true]

# ====================== MIDDLE WORKING -- CONTAINMENT ======================
fill ~4 ~-7 ~28 ~10 ~-7 ~34 deepslate_tiles
fill ~6 ~-6 ~30 ~8 ~-6 ~32 reinforced_deepslate
fill ~6 ~-5 ~30 ~8 ~-4 ~30 iron_bars
fill ~6 ~-5 ~32 ~8 ~-4 ~32 iron_bars
fill ~6 ~-5 ~31 ~6 ~-4 ~31 iron_bars
fill ~8 ~-5 ~31 ~8 ~-4 ~31 iron_bars
setblock ~8 ~-5 ~32 air
setblock ~8 ~-4 ~32 air
setblock ~9 ~-6 ~33 cobbled_deepslate
setblock ~7 ~-5 ~31 amethyst_block
setblock ~7 ~-4 ~31 amethyst_cluster[facing=down]
setblock ~5 ~-6 ~33 light_gray_shulker_box[facing=up]
setblock ~5 ~-6 ~29 chest[facing=east]
data merge block ~5 ~-6 ~29 {LootTable:"minecraft:chests/ancient_city"}
setblock ~9 ~-6 ~29 lectern[facing=west,has_book=false]
setblock ~7 ~-3 ~29 soul_lantern[hanging=true]

# ====================== LOWER WORKING -- THE FUSION FLOOR ======================
fill ~24 ~-13 ~24 ~30 ~-13 ~30 deepslate_tiles
fill ~26 ~-12 ~26 ~28 ~-12 ~28 reinforced_deepslate
setblock ~27 ~-11 ~27 budding_amethyst
setblock ~27 ~-10 ~27 amethyst_cluster[facing=up]
setblock ~26 ~-11 ~26 sculk_catalyst
setblock ~28 ~-11 ~28 chiseled_deepslate
setblock ~26 ~-11 ~28 soul_fire
fill ~24 ~-13 ~24 ~26 ~-13 ~26 sculk replace deepslate_tiles
fill ~28 ~-13 ~28 ~30 ~-13 ~30 sculk replace deepslate_tiles
setblock ~25 ~-12 ~25 sculk_shrieker[can_summon=true,shrieking=false]
setblock ~29 ~-12 ~29 sculk_sensor
setblock ~24 ~-12 ~28 deepslate_tile_slab[type=top]
setblock ~24 ~-11 ~28 brewing_stand
setblock ~24 ~-12 ~29 water_cauldron[level=3]
setblock ~30 ~-12 ~24 chest[facing=west]
data merge block ~30 ~-12 ~24 {LootTable:"minecraft:chests/ancient_city"}
fill ~24 ~-12 ~24 ~24 ~-10 ~24 polished_deepslate
setblock ~27 ~-9 ~25 soul_lantern[hanging=true]

execute store result score #roll relic_rng run random value 1..8
execute if score #roll relic_rng matches 1 run setblock ~29 ~-12 ~27 ender_chest[facing=west]
execute unless score #roll relic_rng matches 1 run setblock ~29 ~-12 ~27 chiseled_deepslate

fill ~26 ~-11 ~26 ~28 ~-9 ~28 air replace #minecraft:slabs
say [hbs_relicfuse] Ancient Catacombs placed. Ladders at the shafts.
