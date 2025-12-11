package com.xykit.managers;

import com.xykit.XyKitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class KitManager {
    private final XyKitPlugin plugin;
    private final ConfigManager configManager;
    private final DataManager dataManager;
    private Map<String, Map<String, Object>> kits;

    // 用于跟踪已生成的CDK，防止重复
    private final Set<String> generatedCDKs;

    public KitManager(XyKitPlugin plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.dataManager = plugin.getDataManager();
        this.kits = new HashMap<>();
        this.generatedCDKs = new HashSet<>();
    }

    /**
     * 加载所有礼包配置
     */
    public void loadKits() {
        kits.clear();
        FileConfiguration config = configManager.getConfig();

        if (config.contains("kits")) {
            for (String kitName : config.getConfigurationSection("kits").getKeys(false)) {
                String path = "kits." + kitName;
                Map<String, Object> kitData = new HashMap<>();

                kitData.put("name", config.getString(path + ".name", kitName));
                kitData.put("type", config.getString(path + ".type", "starter"));
                kitData.put("cooldown", config.getInt(path + ".cooldown", 0));
                kitData.put("commands", config.getStringList(path + ".commands"));

                kits.put(kitName.toLowerCase(), kitData);
            }
        }

        plugin.getLogger().info("已加载 " + kits.size() + " 个礼包");

        // 加载已存在的CDK到内存中，防止生成重复
        loadExistingCDKs();
    }

    /**
     * 加载已存在的CDK到内存中
     */
    private void loadExistingCDKs() {
        generatedCDKs.clear();
        generatedCDKs.addAll(dataManager.getAllCDKs());
        plugin.getLogger().info("已加载 " + generatedCDKs.size() + " 个现有CDK到内存");
    }

    /**
     * 给玩家发放礼包
     * @param player 玩家对象
     * @param kitName 礼包名称
     * @param cdkCode CDK代码（可为null）
     * @return 是否发放成功
     */
    public boolean giveKit(Player player, String kitName, String cdkCode) {
        String lowerKitName = kitName.toLowerCase();
        Map<String, Object> kit = kits.get(lowerKitName);
        if (kit == null) {
            player.sendMessage("§c礼包不存在!");
            return false;
        }

        String type = (String) kit.get("type");
        UUID playerId = player.getUniqueId();

        // 检查礼包类型和领取条件
        if ("starter".equals(type)) {
            if (dataManager.hasClaimedStarterKit(playerId, lowerKitName)) {
                player.sendMessage("§c你已经领取过这个礼包了!");
                return false;
            }
        } else if ("cdk".equals(type)) {
            if (cdkCode == null) {
                player.sendMessage("§c这个礼包需要使用CDK兑换!");
                return false;
            }

            if (!dataManager.isValidCDK(cdkCode)) {
                player.sendMessage("§c无效的CDK代码!");
                return false;
            }

            String cdkKit = dataManager.getCDKKit(cdkCode);
            if (cdkKit == null) {
                player.sendMessage("§cCDK对应的礼包不存在!");
                return false;
            }

            // 统一使用小写进行比较
            if (!lowerKitName.equals(cdkKit.toLowerCase())) {
                player.sendMessage("§c这个CDK不能用于此礼包!");
                return false;
            }

            int used = dataManager.getCDKUses(cdkCode);
            int maxUses = dataManager.getCDKMaxUses(cdkCode);

            if (used >= maxUses) {
                player.sendMessage("§c这个CDK已经被使用完了!");
                return false;
            }

            // 增加CDK使用次数 - 确保调用成功
            dataManager.incrementCDKUses(cdkCode);

            // 立即保存数据
            dataManager.saveData();

            // 验证使用次数已更新
            int newUsed = dataManager.getCDKUses(cdkCode);
            plugin.getLogger().info("CDK " + cdkCode + " 使用次数: " + used + " -> " + newUsed);
        }

        // 执行礼包命令
        List<String> commands = (List<String>) kit.get("commands");
        for (String command : commands) {
            executeCommand(player, command);
        }

        // 记录新手礼包领取
        if ("starter".equals(type)) {
            dataManager.setClaimedStarterKit(playerId, lowerKitName);
        }

        player.sendMessage("§a成功领取礼包: " + kit.get("name"));
        return true;
    }

    /**
     * 执行礼包中的命令
     * @param player 玩家对象
     * @param command 命令字符串
     */
    private void executeCommand(Player player, String command) {
        command = command.replace("{player}", player.getName());

        if (command.startsWith("msg ")) {
            // 消息命令
            String message = command.substring(4).replace("&", "§");
            player.sendMessage(message);
        } else if (command.startsWith("op:")) {
            // OP命令 - 以OP身份执行
            String opCommand = command.substring(3);
            boolean wasOp = player.isOp();
            try {
                player.setOp(true);
                Bukkit.dispatchCommand(player, opCommand);
            } finally {
                player.setOp(wasOp);
            }
        } else if (command.startsWith("cmd:")) {
            // 控制台命令
            String consoleCommand = command.substring(4).replace("{player}", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleCommand);
        } else if (command.startsWith("broadcast:")) {
            // 广播命令
            String broadcastMessage = command.substring(10).replace("&", "§");
            Bukkit.broadcastMessage(broadcastMessage);
        } else {
            // 玩家命令
            Bukkit.dispatchCommand(player, command);
        }
    }

    /**
     * 创建CDK礼包
     * @param kitName 礼包名称
     * @param amount CDK可使用次数
     * @return 创建的CDK代码，如果失败返回null
     */
    public String createCDK(String kitName, int amount) {
        String lowerKitName = kitName.toLowerCase();
        if (!kits.containsKey(lowerKitName)) {
            return null;
        }

        String code = generateUniqueCDKCode();
        if (code == null) {
            plugin.getLogger().warning("生成唯一CDK代码失败，可能字符集太小或尝试次数过多");
            return null;
        }

        // 存储小写的礼包名称，确保一致性
        boolean success = dataManager.addCDK(code, lowerKitName, amount);
        if (success) {
            // 添加到内存集合，防止重复
            generatedCDKs.add(code);
        }
        return success ? code : null;
    }

    /**
     * 批量创建CDK礼包
     * @param kitName 礼包名称
     * @param amount 每个CDK可使用次数
     * @param count 要创建的CDK数量
     * @return 创建的CDK代码列表
     */
    public List<String> createMultipleCDKs(String kitName, int amount, int count) {
        String lowerKitName = kitName.toLowerCase();
        if (!kits.containsKey(lowerKitName)) {
            return Collections.emptyList();
        }

        List<String> codes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String code = generateUniqueCDKCode();
            if (code != null && dataManager.addCDK(code, lowerKitName, amount)) {
                generatedCDKs.add(code);
                codes.add(code);
            } else {
                plugin.getLogger().warning("生成第 " + (i + 1) + " 个CDK失败");
            }
        }

        return codes;
    }

    /**
     * 生成唯一的CDK代码
     * @return 唯一的CDK代码，如果生成失败返回null
     */
    private String generateUniqueCDKCode() {
        FileConfiguration config = configManager.getConfig();
        int length = config.getInt("cdk-settings.code-length", 12);
        String charset = config.getString("cdk-settings.code-charset",
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");

        Random random = new Random();
        int maxAttempts = 100; // 最大尝试次数，防止无限循环

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            StringBuilder code = new StringBuilder();

            for (int i = 0; i < length; i++) {
                code.append(charset.charAt(random.nextInt(charset.length())));
            }

            String finalCode = code.toString();

            // 检查是否在内存中已存在
            if (!generatedCDKs.contains(finalCode)) {
                // 双重检查：确保在数据文件中也不存在
                if (!dataManager.isValidCDK(finalCode)) {
                    return finalCode;
                } else {
                    // 如果数据文件中存在但内存中不存在，更新内存集合
                    generatedCDKs.add(finalCode);
                }
            }
        }

        return null; // 达到最大尝试次数仍未生成唯一代码
    }

    /**
     * 通过CDK代码获取礼包名称
     * @param cdkCode CDK代码
     * @return 礼包名称，如果CDK不存在返回null
     */
    public String getKitNameByCDK(String cdkCode) {
        if (!dataManager.isValidCDK(cdkCode)) {
            return null;
        }
        return dataManager.getCDKKit(cdkCode);
    }

    /**
     * 获取所有礼包
     * @return 礼包映射表
     */
    public Map<String, Map<String, Object>> getKits() {
        return new HashMap<>(kits);
    }

    /**
     * 检查礼包是否存在
     * @param kitName 礼包名称
     * @return 是否存在
     */
    public boolean kitExists(String kitName) {
        return kits.containsKey(kitName.toLowerCase());
    }

    /**
     * 重载礼包配置和数据
     */
    public void reload() {
        // 先重载配置
        configManager.reloadConfig();

        // 重载数据
        dataManager.reloadData();

        // 重新加载礼包和CDK
        loadKits();

        plugin.getLogger().info("配置和数据重载完成");
    }

    /**
     * 获取已生成的CDK数量（内存中）
     * @return CDK数量
     */
    public int getGeneratedCDKCount() {
        return generatedCDKs.size();
    }

    /**
     * 清除所有已使用完的CDK
     * @return 被清除的CDK数量
     */
    public int cleanUsedUpCDKs() {
        int count = dataManager.cleanUsedUpCDKs();

        // 更新内存中的CDK集合
        loadExistingCDKs();

        return count;
    }

    /**
     * 获取CDK统计信息
     * @return 包含CDK统计信息的Map
     */
    public Map<String, Object> getCDKStatistics() {
        return dataManager.getCDKStatistics();
    }

    /**
     * 获取所有已使用完的CDK列表
     * @return 已使用完的CDK集合
     */
    public Set<String> getUsedUpCDKs() {
        return dataManager.getUsedUpCDKs();
    }
}