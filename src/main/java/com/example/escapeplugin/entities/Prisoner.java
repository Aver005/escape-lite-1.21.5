package com.example.escapeplugin.entities;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Prisoner 
{
    private final Player player;
    private Arena arena;
    private Location spawn;
    private SpawnBlock spawnBlock;

    private Location oldLocation;

    public Prisoner(Player player)
    {
        this.player = player;
    }

    public void setArena(Arena arena, Location spawn)
    {
        if (this.arena != null) this.arena.leave(this);
        this.arena = arena;
        this.spawn = spawn;
    }

    public Player getPlayer() { return player; }
    public Arena getArena() { return arena; }
    public Location getSpawn() { return spawn; }
    public SpawnBlock getSpawnBlock() { return spawnBlock; }
    public boolean isPlaying() { return arena != null && arena.isPlaying(); }

    public void teleportToSpawn()
    {
        if (spawn == null) return;
        player.teleport(spawn);
    }

    public void saveState()
    {
        oldLocation = player.getLocation();
    }

    public void restoreState()
    {
        if (oldLocation == null) return;
        player.teleport(oldLocation);
    }
}
