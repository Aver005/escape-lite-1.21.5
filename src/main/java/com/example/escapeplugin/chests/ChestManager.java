package com.example.escapeplugin.chests;

import com.example.escapeplugin.arena.ArenaManager;
import com.example.escapeplugin.loot.LootGenerator;
import com.example.escapeplugin.notifications.NotificationService;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ChestManager {
    private final JavaPlugin plugin;
    private final ArenaManager arenaManager;
    private final LootGenerator lootGenerator;
    private final NotificationService notificationService;
    private int refillInterval = 300; // seconds
    private boolean autoRefillEnabled = true;
    
    public ChestManager(JavaPlugin plugin, ArenaManager arenaManager, 
                      LootGenerator lootGenerator, NotificationService notificationService) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
        this.lootGenerator = lootGenerator;
        this.notificationService = notificationService;
        loadConfig();
    }
    
    private void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        refillInterval = config.getInt("chest-refill.interval", 300);
        autoRefillEnabled = config.getBoolean("chest-refill.auto-refill", true);
    }
    
    public void refillAllChests() {
        arenaManager.getArenas().values().forEach(arena -> {
            arena.getChestLocations().forEach(loc -> {
                if (loc.getLocation().getBlock().getState() instanceof Chest chest) {
                    refillChest(chest);
                }
            });
        });
        
        notificationService.notifyPlayers("§aВсе сундуки были перезаполнены!", "Сундуки перезаполнены");
    }
    
    public void refillChest(Chest chest) {
        Inventory inv = chest.getBlockInventory();
        String category = arenaManager.getArenaForChest(chest.getLocation())
                            .map(arena -> arena.getChestCategory(chest.getLocation()))
                            .orElse("COMMON");
        lootGenerator.fillChest(inv, category);
    }
    
    public void scheduleAutoRefill() {
        if (autoRefillEnabled) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    refillAllChests();
                }
            }.runTaskTimer(plugin, refillInterval * 20L, refillInterval * 20L);
        }
    }
    
    // Getters and setters
    public int getRefillInterval() {
        return refillInterval;
    }
    
    public void setRefillInterval(int seconds) {
        this.refillInterval = seconds;
        plugin.getConfig().set("chest-refill.interval", seconds);
        plugin.saveConfig();
    }
    
    public boolean isAutoRefillEnabled() {
        return autoRefillEnabled;
    }
    
    public void setAutoRefillEnabled(boolean enabled) {
        this.autoRefillEnabled = enabled;
        plugin.getConfig().set("chest-refill.auto-refill", enabled);
        plugin.saveConfig();
    }
}