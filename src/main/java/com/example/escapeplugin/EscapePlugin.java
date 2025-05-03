package com.example.escapeplugin;

import org.bukkit.plugin.java.JavaPlugin;

import com.example.escapeplugin.commands.EscapeCommand;
import com.example.escapeplugin.gui.ArenasGUI;
import com.example.escapeplugin.gui.TraderSelectionGUI;
import com.example.escapeplugin.listeners.ArenaListeners;
import com.example.escapeplugin.managers.ArenaStorage;
import com.example.escapeplugin.managers.LootManager;
import com.example.escapeplugin.managers.SetupTools;

public class EscapePlugin extends JavaPlugin
{
    private static EscapePlugin instance;
    private LootManager lootManager;

    @Override
    public void onEnable()
    {
        instance = this;
        this.lootManager = new LootManager(this);

        getServer().getPluginManager().registerEvents(new SetupTools(), instance);
        getServer().getPluginManager().registerEvents(new TraderSelectionGUI(), instance);
        getServer().getPluginManager().registerEvents(new ArenasGUI(), instance);
        getServer().getPluginManager().registerEvents(new ArenaListeners(), instance);
        getCommand("es").setExecutor(new EscapeCommand());

        getLogger().info("Escape enabled");
        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        ArenaStorage.load();
    }

    public LootManager getLootManager() {
        return lootManager;
    }

    @Override
    public void onDisable() 
    {
        ArenaStorage.save();
    }

    public static EscapePlugin getInstance() { return instance; }
}