package com.example.escapeplugin.loot;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LootManager {
    private final LootConfiguration config;

    public LootManager(LootConfiguration config) {
        this.config = config;
    }

    public Set<String> getCategories() {
        if (!config.getConfig().contains("categories")) {
            return new HashSet<>();
        }
        return config.getConfig().getConfigurationSection("categories").getKeys(false);
    }

    public void addCategory(String name) {
        config.getConfig().createSection("categories." + name);
        config.saveConfig();
    }

    public void removeCategory(String name) {
        config.getConfig().set("categories." + name, null);
        config.saveConfig();
    }

    public void addItemToCategory(String category, Material material, int amount, double chance) {
        config.getConfig().set("categories." + category + ".items." + material.name() + ".amount", amount);
        config.getConfig().set("categories." + category + ".items." + material.name() + ".chance", chance);
        config.saveConfig();
    }

    public void removeItemFromCategory(String category, Material material) {
        config.getConfig().set("categories." + category + ".items." + material.name(), null);
        config.saveConfig();
    }

    public void saveItemFromInventory(Inventory blockInventory, String category) {
        for (ItemStack itemStack : blockInventory.getContents()) {
            config.getConfig().set()
        }
    }
}
