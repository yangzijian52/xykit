package com.xykit.managers;

import com.xykit.XyKitPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

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

        // 确保数据结构存在
        if (!dataConfig.contains("players")) {
            dataConfig.set("players", new HashMap<>());
        }
        if (!dataConfig.contains("cdks")) {
            dataConfig.set("cdks", new HashMap<>());
        }

        saveData(); // 保存以确保结构被写入
        plugin.getLogger().info("数据文件加载完成，当前有 " + getAllCDKs().size() + " 个CDK");
    }

    public void saveData() {
        if (dataConfig == null || dataFile == null) {
            plugin.getLogger().warning("尝试保存数据但配置为空，跳过保存");
            return;
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("保存数据文件时出错: " + e.getMessage());
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
            dataConfig.set("cdks." + code + ".kit", kitName);
            dataConfig.set("cdks." + code + ".max-uses", uses);
            dataConfig.set("cdks." + code + ".used", 0);
            dataConfig.set("cdks." + code + ".created", System.currentTimeMillis());
            saveData();
            plugin.getLogger().info("成功添加CDK: " + code + " -> " + kitName + " (使用次数: " + uses + ")");
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

    public FileConfiguration getDataConfig() {
        return dataConfig;
    }
}