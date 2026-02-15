package com.xykit.managers;

import com.xykit.XyKitPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.Date;

public class DataManager {
    private final XyKitPlugin plugin;
    private FileConfiguration dataConfig;
    private File dataFile;

    public DataManager(XyKitPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadData() {
        dataFile = new File(plugin.getDataFolder(), "data.yml");

        if (!dataFile.exists()) {
            // 如果数据文件不存在，创建插件文件夹并初始化数据文件
            plugin.getDataFolder().mkdirs();
            try {
                // 尝试从JAR中复制默认的data.yml，如果不存在则创建新的
                InputStream inputStream = plugin.getResource("data.yml");
                if (inputStream != null) {
                    Files.copy(inputStream, dataFile.toPath());
                } else {
                    // 创建空的data.yml
                    dataFile.createNewFile();
                    dataConfig = YamlConfiguration.loadConfiguration(dataFile);
                    // 初始化数据结构
                    dataConfig.set("players", new HashMap<>());
                    dataConfig.set("cdks", new HashMap<>());
                    saveData();
                }
            } catch (IOException e) {
                plugin.getLogger().severe("创建数据文件时出错: " + e.getMessage());
                // 创建空的配置对象避免NPE
                dataConfig = new YamlConfiguration();
                dataConfig.set("players", new HashMap<>());
                dataConfig.set("cdks", new HashMap<>());
                return;
            }
        }

        // 加载数据文件
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        // 确保数据结构存在 - 只在缺失时添加，不覆盖现有数据
        boolean needsSave = false;
        if (!dataConfig.contains("players")) {
            dataConfig.set("players", new HashMap<>());
            needsSave = true;
        }
        if (!dataConfig.contains("cdks")) {
            dataConfig.set("cdks", new HashMap<>());
            needsSave = true;
        }

        // 只在添加了新结构时才保存，避免覆盖现有数据
        if (needsSave) {
            saveData();
            plugin.getLogger().info("数据文件结构已初始化");
        }
        
        plugin.getLogger().info("数据文件加载完成，当前有 " + getAllCDKs().size() + " 个CDK");
    }

    /**
     * 重载数据文件
     */
    public void reloadData() {
        if (dataFile == null) {
            dataFile = new File(plugin.getDataFolder(), "data.yml");
        }

        if (dataFile.exists()) {
            // 重新从文件加载配置
            dataConfig = YamlConfiguration.loadConfiguration(dataFile);
            plugin.getLogger().info("数据文件重载完成，当前有 " + getAllCDKs().size() + " 个CDK");
        } else {
            // 如果文件不存在，重新加载
            loadData();
        }
    }

    public void saveData() {
        if (dataConfig == null || dataFile == null) {
            plugin.getLogger().warning("尝试保存数据但配置为空，跳过保存");
            return;
        }

        try {
            // 在保存前创建备份
            if (dataFile.exists() && dataFile.length() > 0) {
                File backupFile = new File(plugin.getDataFolder(), "data.yml.backup");
                try {
                    Files.copy(dataFile.toPath(), backupFile.toPath(), 
                              java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    plugin.getLogger().fine("数据备份已创建");
                } catch (IOException e) {
                    plugin.getLogger().warning("创建数据备份失败: " + e.getMessage());
                }
            }
            
            dataConfig.save(dataFile);
            plugin.getLogger().info("数据保存成功");
        } catch (IOException e) {
            plugin.getLogger().severe("保存数据文件时出错: " + e.getMessage());
            
            // 尝试从备份恢复
            File backupFile = new File(plugin.getDataFolder(), "data.yml.backup");
            if (backupFile.exists()) {
                plugin.getLogger().warning("尝试从备份恢复数据...");
                try {
                    Files.copy(backupFile.toPath(), dataFile.toPath(), 
                              java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    dataConfig = YamlConfiguration.loadConfiguration(dataFile);
                    plugin.getLogger().info("数据已从备份恢复");
                } catch (IOException ex) {
                    plugin.getLogger().severe("从备份恢复失败: " + ex.getMessage());
                }
            }
        }
    }

    // 玩家数据方法
    public boolean hasClaimedStarterKit(UUID playerId, String kitName) {
        if (dataConfig == null) return false;
        return dataConfig.getStringList("players." + playerId + ".claimed-kits").contains(kitName);
    }

    public void setClaimedStarterKit(UUID playerId, String kitName) {
        if (dataConfig == null) return;

        List<String> claimedKits = new ArrayList<>(
                dataConfig.getStringList("players." + playerId + ".claimed-kits")
        );
        if (!claimedKits.contains(kitName)) {
            claimedKits.add(kitName);
            dataConfig.set("players." + playerId + ".claimed-kits", claimedKits);
            saveData();
        }
    }

    // CDK数据方法
    public boolean addCDK(String code, String kitName, int uses) {
        if (dataConfig == null) return false;

        // 再次检查CDK是否已存在（双重保险）
        if (dataConfig.contains("cdks." + code)) {
            plugin.getLogger().warning("尝试添加已存在的CDK: " + code);
            return false;
        }

        try {
            // 存储小写的礼包名称，确保一致性
            String lowerKitName = kitName.toLowerCase();
            dataConfig.set("cdks." + code + ".kit", lowerKitName);
            dataConfig.set("cdks." + code + ".max-uses", uses);
            dataConfig.set("cdks." + code + ".used", 0);
            dataConfig.set("cdks." + code + ".created", System.currentTimeMillis());
            saveData();
            plugin.getLogger().info("成功添加CDK: " + code + " -> " + lowerKitName + " (使用次数: " + uses + ")");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("添加CDK时出错: " + e.getMessage());
            return false;
        }
    }

    public boolean isValidCDK(String code) {
        if (dataConfig == null) return false;
        return dataConfig.contains("cdks." + code);
    }

    public String getCDKKit(String code) {
        if (dataConfig == null) return null;
        return dataConfig.getString("cdks." + code + ".kit");
    }

    public int getCDKUses(String code) {
        if (dataConfig == null) return 0;
        return dataConfig.getInt("cdks." + code + ".used", 0);
    }

    public int getCDKMaxUses(String code) {
        if (dataConfig == null) return 0;
        return dataConfig.getInt("cdks." + code + ".max-uses", 0);
    }

    public void incrementCDKUses(String code) {
        if (dataConfig == null) return;

        int used = getCDKUses(code) + 1;
        dataConfig.set("cdks." + code + ".used", used);

        // 立即保存数据
        saveData();

        plugin.getLogger().info("CDK " + code + " 被使用，当前使用次数: " + used + "/" + getCDKMaxUses(code));
    }

    public Set<String> getAllCDKs() {
        if (dataConfig == null || !dataConfig.contains("cdks")) {
            return new HashSet<>();
        }
        return dataConfig.getConfigurationSection("cdks").getKeys(false);
    }

    /**
     * 删除CDK
     * @param code CDK代码
     * @return 是否删除成功
     */
    public boolean removeCDK(String code) {
        if (dataConfig == null || !dataConfig.contains("cdks." + code)) {
            return false;
        }

        dataConfig.set("cdks." + code, null);
        saveData();
        plugin.getLogger().info("已删除CDK: " + code);
        return true;
    }

    /**
     * 获取CDK创建时间
     * @param code CDK代码
     * @return 创建时间戳
     */
    public long getCDKCreateTime(String code) {
        if (dataConfig == null) return 0;
        return dataConfig.getLong("cdks." + code + ".created", 0);
    }

    /**
     * 获取所有已使用完的CDK（used >= max-uses）
     * @return 已使用完的CDK集合
     */
    public Set<String> getUsedUpCDKs() {
        Set<String> usedUpCDKs = new HashSet<>();
        if (dataConfig == null || !dataConfig.contains("cdks")) {
            return usedUpCDKs;
        }

        Set<String> allCDKs = getAllCDKs();
        for (String code : allCDKs) {
            int used = getCDKUses(code);
            int maxUses = getCDKMaxUses(code);
            if (maxUses > 0 && used >= maxUses) {
                usedUpCDKs.add(code);
            }
        }

        return usedUpCDKs;
    }

    /**
     * 清除所有已使用完的CDK
     * @return 被清除的CDK数量
     */
    public int cleanUsedUpCDKs() {
        Set<String> usedUpCDKs = getUsedUpCDKs();
        int count = 0;

        for (String code : usedUpCDKs) {
            if (removeCDK(code)) {
                count++;
            }
        }

        plugin.getLogger().info("已清除 " + count + " 个已使用完的CDK");
        return count;
    }

    /**
     * 获取CDK统计信息
     * @return 包含CDK统计信息的Map
     */
    public Map<String, Object> getCDKStatistics() {
        Map<String, Object> stats = new HashMap<>();
        if (dataConfig == null || !dataConfig.contains("cdks")) {
            stats.put("total", 0);
            stats.put("used_up", 0);
            stats.put("available", 0);
            return stats;
        }

        Set<String> allCDKs = getAllCDKs();
        int total = allCDKs.size();
        int usedUp = 0;
        int available = 0;

        for (String code : allCDKs) {
            int used = getCDKUses(code);
            int maxUses = getCDKMaxUses(code);
            if (maxUses > 0 && used >= maxUses) {
                usedUp++;
            } else {
                available++;
            }
        }

        stats.put("total", total);
        stats.put("used_up", usedUp);
        stats.put("available", available);

        return stats;
    }

    public FileConfiguration getDataConfig() {
        return dataConfig;
    }

    /**
     * 创建手动备份
     * @return 是否成功
     */
    public boolean createManualBackup() {
        if (dataFile == null || !dataFile.exists()) {
            plugin.getLogger().warning("数据文件不存在，无法创建备份");
            return false;
        }

        try {
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            File backupFile = new File(plugin.getDataFolder(), "data_backup_" + timestamp + ".yml");
            Files.copy(dataFile.toPath(), backupFile.toPath());
            plugin.getLogger().info("手动备份已创建: " + backupFile.getName());
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe("创建手动备份失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 从备份恢复数据
     * @return 是否成功
     */
    public boolean restoreFromBackup() {
        File backupFile = new File(plugin.getDataFolder(), "data.yml.backup");
        
        if (!backupFile.exists()) {
            plugin.getLogger().warning("备份文件不存在");
            return false;
        }

        try {
            // 先备份当前文件
            if (dataFile.exists()) {
                String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
                File oldFile = new File(plugin.getDataFolder(), "data_before_restore_" + timestamp + ".yml");
                Files.copy(dataFile.toPath(), oldFile.toPath());
                plugin.getLogger().info("当前数据已备份为: " + oldFile.getName());
            }

            // 从备份恢复
            Files.copy(backupFile.toPath(), dataFile.toPath(), 
                      java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            // 重新加载数据
            dataConfig = YamlConfiguration.loadConfiguration(dataFile);
            
            plugin.getLogger().info("数据已从备份恢复");
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe("从备份恢复失败: " + e.getMessage());
            return false;
        }
    }
}