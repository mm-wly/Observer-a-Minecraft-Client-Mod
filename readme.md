# Observer 2.1.0

简体中文 | [English](README.en.md) | [下载 / Downloads](https://github.com/mm-wly/Observer-a-Minecraft-Client-Mod/releases)

Minecraft Java Edition **26.3** 的纯客户端 Fabric 检索、持续追踪与高亮工具。服务端不需要安装 Observer。只使用客户端已经收到、当前仍加载的方块、群系和实体，不读取种子、服务端结构起点或尚未收到的区块。

## 安装

需要 Java 25、Fabric Loader 0.19.5，以及 Fabric API 0.161.0+26.3。将 `observer-2.1.0.jar` 放入当前游戏实例的 `mods` 文件夹，确保其中只有一份 Observer，然后启动游戏。正式 JAR 不含测试 Mod。可以用于单人世界或多人服务器；可识别范围取决于服务器实际发送给客户端的内容。

## 快速开始

```mcfunction
/obs structure mineshaft
/obs structure village
/obs structure ocean_ruin
/obs structure shipwreck
/obs feature amethyst_geode
/obs block diamond_ore
/obs entity creeper
/obs biome cherry_grove
```

所有查询默认持续搜索、追踪和高亮，直到清除。无需附加 `tracking` 或时间参数，命令也不接受这两个参数。重复输入相同查询会显示当前结果，不重复创建任务或重建索引。

只清除指定查询：

```mcfunction
/obs clear structure mineshaft
/obs clear block diamond_ore
/obs clear entity creeper
```

清除所有查询、导航和 Observer 添加的高亮：

```mcfunction
/obs clear
```

## 命令说明

| 命令 | 行为 |
|---|---|
| `/obs structure <名称>` | 搜索结构的启发式候选区域 |
| `/obs feature <名称>` | 搜索紫晶洞、刷怪房、岩浆池或化石 |
| `/obs biome <名称>` | 搜索已接收的三维群系采样数据 |
| `/obs block <名称>` | 实时搜索指定方块类型，包含其所有方块状态 |
| `/obs entity <名称>` | 实时搜索当前客户端已接收且仍存活的该类型实体 |
| `/obs clear <类别> <名称>` | 删除这一条查询及它的导航、点击链接和高亮贡献 |
| `/obs clear` | 删除全部查询 |
| `/obs page <页码>` | 翻阅最近创建的查询 |
| `/obs page <查询ID> <页码>` | 翻阅指定查询 |
| `/obs list` | 显示活动查询数、扫描进度与高亮数量，并列出每条查询；点击名称查看当前结果 |

类别为 `structure`、`feature`、`biome`、`block`、`entity`。搜索和指定清除共享同一个名称来源与 Tab 补全实现。支持单词开头补全：输入 `out` 可补全 `pillager_outpost`，输入 `geode` 可补全 `amethyst_geode`；`_`、`/`、`.`、`-` 均视为单词分隔符，不匹配单词中间。显式命名空间会限制匹配范围，例如 `minecraft:out`。名称可省略 `minecraft:`；方块、实体和群系使用客户端注册表，也支持存在于客户端的 Mod 命名空间。结构和地物规则针对原版。`/obs look <令牌>` 是点击聊天行使用的内部命令，不接受任意坐标。`page` 和 `look` 不出现在命令名 Tab 补全中，但聊天链接仍可执行它们；`status` 已移除，请用 `/obs list`。

游戏内提示、错误、分页、导航与识别依据支持简体中文和英文，跟随 Minecraft 语言设置；其他语言使用英文回退。命令名称和注册 ID 保持不变。

指定清除按**查询名称**匹配。例如同时启动 `village` 与 `village_plains`，清除 `village` 后，`village_plains` 查询仍然有效；共同目标仍可能被另一条查询高亮。

## 结果、翻页与通知

示例：

```text
#1 找到了12个 mineshaft · 第 1/2 页
1. 坐标:120 -24 86，距离43 [估算]
2. 坐标:168 -31 104，距离91 [估算]
…
[下一页]
```

每页最多 8 个目标，按玩家到目标导航点的三维直线距离从近到远排列。坐标为整数，距离向下取整，因此不同实际距离可能显示相同整数。结构的导航点位于已接收的候选证据处，不保证是入口；合并区域不会把导航点移到未接收的空洞中。

点击任意目标行，会**立即转动视角**朝向目标，并在屏幕上显示坐标和距离。不会自动走路、传送、挖掘或发送定位指令。实体导航会跟随实体移动。旧目标被移除、卸载、清除或发生不再匹配的变化时，链接会失效。

首次发现结果或首次完成空结果扫描时，聊天显示目标列表。之后只有目标增加、消失或被替换才自动更新；移动、距离排序改变和实体走动不会反复刷屏。每条查询最多每秒自动报告一次，多查询全局最多每秒 4 份列表，按轮转顺序合并短时间内的变化。输出自动报告的 tick 会让出扫描预算，避免扫描与聊天排版开销叠加。数量很多时，可以随时用分页命令主动刷新。结果列表保留在游戏聊天历史中，但不逐份写入控制台/日志文件，以避免同步日志 I/O 阻塞游戏线程；普通聊天和错误日志不受影响。

`扫描中` 表示还有已接收分节等待索引；`结果刷新中` 表示后台结果尚未追上区块变化。数量是当前已扫描快照的匹配总数，扫描期间会逐步变化。默认只保留最近 4096 个结果供翻页，默认同时绘制最近 512 个方框，超限会给出提示。

## 可搜索名称

### 结构

名称与原版 locate 结构 ID 对应。增加以下跨变体查询：

| 统一名称 | 覆盖范围 |
|---|---|
| `village` | plains、desert、savanna、snowy、taiga 五种村庄 |
| `ocean_ruin` | cold、warm 两种海底废墟 |
| `mineshaft` | 普通及 mesa 废弃矿井 |
| `ruined_portal` | 普通、desert、jungle、swamp、mountain、ocean、nether 变体 |
| `shipwreck` | 海中和搁浅沉船，包括侧翻、倒翻及残缺船模板 |

也可查询具体变体，例如 `village_taiga`、`ocean_ruin_warm`、`mineshaft_mesa`、`ruined_portal_nether`。沉船不单独列出搁浅类型；`shipwreck_beached` 输入会规范化为 `shipwreck`，清除时同样处理。

其余结构名称：`ancient_city`、`bastion_remnant`、`buried_treasure`、`desert_pyramid`、`end_city`、`fortress`、`igloo`、`jungle_pyramid`、`mansion`、`monument`、`nether_fossil`、`pillager_outpost`、`stronghold`、`swamp_hut`、`trail_ruins`、`trial_chambers`。

### 地物

| 名称 | 含义 |
|---|---|
| `amethyst_geode` | 紫水晶、方解石和平滑玄武岩组成的紫晶洞 |
| `dungeon` | 传统刷怪房；刷怪笼配合苔石地板与内室几何 |
| `lava_lake` | 主世界局部连通岩浆源面，不区分生成器岩浆湖与天然洞穴岩浆 |
| `fossil` | 主世界骨块残骸，排除邻近试炼刷怪笼、宝库或明显凝灰岩建筑材料的候选 |

### 群系、方块与实体

用 Tab 查看当前客户端支持的完整名称。群系来自 4×4×4 三维采样格，包括地下群系；每个区块提供一个最近匹配点，所以“目标数”是匹配区块数，**不是独立群系区域数量**。默认不为所有群系采样格画框，只高亮当前点击导航的采样格。

方块按精确注册 ID 匹配；例如 `diamond_ore` 不包含 `deepslate_diamond_ore`，两者可以同时查询。实体包括生物、掉落物、矿车等注册实体类型，只能找到客户端当前知道的实体。

## 识别与区域合并

普通服务器不向客户端提供完整的结构起点和包围盒，因此结构/地物结果都是**启发式候选**，不能承诺零误报或精确生成边界。玩家建筑、被破坏的结构、只加载一部分的结构都可能影响识别；客户端隐藏或未接收的内容无法搜索。

矿井使用支架和已接收通道的连通图。候选区域按水平间隙 12 格、垂直间隙 8 格简单合并，不再要求走廊方向或额外通道证据；附近独立矿井也可能被合并。真实相交的两个生成矿井无法可靠分辨来源。其他结构按原始候选框的接近程度合并：村庄允许较大的建筑间距，大型建筑允许较小断口，小型废墟使用更紧的阈值。合并只在同一查询内发生，不把不同结构类型合成一个目标。

埋藏宝藏始终高亮单个 1×1×1 箱子，不与旁边箱子合并。刷怪房、沼泽小屋保留其独立锚点。完整阈值及各规则证据见 [识别说明](docs/DETECTION.md)。

沉船必须同时存在船体材料、箱子和木活板门；纯矿井木材不能满足活板门条件。被玩家拆掉活板门或尚未接收到活板门的船可能暂时不显示。沼泽小屋使用沼泽环境中的炼药锅与花盆相对位置，不依赖木材。海底废墟不使用岩浆块作证据。

## 高亮与距离感

方块用柔和青色线框，启发式结构/地物用红色线框。近处边框较亮、较粗，距离衰减在 32 格左右明显增强；透明度约 100–250、线宽约 1.5–3 px，远处保留更清晰的可见度，RGB 颜色不变；遮挡不会隐藏边框。距离按玩家视点到包围盒最近表面的距离计算。实体沿用原版发光轮廓，不替换实体材质。清除 Observer 不会取消服务器给予实体的原版发光效果。

## 生命周期与性能

死亡重生与维度切换会保留查询意图；换维度时清除旧世界结果、链接和导航，在新维度重新索引。断开服务器或退出世界会清除全部查询，避免跨世界保留目标。区块卸载后移除其结果；无需用 `clear` 手动清理走过的区域。

索引按变化增量更新，普通方块变化只使相关分节失效；新区块先排队，按时间预算逐步扫描。后台处理材料证据、区域合并和最近目标排序，不在后台访问 Minecraft 世界对象。高速飞行时允许扫描进度落后，以限制主线程负担；没有活动查询时不扫描。

配置文件首次运行后生成于游戏实例 `config/observer.json`，修改后重启游戏：

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

| 配置 | 含义与范围 |
|---|---|
| `scanBudgetMillis` | 每 tick 扫描软预算，0.25–10 ms；不是整款 Mod 的绝对帧耗时上限 |
| `maxSectionsPerTick` | 每 tick 最多完成 1–128 个 16³ 分节 |
| `maxResults` | 每条查询保留最近 8–65536 个目标；不限制统计总数 |
| `maxRenderBoxes` | 全局最多绘制 8–4096 个区域；越大越费渲染时间 |
| `maxQueries` | 同时运行 1–32 条查询 |
| `refreshMillis` | 后台刷新最小间隔，100–10000 ms；实体按约 100 ms 更新 |

若高速飞行仍卡顿，可把扫描预算降到 1、亮框数降到 128；扫描会更慢，但任务不会丢失。`stone`、`air` 等高频方块会有大量匹配，尽量只开启需要的查询。GC、首次类加载、其他 Mod 和游戏本身的区块生成也会造成尖峰，不能用扫描软预算保证固定 FPS。

## 常见问题

- **搜不到远处结构？** 只能搜索客户端仍加载的范围，增加服务端实际发送距离才可能扩大范围。不能搜索只在远景 Mod 中显示、原版客户端没有方块数据的区域。
- **一个结构仍显示多个框？** 缺失证据、未加载的连接处或合并上限可能保留多个候选。继续接近并加载后会增量修正；不会把未知空洞假定成通路。
- **框包含空气或附近地形？** 使用近似轴对齐包围盒；L 形、分支结构的外接框自然包含空白。
- **新增方块没有立即显示？** 等待扫描与后台刷新；用 `/obs list` 查看队列。第三方 Mod 若绕过原版方块/群系更新通知，不能保证即时刷新。
- **清除后还有发光？** 其他 Observer 查询或服务器原版发光仍可能生效。
- **为什么收到的数量与可翻页数量不同？** 默认仅保留最近 4096 个目标；大量加载/卸载过程中结果也可能短暂处于刷新状态。

## 构建、测试与扩展

JDK 25 下运行 `./gradlew build`（Windows 为 `gradlew.bat`）。构建结果位于 `build/libs/observer-2.1.0.jar`。源码位于本仓库根目录，包含 Gradle Wrapper，不附带测试；请在仓库根目录构建，不含游戏资源、存档或缓存。


许可证：MIT，见 [LICENSE](LICENSE)。

## 26.3 更新（2.1.0）

新增 /obs structure abandoned_camp 统一查询，以及 18 个 abandoned_camp_<群系名> 精确变体（可用 Tab 补全）。识别结合对应群系、白色羊毛楼梯、支撑栅栏、储物容器和泥土小径。这是启发式检测：玩家搭建的类似帐篷也可能命中，损坏或尚未加载的营地可能漏检，边界框不是服务端结构边界。部分原版帐篷没有草床，因此不将草床设为必需。

新增方块、群系和实体通过客户端注册表提供查询与补全，例如 /obs block straw_bed、/obs block poplar_log、/obs biome dappled_forest。坐垫属于实体，请从 /obs entity 的 Tab 列表选择。

远处最低线宽从 0.75 px 提高到 1.5 px，最低透明度从 40 提高到 100（满值 255）；近处仍为 3 px / 250，RGB 不变，保留距离渐变。

验证：生产源码使用本机 26.3 依赖编译成功，164 项回归检查通过，并完成独立客户端启动初始化。未进行世界内完整回归和视觉验收。标准 Gradle 下载受网络影响，交付 JAR 通过本机依赖编译打包。详见 [移植说明](docs/PORT-26.3.md)。

## 下载与兼容版本

- [Minecraft 26.3：Observer v2.1.0](https://github.com/mm-wly/Observer-a-Minecraft-Client-Mod/releases/tag/v2.1.0)
- [Minecraft 26.2：Observer v2.0.1](https://github.com/mm-wly/Observer-a-Minecraft-Client-Mod/releases/tag/v2.0.1)

请下载对应 Release 的 JAR；GitHub 自动生成的 Source code 压缩包是源码，不能直接安装为 Mod。
完整算法与运行流程见 [report.md](report.md)。
