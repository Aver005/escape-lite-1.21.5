package com.example.escapeplugin;

import org.bukkit.plugin.java.JavaPlugin;

import com.example.escapeplugin.managers.SetupTools;

public class EscapePlugin extends JavaPlugin
{
    private static EscapePlugin instance;

    @Override
    public void onEnable() 
    {
        instance = this;

        getServer().getPluginManager().registerEvents(new SetupTools(), instance);
    }

    @Override
    public void onDisable() 
    {
        
    }

    public static EscapePlugin getInstance() { return instance; }
}