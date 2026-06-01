#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter


ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "app" / "src" / "main" / "res"

BG_TOP = (12, 54, 67)
BG_BOTTOM = (13, 104, 91)
INK = (248, 252, 249)
MIST = (196, 229, 219)
ORANGE = (244, 154, 28)
SHADOW = (5, 20, 23, 82)
DEEP = (9, 43, 52)
TEAL = (16, 86, 82)


def lerp(a: int, b: int, t: float) -> int:
    return int(a + (b - a) * t)


def scaled(size: int, value: float) -> int:
    return round(size * value / 512)


def line_width(size: int, value: float) -> int:
    return max(1, scaled(size, value))


def draw_background(draw: ImageDraw.ImageDraw, size: int) -> None:
    for y in range(size):
        t = y / max(1, size - 1)
        color = tuple(lerp(BG_TOP[i], BG_BOTTOM[i], t) for i in range(3))
        draw.line([(0, y), (size, y)], fill=color)

    contour = (255, 255, 255, 13)
    for offset in (-185, -90, 8, 112):
        box = [
            scaled(size, -125 + offset),
            scaled(size, 66),
            scaled(size, 372 + offset),
            scaled(size, 562),
        ]
        draw.arc(box, start=204, end=338, fill=contour, width=line_width(size, 5))

    for offset in (-96, 42, 180):
        box = [
            scaled(size, 140 + offset),
            scaled(size, -118),
            scaled(size, 700 + offset),
            scaled(size, 442),
        ]
        draw.arc(box, start=112, end=248, fill=contour, width=line_width(size, 4))


def draw_oracle_mark(draw: ImageDraw.ImageDraw, size: int, include_shadow: bool) -> None:
    def p(x: float, y: float) -> tuple[int, int]:
        return (scaled(size, x), scaled(size, y))

    if include_shadow:
        draw.rounded_rectangle(
            [
                scaled(size, 154),
                scaled(size, 256),
                scaled(size, 357),
                scaled(size, 459),
            ],
            radius=scaled(size, 23),
            fill=SHADOW,
        )

    # Oracle eye/crystal-ball transmitter. The eye shape carries the "oracle"
    # cue while the waves keep the Radio-O purpose visible at small sizes.
    eye_box = [
        scaled(size, 188),
        scaled(size, 104),
        scaled(size, 324),
        scaled(size, 200),
    ]
    draw.ellipse(eye_box, fill=INK)
    draw.polygon([p(185, 152), p(256, 100), p(327, 152), p(256, 204)], fill=INK)
    draw.ellipse(
        [
            scaled(size, 226),
            scaled(size, 122),
            scaled(size, 286),
            scaled(size, 182),
        ],
        fill=TEAL,
    )
    draw.ellipse(
        [
            scaled(size, 241),
            scaled(size, 137),
            scaled(size, 271),
            scaled(size, 167),
        ],
        fill=DEEP,
    )
    draw.ellipse(
        [
            scaled(size, 268),
            scaled(size, 122),
            scaled(size, 281),
            scaled(size, 135),
        ],
        fill=ORANGE,
    )
    draw.line([p(256, 196), p(256, 286)], fill=INK, width=line_width(size, 20))

    for inner, outer in ((64, 99), (106, 145)):
        draw.arc(
            [
                scaled(size, 256 - outer),
                scaled(size, 150 - outer),
                scaled(size, 256 + outer),
                scaled(size, 150 + outer),
            ],
            start=132,
            end=214,
            fill=MIST,
            width=line_width(size, 15),
        )
        draw.arc(
            [
                scaled(size, 256 - outer),
                scaled(size, 150 - outer),
                scaled(size, 256 + outer),
                scaled(size, 150 + outer),
            ],
            start=326,
            end=48,
            fill=MIST,
            width=line_width(size, 15),
        )

    # Orienteering control flag, simplified for launcher-icon scale.
    draw.rounded_rectangle(
        [
            scaled(size, 154),
            scaled(size, 275),
            scaled(size, 358),
            scaled(size, 459),
        ],
        radius=scaled(size, 22),
        fill=INK,
    )
    draw.rounded_rectangle(
        [
            scaled(size, 183),
            scaled(size, 302),
            scaled(size, 329),
            scaled(size, 430),
        ],
        radius=scaled(size, 7),
        fill=DEEP,
    )
    draw.polygon([p(183, 430), p(329, 302), p(329, 430)], fill=ORANGE)
    draw.polygon([p(183, 302), p(329, 302), p(183, 430)], fill=INK)

    # Start-finish/target dot reinforces the race-result role at tiny sizes.
    draw.ellipse(
        [
            scaled(size, 225),
            scaled(size, 346),
            scaled(size, 287),
            scaled(size, 408),
        ],
        fill=DEEP,
        outline=INK,
        width=line_width(size, 10),
    )

    # Small prediction/star cue, restrained so it survives masking but does not
    # become a text-like glyph.
    draw.polygon(
        [p(351, 223), p(361, 245), p(384, 254), p(361, 263), p(351, 286), p(341, 263), p(318, 254), p(341, 245)],
        fill=ORANGE,
    )


def rounded_alpha(size: int, radius: int) -> Image.Image:
    mask = Image.new("L", (size, size), 0)
    draw = ImageDraw.Draw(mask)
    draw.rounded_rectangle([0, 0, size - 1, size - 1], radius=radius, fill=255)
    return mask


def render_full_icon(size: int, rounded: bool = False) -> Image.Image:
    scale = 4
    big = size * scale
    img = Image.new("RGBA", (big, big), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img, "RGBA")
    draw_background(draw, big)
    draw_oracle_mark(draw, big, include_shadow=True)
    img = img.resize((size, size), Image.Resampling.LANCZOS)
    if rounded:
        img.putalpha(rounded_alpha(size, round(size * 0.21)))
    return img


def render_foreground(size: int) -> Image.Image:
    scale = 4
    big = size * scale
    img = Image.new("RGBA", (big, big), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img, "RGBA")
    draw_oracle_mark(draw, big, include_shadow=False)
    return img.resize((size, size), Image.Resampling.LANCZOS)


def save_webp(path: Path, image: Image.Image) -> None:
    image.save(path, "WEBP", quality=95, method=6)


def main() -> None:
    density_sizes = {
        "mipmap-mdpi": 48,
        "mipmap-hdpi": 72,
        "mipmap-xhdpi": 96,
        "mipmap-xxhdpi": 144,
        "mipmap-xxxhdpi": 192,
    }
    foreground_sizes = {
        "mipmap-mdpi": 108,
        "mipmap-hdpi": 162,
        "mipmap-xhdpi": 216,
        "mipmap-xxhdpi": 324,
        "mipmap-xxxhdpi": 432,
    }

    for folder, size in density_sizes.items():
        out_dir = RES / folder
        render_full_icon(size).save(out_dir / "ic_logo.png")
        render_full_icon(size, rounded=True).save(out_dir / "ic_logo_round.png")
        save_webp(out_dir / "ic_launcher.webp", render_full_icon(size))
        save_webp(out_dir / "ic_launcher_round.webp", render_full_icon(size, rounded=True))

    for folder, size in foreground_sizes.items():
        render_foreground(size).save(RES / folder / "ic_runner_foreground.png")

    render_full_icon(512).save(ROOT / "app" / "src" / "main" / "ic_runner-playstore.png")


if __name__ == "__main__":
    main()
