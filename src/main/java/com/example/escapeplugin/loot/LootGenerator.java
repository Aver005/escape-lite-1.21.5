package com.example.escapeplugin.loot;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LootGenerator 
{
    private final LootConfiguration config;

    public LootGenerator(LootConfiguration config) {
        this.config = config;
    }

    public List<ItemStack> generateLoot(String category, int count) {
        List<ItemStack> loot = new ArrayList<>();
        Random random = new Random();

        if (!config.getConfig().contains("categories." + category)) {
            category = "COMMON"; // Default category
        }

        Map<String, Object> items = config.getConfig().getConfigurationSection("categories." + category + ".items")
                .getValues(false);

        for (int i = 0; i < count; i++) {
            for (Map.Entry<String, Object> entry : items.entrySet()) {
                Material material = Material.getMaterial(entry.getKey());
                if (material == null) continue;

                int amount = config.getConfig().getInt("categories." + category + ".items." + entry.getKey() + ".amount", 1);
                double chance = config.getConfig().getDouble("categories." + category + ".items." + entry.getKey() + ".chance", 0.5);

                if (random.nextDouble() < chance) {
                    loot.add(new ItemStack(material, amount));
                }
            }
        }
        return loot;
    }

    public void fillChest(Inventory chest, String category) {
        chest.clear();
        List<ItemStack> loot = generateLoot(category, 3 + new Random().nextInt(5));
        loot.forEach(chest::addItem);
    }
}