# Neko_Fix

> ## ⚠️ AI 生成声明
>
> 本项目（源码、构建说明与文档）由 **AI 辅助生成**，并经过人工实测验证。
> 按 GitHub 对 AI 生成内容的标注要求，特此明确说明。
>
> **使用风险自负。** 本项目针对以下固定版本环境验证：
> - Minecraft 1.21.1 / NeoForge 21.1.248
> - toNeko 1.9.6
> - JustARod 0.3.0
>
> 模组版本升级后，Mixin 目标类、toNeko 姿态同步逻辑、JustARod 界面实现都可能变化，
> 本修复可能失效、报错或引入新问题。请勿未经测试直接用于正式服务器。

NeoForge 21.1.248 / Minecraft 1.21.1 的客户端+服务端修复模组。

当前版本：**1.0.8**

## 修复内容

1. **JustARod F10 菜单背景模糊**
   - `JRSyncScreen` / `FrictionScreen` / `MateScreen` 不再渲染原版 1.21+ 的整屏高斯模糊
   - 只画普通菜单背景色块
   - 自实现 `NekoRenderingUtils` + 原版 Mixin，**不依赖 FancyMenu**

2. **toNeko 睡觉站/躺闪烁**
   - 根因：toNeko 每 2 tick 向客户端同步 `STANDING`，并写入 `ClientEntityPoseManager`，其 `EntityMixin.getPose` 覆盖了原版 SLEEPING
   - `ClientPoseSleepGuardMixin`：睡觉时禁止 toNeko 客户端 PoseManager 写入非 SLEEPING 姿态
   - `SleepingPoseFinalMixin`：最低优先级在 `getPose` 最后强制 SLEEPING

3. **toNeko NPC NekoSleepInBedGoal**
   - 已躺下后跳过重复 `startSleeping()`，避免 NPC Neko 站/躺动画反复抽搐

## 安装

把 `build/libs/Neko_Fix-1.0.8.jar` 放到：

- 客户端：`.minecraft/versions/<版本>/mods/`
- 服务端：`<服务端>/mods/`

需要 NeoForge `21.1.248+`，Minecraft `1.21.1`。

## 构建

项目目前使用手动 `javac` + `jar` 构建，不依赖 Gradle。

1. JDK 21+
2. 准备以下 classpath：
   - Minecraft client/server srg jar（1.21.1）
   - NeoForge universal + fancymodloader loader
   - Sponge Mixin
   - Mojang logging / slf4j
   - `toneko-neoforge-1.9.6.jar`
   - `JustARod-neoforge-0.3.0.jar`
3. 编译并打包：

```powershell
# 把下面的路径换成你自己的
$cp = @(
  'path/to/client-1.21.1-srg.jar',
  'path/to/server-1.21.1-srg.jar',
  'path/to/neoforge-21.1.248-universal.jar',
  'path/to/loader-4.0.43.jar',
  'path/to/sponge-mixin-0.15.2.jar',
  'path/to/logging-1.2.7.jar',
  'path/to/slf4j-api-2.0.9.jar',
  'path/to/toneko-neoforge-1.9.6.jar',
  'path/to/JustARod-neoforge-0.3.0.jar'
)

javac -encoding UTF-8 -source 21 -target 21 -cp ($cp -join ';') `
  -d build/classes (Get-ChildItem src/main/java -Recurse -Filter *.java).FullName

jar --create --file build/libs/Neko_Fix-1.0.8.jar `
  -C build/classes . -C src/main/resources .
```

## 目录结构

```
src/main/java/com/neko/fix/
  NekoFix.java
  mixin/
    JustARodFrictionScreenMixin.java
    JustARodJRSyncScreenMixin.java
    JustARodMateScreenMixin.java
    ClientPoseSleepGuardMixin.java
    SleepingPoseStabilizerMixin.java
    SleepingStateStabilizerMixin.java
    SleepingPoseFinalMixin.java
    NekoSleepInBedGoalMixin.java
    VanillaBlurBlockScreenMixin.java
    VanillaBlurBlockGameRendererMixin.java
    VanillaBlurRadiusMixin.java
  util/
    NekoRenderingUtils.java
src/main/resources/
  neko_fix.mixins.json
  META-INF/neoforge.mods.toml
  data/justarod/...
```

## 已知限制 / 可能的暗坑

- **非标准构建**：项目没有 Gradle/Maven 构建文件，`README` 里是手动 `javac` 构建命令。换环境需要自己调整 classpath。
- **Mixin 依赖第三方类**：`NekoSleepInBedGoalMixin`、`ClientPoseSleepGuardMixin` 直接 Mixin toNeko/JustARod 的类，这些类一旦改名/改包就会失效。
- **可选依赖行为**：`neoforge.mods.toml` 里 `justarod`/`toneko` 是 optional 依赖。缺少对应模组时相关 Mixin 不应加载，但 Mixin 配置变更后仍需实测。
- **不依赖 FancyMenu**：1.0.8 起 F10 模糊修复已用自实现 Mixin，不再调用 FancyMenu；但如果同时装了 FancyMenu，双方可能各自拦截模糊，需要留意。
- **toNeko 姿态同步**：修复针对“真上床睡觉（sleepingPos 存在）”。toNeko 的 `/neko lie` 等非床位姿态不在保护范围内，若官方更新后行为变化需重新测试。

## License

- 本仓库代码/文档：**MIT License**（见 `LICENSE`）
- 本仓库**不包含** JustARod / toNeko 的源码或资源，仅通过 Mixin 挂钩其运行时类进行修复。
- 被修复的原项目许可：JustARod 为 **GPL-3.0**，toNeko 为 **GPL-v3**。
  如果你的使用场景要求严格兼容 GPL，请自行评估是否将本项目整体改为 GPL-3.0。
