package com.xykit;

import com.xykit.commands.KitCommand;
import com.xykit.managers.ConfigManager;
import com.xykit.managers.DataManager;
import com.xykit.managers.KitManager;
import org.bukkit.plugin.java.JavaPlugin;

public class XyKitPlugin extends JavaPlugin {
    private static XyKitPlugin instance;
    private ConfigManager configManager;
    private DataManager dataManager;
    private KitManager kitManager;

    @Override
    public void onEnable() {
        instance = this;

        // 先创建插件数据文件夹
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        try {
            // 初始化管理器 - 注意顺序很重要
            this.configManager = new ConfigManager(this);
            this.dataManager = new DataManager(this);
            this.kitManager = new KitManager(this);

            // 加载配置 - 严格按照顺序
            configManager.loadConfig();  // 先加载主配置
            dataManager.loadData();      // 再加载数据
            kitManager.loadKits();       // 最后加载礼包

            // 注册命令
            getCommand("kit").setExecutor(new KitCommand(this));
            getCommand("cdk").setExecutor(new KitCommand(this));

            getLogger().info("XyKit 礼包插件已启用!");

        } catch (Exception e) {
            getLogger().severe("启用插件时发生严重错误: " + e.getMessage());
            e.printStackTrace();
            // 禁用插件以避免进一步错误
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (dataManager != null) {
            dataManager.saveData();
        }
        getLogger().info("XyKit 礼包插件已禁用!");
    }

    public static XyKitPlugin getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }
}