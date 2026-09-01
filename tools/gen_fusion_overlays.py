"""Generate fusion overlay textures, layer models and the item model definition.

The fused look is built as a composite over the base tool's own model, so nothing about the
base sprite is copied or redistributed. Three overlay structures are supported:

  crystal  gild extracted from hand-authored reference art, split into luminance bands
  bone     hand-authored bone art diffed against the base tool (or, with --shape, a
           bone sprite grafted onto the handle)
  powder   one-pixel dilation of the tool silhouette used as a faint glow ring

Each structure emits an untinted structural layer plus N greyscale band layers. Band layers
are neutral grey so a multiply tint fully controls hue; per-modifier colour ramps are derived
from each modifier's own sprite, so adding a modifier means adding a png, not editing code.

Usage
  gen_fusion_overlays.py --kind crystal --tool axe \
      --base vanilla/netherite_axe.png \
      --reference art/tooltextures/archeologyaxfe.png \
      --modifiers art/itemtextures/*crystal*.png \
      --base-model minecraft:item/netherite_axe --item minecraft:netherite_axe

  gen_fusion_overlays.py --kind bone --tool axe \
      --base vanilla/netherite_axe.png \
      --reference art/tooltextures/boneAxe.png \
      --modifiers art/itemtextures/*bone*.png \
      --base-model minecraft:item/netherite_axe --item minecraft:netherite_axe

  gen_fusion_overlays.py --kind powder --tool axe \
      --base vanilla/netherite_axe.png \
      --modifiers art/powders/*.png \
      --base-model minecraft:item/netherite_axe --item minecraft:netherite_axe
"""

import argparse
import colorsys
import json
import os
import re
from PIL import Image

KIND_CRYSTAL = "crystal"
KIND_BONE = "bone"
KIND_POWDER = "powder"
KINDS = (KIND_CRYSTAL, KIND_BONE, KIND_POWDER)

SAT_MIN = 0.15
GILD_HUE_LO, GILD_HUE_HI = 15.0 / 360.0, 65.0 / 360.0
GLOW_LEVEL = 200

TRANSPARENT = (0, 0, 0, 0)


# ---------------------------------------------------------------- pixel helpers

def load(path):
    return Image.open(path).convert("RGBA")


def opaque(image):
    w, h = image.size
    return {(x, y) for y in range(h) for x in range(w) if image.getpixel((x, y))[3] > 0}


def luma(px):
    return 0.2126 * px[0] + 0.7152 * px[1] + 0.0722 * px[2]


def is_chromatic(px):
    if px[3] == 0:
        return False
    _, s, _ = colorsys.rgb_to_hsv(px[0] / 255, px[1] / 255, px[2] / 255)
    return s >= SAT_MIN


def is_gild(px):
    if not is_chromatic(px):
        return False
    h, _, _ = colorsys.rgb_to_hsv(px[0] / 255, px[1] / 255, px[2] / 255)
    return GILD_HUE_LO <= h <= GILD_HUE_HI


def blank(size):
    return Image.new("RGBA", size, TRANSPARENT)


def dilate(cells, size):
    w, h = size
    grown = set()
    for x, y in cells:
        for dx in (-1, 0, 1):
            for dy in (-1, 0, 1):
                nx, ny = x + dx, y + dy
                if 0 <= nx < w and 0 <= ny < h:
                    grown.add((nx, ny))
    return grown


def centroid(cells):
    if not cells:
        return 0.0, 0.0
    return (sum(c[0] for c in cells) / len(cells),
            sum(c[1] for c in cells) / len(cells))


# ---------------------------------------------------------------- band splitting

def band_layers(pixels, size, bands):
    """Split pixels into luminance bands, emitting neutral grey masks plus anchor colours."""
    if not pixels:
        return [], []

    ordered = sorted(pixels, key=lambda e: luma(e[2]))
    per = len(ordered) / bands
    layers, anchors = [], []

    for b in range(bands):
        chunk = ordered[int(b * per):int((b + 1) * per)]
        if not chunk:
            chunk = [ordered[-1]]
        anchor = max(chunk, key=lambda e: luma(e[2]))[2]
        layer = blank(size)
        for x, y, px in chunk:
            level = max(0, min(255, round(luma(px) / max(luma(anchor), 1.0) * 255)))
            layer.putpixel((x, y), (level, level, level, 255))
        layers.append(layer)
        anchors.append(anchor)

    return layers, anchors


def derive_ramp(sprite_path, bands):
    """Per-modifier colour ramp, sampled from the modifier's own chromatic palette."""
    image = load(sprite_path)
    w, h = image.size
    pixels = [(x, y, image.getpixel((x, y)))
              for y in range(h) for x in range(w)
              if is_chromatic(image.getpixel((x, y)))]
    if not pixels:
        pixels = [(x, y, image.getpixel((x, y)))
                  for y in range(h) for x in range(w)
                  if image.getpixel((x, y))[3] > 0]
    _, anchors = band_layers(pixels, (w, h), bands)
    return [rgb_int(a) for a in anchors]


def rgb_int(px):
    return (px[0] << 16) | (px[1] << 8) | px[2]


# ---------------------------------------------------------------- structures

def build_crystal(base, reference, bands):
    """Gild pixels become tinted bands; added non-gild pixels become the structural layer."""
    size = reference.size
    base_cells = opaque(base)
    gild, struct = [], []

    for y in range(size[1]):
        for x in range(size[0]):
            px = reference.getpixel((x, y))
            if px[3] == 0:
                continue
            if is_gild(px):
                gild.append((x, y, px))
            elif (x, y) not in base_cells:
                struct.append((x, y, px))

    struct_layer = blank(size)
    for x, y, px in struct:
        struct_layer.putpixel((x, y), px)

    layers, anchors = band_layers(gild, size, bands)
    return struct_layer, layers, anchors


OUTLINE_VALUE = 0.25


# Regions of the sprite the bone overlay must leave alone so the tool's own material still reads.
# Inclusive (x0, y0, x1, y1) in 16x16 sprite space; the reference art covers these areas but the
# base material is what should show there.
REVEAL_REGIONS = {
    # Mid blade, immediately above the crossguard at y8.
    "sword": [(7, 3, 12, 7)],
    # The whole shovel head, which sits top right of the handle.
    "shovel": [(8, 2, 14, 7)],
}


def is_revealed(tool, x, y):
    for x0, y0, x1, y1 in REVEAL_REGIONS.get(tool, ()):
        if x0 <= x <= x1 and y0 <= y <= y1:
            return True
    return False


def build_bone_from_reference(base, reference, bands, tool=None):
    """Diff hand-authored bone art against the base tool.

    Unlike the gild, bone material is desaturated (sat 0.05-0.19), so it cannot be picked out by
    hue. Instead every pixel that differs from the base sprite is treated as bone: near-black
    pixels become the untinted outline, the rest split into neutral grey luminance bands.
    """
    size = reference.size
    outline, material = [], []

    for y in range(size[1]):
        for x in range(size[0]):
            px = reference.getpixel((x, y))
            if px[3] == 0:
                continue
            # Leave the material showing: emit nothing here so the base model is what renders.
            if is_revealed(tool, x, y):
                continue
            under = base.getpixel((x, y))
            if under[3] != 0 and under[:3] == px[:3]:
                continue

            _, _, value = colorsys.rgb_to_hsv(px[0] / 255, px[1] / 255, px[2] / 255)
            (outline if value < OUTLINE_VALUE else material).append((x, y, px))

    struct = blank(size)
    for x, y, px in outline:
        struct.putpixel((x, y), px)

    layers, anchors = band_layers(material, size, bands)
    return struct, layers, anchors


def build_bone(base, shape, bands):
    """The bone's lower half is wrapped along the tool handle rather than stamped onto it.

    Pasting the crop directly leaves a blob, because a bone's lower half is a wide chunk while a
    handle is a thin diagonal. Instead the handle is walked along its own axis and each step is
    coloured from the corresponding row of the bone crop, so the hilt follows any tool silhouette.
    """
    size = base.size
    cells = opaque(shape)
    if not cells:
        raise SystemExit("bone shape sprite has no opaque pixels")

    min_y, max_y = min(c[1] for c in cells), max(c[1] for c in cells)
    midpoint = min_y + (max_y - min_y) // 2
    lower = [(x, y) for x, y in cells if y >= midpoint]
    if not lower:
        raise SystemExit("bone shape sprite has no lower half")

    crop_lo, crop_hi = min(p[1] for p in lower), max(p[1] for p in lower)
    rows = {}
    for x, y in lower:
        rows.setdefault(y, []).append(shape.getpixel((x, y)))

    def bone_row(fraction):
        target = crop_lo + fraction * (crop_hi - crop_lo)
        row = min(rows.keys(), key=lambda y: abs(y - target))
        return max(rows[row], key=luma)

    base_cells = opaque(base)
    handle = {(x, y) for x, y in base_cells if x < size[0] / 2 and y >= size[1] / 2}
    if not handle:
        handle = base_cells
    hilt = dilate(handle, size) & (dilate(base_cells, size))

    # Grip runs bottom-left to top-right; project onto that axis to get a 0..1 walk.
    span_lo = min(x - y for x, y in hilt)
    span_hi = max(x - y for x, y in hilt)
    span = max(span_hi - span_lo, 1)

    placed = [(x, y, bone_row((x - y - span_lo) / span)) for x, y in hilt]

    struct = blank(size)
    for x, y, px in placed:
        if not is_chromatic(px):
            struct.putpixel((x, y), px)

    tinted = [p for p in placed if is_chromatic(p[2])]
    if not tinted:
        tinted = placed
    layers, anchors = band_layers(tinted, size, bands)
    return struct, layers, anchors


def build_powder(base, bands):
    """A one-pixel ring around the tool silhouette, tinted to the powder colour."""
    size = base.size
    cells = opaque(base)
    ring = dilate(cells, size) - cells

    struct = blank(size)
    glow = blank(size)
    for x, y in ring:
        glow.putpixel((x, y), (GLOW_LEVEL, GLOW_LEVEL, GLOW_LEVEL, 255))

    # A glow reads as one flat colour, so only the first band carries pixels.
    layers = [glow] + [blank(size) for _ in range(bands - 1)]
    anchors = [(255, 255, 255, 255)] * bands
    return struct, layers, anchors


# ---------------------------------------------------------------- json emission

def layer_model(namespace, name, parent="minecraft:item/generated"):
    """The parent carries the display transforms. An overlay drawn over a held model must inherit
    the same parent or it renders flat while the base is rotated into the hand pose."""
    return {
        "parent": parent,
        "textures": {"layer0": f"{namespace}:item/overlay/{name}"},
    }


def base_model(texture):
    return {
        "parent": "minecraft:item/generated",
        "textures": {"layer0": texture},
    }


def composite(namespace, base_model, struct_name, band_names, ramp):
    models = [
        {"type": "minecraft:model", "model": base_model},
        {"type": "minecraft:model", "model": f"{namespace}:item/overlay/{struct_name}"},
    ]
    for name, color in zip(band_names, ramp):
        models.append({
            "type": "minecraft:model",
            "model": f"{namespace}:item/overlay/{name}",
            "tints": [{"type": "minecraft:constant", "value": color}],
        })
    return {"type": "minecraft:composite", "models": models}


def item_definition(namespace, component, base_model, cases):
    return {
        "model": {
            "type": "minecraft:select",
            "property": "minecraft:component",
            "component": component,
            "cases": cases,
            "fallback": {"type": "minecraft:model", "model": base_model},
        }
    }


def write_json(path, doc):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as handle:
        json.dump(doc, handle, indent=2)
        handle.write("\n")


def merge_cases(path, doc):
    """Fold new cases into an existing definition so several kinds can share one tool file."""
    existing = read_json(path)
    if existing is None:
        return doc

    kept = [c for c in existing.get("model", {}).get("cases", [])
            if c.get("when") not in {n["when"] for n in doc["model"]["cases"]}]
    doc["model"]["cases"] = kept + doc["model"]["cases"]
    return doc


def read_json(path):
    if not os.path.exists(path):
        return None
    with open(path, encoding="utf-8") as handle:
        return json.load(handle)


# Vanilla spears select a flat sprite for these contexts and the 3d held model for everything else.
GUI_CONTEXTS = ["gui", "ground", "fixed", "on_shelf"]


def component_selects(model):
    """Every component select in a definition, whether or not it is display_context wrapped."""
    if not isinstance(model, dict):
        return []
    if model.get("property") == "minecraft:component":
        return [model]
    if model.get("property") != "minecraft:display_context":
        return []
    found = [case.get("model") for case in model.get("cases") or []]
    found.append(model.get("fallback"))
    return [m for m in found if isinstance(m, dict)
            and m.get("property") == "minecraft:component"]


def display_select(gui_model, hand_model):
    return {
        "type": "minecraft:select",
        "property": "minecraft:display_context",
        "cases": [{"when": list(GUI_CONTEXTS), "model": gui_model}],
        "fallback": hand_model,
    }


def display_branch(document, context):
    """The component select already stored for one display branch, or None."""
    if not document:
        return None
    model = document.get("model")
    if not isinstance(model, dict):
        return None
    if model.get("property") != "minecraft:display_context":
        return model if context == "gui" else None
    if context == "hand":
        return model.get("fallback")
    cases = model.get("cases") or []
    return cases[0].get("model") if cases else None


def merge_display_cases(path, model, context):
    """Merge into one branch of a display_context select, leaving the other branch alone."""
    existing = read_json(path)
    previous = display_branch(existing, context)

    if previous and previous.get("property") == "minecraft:component":
        new_whens = {c["when"] for c in model["cases"]}
        kept = [c for c in previous.get("cases", []) if c.get("when") not in new_whens]
        model["cases"] = kept + model["cases"]

    other = "hand" if context == "gui" else "gui"
    sibling = display_branch(existing, other)

    gui_model = model if context == "gui" else sibling
    hand_model = model if context == "hand" else sibling

    document = dict(existing) if existing else {}
    document["model"] = display_select(gui_model, hand_model)
    return document


def modifier_id(namespace, sprite_path):
    stem = os.path.splitext(os.path.basename(sprite_path))[0]
    stem = re.sub(r"[^A-Za-z0-9]+", "_", stem).strip("_").lower()
    stem = re.sub(r"_+", "_", stem)
    return f"{namespace}:{stem}"


# ---------------------------------------------------------------- driver

def generate(args):
    base = load(args.base)
    namespace = args.namespace
    bands = args.bands

    if args.kind == KIND_CRYSTAL:
        if not args.reference:
            raise SystemExit("--reference is required for --kind crystal")
        struct, layers, anchors = build_crystal(base, load(args.reference), bands)
    elif args.kind == KIND_BONE:
        # Prefer hand-authored reference art; fall back to grafting a bone sprite onto the handle.
        if args.reference:
            struct, layers, anchors = build_bone_from_reference(base, load(args.reference), bands, args.tool)
        elif args.shape:
            struct, layers, anchors = build_bone(base, load(args.shape), bands)
        else:
            raise SystemExit("--reference (preferred) or --shape is required for --kind bone")
    else:
        struct, layers, anchors = build_powder(base, bands)

    tex_dir = os.path.join(args.out, "assets", namespace, "textures", "item", "overlay")
    model_dir = os.path.join(args.out, "assets", namespace, "models", "item", "overlay")
    os.makedirs(tex_dir, exist_ok=True)

    # Vanilla tools already ship a base model; a modded one does not, and the composite silently
    # renders as the bare item when its first layer is missing.
    if args.emit_base:
        base_ns, base_path = args.base_model.split(":", 1)
        if base_path.startswith("item/"):
            base_path = base_path[len("item/"):]
        base_model_dir = os.path.join(args.out, "assets", base_ns, "models", "item")
        base_tex_dir = os.path.join(args.out, "assets", base_ns, "textures", "item")
        os.makedirs(base_model_dir, exist_ok=True)
        os.makedirs(base_tex_dir, exist_ok=True)
        write_json(os.path.join(base_model_dir, f"{base_path}.json"),
                   base_model(f"{base_ns}:item/{base_path}"))
        base.save(os.path.join(base_tex_dir, f"{base_path}.png"))
        print(f"  base model  {base_ns}:item/{base_path}")

    struct_name = f"{args.tool}_struct_{args.kind}"
    struct.save(os.path.join(tex_dir, f"{struct_name}.png"))
    write_json(os.path.join(model_dir, f"{struct_name}.json"),
               layer_model(namespace, struct_name, args.layer_parent))

    band_names = []
    for index, layer in enumerate(layers):
        name = f"{args.tool}_tint_{args.kind}_{index}"
        band_names.append(name)
        layer.save(os.path.join(tex_dir, f"{name}.png"))
        write_json(os.path.join(model_dir, f"{name}.json"),
                   layer_model(namespace, name, args.layer_parent))

    if args.modifier_ids and len(args.modifier_ids) != len(args.modifiers):
        raise SystemExit("--modifier-ids must have one entry per --modifiers sprite")

    cases = []
    for index, sprite in enumerate(args.modifiers):
        mod_id = args.modifier_ids[index] if args.modifier_ids \
            else modifier_id(args.modifier_namespace, sprite)
        ramp = derive_ramp(sprite, bands)
        cases.append({
            "when": mod_id,
            "model": composite(namespace, args.base_model, struct_name, band_names, ramp),
        })
        print(f"  {mod_id:44s} {' '.join(f'#{c:06X}' for c in ramp)}")

    item_ns, item_path = args.item.split(":", 1)
    definition_path = os.path.join(args.out, "assets", item_ns, "items", f"{item_path}.json")
    document = item_definition(namespace, args.component, args.base_model, cases)

    if args.display_context:
        document = merge_display_cases(definition_path, document["model"], args.display_context)
        if args.swap_animation_scale is not None:
            document["swap_animation_scale"] = args.swap_animation_scale
    elif args.merge:
        document = merge_cases(definition_path, document)

    # Minecraft rejects the entire definition on a repeated case, taking the unfused model with it.
    for branch in component_selects(document["model"]):
        whens = [c["when"] for c in branch.get("cases", [])]
        repeated = sorted({w for w in whens if whens.count(w) > 1})
        if repeated:
            raise SystemExit(f"duplicate case conditions in {definition_path}: {', '.join(repeated)}")

    write_json(definition_path, document)

    default_ramp = [rgb_int(a) for a in anchors]
    print(f"\n{args.kind}/{args.tool}: struct + {len(band_names)} band layers, {len(cases)} cases")
    print(f"  reference ramp {' '.join(f'#{c:06X}' for c in default_ramp)}")
    print(f"  textures  {tex_dir}")
    print(f"  models    {model_dir}")
    print(f"  item      {definition_path}")


def main():
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--kind", required=True, choices=KINDS)
    parser.add_argument("--tool", required=True, help="tool archetype, e.g. axe")
    parser.add_argument("--base", required=True, help="vanilla tool sprite the overlay sits on")
    parser.add_argument("--reference", help="hand-authored fused art (crystal)")
    parser.add_argument("--shape", help="sprite the structure is cut from (bone)")
    parser.add_argument("--modifiers", nargs="*", default=[],
                        help="modifier sprites; one select case each, ramp derived from its palette")
    parser.add_argument("--modifier-ids", nargs="*", default=[],
                        help="explicit ids parallel to --modifiers; overrides filename guessing")
    parser.add_argument("--modifier-namespace", default="hbs_relicfuse",
                        help="namespace used when ids are guessed from filenames")
    parser.add_argument("--merge", action="store_true",
                        help="fold cases into an existing item definition instead of replacing it")
    parser.add_argument("--base-model", required=True, help="e.g. minecraft:item/netherite_axe")
    parser.add_argument("--item", required=True, help="e.g. minecraft:netherite_axe")
    parser.add_argument("--component", default="hbs_relicfuse:fusion")
    parser.add_argument("--namespace", default="hbs_relicfuse")
    parser.add_argument("--bands", type=int, default=5)
    parser.add_argument("--layer-parent", default="minecraft:item/generated",
                        help="parent for the overlay layer models; must match the base model's "
                             "parent so the overlays inherit the same display transforms")
    parser.add_argument("--display-context", choices=("gui", "hand"),
                        help="write into one branch of a display_context select instead of "
                             "replacing the definition; spears need gui and hand")
    parser.add_argument("--swap-animation-scale", type=float,
                        help="preserved on the item definition, e.g. 1.95 for spears")
    parser.add_argument("--emit-base", action="store_true",
                        help="also write the base model and sprite; required for modded tools "
                             "whose base model has no vanilla counterpart")
    parser.add_argument("--out", default="tools/generated",
                        help="output root; mirrors the resources tree")
    generate(parser.parse_args())


if __name__ == "__main__":
    main()
