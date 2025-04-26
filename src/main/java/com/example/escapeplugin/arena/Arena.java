package com.example.escapeplugin.arena;

import com.example.escapeplugin.EscapePlugin;
import com.example.escapeplugin.game.GameTimer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Item;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Arena
{
    private final String name;
    private final ArenaManager arenaManager;
    private GameTimer gameTimer;
    private int minPlayersToStart = 2;
    private int matchDuration = 720;
    private int chestRefillInterval = 300; // 5 минут по умолчанию
    private BukkitRunnable chestRefillTask;

    private List<Location> playerSpawns = new ArrayList<>();
    private List<ChestLocation> chestLocations = new ArrayList<>();
    private List<Location> leverLocations = new ArrayList<>();
    private List<TraderLocation> traderLocations = new ArrayList<>();
    private List<Villager> spawnedTraders = new ArrayList<>();
    private List<Item> droppedItems = new ArrayList<>();

    private List<Location> freeSpawns = new ArrayList<>();
    private List<Player> activePlayers = new ArrayList<>();
    private HashMap<Player, Location> playersBlock = new HashMap<>();

    public Arena(String name, ArenaManager arenaManager)
    {
        this.name = name;
        this.arenaManager = arenaManager;
    }

    public void startMatch()
    {
        playersBlock.clear();
        clearTraders();
        gameTimer = new GameTimer(this, matchDuration); // 12 минут
        gameTimer.start();
        
        // Вызываем setupPlayers для выдачи спавн-блоков
        arenaManager.setupPlayers(this);
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

        // Автоматический старт игры при наборе минимального количества игроков
        if (activePlayers.size() >= minPlayersToStart && gameTimer == null) {
            broadcast("§aДостигнуто минимальное количество игроков! Игра начинается!");
            startMatch();
        }
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
    public List<ChestLocation> getChestLocations() { return chestLocations; }
    public List<Location> getLeverLocations() { return leverLocations; }
    public List<TraderLocation> getTraderLocations() { return traderLocations; }
    public List<Location> getPlayerSpawns() { return playerSpawns; }
    public int getMinPlayersToStart() { return minPlayersToStart; }
    public int getMatchDuration() { return matchDuration; }

    public void addPlayerSpawn(Location loc) { playerSpawns.add(loc); }
    public void addChestLocation(Location loc) { chestLocations.add(new ChestLocation(loc, "COMMON")); }
    public void addChestLocation(Location loc, String category) { chestLocations.add(new ChestLocation(loc, category)); }
    public void addLeverLocation(Location loc) { leverLocations.add(loc); }
    public void addTraderLocation(Location loc) { traderLocations.add(new TraderLocation(loc, "default")); }
    public void addTraderLocation(Location loc, String traderType) { traderLocations.add(new TraderLocation(loc, traderType)); }
    
    public void addSpawnedTrader(Villager trader) {
        spawnedTraders.add(trader);
    }
    
    public void clearTraders() {
        spawnedTraders.forEach(Villager::remove);
        spawnedTraders.clear();
    }

    public void addDroppedItem(Item item) {
        droppedItems.add(item);
    }

    public void clearDroppedItems() {
        droppedItems.forEach(Item::remove);
        droppedItems.clear();
    }

    public void clearChests() {
        for (ChestLocation chestLoc : chestLocations) {
            Block block = chestLoc.getLocation().getBlock();
            if (block.getType() == Material.CHEST) {
                block.setType(Material.AIR);
            }
        }
    }

    public void cleanupAfterMatch() {
        clearTraders();
        clearDroppedItems();
        clearChests();
        
        // Очищаем спавн-блоки игроков
        for (Player player : activePlayers) {
            Location blockLoc = playersBlock.get(player);
            if (blockLoc != null && blockLoc.getBlock().getType() == Material.BEDROCK) {
                blockLoc.getBlock().setType(Material.AIR);
            }
        }
        playersBlock.clear();
    }
    public void setMinPlayersToStart(int count) { minPlayersToStart = count; }
    public void setMatchDuration(int duration) { matchDuration = duration; }
    
    public int getChestRefillInterval() { return chestRefillInterval; }
    public void setChestRefillInterval(int interval) { chestRefillInterval = interval; }
    
    public void startChestRefillTimer(ArenaManager arenaManager) {
        if (chestRefillTask != null) {
            chestRefillTask.cancel();
        }
        
        chestRefillTask = new BukkitRunnable() {
            @Override
            public void run() {
                arenaManager.spawnChests(Arena.this);
                broadcast("§aСундуки были перезаполнены!");
            }
        };
        chestRefillTask.runTaskTimer(EscapePlugin.getInstance(),
            chestRefillInterval * 20L, // Начальная задержка (секунды -> тики)
            chestRefillInterval * 20L  // Период повторения
        );
    }
    
    public void stopChestRefillTimer() {
        if (chestRefillTask != null) {
            chestRefillTask.cancel();
            chestRefillTask = null;
        }
    }
}
