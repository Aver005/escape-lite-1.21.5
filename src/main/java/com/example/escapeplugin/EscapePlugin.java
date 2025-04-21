package com.example.escapeplugin;

import com.example.escapeplugin.arena.ArenaManager;
import com.example.escapeplugin.commands.EscapeCommand;
import com.example.escapeplugin.listeners.BlockBreakListener;
import com.example.escapeplugin.loot.LootManager;
import com.example.escapeplugin.quests.KillQuest;
import com.example.escapeplugin.quests.MiningQuest;
import com.example.escapeplugin.quests.QuestManager;
import com.example.escapeplugin.traders.TraderManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EscapePlugin extends JavaPlugin
{
    private static EscapePlugin instance;
    private ArenaManager arenaManager;
    private QuestManager questManager;
    private LootManager lootManager;
    private TraderManager traderManager;

    @Override
    public void onEnable() {
        // Создаем папку плагина, если её нет
        saveDefaultConfig();

        // Инициализация менеджера арен
        instance = this;
        arenaManager = new ArenaManager(this);
        questManager = new QuestManager();
        lootManager = new LootManager();
        traderManager = new TraderManager();

        // Регистрация команд и событий
        getCommand("es").setExecutor(new EscapeCommand(arenaManager, questManager));
        getServer().getPluginManager().registerEvents(new KillQuest(), this);
        getServer().getPluginManager().registerEvents(new MiningQuest(), this);
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
