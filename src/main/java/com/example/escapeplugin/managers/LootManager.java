package com.example.escapeplugin.managers;

import com.example.escapeplugin.EscapePlugin;
import com.example.escapeplugin.entities.LootItem;
import com.example.escapeplugin.enums.LootRarity;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LootManager 
{
    private final EscapePlugin plugin;
    private final Random random = new Random();
    private final Map<LootRarity, List<LootItem>> lootByRarity = new HashMap<>();
    private File lootFile;
    private YamlConfiguration lootConfig;

    public LootManager(EscapePlugin plugin) 
    {
        this.plugin = plugin;
        loadLootConfig();
    }

    private void loadLootConfig() 
    {
        lootFile = new File(plugin.getDataFolder(), "loot.yml");
        if (!lootFile.exists()) plugin.saveResource("loot.yml", false);
        lootConfig = YamlConfiguration.loadConfiguration(lootFile);
        loadLootItems();
    }

    private void loadLootItems() 
    {
        for (LootRarity rarity : LootRarity.values()) 
        {
            lootByRarity.put(rarity, new ArrayList<>());
        }

        for (String key : lootConfig.getKeys(false)) 
        {
            ItemStack item = lootConfig.getItemStack(key + ".item");
            LootRarity rarity = LootRarity.valueOf(lootConfig.getString(key + ".rarity"));
            double chance = lootConfig.getDouble(key + ".chance");
            lootByRarity.get(rarity).add(new LootItem(item, rarity, chance));
        }
    }

    public void saveLootConfig() throws IOException {
        lootConfig.save(lootFile);
    }

    public void addLootItem(String key, ItemStack item, LootRarity rarity, double chance) {
        lootConfig.set(key + ".item", item);
        lootConfig.set(key + ".rarity", rarity.name());
        lootConfig.set(key + ".chance", chance);
        lootByRarity.get(rarity).add(new LootItem(item, rarity, chance));
    }

    public List<ItemStack> generateLoot() {
        List<ItemStack> loot = new ArrayList<>();
        Map<LootRarity, Integer> counts = new HashMap<>();

        for (LootRarity rarity : LootRarity.values()) {
            counts.put(rarity, 0);
        }

        for (LootRarity rarity : LootRarity.values()) {
            for (LootItem lootItem : lootByRarity.get(rarity)) {
                if (random.nextDouble() < lootItem.getChance() * rarity.getBaseChance() 
                    && counts.get(rarity) < rarity.getMaxPerChest()) {
                    loot.add(lootItem.getItem());
                    counts.put(rarity, counts.get(rarity) + 1);
                }
            }
        }

        return loot;
    }
}