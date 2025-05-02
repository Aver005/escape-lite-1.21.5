package com.example.escapeplugin.managers;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;

import com.example.escapeplugin.entities.Arena;
import com.example.escapeplugin.entities.Prisoner;

public class ArenaRunnable extends BukkitRunnable
{
    private final Arena arena;
    private int countdown;
    private BossBar bossBar;

    public ArenaRunnable(Arena arena)
    {
        this(arena, 30); // Default 30 second countdown
    }

    public ArenaRunnable(Arena arena, int countdownSeconds)
    {
        this.arena = arena;
        this.countdown = countdownSeconds;
        this.bossBar = Bukkit.createBossBar(
            "Starting in " + countdown + " seconds",
            BarColor.YELLOW,
            BarStyle.SOLID
        );
    }

    @Override
    public void run()
    {
        // Check if we still have enough players
        if (arena.getPrisoners().size() < arena.getMinPlayers()) {
            cancelCountdown();
            return;
        }

        // Update bossbar for all players
        updateBossBar();

        // Countdown logic
        if (countdown <= 0) {
            arena.start();
            bossBar.removeAll();
            this.cancel();
            return;
        }

        countdown--;
    }

    private void updateBossBar() {
        bossBar.setTitle("Starting in " + countdown + " seconds");
        bossBar.setProgress((double)countdown / 30.0);
        
        // Add/remove players from bossbar as needed
        for (Prisoner prisoner : arena.getPrisoners()) {
            if (!bossBar.getPlayers().contains(prisoner.getPlayer())) {
                bossBar.addPlayer(prisoner.getPlayer());
            }
        }
    }

    public void removePlayer(Prisoner prisoner) {
        bossBar.removePlayer(prisoner.getPlayer());
        if (arena.getPrisoners().size() < arena.getMinPlayers()) {
            cancelCountdown();
        }
    }

    private void cancelCountdown() {
        bossBar.removeAll();
        this.cancel();
    }
}