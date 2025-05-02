package com.example.escapeplugin.managers;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.example.escapeplugin.EscapePlugin;
import com.example.escapeplugin.entities.Arena;
import com.example.escapeplugin.enums.TraderType;

public class ArenaStorage
{
    private static HashMap<String, Arena> arenas = new HashMap<>();
    private static File folder;

    static 
    {
        File folder = new File(EscapePlugin.getInstance().getDataFolder() + "/arenas");
        if (!folder.exists()) folder.mkdirs();
    }

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

    public static void load()
    {
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;
        
        for (File file : files)
        {
            try
            {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                String id = file.getName().replace(".yml", "");
                
                Arena arena = new Arena(id);
                arena.setName(config.getString("name", id));
                arena.setMaxPlayers(config.getInt("maxPlayers", 16));
                arena.setMinPlayers(config.getInt("minPlayers", 2));
                
                List<Location> prisonerSpawns = (List<Location>) config.getList("prisonerSpawns");
                if (prisonerSpawns != null) prisonerSpawns.forEach(arena::addPrisonerSpawn);
                
                List<Location> stashSpawns = (List<Location>) config.getList("stashSpawns");
                if (stashSpawns != null) stashSpawns.forEach(arena::addChestSpawn);
                
                ConfigurationSection leverSection = config.getConfigurationSection("leverSpawns");
                if (leverSection != null) 
                {
                    for (String key : leverSection.getKeys(false)) 
                    {
                        Location loc = (Location) leverSection.get(key);
                        arena.addLeverSpawn(key, loc);
                    }
                }
                
                ConfigurationSection traderSection = config.getConfigurationSection("traderSpawns");
                if (traderSection != null) 
                {
                    for (String typeName : traderSection.getKeys(false)) 
                    {
                        TraderType type = TraderType.valueOf(typeName);
                        List<Location> spawns = (List<Location>) traderSection.getList(typeName);
                        if (spawns != null) 
                        {
                            spawns.forEach(loc -> 
                            {
                                if (!arena.getTraderSpawns().containsKey(type)) 
                                    arena.getTraderSpawns().put(type, new ArrayList<>());
                                
                                arena.getTraderSpawns().get(type).add(loc);
                            });
                        }
                    }
                }
                
                arenas.put(id, arena);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void save()
    {
        for (Arena arena : arenas.values())
        {
            try
            {
                YamlConfiguration config = new YamlConfiguration();
                
                config.set("name", arena.getName());
                config.set("maxPlayers", arena.getMaxPlayers());
                config.set("minPlayers", arena.getMinPlayers());
                config.set("prisonerSpawns", arena.getPrisonerSpawns());
                config.set("stashSpawns", arena.getStashSpawns());
                
                ConfigurationSection leverSection = config.createSection("leverSpawns");
                arena.getLeverSpawns().forEach(leverSection::set);
                
                ConfigurationSection traderSection = config.createSection("traderSpawns");
                arena.getTraderSpawns().forEach((type, spawns) -> {
                    traderSection.set(type.name(), spawns);
                });
                
                File file = new File(folder, arena.getID() + ".yml");
                config.save(file);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
