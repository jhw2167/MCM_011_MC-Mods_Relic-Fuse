"""Derive composite overlay layers from a hand-authored fused tool sprite.

Given the fused reference art and the vanilla base sprite it was drawn over, emits:
  <name>_struct.png  untinted pixels that extend the silhouette but are not gild
  <name>_0..N.png    gild pixels split into N luminance bands, neutral grey

Each gild band ships an anchor colour; mask * anchor reproduces the reference
under a multiply tint, so the anchors are the default "gold" tint preset.
"""
import colorsys, os, sys
from PIL import Image

BANDS = 5
SAT_MIN = 0.15
HUE_LO, HUE_HI = 15.0 / 360.0, 65.0 / 360.0


def is_gild(px):
    r, g, b, a = px
    if a == 0:
        return False
    h, s, _ = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
    return s >= SAT_MIN and HUE_LO <= h <= HUE_HI


def luma(px):
    return 0.2126 * px[0] + 0.7152 * px[1] + 0.0722 * px[2]


def generate(ref_path, base_path, out_dir, name, bands=BANDS):
    ref = Image.open(ref_path).convert("RGBA")
    base = Image.open(base_path).convert("RGBA")
    w, h = ref.size
    os.makedirs(out_dir, exist_ok=True)

    gild, struct = [], []
    for y in range(h):
        for x in range(w):
            px = ref.getpixel((x, y))
            if px[3] == 0:
                continue
            if is_gild(px):
                gild.append((x, y, px))
            elif base.getpixel((x, y))[3] == 0:
                struct.append((x, y, px))

    layer = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    for x, y, px in struct:
        layer.putpixel((x, y), px)
    layer.save(os.path.join(out_dir, f"{name}_struct.png"))

    gild.sort(key=lambda e: luma(e[2]))
    per = len(gild) / bands
    anchors, err, n = [], 0.0, 0
    for b in range(bands):
        chunk = gild[int(b * per):int((b + 1) * per)]
        anchor = max(chunk, key=lambda e: luma(e[2]))[2]
        anchors.append(anchor)
        layer = Image.new("RGBA", (w, h), (0, 0, 0, 0))
        for x, y, px in chunk:
            g = max(0, min(255, round(luma(px) / max(luma(anchor), 1.0) * 255)))
            layer.putpixel((x, y), (g, g, g, 255))
            for i in range(3):
                err += abs(px[i] - (g / 255) * anchor[i])
                n += 1
        layer.save(os.path.join(out_dir, f"{name}_{b}.png"))

    print(f"{name}: {len(gild)} gild px in {bands} bands, {len(struct)} structural px")
    print(f"  mean channel error {err / max(n, 1):.2f}/255")
    for i, a in enumerate(anchors):
        print(f"  index {i}: #{a[0]:02X}{a[1]:02X}{a[2]:02X}  decimal {(a[0] << 16) | (a[1] << 8) | a[2]}")
    return anchors


if __name__ == "__main__":
    generate(sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4])
