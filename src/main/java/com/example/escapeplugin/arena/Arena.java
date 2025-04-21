package com.example.escapeplugin.arena;

import com.example.escapeplugin.game.GameTimer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Arena
{
    private final String name;
    private GameTimer gameTimer;
    private int minPlayersToStart = 2;

    private List<Location> playerSpawns = new ArrayList<>();
    private List<Location> chestLocations = new ArrayList<>();
    private List<Location> leverLocations = new ArrayList<>();
    private List<Location> traderLocations = new ArrayList<>();

    private List<Player> activePlayers = new ArrayList<>();
    private List<Location> freeSpawns = new ArrayList<>();

    public Arena(String name)
    {
        this.name = name;
    }

    public void startMatch()
    {
        this.activePlayers.clear();
        gameTimer = new GameTimer(this, 720); // 12 минут
        gameTimer.start();
    }

    public void join(Player p)
    {
        if (activePlayers.contains(p)) return;
        if (freeSpawns.isEmpty()) return;

        ArenaPlayer player = ArenaPlayer.getPlayer(p);
        if (player.isPlaying()) return;

        Location spawn = freeSpawns.getFirst();
        activePlayers.add(p);
        freeSpawns.remove(spawn);
        player.join(this, spawn);
        p.sendMessage("§aВы вошли на арену §6" + name + "§a!");
    }

    public void leave(Player p )
    {
        if (!activePlayers.contains(p)) return;

        ArenaPlayer player = ArenaPlayer.getPlayer(p);
        if (!player.isPlaying()) return;
        if (!Objects.equals(player.getArena().name, this.getName())) return;

        freeSpawns.add(player.getSpawn());
        player.leave();
        p.sendMessage("§aВы вышли с арены.");
    }

    public void broadcast(String message)
    {
        // Проходим по всем игрокам на сервере
        for (Player player : activePlayers)
        {
            // Отправляем сообщение каждому игроку
            player.sendMessage(message);
        }
    }

    public boolean isPlaying(Player p) { return this.activePlayers.contains(p); }

    // Геттеры и сеттеры
    public String getName() { return name; }
    public List<Player> getPlayers() { return activePlayers; }
    public List<Location> getChestLocations() { return chestLocations; }
    public List<Location> getLeverLocations() { return leverLocations; }
    public List<Location> getTraderLocations() { return traderLocations; }
    public List<Location> getPlayerSpawns() { return playerSpawns; }
    public int getMinPlayersToStart() { return minPlayersToStart; }

    public void addPlayerSpawn(Location loc) { playerSpawns.add(loc); }
    public void addChestLocation(Location loc) { chestLocations.add(loc); }
    public void addLeverLocation(Location loc) { leverLocations.add(loc); }
    public void addTraderLocation(Location loc) { traderLocations.add(loc); }
    public void setMinPlayersToStart(int count) { minPlayersToStart = count; }
}
