package com.example.escapeplugin;

import org.bukkit.plugin.java.JavaPlugin;

import com.example.escapeplugin.commands.EscapeCommand;
import com.example.escapeplugin.gui.ArenasGUI;
import com.example.escapeplugin.gui.TraderSelectionGUI;
import com.example.escapeplugin.managers.ArenaStorage;
import com.example.escapeplugin.managers.SetupTools;

public class EscapePlugin extends JavaPlugin
{
    private static EscapePlugin instance;

    @Override
    public void onEnable() 
    {
        instance = this;

        getServer().getPluginManager().registerEvents(new SetupTools(), instance);
        getServer().getPluginManager().registerEvents(new TraderSelectionGUI(), instance);
        getServer().getPluginManager().registerEvents(new ArenasGUI(), instance);
        getCommand("es").setExecutor(new EscapeCommand());

        getLogger().info("Escape enabled");
        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        ArenaStorage.load();
    }

    @Override
    public void onDisable() 
    {
        ArenaStorage.save();
    }

    public static EscapePlugin getInstance() { return instance; }
}