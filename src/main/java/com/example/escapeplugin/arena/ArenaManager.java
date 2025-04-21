package com.example.escapeplugin.arena;

import com.example.escapeplugin.loot.LootManager;
import com.example.escapeplugin.traders.TraderManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ArenaManager {
    private final JavaPlugin plugin;
    private final Map<String, Arena> arenas = new HashMap<>();
    private final Map<Player, Arena> playerArenaCache = new HashMap<>();
    private File configFile;
    private FileConfiguration config;

    public ArenaManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
        loadArenas();
    }

    private void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "arenas.yml");
        if (!configFile.exists()) {
            plugin.saveResource("arenas.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    private void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Ошибка сохранения arenas.yml: " + e.getMessage());
        }
    }

    public void createArena(String name) {
        Arena arena = new Arena(name);
        arenas.put(name, arena);
        saveArenaToConfig(arena);
    }

    private void saveArenaToConfig(Arena arena) {
        String path = "arenas." + arena.getName() + ".";
        config.set(path + "spawns", arena.getPlayerSpawns());
        config.set(path + "chests", arena.getChestLocations());
        config.set(path + "levers", arena.getLeverLocations());
        config.set(path + "traders", arena.getTraderLocations());
        saveConfig();
    }

    private void loadArenas() {
        if (config.getConfigurationSection("arenas") == null) return;
        for (String name : config.getConfigurationSection("arenas").getKeys(false))
        {
            Arena arena = new Arena(name);
            // Загрузка остальных локаций (сундуки, рычаги, торговцы)
            arenas.put(name, arena);
        }
    }

    public Arena getArena(String name) {
        return arenas.get(name);
    }

    public void spawnChests(Arena arena) {
        LootManager lootManager = new LootManager();
        for (Location loc : arena.getChestLocations()) {
            if (loc.getBlock().getType() == Material.CHEST) {
                Chest chest = (Chest) loc.getBlock().getState();
                lootManager.fillChest(chest.getBlockInventory());
            }
        }
    }

    public void spawnTraders(Arena arena) {
        TraderManager traderManager = new TraderManager();
        for (Location loc : arena.getTraderLocations()) {
            traderManager.spawnTrader(loc, "оружие"); // Можно добавить разные типы
        }
    }

    public Arena getPlayerArena(Player player) {
        return playerArenaCache.computeIfAbsent(player, p -> {
            for (Arena arena : arenas.values()) {
                if (arena.isPlaying(p)) return arena;
            }
            return null;
        });
    }
}
