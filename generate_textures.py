"""Original fan-mod pixel art. Optional regeneration: python -m pip install pillow; python tools/generate_textures.py"""
from pathlib import Path
import math
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1] / 'src/main/resources/assets/dilucarmor/textures'
BLACK = '#191820'
EDGE = '#101016'
SHADE = '#2b2831'
RED = '#9b2533'
REDLIGHT = '#cb3540'
IVORY = '#eee9dc'
GOLD = '#cba466'
HAIR = '#a82d29'
HAIRLIGHT = '#d34b36'

def save(image, name):
    path = ROOT / name
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path)

def armor():
    image = Image.new('RGBA', (64, 32))
    d = ImageDraw.Draw(image)
    # Standard humanoid armor UVs. Hair-shaped open face, red strands, no opaque mask.
    d.rectangle((8, 0, 23, 7), fill=HAIR)
    for x in range(9, 23, 3): d.line((x, 0, x, 7), fill=HAIRLIGHT)
    d.rectangle((0, 8, 7, 15), fill=HAIR)
    d.rectangle((16, 8, 31, 15), fill=HAIR)
    d.rectangle((8, 8, 15, 9), fill=HAIR)
    d.polygon([(8, 9), (11, 9), (10, 12), (8, 13)], fill=HAIR)
    d.polygon([(14, 9), (15, 9), (15, 12)], fill=HAIRLIGHT)
    for x in [1, 4, 18, 21, 26, 29]: d.line((x, 9, x, 14), fill=HAIRLIGHT)
    d.point((2, 13), fill=GOLD)
    # Long dark jacket reads through red seams, ivory open shirt and gold fastenings.
    d.rectangle((16, 16, 39, 31), fill=BLACK)
    d.rectangle((20, 20, 27, 31), fill=SHADE)
    d.polygon([(22, 20), (25, 20), (24, 26), (23, 26)], fill=IVORY)
    d.polygon([(20, 20), (22, 20), (23, 24), (21, 23)], fill='#d6d0c6')
    d.polygon([(25, 20), (27, 20), (26, 23), (24, 25)], fill=IVORY)
    d.line((24, 22, 24, 26), fill=RED)
    d.line((21, 25, 20, 31), fill=RED)
    d.line((26, 25, 27, 31), fill=RED)
    d.rectangle((20, 28, 27, 28), fill=EDGE)
    d.point((24, 28), fill=GOLD)
    d.line((27, 24, 25, 26), fill=GOLD)
    d.point((27, 23), fill=GOLD)
    d.rectangle((16, 30, 19, 31), fill=RED)
    d.rectangle((28, 30, 39, 31), fill=RED)
    d.line((35, 21, 35, 30), fill=SHADE)
    d.line((32, 31, 39, 31), fill=GOLD)
    # Both sleeves use the same humanoid UVs. Gloves and red/gold cuffs.
    d.rectangle((40, 16, 55, 31), fill=BLACK)
    d.rectangle((44, 16, 47, 19), fill=SHADE)
    d.rectangle((40, 27, 55, 28), fill=RED)
    d.line((40, 29, 55, 29), fill=GOLD)
    d.line((45, 20, 45, 25), fill=SHADE)
    # Outer boot layer: keep upper leg transparent so the trousers show.
    d.rectangle((0, 26, 15, 31), fill=BLACK)
    d.line((0, 26, 15, 26), fill=RED)
    d.line((0, 27, 15, 27), fill=GOLD)
    d.rectangle((4, 30, 7, 31), fill='#35303a')
    d.point((5, 27), fill=IVORY)
    save(image, 'models/armor/diluc_layer_1.png')
    pants = Image.new('RGBA', (64, 32))
    p = ImageDraw.Draw(pants)
    p.rectangle((0, 16, 15, 31), fill=BLACK)
    p.rectangle((16, 16, 39, 31), fill=BLACK)
    p.line((20, 29, 27, 29), fill=GOLD)
    p.rectangle((20, 30, 27, 31), fill=RED)
    p.line((0, 20, 0, 31), fill=RED)
    p.line((11, 20, 11, 31), fill=RED)
    p.line((5, 21, 5, 31), fill=SHADE)
    p.line((13, 21, 13, 31), fill=SHADE)
    # Red inner coat-tail accents above the boots.
    p.rectangle((0, 20, 2, 24), fill='#641c2a')
    p.rectangle((10, 20, 11, 24), fill='#641c2a')
    save(pants, 'models/armor/diluc_layer_2.png')

def icons():
    for name in ['crimson_crest', 'diluc_coat', 'diluc_trousers', 'diluc_boots']:
        im = Image.new('RGBA', (16, 16))
        d = ImageDraw.Draw(im)
        if name == 'crimson_crest':
            d.polygon([(3, 4), (5, 1), (11, 1), (13, 4), (13, 12), (10, 10), (10, 6), (5, 6), (5, 10), (2, 12)], fill=EDGE)
            d.polygon([(4, 4), (6, 2), (10, 2), (12, 4), (12, 10), (10, 8), (10, 5), (5, 5), (4, 10), (3, 10)], fill=HAIR)
            d.line((6, 2, 5, 6), fill=HAIRLIGHT)
            d.line((9, 2, 11, 5), fill=HAIRLIGHT)
            d.point((3, 8), fill=GOLD)
        elif name == 'diluc_coat':
            d.polygon([(5, 1), (10, 1), (14, 4), (13, 9), (11, 8), (11, 14), (8, 13), (7, 13), (4, 14), (4, 8), (2, 9), (1, 4)], fill=EDGE)
            d.polygon([(5, 2), (10, 2), (13, 4), (12, 8), (10, 7), (10, 13), (8, 12), (7, 12), (5, 13), (5, 7), (3, 8), (2, 4)], fill=SHADE)
            d.polygon([(5, 2), (7, 3), (7, 8)], fill=IVORY)
            d.polygon([(10, 2), (8, 3), (8, 8)], fill=IVORY)
            d.line((7, 4, 7, 8), fill=RED)
            d.line((5, 8, 5, 12), fill=RED)
            d.line((10, 8, 10, 12), fill=RED)
            d.line((5, 9, 10, 9), fill=EDGE)
            d.point((8, 9), fill=GOLD)
            d.point((10, 5), fill=GOLD)
            d.point((3, 7), fill=GOLD)
            d.point((12, 7), fill=GOLD)
        elif name == 'diluc_trousers':
            d.polygon([(4, 1), (11, 1), (12, 14), (8, 14), (7, 7), (7, 14), (3, 14)], fill=EDGE)
            d.rectangle((4, 2, 10, 5), fill=SHADE)
            d.rectangle((4, 6, 6, 13), fill=BLACK)
            d.rectangle((8, 6, 10, 13), fill=BLACK)
            d.line((4, 2, 10, 2), fill=RED)
            d.point((7, 2), fill=GOLD)
            d.line((4, 6, 4, 12), fill=RED)
            d.line((10, 6, 10, 12), fill=RED)
        else:
            d.polygon([(3, 2), (6, 2), (6, 11), (7, 12), (7, 14), (1, 14), (1, 11), (3, 10)], fill=EDGE)
            d.polygon([(9, 2), (12, 2), (12, 10), (14, 11), (14, 14), (8, 14), (8, 12), (9, 11)], fill=EDGE)
            d.rectangle((3, 3, 5, 11), fill=SHADE)
            d.rectangle((9, 3, 11, 11), fill=SHADE)
            d.line((3, 4, 5, 4), fill=RED)
            d.line((9, 4, 11, 4), fill=RED)
            d.point((4, 5), fill=GOLD)
            d.point((10, 5), fill=GOLD)
            d.line((2, 12, 6, 12), fill=SHADE)
            d.line((9, 12, 13, 12), fill=SHADE)
        save(im, 'item/' + name + '.png')

def phoenix(frame):
    im = Image.new('RGBA', (64, 64))
    d = ImageDraw.Draw(im)
    outer = '#a82722'
    red = '#e4461e'
    orange = '#ff8428'
    yellow = '#ffd36a'
    white = '#fff4bc'
    def mirror(points): return [(63 - x, y) for x, y in points]
    if frame == 0:
        wing = [(30, 27), (24, 22), (16, 18), (5, 5), (6, 16), (10, 24), (2, 19), (7, 30), (14, 36), (25, 36)]
        inner = [(29, 29), (21, 25), (14, 20), (9, 13), (12, 25), (8, 24), (15, 32), (24, 33)]
        veins = [[(26, 29), (18, 25), (11, 18)], [(25, 32), (17, 30), (10, 26)]]
    else:
        wing = [(30, 27), (23, 29), (15, 27), (3, 20), (7, 30), (13, 36), (5, 35), (13, 44), (23, 42), (29, 35)]
        inner = [(28, 30), (19, 32), (10, 27), (16, 36), (11, 37), (19, 40), (25, 36)]
        veins = [[(26, 32), (18, 34), (11, 30)], [(25, 36), (20, 39), (14, 38)]]
    for pts in [wing, mirror(wing)]: d.polygon(pts, fill=outer)
    for pts in [inner, mirror(inner)]: d.polygon(pts, fill=orange)
    for line in veins:
        d.line(line, fill=yellow, width=2)
        d.line(mirror(line), fill=yellow, width=2)
    # Forked flame tails, bright body, hooked beak and a crown of fire.
    d.polygon([(27, 35), (25, 45), (20, 55), (27, 51), (26, 62), (33, 52), (38, 59), (36, 46), (38, 39)], fill=outer)
    d.polygon([(29, 35), (28, 45), (25, 53), (30, 50), (29, 57), (33, 49), (35, 53), (34, 42)], fill=orange)
    d.polygon([(31, 37), (30, 47), (32, 52), (34, 43)], fill=yellow)
    d.polygon([(29, 21), (28, 13), (31, 16), (33, 6), (35, 16), (39, 19), (37, 24), (37, 33), (34, 42), (29, 40), (26, 32)], fill=red)
    d.polygon([(31, 22), (31, 16), (34, 18), (37, 20), (34, 25), (35, 32), (32, 39), (29, 32)], fill=orange)
    d.polygon([(31, 25), (33, 23), (33, 32), (31, 36), (30, 31)], fill=white)
    d.polygon([(37, 20), (43, 22), (37, 24)], fill=yellow)
    d.point((36, 20), fill=white)
    for x, y in [(16, 45), (47, 15), (21, 11), (43, 48), (10, 39)]:
        d.rectangle((x, y + frame * 2, x + 1, y + 1 + frame * 2), fill=orange)
    save(im, 'entity/phoenix_' + str(frame) + '.png')

def fireball():
    im = Image.new('RGBA', (32, 32))
    d = ImageDraw.Draw(im)
    d.polygon([(4, 14), (3, 5), (10, 9), (13, 1), (17, 7), (25, 3), (24, 12), (30, 16), (27, 23), (22, 28), (12, 29), (5, 24), (2, 18)], fill='#b92d1e')
    d.ellipse((5, 7, 26, 28), fill='#f56020')
    d.polygon([(9, 19), (9, 13), (14, 15), (16, 5), (20, 15), (24, 13), (24, 21), (19, 26), (13, 25)], fill='#ffb832')
    d.ellipse((12, 15, 21, 25), fill='#fff1a6')
    save(im, 'entity/fireball.png')

if __name__ == '__main__':
    armor()
    icons()
    phoenix(0)
    phoenix(1)
    fireball()
    print('Generated 9 original textures in', ROOT)
