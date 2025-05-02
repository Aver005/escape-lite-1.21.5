package com.example.escapeplugin.managers;

import java.util.HashMap;

import org.bukkit.entity.Player;

import com.example.escapeplugin.entities.Prisoner;

public class PrisonerStorage 
{
    private static HashMap<String, Prisoner> prisoners = new HashMap<>();

    public static Prisoner get(Player player)
    {
        String name = player.getName();
        if (prisoners.containsKey(name)) return prisoners.get(name);
        Prisoner prisoner = new Prisoner(player);
        prisoners.put(name, prisoner);
        return prisoner;
    }
}
