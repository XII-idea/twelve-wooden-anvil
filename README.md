# 拾贰木砧（Twelve Wooden Anvil）

为 Minecraft 添加由各种木材制成的木质砧。

> **本仓库按 Minecraft 版本分分支维护。**
> `main` 分支仅作为版本引导页，**不包含任何代码**，请按下面的对应关系切到你要的版本分支。

## 版本分支

| Minecraft 版本 | 分支 | 说明 |
| --- | --- | --- |
| 26.2 | [`26.2`](../../tree/26.2) | 适配 MC 26.2 / NeoForge 26.2.x |
| 26.1.x | [`26.1`](../../tree/26.1) | 支持 MC 26.1 / 26.1.1 / 26.1.2 |

一条 Minecraft 版本线对应一条分支。新版本适配在新分支上进行，老分支保持原样，便于各版本独立维护与发版。

## 获取与构建

```bash
# 切到目标版本分支（以 26.2 为例）
git checkout 26.2

# 构建
./gradlew build
```

构建产物位于 `build/libs/`。各版本的环境要求（Java、Minecraft、NeoForge 版本）见对应分支的 `README.md`。

## 许可证

[MIT](./LICENSE)