package com.xykit.commands;

import com.xykit.XyKitPlugin;
import com.xykit.managers.KitManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class KitCommand implements CommandExecutor {
    private final XyKitPlugin plugin;
    private final KitManager kitManager;

    public KitCommand(XyKitPlugin plugin) {
        this.plugin = plugin;
        this.kitManager = plugin.getKitManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("kit")) {
            return handleKitCommand(sender, args);
        } else if (command.getName().equalsIgnoreCase("cdk")) {
            return handleCDKCommand(sender, args);
        }
        return false;
    }

    private boolean handleKitCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            // 显示可用礼包
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c只有玩家才能执行此命令!");
                return true;
            }

            Player player = (Player) sender;
            Map<String, Map<String, Object>> kits = kitManager.getKits();

            if (kits.isEmpty()) {
                player.sendMessage("§c当前没有可用的礼包!");
                return true;
            }

            player.sendMessage("§6=== 可用礼包 ===");
            for (Map.Entry<String, Map<String, Object>> entry : kits.entrySet()) {
                String type = (String) entry.getValue().get("type");
                String name = (String) entry.getValue().get("name");
                String displayCommand = "starter".equals(type) ? "/kit claim " + entry.getKey() : "/cdk <代码>";
                player.sendMessage("§a" + displayCommand + " §7- " + name);
            }
            player.sendMessage("§6=================");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "claim":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§c只有玩家才能执行此命令!");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("§c用法: /kit claim <礼包名>");
                    sender.sendMessage("§c可用礼包: " + String.join(", ", kitManager.getKits().keySet()));
                    return true;
                }
                Player player = (Player) sender;
                kitManager.giveKit(player, args[1], null);
                break;

            case "create":
                if (!sender.hasPermission("xykit.admin")) {
                    sender.sendMessage("§c你没有权限执行此命令!");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("§c用法: /kit create <礼包名>");
                    return true;
                }
                sender.sendMessage("§a请在config.yml中配置礼包内容!");
                break;

            case "createcdk":
                if (!sender.hasPermission("xykit.admin")) {
                    sender.sendMessage("§c你没有权限执行此命令!");
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage("§c用法: /kit createcdk <礼包名> <数量> [批量数量]");
                    sender.sendMessage("§c可用礼包: " + String.join(", ", kitManager.getKits().keySet()));
                    return true;
                }
                try {
                    int amount = Integer.parseInt(args[2]);

                    // 检查是否要批量生成
                    int batchCount = 1;
                    if (args.length >= 4) {
                        batchCount = Integer.parseInt(args[3]);
                        if (batchCount < 1) batchCount = 1;
                        if (batchCount > 100) {
                            sender.sendMessage("§c批量生成数量不能超过100!");
                            return true;
                        }
                    }

                    if (batchCount == 1) {
                        // 单个CDK生成
                        String cdkCode = kitManager.createCDK(args[1], amount);
                        if (cdkCode != null) {
                            sender.sendMessage("§a成功创建CDK礼包!");
                            sender.sendMessage("§e礼包: §6" + args[1]);
                            sender.sendMessage("§eCDK代码: §6" + cdkCode);
                            sender.sendMessage("§e可使用次数: §6" + amount);
                        } else {
                            sender.sendMessage("§c创建CDK失败! 可能礼包不存在或生成CDK冲突。");
                            sender.sendMessage("§c可用礼包: " + String.join(", ", kitManager.getKits().keySet()));
                        }
                    } else {
                        // 批量CDK生成
                        List<String> cdkCodes = kitManager.createMultipleCDKs(args[1], amount, batchCount);
                        if (!cdkCodes.isEmpty()) {
                            sender.sendMessage("§a成功批量创建 " + cdkCodes.size() + " 个CDK礼包!");
                            sender.sendMessage("§e礼包: §6" + args[1]);
                            sender.sendMessage("§e每个CDK可使用次数: §6" + amount);
                            sender.sendMessage("§eCDK代码列表:");
                            for (String code : cdkCodes) {
                                sender.sendMessage("§7- §6" + code);
                            }
                        } else {
                            sender.sendMessage("§c批量创建CDK失败! 可能礼包不存在。");
                            sender.sendMessage("§c可用礼包: " + String.join(", ", kitManager.getKits().keySet()));
                        }
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("§c数量和批量数量必须是数字!");
                }
                break;

            case "reload":
                if (!sender.hasPermission("xykit.admin")) {
                    sender.sendMessage("§c你没有权限执行此命令!");
                    return true;
                }
                // 调用KitManager的reload方法，它会处理所有重载
                kitManager.reload();
                sender.sendMessage("§a配置和数据重载完成!");
                break;

            case "cdkinfo":
                if (!sender.hasPermission("xykit.admin")) {
                    sender.sendMessage("§c你没有权限执行此命令!");
                    return true;
                }
                Map<String, Object> stats = kitManager.getCDKStatistics();
                sender.sendMessage("§6=== CDK统计信息 ===");
                sender.sendMessage("§e总CDK数量: §6" + stats.get("total"));
                sender.sendMessage("§e已使用完的CDK: §6" + stats.get("used_up"));
                sender.sendMessage("§e可用的CDK: §6" + stats.get("available"));

                // 显示已使用完的CDK列表
                Set<String> usedUpList = kitManager.getUsedUpCDKs();
                if (!usedUpList.isEmpty()) {
                    sender.sendMessage("§e已使用完的CDK列表:");
                    int count = 0;
                    for (String code : usedUpList) {
                        if (count < 10) { // 只显示前10个
                            sender.sendMessage("§7- §6" + code);
                            count++;
                        } else {
                            sender.sendMessage("§7... 还有 " + (usedUpList.size() - 10) + " 个");
                            break;
                        }
                    }
                }
                sender.sendMessage("§6使用 §c/kit cleancdk§6 清理已使用完的CDK");
                break;

            case "cleancdk":
                if (!sender.hasPermission("xykit.admin")) {
                    sender.sendMessage("§c你没有权限执行此命令!");
                    return true;
                }

                // 检查是否需要确认
                boolean confirm = args.length > 1 && "confirm".equalsIgnoreCase(args[1]);

                // 获取已使用完的CDK集合
                Set<String> expiredCDKs = kitManager.getUsedUpCDKs();

                if (!confirm) {
                    if (expiredCDKs.isEmpty()) {
                        sender.sendMessage("§a当前没有已使用完的CDK需要清理!");
                        return true;
                    }

                    sender.sendMessage("§c警告：此操作将永久删除所有已使用完的CDK！");
                    sender.sendMessage("§c当前有 §6" + expiredCDKs.size() + " §c个已使用完的CDK");
                    sender.sendMessage("§c如果您确定要执行，请使用 §6/kit cleancdk confirm");

                    // 显示部分CDK代码
                    if (expiredCDKs.size() <= 5) {
                        sender.sendMessage("§c将被删除的CDK:");
                        for (String code : expiredCDKs) {
                            sender.sendMessage("§7- §6" + code);
                        }
                    } else {
                        sender.sendMessage("§c部分CDK代码:");
                        int count = 0;
                        for (String code : expiredCDKs) {
                            if (count < 3) {
                                sender.sendMessage("§7- §6" + code);
                                count++;
                            } else {
                                break;
                            }
                        }
                        sender.sendMessage("§7... 还有 " + (expiredCDKs.size() - 3) + " 个");
                    }
                    return true;
                }

                // 执行清理操作
                int cleanedCount = kitManager.cleanUsedUpCDKs();
                if (cleanedCount > 0) {
                    sender.sendMessage("§a成功清理 §6" + cleanedCount + " §a个已使用完的CDK!");
                } else {
                    sender.sendMessage("§a没有找到需要清理的已使用完CDK!");
                }
                break;

            default:
                sender.sendMessage("§c未知命令! 可用子命令: claim, create, createcdk, reload, cdkinfo, cleancdk");
                break;
        }
        return true;
    }

    private boolean handleCDKCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c只有玩家才能执行此命令!");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("§c用法: /cdk <代码>");
            return true;
        }

        Player player = (Player) sender;
        String code = args[0];

        // 通过CDK代码获取对应的礼包名称
        String kitName = kitManager.getKitNameByCDK(code);
        if (kitName == null) {
            player.sendMessage("§c无效的CDK代码!");
            return true;
        }

        // 使用正确的礼包名称发放礼包
        kitManager.giveKit(player, kitName, code);
        return true;
    }
}