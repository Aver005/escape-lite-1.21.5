package com.example.escapeplugin.game;

import com.example.escapeplugin.EscapePlugin;
import com.example.escapeplugin.arena.Arena;
import com.example.escapeplugin.events.BossEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTimer {
    private Arena arena;
    private final int matchDuration; // В секундах
    private int timeLeft;
    private boolean isRunning;

    public GameTimer(Arena arena, int duration) {
        this.matchDuration = duration;
        this.timeLeft = duration;
        this.arena = arena;
    }

    public void start() {
        isRunning = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isRunning) cancel();

                timeLeft--;
                // Уведомления при 50%, 20%, 5% времени
                if (timeLeft == matchDuration / 2) {
                    Bukkit.broadcastMessage("§cОсталось 50% времени!");
                } else if (timeLeft == matchDuration / 4) { // 25% времени
                    Bukkit.broadcastMessage("§4§lВолна мобов началась!");
                    BossEvent.startWave(arena, 10); // 10 мобов
                } else if (timeLeft == matchDuration / 5) {
                    Bukkit.broadcastMessage("§cОсталось 20% времени!");
                } else if (timeLeft == matchDuration / 20) {
                    Bukkit.broadcastMessage("§cОсталось 5% времени!");
                }


                if (timeLeft <= 0) {
                    Bukkit.broadcastMessage("§4Матч завершен!");
                    cancel();
                }
            }
        }.runTaskTimer(EscapePlugin.getInstance(), 0, 20); // 20 тиков = 1 секунда
    }

    public void stop() {
        isRunning = false;
    }
}
