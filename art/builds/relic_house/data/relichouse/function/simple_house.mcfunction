# simple_house.mcfunction
# Build origin = the command block that runs this. House builds UP and to +X/+Z (east/south).

# --- clear the plot ---
fill ~ ~1 ~ ~8 ~10 ~6 air

# --- foundation / floor ---
fill ~ ~ ~ ~8 ~ ~6 stone_bricks

# --- shell: walls + ceiling (hollow leaves the interior empty) ---
fill ~ ~1 ~ ~8 ~4 ~6 oak_planks hollow

# --- corner posts ---
fill ~0 ~1 ~0 ~0 ~4 ~0 stripped_oak_log
fill ~8 ~1 ~0 ~8 ~4 ~0 stripped_oak_log
fill ~0 ~1 ~6 ~0 ~4 ~6 stripped_oak_log
fill ~8 ~1 ~6 ~8 ~4 ~6 stripped_oak_log

# --- windows ---
fill ~2 ~2 ~0 ~2 ~3 ~0 glass_pane
fill ~2 ~2 ~6 ~2 ~3 ~6 glass_pane
fill ~6 ~2 ~0 ~6 ~3 ~0 glass_pane
fill ~6 ~2 ~6 ~6 ~3 ~6 glass_pane
fill ~0 ~2 ~2 ~0 ~3 ~4 glass_pane
fill ~8 ~2 ~2 ~8 ~3 ~4 glass_pane

# --- front door (north wall, z=0) ---
setblock ~4 ~1 ~0 oak_door[facing=south,half=lower,hinge=left]
setblock ~4 ~2 ~0 oak_door[facing=south,half=upper,hinge=left]
setblock ~4 ~ ~-1 stone_brick_stairs[facing=north]

# --- gabled roof (ridge runs east-west) ---
fill ~-1 ~5 ~-1 ~9 ~5 ~-1 oak_stairs[facing=south]
fill ~-1 ~5 ~7 ~9 ~5 ~7 oak_stairs[facing=north]
fill ~-1 ~6 ~0 ~9 ~6 ~0 oak_stairs[facing=south]
fill ~-1 ~6 ~6 ~9 ~6 ~6 oak_stairs[facing=north]
fill ~-1 ~7 ~1 ~9 ~7 ~1 oak_stairs[facing=south]
fill ~-1 ~7 ~5 ~9 ~7 ~5 oak_stairs[facing=north]
fill ~-1 ~8 ~2 ~9 ~8 ~2 oak_stairs[facing=south]
fill ~-1 ~8 ~4 ~9 ~8 ~4 oak_stairs[facing=north]
fill ~-1 ~9 ~3 ~9 ~9 ~3 oak_planks

# --- gable end walls (fills the triangles under the roof) ---
fill ~0 ~5 ~0 ~0 ~5 ~6 oak_planks
fill ~0 ~6 ~1 ~0 ~6 ~5 oak_planks
fill ~0 ~7 ~2 ~0 ~7 ~4 oak_planks
fill ~0 ~8 ~3 ~0 ~8 ~3 oak_planks
fill ~8 ~5 ~0 ~8 ~5 ~6 oak_planks
fill ~8 ~6 ~1 ~8 ~6 ~5 oak_planks
fill ~8 ~7 ~2 ~8 ~7 ~4 oak_planks
fill ~8 ~8 ~3 ~8 ~8 ~3 oak_planks

# --- lighting ---
setblock ~1 ~3 ~1 lantern[hanging=false]
setblock ~7 ~3 ~5 lantern[hanging=false]
setblock ~4 ~4 ~3 lantern[hanging=true]
setblock ~-1 ~2 ~-1 torch
setblock ~9 ~2 ~-1 torch

# --- furniture ---
setblock ~1 ~1 ~5 red_bed[facing=east,part=foot]
setblock ~2 ~1 ~5 red_bed[facing=east,part=head]
setblock ~7 ~1 ~1 crafting_table
setblock ~7 ~1 ~2 furnace[facing=west]
setblock ~6 ~1 ~5 chest[facing=north]
setblock ~1 ~1 ~1 bookshelf
