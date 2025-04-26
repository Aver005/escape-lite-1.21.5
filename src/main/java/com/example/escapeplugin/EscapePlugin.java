package com.example.escapeplugin;

import com.example.escapeplugin.commands.TraderEditorCommand;
import com.example.escapeplugin.traders.TraderManager;

import com.example.escapeplugin.arena.ArenaManager;
import com.example.escapeplugin.commands.EscapeCommand;
import com.example.escapeplugin.commands.LootEditorCommand;
import com.example.escapeplugin.listeners.BlockBreakListener;
import com.example.escapeplugin.listeners.ItemDropListener;
import com.example.escapeplugin.listeners.PlayerRespawnListener;
import com.example.escapeplugin.loot.LootManager;
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

    private TraderEditorCommand traderEditorCommand;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (!getDataFolder().exists()) getDataFolder().mkdir();

        instance = this;
        arenaManager = new ArenaManager();
        questManager = new QuestManager();
        lootManager = new LootManager();
        traderManager = new TraderManager();
        traderManager.loadTraders();
        
        traderEditorCommand = new TraderEditorCommand();
        getCommand("tradeditor").setExecutor(traderEditorCommand);
        getServer().getPluginManager().registerEvents(traderEditorCommand, this);

        questLoader = new QuestLoader(this);
        questLoader.registerQuests();

        getCommand("es").setExecutor(new EscapeCommand());
        getCommand("looteditor").setExecutor(new LootEditorCommand());
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(), this);
        getServer().getPluginManager().registerEvents(new ItemDropListener(), this);

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
}
