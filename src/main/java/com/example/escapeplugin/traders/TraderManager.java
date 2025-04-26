package com.example.escapeplugin.traders;

import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TraderManager {
    private Map<String, Trader> traders = new HashMap<>();
    private Random random = new Random();
    private FileConfiguration config;
    private File configFile;

    public TraderManager(JavaPlugin plugin) {
        configFile = new File(plugin.getDataFolder(), "traders.yml");
        if (!configFile.exists()) {
            plugin.saveResource("traders.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        loadTraders();
    }

    public void loadTraders() {
        traders.clear();
        ConfigurationSection tradersSection = config.getConfigurationSection("traders");
        if (tradersSection != null) {
            for (String key : tradersSection.getKeys(false)) {
                ConfigurationSection traderSection = tradersSection.getConfigurationSection(key);
                List<ItemStack> items = new ArrayList<>();
                if (traderSection.isList("items")) {
                    items = traderSection.getList("items").stream()
                        .filter(obj -> obj instanceof ItemStack)
                        .map(obj -> (ItemStack) obj)
                        .collect(Collectors.toList());
                } else if (traderSection.isConfigurationSection("items")) {
                    ConfigurationSection itemsSection = traderSection.getConfigurationSection("items");
                    for (String itemKey : itemsSection.getKeys(false)) {
                        items.add(itemsSection.getItemStack(itemKey));
                    }
                }
                
                traders.put(key, new Trader(
                    key,
                    traderSection.getString("name", "Торговец"),
                    items
                ));
            }
        }
    }

    public void saveTraders() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Villager spawnTrader(Location location, String traderType) {
        if (traders.isEmpty()) return null;
        
        Trader trader = traders.get(traderType);
        if (trader == null) {
            trader = traders.values().stream()
                .skip(random.nextInt(traders.size()))
                .findFirst()
                .orElse(null);
            if (trader == null) return null;
        }
        
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.setCustomName("§e" + trader.getName());
        villager.setProfession(Profession.NITWIT);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setRecipes(trader.getRecipes());
        return villager;
    }

    public class Trader {
        private String id;
        private String name;
        private List<MerchantRecipe> recipes;

        public Trader(String id, String name, List<ItemStack> items) {
            this.id = id;
            this.name = name;
            this.recipes = new ArrayList<>();
            for (ItemStack item : items) {
                MerchantRecipe recipe = new MerchantRecipe(item, 0, 999, false);
                // Default price is 1 emerald
                recipe.addIngredient(new ItemStack(Material.EMERALD, 1));
                recipes.add(recipe);
            }
        }

        public void addItem(ItemStack item, int price) {
            MerchantRecipe recipe = new MerchantRecipe(item, 0, 999, false);
            recipe.addIngredient(new ItemStack(Material.EMERALD, price));
            recipes.add(recipe);
        }

        public void removeItem(int index) {
            if (index >= 0 && index < recipes.size()) {
                recipes.remove(index);
            }
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public List<MerchantRecipe> getRecipes() { return recipes; }
    }

    public Map<String, Trader> getTraders() {
        return traders;
    }

    public String getTraderName(String traderId) {
        Trader trader = traders.get(traderId);
        return trader != null ? trader.getName() : "Торговец";
    }

    public List<ItemStack> getTraderItems(String traderId) {
        Trader trader = traders.get(traderId);
        if (trader == null) return new ArrayList<>();
        
        List<ItemStack> items = new ArrayList<>();
        for (MerchantRecipe recipe : trader.getRecipes()) {
            items.add(recipe.getResult());
        }
        return items;
    }
}
