
"""Emit a bash script of gen_fusion_overlays.py invocations.

Discovers work from the filesystem rather than a hardcoded matrix:

  --tools DIR         vanilla tool sprites; the filename gives tier and tool type
                      (netherite_axe.png -> tier netherite, type axe, item minecraft:netherite_axe)
  --fusion DIR...     one directory per fusion kind; the folder NAME picks the kind, so
                      crystals/ -> crystal, bones/ -> bone, powders/ -> powder
  --references DIR    hand-authored fused tool art, matched to a tool type by filename
                      (only consulted for the crystal kind)

Output is grouped in major blocks per fusion kind and minor blocks per material tier.
Kinds named with --skip are still emitted, but commented out.

Usage
  gen_build_script.py --tools refs --fusion art/crystals art/bones art/powders \
      --references ../art/archeologyproject/tooltextures \
      --tool-types axe sword --skip bone --out build_fusions.sh
"""

import argparse
import os
import re

# Longest suffix first so pickaxe is not swallowed by axe.
TOOL_SUFFIXES = (
    ("_pickaxe", "pick"),
    ("_trident", "trident"),
    ("_shovel", "shovel"),
    ("_sword", "sword"),
    ("_spear", "spear"),
    ("_mace", "mace"),
    ("_hoe", "hoe"),
    ("_axe", "axe"),
)

# Vanilla items with no material tier in the name, so the whole filename is the tool type.
UNTIERED_TOOLS = {
    "trident": "trident",
    "mace": "mace",
}
UNTIERED_LABEL = "untiered"

# Tool types with no vanilla counterpart; their item ids resolve into the mod namespace.
MODDED_TOOL_TYPES = {"spear"}

# Which reference sprite belongs to which tool type, matched as substrings of the filename.
# Several spellings per type because the hand-authored names are inconsistent (archeologyhofe,
# boneHoe) and both kinds live in the same folder.
REFERENCE_HINTS = {
    "pick": ("pickax", "pick"),
    "shovel": ("shov",),
    "sword": ("swor",),
    "hoe": ("hofe", "hoe"),
    "axe": ("axe", "ax"),
    "trident": ("trident",),
    "spear": ("spear",),
    "mace": ("mace",),
}

# Reference filenames are prefixed by kind, which is what keeps boneAxe apart from archeologyaxfe.
# Crystal art is inconsistent: the original tools are archeology*, the newer weapons are crystal*.
KIND_REFERENCE_PREFIXES = {"crystal": ("archeology", "crystal"), "bone": ("bone",)}

FOLDER_KINDS = {
    "crystal": "crystal", "crystals": "crystal",
    "bone": "bone", "bones": "bone",
    "powder": "powder", "powders": "powder",
}

# Powder ids are vanilla and already snake_case; mod categories are concatenated in the art.
KIND_NAMESPACE = {"crystal": "hbs_relicfuse", "bone": "hbs_relicfuse", "powder": "minecraft"}
SPLIT_CATEGORIES = {"crystal": "crystal", "bone": "bone"}

IMAGES = (".png",)


def sprites(directory):
    return sorted(os.path.join(directory, f) for f in os.listdir(directory)
                  if f.lower().endswith(IMAGES))


def stem(path):
    return os.path.splitext(os.path.basename(path))[0]


def sh(path):
    """Quote a path for bash. Windows separators must not survive: bash reads the backslash
    as an escape, so refs\\copper_axe.png would arrive as refscopper_axe.png."""
    return '"' + str(path).replace("\\", "/") + '"'


def normalise(name):
    name = re.sub(r"[^A-Za-z0-9]+", "_", name).strip("_").lower()
    return re.sub(r"_+", "_", name)


def parse_tool(path):
    """netherite_axe.png -> (netherite, axe, minecraft:netherite_axe)

    trident.png carries no tier, and spears have no vanilla item, so both are special cased.
    The _in_hand sprites fall out on their own: they no longer end in a tool suffix."""
    name = normalise(stem(path))

    tool_type = UNTIERED_TOOLS.get(name)
    if tool_type:
        return UNTIERED_LABEL, tool_type, f"minecraft:{name}"

    for suffix, tool_type in TOOL_SUFFIXES:
        if name.endswith(suffix):
            namespace = "hbs_relicfuse" if tool_type in MODDED_TOOL_TYPES else "minecraft"
            return name[:-len(suffix)], tool_type, f"{namespace}:{name}"
    return None, None, None


def modifier_id(kind, path):
    """Art filenames are concatenated (blessedcrystal); ids are not (blessed_crystal)."""
    name = normalise(stem(path))
    category = SPLIT_CATEGORIES.get(kind)
    if category and name.endswith(category) and not name.endswith("_" + category):
        name = f"{name[:-len(category)]}_{category}"
    return f"{KIND_NAMESPACE[kind]}:{normalise(name)}"


def dedupe(kind, mods):
    """Keep the first sprite per modifier id; return (sprites, ids, dropped)."""
    keep, ids, dropped, taken = [], [], [], {}
    for sprite in mods:
        mod_id = modifier_id(kind, sprite)
        if mod_id in taken:
            dropped.append((sprite, mod_id))
            continue
        taken[mod_id] = sprite
        keep.append(sprite)
        ids.append(mod_id)
    return keep, ids, dropped


def find_reference(references, kind, tool_type):
    if not references:
        return None
    prefixes = KIND_REFERENCE_PREFIXES.get(kind, ())
    hints = REFERENCE_HINTS.get(tool_type, ())

    candidates = []
    for path in sprites(references):
        name = normalise(stem(path))
        if prefixes and not any(name.startswith(p) for p in prefixes):
            continue
        if not any(h in name for h in hints):
            continue
        if tool_type == "axe" and "pickax" in name:
            continue
        candidates.append(path)
    return candidates[0] if candidates else None


def command(kind, tier, tool_type, tool_sprite, item, mods, ids, reference, shape, args):
    item_namespace, item_path = item.split(":", 1)
    parts = [
        sh(args.script),
        f"--kind {kind}",
        f"--tool {tool_type}",
        f"--base {sh(tool_sprite)}",
    ]
    if kind in ("crystal", "bone") and reference:
        parts.append(f"--reference {sh(reference)}")
    elif kind == "bone":
        parts.append(f"--shape {sh(shape)}")
    parts.append("--modifiers " + " ".join(sh(m) for m in mods))
    parts.append("--modifier-ids " + " ".join(ids))
    parts += [
        f"--base-model {item_namespace}:item/{item_path}",
        f"--item {item}",
        f"--component {args.component}",
        f"--namespace {args.namespace}",
        f"--bands {args.bands}",
        f"--out {sh(args.out_dir)}",
        "--merge",
    ]
    return " \\\n    ".join([f'"$PYTHON" {parts[0]}'] + parts[1:])


# On Windows the WindowsApps stub named python.exe shadows a real install and only prints an
# advert for the Microsoft Store, so the interpreter is probed rather than assumed.
PYTHON_PREAMBLE = """\
PYTHON="${{PYTHON:-}}"
if [ -z "$PYTHON" ]; then
  for candidate in {candidates}; do
    if command -v "$candidate" >/dev/null 2>&1 \\
       && "$candidate" -c 'import PIL' >/dev/null 2>&1; then
      PYTHON="$candidate"
      break
    fi
  done
fi
if [ -z "$PYTHON" ]; then
  echo "No python with Pillow found. Set PYTHON=/path/to/python.exe and re-run." >&2
  exit 1
fi
echo "using $PYTHON ($("$PYTHON" --version 2>&1))"
"""


def generate(args):
    tools = []
    for path in sprites(args.tools):
        tier, tool_type, item = parse_tool(path)
        if tier is None:
            continue
        if args.tool_types and tool_type not in args.tool_types:
            continue
        tools.append((tier, tool_type, path, item))

    if not tools:
        raise SystemExit(f"no matching tool sprites in {args.tools}")

    lines = [
        "#!/usr/bin/env bash",
        "# Generated by gen_build_script.py -- regenerate rather than hand-editing.",
        "set -euo pipefail",
        'cd "$(dirname "$0")"',
        "",
        PYTHON_PREAMBLE.format(candidates=" ".join(args.python)),
    ]

    for fusion_dir in args.fusion:
        folder = os.path.basename(os.path.normpath(fusion_dir)).lower()
        kind = FOLDER_KINDS.get(folder)
        if kind is None:
            raise SystemExit(f"cannot infer kind from folder '{folder}'; "
                             f"expected one of {sorted(set(FOLDER_KINDS))}")

        mods = sprites(fusion_dir)
        if not mods:
            print(f"  skipping {fusion_dir}: no sprites")
            continue

        # Two art files can normalise to one id (Demoniccrystal..png and demoniccrystal.png).
        # Minecraft rejects an item definition outright on a duplicate case, so drop them here.
        mods, ids, seen = dedupe(kind, mods)
        for dropped, mod_id in seen:
            print(f"  WARNING duplicate id {mod_id}: ignoring {dropped}")
        shape = args.shape or mods[0]
        skipped = kind in args.skip

        lines.append("#" * 78)
        title = f"# {kind.upper()} FUSIONS  ({len(mods)} modifiers)"
        lines.append(title + ("   -- PENDING, COMMENTED OUT" if skipped else ""))
        if kind == "bone":
            lines.append(f"# reference art per tool; falls back to hilt cut from {shape}")
        lines.append("#" * 78)
        lines.append("")

        for tier in sorted({t[0] for t in tools}):
            group = [t for t in tools if t[0] == tier]
            lines.append(f"# --- {tier} " + "-" * (60 - len(tier)))
            for _, tool_type, sprite, item in sorted(group, key=lambda t: t[1]):
                reference = find_reference(args.references, kind, tool_type)
                if kind == "crystal" and reference is None:
                    lines.append(f"# no reference art for {tool_type}; skipped {item}")
                    continue
                text = command(kind, tier, tool_type, sprite, item, mods, ids,
                               reference, shape, args)
                if skipped:
                    text = "\n".join("# " + ln for ln in text.splitlines())
                lines.append(text)
                lines.append("")
        lines.append("")

    script = "\n".join(lines)
    with open(args.out, "w", encoding="utf-8", newline="\n") as handle:
        handle.write(script)
    os.chmod(args.out, 0o755)

    active = sum(1 for ln in script.splitlines() if ln.startswith('"$PYTHON"'))
    print(f"wrote {args.out}: {active} active commands, {len(tools)} tools")


def main():
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--tools", required=True, help="directory of vanilla tool sprites")
    parser.add_argument("--fusion", nargs="+", required=True,
                        help="fusion directories; folder name selects the kind")
    parser.add_argument("--references", help="directory of hand-authored fused tool art")
    parser.add_argument("--tool-types", nargs="*", default=[],
                        help="restrict to these tool types, e.g. axe sword")
    parser.add_argument("--skip", nargs="*", default=[],
                        help="kinds to emit commented out, e.g. bone")
    parser.add_argument("--shape", help="override the bone hilt source sprite")
    parser.add_argument("--script", default="gen_fusion_overlays.py")
    parser.add_argument("--python", nargs="*",
                        default=["py", "python3", "python",
                                 "/c/Python312/python.exe", "/c/Python313/python.exe"],
                        help="interpreter candidates probed in order; PYTHON env var wins")
    parser.add_argument("--component", default="hbs_relicfuse:fusion")
    parser.add_argument("--namespace", default="hbs_relicfuse")
    parser.add_argument("--bands", type=int, default=5)
    parser.add_argument("--out-dir", default="generated", help="passed through as --out")
    parser.add_argument("--out", default="build_fusions.sh")
    generate(parser.parse_args())


if __name__ == "__main__":
    main()
