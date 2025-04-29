package com.example.escapeplugin.loot;

import org.bukkit.configuration.file.YamlConfiguration;
import com.example.escapeplugin.EscapePlugin;

import java.io.File;
import java.io.IOException;

public class LootConfiguration {
    private final File configFile;
    private YamlConfiguration config;

    public LootConfiguration() {
        File dataFolder = EscapePlugin.getInstance().getDataFolder();
        this.configFile = new File(dataFolder, "loot.yml");
        loadConfig();
    }

    public void loadConfig() {
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}