package com.example.escapeplugin.managers;

import java.util.HashMap;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import com.example.escapeplugin.EscapePlugin;
import com.example.escapeplugin.entities.Arena;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;

public class ArenaStorage
{
    private static HashMap<String, Arena> arenas = new HashMap<>();
    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(Location.class, new LocationAdapter())
        .setPrettyPrinting()
        .create();
    private static File storageFile;

    static {
        File dataFolder = EscapePlugin.getInstance().getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        storageFile = new File(dataFolder, "arenas.json");
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
        try {
            if (!storageFile.exists()) return;
            
            FileReader reader = new FileReader(storageFile);
            Map<String, String> serializedArenas = gson.fromJson(
                reader,
                new TypeToken<Map<String, String>>(){}.getType()
            );
            reader.close();

            if (serializedArenas != null) {
                serializedArenas.forEach((id, json) -> {
                    Arena arena = gson.fromJson(json, Arena.class);
                    arenas.put(id, arena);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save()
    {
        try {
            Map<String, String> serializedArenas = new HashMap<>();
            arenas.forEach((id, arena) -> {
                String json = gson.toJson(arena);
                serializedArenas.put(id, json);
            });

            FileWriter writer = new FileWriter(storageFile);
            gson.toJson(serializedArenas, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
