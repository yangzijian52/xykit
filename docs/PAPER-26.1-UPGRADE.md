# XyKit Paper 26.1 升级说明

## 文档范围

本文档用于记录 `XyKit` 适配 Paper 26.1 的升级内容。

- 项目版本：`1.3.2`
- 目标 Paper API：`26.1.1.build.20-alpha`
- 所需 Java 版本：`25+`
- 升级日期：`2026-04-05`

## 本次变更

- Maven 依赖从 `1.21.11-R0.1-SNAPSHOT` 升级到 `26.1.1.build.20-alpha`
- 编译目标从 Java `21` 升级到 Java `25`
- 更新插件元数据与 README，使其与 Paper 26.1 支持信息保持一致
- 保留升级前工作区里已经存在的功能改动

## 说明

- 截至 `2026-04-05`，PaperMC Maven 中 `paper-api` 的 `latest` 与 `release` 都是 `26.1.1.build.20-alpha`
- 本插件仍使用 Bukkit 风格的 `plugin.yml` 作为入口，本次升级不需要切换到 `paper-plugin.yml`
- 经过源码检查后，本次升级没有发现必须同步修改的 26.1 专用代码，重点在于依赖、Java 版本和文档对齐

## 验证情况

- 已检查版本相关文件
- 本机 Java 运行环境已确认是 `25.0.2`
- 本机原本没有预装 Maven，因此使用临时下载的 Maven 完成了本地构建验证
- 已在 `Paper 26.1.1-20-dev/26.1@d29063d` 服务端中完成插件加载、`/kit reload`、`/kit claim starter`、`/kit createcdk vip 1` 与 `/cdk` 兑换测试

## 发布前检查

1. 使用 Java 25 构建项目。
2. 在 Paper 26.1 服务端测试 `/kit`、`/cdk`、`/kit backup`、`/kit restore confirm`。
3. 使用已有的 `data.yml` 验证数据迁移是否正常。
4. 如有需要，推送发布提交并创建 GitHub Release。
