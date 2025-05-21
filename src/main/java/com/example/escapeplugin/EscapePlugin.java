package com.example.escapeplugin;

import com.example.escapeplugin.commands.TraderEditorCommand;
import com.example.escapeplugin.chests.ChestManager;
import com.example.escapeplugin.traders.TraderManager;
import com.example.escapeplugin.arena.ArenaManager;
import com.example.escapeplugin.commands.EscapeCommand;
import com.example.escapeplugin.commands.LootEditorCommand;
import com.example.escapeplugin.listeners.*;
import com.example.escapeplugin.loot.*;
import com.example.escapeplugin.notifications.NotificationService;
import com.example.escapeplugin.quests.*;
import org.bukkit.plugin.java.JavaPlugin;

public class EscapePlugin extends JavaPlugin 
{
    private static EscapePlugin instance;
    private ArenaManager arenaManager;
    private QuestManager questManager;
    private LootManager lootManager;
    private TraderManager traderManager;
    private QuestLoader questLoader;
    private ChestManager chestManager;
    private LootConfiguration lootConfiguration;
    private LootGenerator lootGenerator;
    private NotificationService notificationService;

    @Override
    public void onEnable() 
    {
        saveDefaultConfig();
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        
        // Set default config values if not present
        getConfig().addDefault("chest-refill.interval", 300);
        getConfig().addDefault("chest-refill.auto-refill", true);
        getConfig().options().copyDefaults(true);
        saveConfig();

        instance = this;
        arenaManager = new ArenaManager();
        questManager = new QuestManager();
        
        // Initialize new loot system
        lootConfiguration = new LootConfiguration();
        lootGenerator = new LootGenerator(lootConfiguration);
        lootManager = new LootManager(lootConfiguration);
        
        traderManager = new TraderManager();
        traderManager.loadTraders();
        
        notificationService = new NotificationService();
        chestManager = new ChestManager(this, arenaManager, lootGenerator, notificationService);
        
        TraderEditorCommand traderEditorCommand = new TraderEditorCommand();
        getCommand("tradeditor").setExecutor(traderEditorCommand);
        getServer().getPluginManager().registerEvents(traderEditorCommand, this);

        questLoader = new QuestLoader(this);
        questLoader.registerQuests();

        getCommand("es").setExecutor(new EscapeCommand());
        getCommand("looteditor").setExecutor(new LootEditorCommand(lootManager));
        getCommand("refillchests").setExecutor((sender, command, label, args) -> {
            if (!sender.hasPermission("escape.refill")) {
                sender.sendMessage("§cУ вас нет прав на эту команду!");
                return true;
            }
            chestManager.refillAllChests();
            return true;
        });
        getServer().getPluginManager().registerEvents(new ArenaGameplayListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(), this);
        getServer().getPluginManager().registerEvents(new ItemDropListener(), this);

        chestManager.scheduleAutoRefill();
        getLogger().info("Плагин Escape успешно загружен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Плагин Escape выгружен.");
    }

    public static EscapePlugin getInstance() {
        return instance;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

    public LootManager getLootManager() {
        return lootManager;
    }

    public TraderManager getTraderManager() {
        return traderManager;
    }

    public QuestLoader getQuestLoader() {
        return questLoader;
    }

    public ChestManager getChestManager() {
        return chestManager;
    }
}
