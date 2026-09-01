"""Swap the programmatic overlay layers of one fusion for hand authored art.

gen_fusion_overlays.py builds every fusion case as a composite of the base model, a derived
structural layer and N tinted band layers. For a fusion that has its own artwork none of that is
wanted: the painting already carries its own colour, so a constant tint would only darken it.

This script rewrites the cases for a single modifier in place:

    before   [ base, shovel_struct_bone, shovel_tint_bone_0 .. _4 ]
    after    [ base, overgrown_bone_shovel ]

The tool type is read back out of the struct layer name (shovel_struct_bone -> shovel), so the
custom art is matched without a lookup table. Cases for every other modifier are untouched, as are
tools with no custom art, which keep the programmatic layers.

Run from the tools folder:

    python apply_custom_overlays.py overgrown_bone
    python apply_custom_overlays.py overgrown_bone --dry-run

Input art       custom/<modifier>/<modifier>_<tool>.png     e.g. custom/overgrown_bone/overgrown_bone_shovel.png
Written to      generated/assets/<namespace>/textures/item/overlay/<modifier>_<tool>.png
                generated/assets/<namespace>/models/item/overlay/<modifier>_<tool>.json
"""

import argparse
import json
import os
import re
import shutil

NAMESPACE = "hbs_relicfuse"

# The generator's tool names on the left, spellings that may appear in art filenames on the right.
TOOL_ALIASES = {
    "pick": ("pick", "pickaxe"),
    "spear": ("spear", "spear_gui"),
    "spear_in_hand": ("spear_in_hand", "spear_hand", "spearhand"),
}

STRUCT_PATTERN = re.compile(r"^(?P<tool>.+)_struct_(?P<kind>bone|crystal|powder)$")


def die(message):
    raise SystemExit(f"error: {message}")


def read_json(path):
    with open(path, encoding="utf-8") as handle:
        return json.load(handle)


def write_json(path, document):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as handle:
        json.dump(document, handle, indent=2)
        handle.write("\n")


def layer_model(name):
    return {"parent": "minecraft:item/generated",
            "textures": {"layer0": f"{NAMESPACE}:item/overlay/{name}"}}


def component_selects(model):
    """Every component select in a definition, flat or display_context wrapped."""
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


def tool_of(case_model):
    """shovel_struct_bone -> shovel. None when the case is not a generated composite."""
    if case_model.get("type") != "minecraft:composite":
        return None
    for entry in case_model.get("models", []):
        name = str(entry.get("model", "")).split("/")[-1]
        match = STRUCT_PATTERN.match(name)
        if match:
            return match.group("tool")
    return None


def find_art(art_dir, modifier, tool):
    """custom/<modifier>/<modifier>_<tool>.png, tolerating the alternate tool spellings."""
    for alias in TOOL_ALIASES.get(tool, (tool,)):
        candidate = os.path.join(art_dir, f"{modifier}_{alias}.png")
        if os.path.exists(candidate):
            return candidate
    return None


def rewrite_case(case, modifier, tool, overlay_name):
    """Keep the base model, drop the struct and tint layers, add the custom overlay."""
    models = case["model"]["models"]
    base = models[0]

    case["model"]["models"] = [
        base,
        {"type": "minecraft:model", "model": f"{NAMESPACE}:item/overlay/{overlay_name}"},
    ]
    return len(models) - 2


def main():
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("modifier", help="fused item name without namespace, e.g. overgrown_bone")
    parser.add_argument("--art-dir", default=None,
                        help="defaults to custom/<modifier>")
    parser.add_argument("--generated", default="generated",
                        help="root written by gen_fusion_overlays.py")
    parser.add_argument("--namespace", default=NAMESPACE)
    parser.add_argument("--dry-run", action="store_true",
                        help="report what would change without writing")
    args = parser.parse_args()

    modifier_id = f"{args.namespace}:{args.modifier}"
    art_dir = args.art_dir or os.path.join("custom", args.modifier)

    if not os.path.isdir(art_dir):
        die(f"no art directory at {art_dir}")
    if not os.path.isdir(args.generated):
        die(f"no generated tree at {args.generated}; run build_fusions.sh first")

    item_files = []
    for namespace_dir in sorted(os.listdir(os.path.join(args.generated, "assets"))):
        items = os.path.join(args.generated, "assets", namespace_dir, "items")
        if os.path.isdir(items):
            item_files += [os.path.join(items, f) for f in sorted(os.listdir(items))
                           if f.endswith(".json")]

    if not item_files:
        die(f"no item definitions under {args.generated}/assets/*/items")

    tex_dir = os.path.join(args.generated, "assets", args.namespace, "textures", "item", "overlay")
    model_dir = os.path.join(args.generated, "assets", args.namespace, "models", "item", "overlay")

    rewritten = 0
    skipped_no_art = set()
    copied = set()

    for path in item_files:
        document = read_json(path)
        touched = False

        for select in component_selects(document.get("model")):
            for case in select.get("cases", []):
                if case.get("when") != modifier_id:
                    continue

                tool = tool_of(case.get("model", {}))
                if tool is None:
                    continue

                art = find_art(art_dir, args.modifier, tool)
                if art is None:
                    skipped_no_art.add(tool)
                    continue

                overlay_name = f"{args.modifier}_{tool}"

                if overlay_name not in copied and not args.dry_run:
                    os.makedirs(tex_dir, exist_ok=True)
                    shutil.copyfile(art, os.path.join(tex_dir, f"{overlay_name}.png"))
                    write_json(os.path.join(model_dir, f"{overlay_name}.json"),
                               layer_model(overlay_name))
                copied.add(overlay_name)

                dropped = rewrite_case(case, args.modifier, tool, overlay_name)
                print(f"  {os.path.basename(path):32s} {tool:14s} "
                      f"-> {overlay_name}  (dropped {dropped} generated layers)")
                rewritten += 1
                touched = True

        if touched and not args.dry_run:
            write_json(path, document)

    print()
    print(f"{'would rewrite' if args.dry_run else 'rewrote'} {rewritten} cases "
          f"for {modifier_id} across {len(item_files)} item definitions")
    print(f"custom overlays used: {len(copied)}  {sorted(copied)}")
    if skipped_no_art:
        print(f"no art, left programmatic: {sorted(skipped_no_art)}")
    if rewritten == 0:
        print("nothing matched; check the modifier name and that build_fusions.sh has been run")


if __name__ == "__main__":
    main()
