package com.example.escapeplugin.loot;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class LootManager
{
    private final File configFile;
    private YamlConfiguration config;

    public LootManager(File dataFolder) {
        this.configFile = new File(dataFolder, "loot.yml");
        loadConfig();
    }

    private void loadConfig() {
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

    public void fillChest(Inventory chest, String category) {
        Random random = new Random();
        chest.clear();

        if (!config.contains("categories." + category)) {
            category = "COMMON"; // Default category
        }

        Map<String, Object> items = config.getConfigurationSection("categories." + category + ".items")
                .getValues(false);

        for (Map.Entry<String, Object> entry : items.entrySet()) {
            Material material = Material.getMaterial(entry.getKey());
            if (material == null) continue;

            int amount = config.getInt("categories." + category + ".items." + entry.getKey() + ".amount", 1);
            double chance = config.getDouble("categories." + category + ".items." + entry.getKey() + ".chance", 0.5);

            if (random.nextDouble() < chance) {
                chest.addItem(new ItemStack(material, amount));
            }
        }
    }

    public Set<String> getCategories() {
        if (!config.contains("categories")) {
            return new HashSet<>();
        }
        return config.getConfigurationSection("categories").getKeys(false);
    }

    public void addCategory(String name) {
        config.createSection("categories." + name);
        saveConfig();
    }

    public void removeCategory(String name) {
        config.set("categories." + name, null);
        saveConfig();
    }

    public void addItemToCategory(String category, Material material, int amount, double chance) {
        config.set("categories." + category + ".items." + material.name() + ".amount", amount);
        config.set("categories." + category + ".items." + material.name() + ".chance", chance);
        saveConfig();
    }

    public void removeItemFromCategory(String category, Material material) {
        config.set("categories." + category + ".items." + material.name(), null);
        saveConfig();
    }
}
