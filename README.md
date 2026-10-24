# Diluc's Dawn Armor — NeoForge 1.21.1

An unofficial Minecraft Java mod adding a four-piece Diluc-inspired outfit, Strength I, a fireball, and a charged flaming phoenix. The black coat, white shirt/lapels, red lining, gold trim, trousers and riding boots use original simplified pixel art. The head item is a crimson hair/crest-inspired headpiece. This is a standard Minecraft humanoid armor retexture, not a ripped Genshin model or a custom long-coat model.

## Important build status

This download is a **source project, not an installable mod JAR**. The authoring workspace had no Java/Javac/Gradle runtime, so compilation and in-game testing could not be performed. Resource/static checks do not establish that the mod compiles or runs. Build and test it locally before using it in a valued world. No official Genshin textures or models are included.

## Target

- Minecraft **Java Edition 1.21.1** (not Bedrock).
- NeoForge **21.1.264**, the version pinned by the official 1.21.1 ModDevGradle MDK used for this project.
- 64-bit **JDK 21** to build and run Minecraft. A newer JDK can launch Gradle, but the Java 21 toolchain must also be available or downloaded.
- Gradle wrapper **9.2.1**; ModDevGradle **2.0.148**. Wrapper copied from the official NeoForge MDK.
- No extra gameplay or animation mods required.
- Both client and dedicated server need this mod. NeoForge/Fabric/Forge are not interchangeable.

## Build the JAR

1. Install a 64-bit JDK 21 and ensure `java -version` works in a new terminal.
2. Extract the ZIP fully. Do not run the batch file from inside the ZIP viewer.
3. Windows: double-click `build-mod.bat`.
4. Linux/macOS: open a terminal in this folder and run `sh build-mod.sh`.
5. The first build needs internet access for Gradle, NeoForge, Minecraft and their dependencies. Allow a few minutes and several GB of disk space.
6. Only after the build says **BUILD SUCCESSFUL**, take `build/libs/diluc-armor-1.0.0.jar` and place it in the `mods` folder of a **Minecraft 1.21.1 NeoForge 21.1.264** installation. Put the same JAR on the server if applicable. Do not put the source ZIP or sources JAR in `mods`.

Development commands: `gradlew.bat runClient` / `sh gradlew runClient`; dedicated server: `gradlew.bat runServer` / `sh gradlew runServer` (accept the generated server EULA only if you agree).

## Gameplay

| Feature | Behavior |
|---|---|
| Full outfit | Crimson Crest + Dawn Coat + Trousers + Riding Boots |
| Set bonus | Strength I while all four pieces are worn; fades within 0.5 seconds of unequipping, without forcibly deleting potion Strength |
| X — Searing Fireball | 8 base damage (4 hearts), direct hit, 2-second cooldown, straight flight up to about 40 blocks |
| Charge | Every successful X cast grants one charge, including misses; five casts fill the meter |
| C — Dawn Phoenix | 24 base damage (12 hearts) per target, exactly 3x the fireball's base damage; consumes all five charges |
| Phoenix flight | Animated flame-bird sprite with particles, pierces multiple enemies, hits each at most once, stops at solid blocks; up to about 46 blocks |
| Phoenix cooldown | 3 seconds, in addition to the five-cast requirement |
| Fire | Both abilities can ignite a damaged target for 4 seconds. Neither places fire blocks or damages terrain |
| Armor | 20 total defense points (10 armor icons), +2 toughness per piece, durability factor 25; repaired with leather |
| HUD | Lower-left charge segments and cooldowns; visible only with the full set |

**Damage values are raw impact damage**, before armor, fire resistance, difficulty, enchantments, vanilla hurt-invulnerability windows, and other mods. Subsequent burn damage is separate and equal-duration for both attacks, so total damage including burning is not a strict 3:1 ratio. Strength I affects ordinary melee; it does not multiply these projectile constants. Fire-immune/fire-resistant targets can ignore the custom fire damage. The owner's allies are skipped, and vanilla player-damage/PvP rules still apply. This outfit does not grant fire resistance.

Abilities require a living, non-spectator player wearing the whole set. Keybindings are remappable under Options > Controls > Key Binds > Diluc's Dawn Armor. The HUD displays the actual assigned keys. X/C may conflict with other mods; remap as needed. Each press casts once, rather than auto-firing while held. Server-side equipment/cooldown/charge checks prevent the client from choosing its own damage or spamming valid casts.

Charge persists across normal saves/logouts. Respawning resets charge and cooldowns. Taking off the set disables casting but does not erase stored charge. Charge cannot exceed five.

## Get the outfit

All four items appear in Creative > Combat. With cheats or operator permission:

```
/give @s dilucarmor:crimson_crest
/give @s dilucarmor:diluc_coat
/give @s dilucarmor:diluc_trousers
/give @s dilucarmor:diluc_boots
```

### Survival crafting (3x3 grid)

`L` = leather, `R` = red dye, `G` = gold ingot, `N` = gold nugget, `.` = empty.

**Crest**
```
L R L
L . L
. . .
```
**Coat**
```
L . L
L G L
L R L
```
**Trousers**
```
L R L
L . L
L . L
```
**Boots**
```
L . L
L . L
N . N
```

Recipes unlock in the recipe book after obtaining leather. Patterns can be entered manually even before unlocking.

## Tuning

Edit constants in `src/main/java/com/superapp/dilucarmor/Abilities.java` and rebuild:
- `FIREBALL_DAMAGE = 8.0F`
- `PHOENIX_DAMAGE = FIREBALL_DAMAGE * 3.0F` (preserves the requested ratio)
- `REQUIRED_CASTS = 5`
- `FIREBALL_COOLDOWN = 40` / `PHOENIX_COOLDOWN = 60` (20 ticks = one second)

If increasing `REQUIRED_CASTS`, also adjust the HUD segment spacing/width in `client/DilucClient.java`. Textures are in `src/main/resources/assets/dilucarmor/textures/`. This project has no runtime config file or cloth-physics dependency.

## Local acceptance test — not yet executed

Use a disposable Creative world first:

1. Build, run the client, and check the log for errors. Confirm four items, no missing textures, and recognizable black/red/white/gold outfit in third person.
2. Wear all four items: verify Strength I and HUD. Remove each piece individually: abilities must be denied and set-only Strength must expire quickly. Test with a separate Strength II potion to ensure it is not downgraded/removed.
3. Press X five times at least two seconds apart. Watch the charge rise 1/5 to 5/5, including misses. Pressing C early must not launch a bird.
4. At 5/5, press C. Confirm the bird is visible to two connected players, flaps, damages a line of non-allied mobs once each, stops at walls, and resets charge to zero.
5. Use unarmored, non-fire-immune targets and space tests apart to avoid vanilla invulnerability. Verify raw impact values of 8 and 24 before burning; damage mitigation can change observed health loss. Fire Resistance should protect a target.
6. Test X/C during cooldown, partial armor, death/respawn, reconnect, Creative/Spectator, and remapped controls. Confirm the HUD never exceeds five charges and no queued menu press unexpectedly casts.
7. Run a dedicated server with this mod installed on both clients. Verify no client-class-loading error; test normal PvP, PvP disabled, team friendly fire off, and pets.
8. Shoot at walls, at point-blank range and at several aligned mobs. No block destruction, fire placement, wall penetration or self-hits should occur.

## Troubleshooting

- Wrong Java / unsupported class version: use JDK 21; check `JAVA_HOME` and terminal `java -version`.
- Dependency download failure: the first build needs internet access; rerun after fixing connectivity.
- Missing mods or incompatible network protocol: both sides need the same mod and target versions.
- No ability: equip all four pieces, leave Spectator, close menus, check key conflicts, wait for cooldown/charge.
- If compilation fails, keep the full Gradle error output. This project was statically checked, **not compiler-verified**.

## Attribution

Unofficial fan work. Diluc and Genshin Impact belong to their respective rights holders. Not affiliated with or endorsed by HoYoverse. Original code/art: MIT (see LICENSE). Gradle wrapper files retain their own upstream licensing; see GRADLE-WRAPPER-NOTICE.txt.
