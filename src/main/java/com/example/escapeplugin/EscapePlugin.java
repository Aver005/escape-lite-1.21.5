package com.example.escapeplugin;

import com.example.escapeplugin.arena.ArenaManager;
import com.example.escapeplugin.commands.EscapeCommand;
import com.example.escapeplugin.listeners.BlockBreakListener;
import com.example.escapeplugin.loot.LootManager;
import com.example.escapeplugin.quests.*;
import com.example.escapeplugin.traders.TraderManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class EscapePlugin extends JavaPlugin
{
    private static EscapePlugin instance;
    private ArenaManager arenaManager;
    private QuestManager questManager;
    private LootManager lootManager;
    private TraderManager traderManager;
    private QuestLoader questLoader;

    @Override
    public void onEnable()
    {
        // Создаем папку плагина, если её нет
        saveDefaultConfig();
        if (!getDataFolder().exists()) getDataFolder().mkdir();


        // Инициализация менеджера арен
        instance = this;
        arenaManager = new ArenaManager(this);
        questManager = new QuestManager();
        lootManager = new LootManager();
        traderManager = new TraderManager();

        questLoader = new QuestLoader(this);
        questLoader.registerQuests();

        // Регистрация команд и событий
        getCommand("es").setExecutor(new EscapeCommand(arenaManager, questManager));
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);

        getLogger().info("Плагин Escape успешно загружен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Плагин Escape выгружен.");
    }

    public static EscapePlugin getInstance()
    {
        return instance;
    }
}
