# XyKit Paper 26.2 升级说明

## 文档范围

本文档记录 `XyKit` 适配 Paper 26.2 的版本升级内容。

- 项目版本：`1.3.3`
- 目标 Paper API：`26.2.build.56-alpha`
- 所需 Java 版本：`25+`
- 升级日期：`2026-07-11`

## 本次变更

- Maven 依赖从 `26.1.1.build.20-alpha` 升级到 `26.2.build.56-alpha`
- 插件版本从 `1.3.2` 升级到 `1.3.3`
- 更新 `plugin.yml` 与 README，使其与 Paper 26.2 支持信息保持一致
- 现有礼包、CDK、数据存储和命令功能代码保持不变

## 说明

- 截至 `2026-07-11`，PaperMC Maven 中 `paper-api` 的 `latest` 与 `release` 都是 `26.2.build.56-alpha`
- 本插件继续使用 Bukkit 风格的 `plugin.yml` 作为入口，无需切换到 `paper-plugin.yml`
- 因旧版插件已可在 Paper 26.2 使用，本次按发布安排不再进行额外的 Paper 26.2 服务端功能测试
- 升级插件前仍建议备份 `plugins/XyKit/data.yml`
