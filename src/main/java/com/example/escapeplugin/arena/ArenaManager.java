package com.example.escapeplugin.arena;

import com.example.escapeplugin.EscapePlugin;
import com.example.escapeplugin.loot.LootManager;
import com.example.escapeplugin.traders.TraderManager;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.IOException;

public class ArenaManager
{
    private final EscapePlugin plugin;
    private final Map<String, Arena> arenas = new HashMap<>();
    private final Map<Player, Arena> playerArenaCache = new HashMap<>();
    private File configFile;
    private FileConfiguration config;

    public ArenaManager(EscapePlugin plugin)
    {
        this.plugin = plugin;
        loadConfig();
        loadArenas();
    }

    private void loadConfig()
    {
        configFile = new File(plugin.getDataFolder(), "arenas.yml");
        if (!configFile.exists()) {
            plugin.saveResource("arenas.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    private void saveConfig()
    {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Ошибка сохранения arenas.yml: " + e.getMessage());
        }
    }

    public Arena createArena(String name)
    {
        Arena arena = new Arena(name, this);
        arenas.put(name, arena);
        saveArenaToConfig(arena);
        return arena;
    }

    public void removeArena(String name)
    {
        if (!arenas.containsKey(name)) return;
        arenas.remove(name);
    }

    private void saveArenaToConfig(Arena arena)
    {
        String path = "arenas." + arena.getName() + ".";
        config.set(path + "spawns", arena.getPlayerSpawns());
        config.set(path + "chests", arena.getChestLocations());
        config.set(path + "levers", arena.getLeverLocations());
        
        // Сохраняем торговцев с их типами
        List<Map<String, Object>> tradersData = new ArrayList<>();
        for (TraderLocation trader : arena.getTraderLocations()) {
            Map<String, Object> data = new HashMap<>();
            data.put("location", trader.getLocation());
            data.put("type", trader.getTraderType());
            tradersData.add(data);
        }
        config.set(path + "traders", tradersData);
        saveConfig();
    }

    private void loadArenas()
    {
        if (config.getConfigurationSection("arenas") == null) return;
        for (String name : config.getConfigurationSection("arenas").getKeys(false))
        {
            Arena arena = new Arena(name, this);
            // Загрузка остальных локаций (сундуки, рычаги, торговцы)
            arenas.put(name, arena);
        }
    }

    public Arena getArena(String name) {
        return arenas.get(name);
    }

    public void spawnChests(Arena arena)
    {
        LootManager lootManager = this.plugin.getLootManager();
        for (ChestLocation chestLoc : arena.getChestLocations()) {
            if (chestLoc.getLocation().getBlock().getType() == Material.CHEST) {
                Chest chest = (Chest) chestLoc.getLocation().getBlock().getState();
                lootManager.fillChest(chest.getBlockInventory(), chestLoc.getLootCategory());
            }
        }
    }

    public void setupPlayers(Arena arena) {
        // Выдаем спавн-блоки всем игрокам
        for (Player player : arena.getPlayers()) {
            ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
            // Даем игроку спавн-блок
            player.getInventory().addItem(new ItemStack(Material.BEDROCK, 1));
            player.sendMessage("§aВы получили спавн-блок! Установите его для точки возрождения.");
        }
    }

    public void spawnTraders(Arena arena)
    {
        TraderManager traderManager = this.plugin.getTraderManager();
        traderManager.loadTraders();
        
        for (TraderLocation traderLoc : arena.getTraderLocations()) {
            Villager trader = traderManager.spawnTrader(traderLoc.getLocation(), traderLoc.getTraderType());
            if (trader != null) {
                arena.addSpawnedTrader(trader);
            }
        }
    }

    public void removeTraders(Arena arena)
    {
        arena.clearTraders();
    }

    public Arena getPlayerArena(Player player)
    {
        return playerArenaCache.computeIfAbsent(player, p -> {
            for (Arena arena : arenas.values()) {
                if (arena.isPlaying(p)) return arena;
            }
            return null;
        });
    }
}
