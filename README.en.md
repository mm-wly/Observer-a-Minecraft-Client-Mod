# Observer 2.1.0

[简体中文](readme.md) | English

A command-driven, client-only Fabric mod for searching, tracking and highlighting loaded Minecraft Java 26.3 world data. The server does not need Observer.

## Installation

Requires Java 25, Fabric Loader 0.19.5 and Fabric API 0.161.0+26.3. Put `observer-2.1.0.jar` in your instance's `mods` folder, keeping only one Observer version.

In-game messages, errors, navigation, pagination and detection evidence follow Minecraft's language setting. Simplified Chinese and English are included; other languages fall back to English. Commands and registry IDs stay unchanged.

## Quick start

```mcfunction
/obs structure mineshaft
/obs structure village
/obs structure pillager_outpost
/obs feature amethyst_geode
/obs block diamond_ore
/obs entity creeper
/obs biome cherry_grove
/obs list
```

All queries track continuously until cleared. Repeating a query displays its current results without creating another query. Neither `tracking` nor a duration argument is accepted.

| Command | Behavior |
|---|---|
| `/obs structure <name>` | Find heuristic structure candidates |
| `/obs feature <name>` | Find geodes, dungeons, small lava pools or fossils |
| `/obs block <name>` | Find a block ID, including all its states |
| `/obs entity <name>` | Find live entities of a registered type |
| `/obs biome <name>` | Find received three-dimensional biome samples |
| `/obs list` | Show query count, indexing progress, pending sections, outline count and all active queries; click a query name for current results |
| `/obs clear <category> <name>` | Remove one query, its links, navigation and highlight contribution |
| `/obs clear` | Remove every query |

Tab completion matches the start of any word in a name: `out` completes `pillager_outpost`, and `geode` completes `amethyst_geode`. Separators are `_`, `/`, `.` and `-`; word interiors do not match. An explicit namespace restricts matches: `minecraft:out` completes `minecraft:pillager_outpost`. Search and clear share completion behavior.

`minecraft:` is optional. Block, entity and biome names come from client registries and can include mod namespaces. Structure and feature rules target vanilla content.

`/obs status` has been removed. Chat links use `/obs page <query ID> <page>` and `/obs look <token>` internally. These command names are hidden from Tab suggestions but remain executable. Manual `/obs page <page>` displays a page of the most recently created query. `look` accepts an issued target token, not arbitrary coordinates.

## Supported structures and features

Structure families:

- `village`: plains, desert, savanna, snowy and taiga; variants such as `village_taiga` can be queried separately.
- `ocean_ruin`: cold and warm variants.
- `mineshaft`: ordinary and mesa variants.
- `ruined_portal`: standard, desert, jungle, swamp, mountain, ocean and nether variants.
- `shipwreck`: ocean and beached, including sideways, upside-down and partial templates. `shipwreck_beached` is accepted as an alias, not a separate query.

Other names: `ancient_city`, `bastion_remnant`, `buried_treasure`, `desert_pyramid`, `end_city`, `fortress`, `igloo`, `jungle_pyramid`, `mansion`, `monument`, `nether_fossil`, `pillager_outpost`, `stronghold`, `swamp_hut`, `trail_ruins`, `trial_chambers`.

Features:

| Name | Evidence and limitations |
|---|---|
| `amethyst_geode` | Amethyst, calcite and smooth basalt layers |
| `dungeon` | Ordinary spawner, mossy/cobblestone floor and chamber geometry |
| `lava_lake` | Small connected overworld lava-source volume; natural and artificial pools cannot be distinguished |
| `fossil` | Overworld bone blocks, excluding nearby trial-chamber evidence |

Family queries and variant queries are independent: clearing `village` does not clear `village_plains`.

## Results and navigation

Results are sorted by three-dimensional straight-line distance to the navigation point, eight per page. Coordinates are integers and displayed distances are rounded down. Hover a result for detection evidence; click it to immediately face the target and display its coordinates and distance. Entity navigation follows its position. This does not walk, teleport or mine for you.

The first results, or a completed empty scan, are reported automatically. Later reports occur when targets appear, disappear or are replaced, not merely when you move. Reports are limited to once per second per query and four lists per second globally. High-frequency result lists remain in chat history but are not individually written to the console/log.

By default each query retains the nearest 4096 targets for pagination, while the count can be larger. Scan/refresh labels indicate incomplete results. Old links stop working when their targets or queries are no longer valid.

Blocks use cyan outlines; heuristic structures/features use red. RGB colors are unchanged by distance. Opacity and width fall more noticeably around 32 blocks, ranging approximately from alpha 250 / 3 px nearby to alpha 100 / 1.5 px far away. Outlines remain visible through terrain. Entities use vanilla glowing outlines; clearing Observer preserves server-provided glowing. Biomes only draw the selected navigation sample.

## Data and detection limits

Only data already received and still loaded by the client is searched. Observer does not read the seed, server structure starts or unloaded chunks. Single-player and multiplayer are supported within this boundary.

Structures/features are heuristic candidates, not authoritative generation records. Player buildings, damaged structures and partially loaded areas can cause false positives or misses. Bounds are estimated axis-aligned boxes and navigation points are received evidence positions, not guaranteed entrances.

Mineshafts use support/passage connectivity, then nearby candidates are merged with horizontal/vertical gaps of 12/8 blocks; independent nearby mines may merge. Other structures use type-specific merging. Buried treasure remains a single chest box. Shipwrecks require wooden trapdoors as well as hull materials and a chest, so damaged ships may not match. See the detailed [detection rules (Chinese)](docs/DETECTION.md).

Biome results represent matching chunks, not distinct biome regions; each chunk contributes its nearest matching 4×4×4 sample. `diamond_ore` does not include `deepslate_diamond_ore`. Entity queries only know entities received by the client. Distant-terrain visuals without actual client block data cannot be searched.

## Lifecycle and performance

Queries survive respawn and dimension changes; switching dimensions clears old results and rebuilds the index. Disconnecting clears all queries. Unloading a chunk removes its results. No active queries means no scanning.

The main thread samples incrementally under a soft budget. Background computation handles evidence, merging and nearest-target selection without accessing live world objects. Fast movement may leave a scan backlog. The budget is not an absolute frame-time or FPS guarantee.

Edit `config/observer.json` and restart:

```json
{
  "scanBudgetMillis": 2.0,
  "maxSectionsPerTick": 128,
  "maxResults": 4096,
  "maxRenderBoxes": 512,
  "maxQueries": 16,
  "refreshMillis": 500
}
```

Ranges: scan budget 0.25–10 ms; sections per tick 1–128; retained results per query 8–65536; global rendered boxes 8–4096; queries 1–32; background refresh interval 100–10000 ms. Entities refresh about every 100 ms. For lower overhead, try a 1 ms scan budget and 128 outlines. Very common blocks such as stone or air can produce many matches.

## Building

The repository excludes tests. With JDK 25, run from the repository root `./gradlew build` (`gradlew.bat` on Windows). Output: `build/libs/observer-2.1.0.jar`. Production JARs exclude the test mod.

[Architecture (Chinese)](docs/ARCHITECTURE.md) · [Algorithms and course project (Chinese)](docs/ALGORITHMS.md) · [Testing and verification (Chinese)](docs/TESTING.md) · [License: MIT](LICENSE)

## 26.3 update (2.1.0)

Use /obs structure abandoned_camp for all camps, or select one of 18 abandoned_camp_<biome> variants with Tab. Detection combines the exact biome, white wool stairs, fences, storage and dirt paths. This is heuristic: player-built tents may match, damaged or unloaded camps may be missed, and boxes are not server structure bounds. Straw beds are optional because some vanilla tents have none.

New blocks, biomes and entities are available through live registry lookup and completion, for example /obs block straw_bed, /obs block poplar_log and /obs biome dappled_forest. Cushions are entities; select their IDs using /obs entity completion.

Validation: compiled against Minecraft 26.3, Loader 0.19.5 and Fabric API 0.161.0+26.3; 164 regression checks passed, and isolated client startup initialized successfully. Full in-world and visual checks were not performed. Gradle downloads were network-limited; the delivered JAR was compiled and packaged using installed dependencies.
