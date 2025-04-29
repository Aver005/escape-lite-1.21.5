package com.example.escapeplugin.arena;

import org.bukkit.Location;
import java.util.Optional;

import com.example.escapeplugin.EscapePlugin;
import com.example.escapeplugin.traders.TraderManager;
import org.bukkit.Material;
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

    public ArenaManager()
    {
        this.plugin = EscapePlugin.getInstance();
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
        config.set("arenas." + name, null);
        saveConfig();
    }

    private void saveArenaToConfig(Arena arena)
    {
        String path = "arenas." + arena.getName() + ".";
        config.set(path + "refill_interval", arena.getChestRefillInterval());
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
            String path = "arenas." + name + ".";
            
            // Load refill interval (default 300 seconds)
            int refillInterval = config.getInt(path + "refill_interval", 300);
            arena.setChestRefillInterval(refillInterval);
            
            // Load spawn location
            Location spawn = (Location) config.get(path + "spawn");
            if (spawn != null) {
                arena.addPlayerSpawn(spawn);
            }
            
            // Load chest locations
            List<Map<String, Object>> chests = (List<Map<String, Object>>) config.getList(path + "chests");
            if (chests != null) {
                chests.forEach(chestData -> {
                    Location loc = (Location) chestData.get("location");
                    String category = (String) chestData.get("category");
                    if (loc != null) {
                        if (category != null) {
                            arena.addChestLocation(loc, category);
                        } else {
                            arena.addChestLocation(loc);
                        }
                    }
                });
            }
            
            // Load lever locations
            List<Location> levers = (List<Location>) config.getList(path + "levers");
            if (levers != null) {
                levers.forEach(arena::addLeverLocation);
            }
            
            // Load traders
            List<Map<String, Object>> traders = (List<Map<String, Object>>) config.getList(path + "traders");
            if (traders != null) {
                traders.forEach(traderData -> {
                    Location loc = (Location) traderData.get("location");
                    String type = (String) traderData.get("type");
                    if (loc != null) {
                        if (type != null) {
                            arena.addTraderLocation(loc, type);
                        } else {
                            arena.addTraderLocation(loc);
                        }
                    }
                });
            }
            
            arenas.put(name, arena);
        }
    }

    public Arena getArena(String name) {
        return arenas.get(name);
    }

    public void spawnChests(Arena arena)
    {
        // TODO: Implement chest filling logic once LootManager API is confirmed
        for (ChestLocation chestLoc : arena.getChestLocations()) {
            if (chestLoc.getLocation().getBlock().getType() == Material.CHEST) {
                // Will be implemented after confirming LootManager API
            }
        }
        
        // Start refill timer
        arena.startChestRefillTimer(this);
    }

    public void setupPlayers(Arena arena) 
    {
        // Выдаем спавн-блоки всем игрокам
        for (Player player : arena.getPlayers()) 
        {
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
    public Map<String, Arena> getArenas() {
        return new HashMap<>(arenas);
    }
    
    public void reloadConfig() {
        loadConfig();
        arenas.clear();
        playerArenaCache.clear();
        loadArenas();
    }

    public Optional<Arena> getArenaForChest(Location location) {
        return arenas.values().stream()
            .filter(arena -> arena.getChestLocations().stream()
                .anyMatch(chest -> chest.getLocation().equals(location)))
            .findFirst();
    }

    public void leave(Player player) {
        Arena arena = getPlayerArena(player);
        if (arena == null) return;

        // Remove player from arena
        arena.getPlayers().remove(player);
        
        // Remove from cache
        playerArenaCache.remove(player);
        
        // Restore player state
        ArenaPlayer.getPlayer(player).leave();
        
        player.sendMessage("§aВы успешно вышли с арены. Ваше состояние восстановлено.");
    }
}
