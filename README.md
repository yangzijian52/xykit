# XyKit 礼包插件

**版本**: 1.1.0
**适用版本**: Paper 1.21.8 (Leaves服务端兼容)
**Java版本**: JDK 21+  
**作者**: yangzijian52  
**GitHub**: [库](https://github.com/yangzijian52/XyKit)

## 插件简介

XyKit 是一个功能完善的Minecraft礼包插件，支持新手礼包和CDK礼包系统。插件提供了严格的防重复领取机制、灵活的配置系统和多种命令执行方式。

## 功能特性

- ✓ 新手礼包系统 - 玩家只能领取一次
- ✓ CDK礼包系统 - 可创建指定数量的唯一兑换码
- ✓ **批量CDK生成** - 支持一次性生成多个CDK
- ✓ **CDK唯一性保证** - 防止CDK重复导致的盗刷
- ✓ 严格的检测机制 - 防止重复领取和滥用
- ✓ 多种命令支持 - OP命令、控制台命令、玩家命令、广播命令
- ✓ 易于配置 - 通过YAML文件轻松添加新礼包
- ✓ 数据持久化 - 服务器重启后数据不丢失
- ✓ 权限系统 - 完善的权限控制
- ✓ **详细的日志记录** - 便于排查问题

## 安装指南

1. 下载插件
   - 从发布页面下载最新版本的 xykit-1.0.0.jar

2. 安装到服务器
   - 将JAR文件放入服务器的 plugins 文件夹

3. 重启服务器
   - 重启服务器以生成配置文件和文件夹

4. 配置礼包
   - 编辑 plugins/XyKit/config.yml 文件配置礼包
   - 重启服务器或使用 `/kit reload` 重载配置

## 命令列表


### 玩家命令
- `/kit` - 查看所有可用礼包
- `/kit claim <礼包名>` - 领取新手礼包
- `/cdk <兑换码>` - 使用CDK兑换礼包

### 管理员命令
- `/kit createcdk <礼包名> <使用次数>` - 创建单个CDK礼包
- `/kit createcdk <礼包名> <使用次数> <生成数量>` - **批量创建CDK礼包**
- `/kit reload` - 重载插件配置
- `/kit cdkinfo` - **查看CDK统计信息**
- `/kit create <礼包名>` - 创建新礼包（需手动配置）

## 权限列表

| 权限 | 默认 | 描述 | 包含命令 |
|------|------|------|----------|
| `xykit.use` | true | 允许玩家使用基础礼包命令 | `/kit`, `/kit claim` |
| `xykit.cdk.use` | true | 允许玩家使用CDK兑换命令 | `/cdk` |
| `xykit.admin` | op | 管理员权限 | `/kit createcdk`, `/kit reload`, `/kit create`, `/kit cdkinfo` |
| `xykit.cdk.admin` | op | CDK管理权限 | `/kit createcdk` |
| `xykit.*` | op | 所有xykit权限的父权限 | 包含所有xykit权限 |

## 配置文件说明

**配置文件位置**: `plugins/XyKit/config.yml`

  ```yaml
# 数据库设置
database:
  type: "flatfile"  # 使用平面文件存储

# 礼包配置
kits:
  # 新手礼包示例
  starter:
    name: "&a新手礼包"
    type: "starter"  # 类型: starter(新手) 或 cdk
    commands:
      - "money give {player} 1000"
      - "give {player} diamond 5"
      - "msg &a欢迎来到服务器！"
  
  # CDK礼包示例
  vip:
    name: "&6VIP礼包"
    type: "cdk"
    commands:
      - "money give {player} 5000"
      - "give {player} diamond_block 3"
      - "op:gamemode creative {player}"
      - "cmd:say {player} 兑换了VIP礼包！"

# CDK设置
cdk-settings:
  code-length: 12  # CDK代码长度增加到12位，提高唯一性
  code-charset: "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
  ```

## 命令类型说明

# 在礼包配置的commands部分，支持以下命令类型：
  ```yaml
1. 普通命令:
   - "give {player} diamond 5"
   - 直接以玩家身份执行

2. 消息命令:
   - "msg &a欢迎消息"
   - 向玩家发送消息，支持颜色代码(&)

3. OP命令:
   - "op:gamemode creative {player}"
   - 以OP权限执行命令，执行后恢复原权限

4. 控制台命令:
   - "cmd:say {player} 领取了礼包"
   - 以控制台身份执行命令

5. 广播命令:
   - "broadcast:&6{player} 领取了豪华礼包"
   - 向全服广播消息

变量说明:
  {player} - 会自动替换为玩家名称
  ```

## 使用示例

1. 添加新手礼包:
   在config.yml的kits部分添加:
  ```yaml
   welcome:
     name: "&e欢迎礼包"
     type: "starter"
     commands:
       - "cmd:give {player} stone 64"
       - "cmd:money give {player} 500"
       - "msg &e欢迎来到我们的服务器！"
  ```

2. 创建CDK礼包:
  ```yaml
   /kit createcdk <礼包> <次数> [数量]
   创建单个CDK礼包:
   在游戏中执行:
   /kit createcdk vip 10
   这会创建一个VIP礼包的CDK，可使用10次
   批量创建CDK礼包:
   /kit createcdk vip 5 10
   这会创建10个VIP礼包的CDK，每个可使用5次
  ```
3. 玩家使用CDK:
  ```yaml
   /cdk ABCDEFGH
  ```
4. 重载配置:
  ```yaml
   /kit reload
  ```

###  **常见问题更新**

## 常见问题

Q: 插件无法正常加载？
A: 确保服务器使用Paper 1.21.8+和Java 21+

Q: 礼包命令不执行？
A: 检查命令语法是否正确，确保插件有相应权限

Q: CDK兑换失败？
A: 检查CDK是否已创建，使用次数是否已用完

Q: **批量生成CDK时出现重复？**
A: **插件已内置CDK唯一性检查机制，确保每个CDK都是唯一的**

Q: 玩家可以重复领取新手礼包？
A: 检查礼包类型是否为"starter"，数据文件是否正常保存

Q: 如何备份数据？
A: 备份 plugins/XyKit/data.yml 文件

Q: **CDK生成失败？**
A: **可能是字符集太小导致冲突，尝试增加code-length或减少批量生成数量**

## 数据文件

数据文件位置: `plugins/XyKit/data.yml`

该文件自动生成，包含:
- 玩家领取记录
- CDK使用数据
- **CDK创建时间戳**
- **CDK使用次数统计**

**安全特性:**
- CDK代码唯一性验证
- 使用次数限制
- 详细的兑换日志

请不要手动修改此文件，以免造成数据损坏

## 更新日志

### 版本 1.1.0 (2024-10-18)
- **新增批量CDK生成功能**
- **增强CDK唯一性保证机制**
- **新增CDK信息查看命令**
- **改进CDK安全性，防止重复CDK导致的盗刷**
- **增加详细的CDK使用日志**
- **优化错误处理和用户提示**

### 版本 1.0.0 (2024-10-18)
- 初始版本发布
- 支持新手礼包和CDK礼包
- 完整的权限系统
- 多种命令执行方式


## 技术特性

- **CDK唯一性算法**: 使用内存集合+文件双重验证确保CDK唯一
- **批量生成优化**: 支持一次性生成最多100个唯一CDK
- **安全防护**: 防止CDK重复、盗刷和滥用
- **性能优化**: 内存缓存已存在CDK，提高生成效率
- **错误恢复**: 完善的异常处理和数据备份机制


如有问题或建议，请通过以下方式联系:

GitHub Issues: [issues](https://github.com/yangzijian52/XyKit)
Email: [3369275827@qq.com]

## 📄 开源协议
本项目采用 MIT License 开源






