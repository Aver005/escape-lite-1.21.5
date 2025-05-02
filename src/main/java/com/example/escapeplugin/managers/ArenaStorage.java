package com.example.escapeplugin.managers;

import java.util.HashMap;

import com.example.escapeplugin.entities.Arena;

public class ArenaStorage 
{
    private static HashMap<String, Arena> arenas = new HashMap<>();


    public static void add(Arena arena) { arenas.put(arena.getID(), arena); }
    public static void remove(Arena arena) { arenas.remove(arena.getID()); }
    public static Arena get(String name) { return arenas.getOrDefault(name, null); }

    public static Arena create(String ID)
    {
        if (arenas.containsKey(ID)) { return get(ID); }
        Arena arena = new Arena(ID);
        add(arena);
        return arena;
    }
}
