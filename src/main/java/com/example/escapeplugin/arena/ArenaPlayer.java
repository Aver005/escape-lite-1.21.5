package com.example.escapeplugin.arena;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ArenaPlayer
{
    private static HashMap<String, ArenaPlayer> playersMap = new HashMap<>();
    public static ArenaPlayer getPlayer(Player p)
    {
        if (p == null) return null;
        String name = p.getName();
        if (playersMap.containsKey(name)) { return playersMap.get(name); }
        ArenaPlayer player = new ArenaPlayer(p);
        playersMap.put(name, player);
        return player;
    }

    private final Player player;
    private Arena activeArena = null;
    private Location activeSpawn = null;
    private Location lastLocation = null;

    public ArenaPlayer(Player p)
    {
        this.player = p;
    }

    public void join(Arena arena, Location spawn)
    {
        activeArena = arena;
        activeSpawn = spawn;
        lastLocation = player.getLocation();
        player.teleport(spawn);
        player.getInventory().clear();
    }

    public void leave()
    {
        activeSpawn = null;
        activeArena = null;
        player.teleport(lastLocation);
    }

    public boolean isPlaying() { return activeArena != null; }
    public Player getPlayer() { return player; }
    public Arena getArena() { return activeArena; }
    public Location getSpawn() { return activeSpawn; }
}
