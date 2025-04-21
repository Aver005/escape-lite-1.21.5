package com.example.escapeplugin.game;

import com.example.escapeplugin.EscapePlugin;
import com.example.escapeplugin.arena.Arena;
import com.example.escapeplugin.events.BossEvent;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTimer
{
    private final Arena arena;
    private final BossBar bossBar;
    private final int matchDuration; // В секундах
    private int timeLeft;
    private boolean isRunning;

    public GameTimer(Arena arena, int duration)
    {
        this.matchDuration = duration;
        this.timeLeft = duration;
        this.arena = arena;
        this.bossBar = Bukkit.createBossBar(
            formatTime(timeLeft),
            BarColor.GREEN,
            BarStyle.SOLID
        );
    }

    public void start()
    {
        isRunning = true;
        // Показать боссбар всем игрокам на арене
        arena.getPlayers().forEach(bossBar::addPlayer);

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (!isRunning)
                {
                    bossBar.removeAll();
                    cancel();
                    return;
                }

                timeLeft--;
                updateBossBar();

                if (timeLeft == matchDuration / 2)
                {
                    arena.broadcast("§cПоловина матча позади");
                }
                else if (timeLeft == matchDuration / 4)
                {
                    arena.broadcast("§4§lВолна мобов началась!");
                    BossEvent.startWave(
                        arena,
                        10, 2, 0.1,
                        WaveEffects.DEFAULT
                    );
                }
                else if (timeLeft == matchDuration / 5)
                {
                    arena.broadcast("§cОсталось " + timeLeft + " секунд!");
                    BossEvent.startWave(
                        arena,
                        15, 4, 0.3,
                        new WaveEffects(true, false, true, false)
                    );
                }
                else if (timeLeft == matchDuration / 20)
                {
                    arena.broadcast("§cМатч почти подошёл к концу!");
                }

                if (timeLeft <= 0)
                {
                    arena.broadcast("§4Матч завершен!");
                    bossBar.removeAll();
                    cancel();
                }
            }
        }.runTaskTimer(EscapePlugin.getInstance(), 0, 20); // 20 тиков = 1 секунда
    }

    public void stop()
    {
        isRunning = false;
        bossBar.removeAll();
    }

    private void updateBossBar()
    {
        // Обновляем текст боссбара
        bossBar.setTitle(formatTime(timeLeft));

        // Обновляем прогресс (от 1.0 до 0.0)
        double progress = (double) timeLeft / matchDuration;
        bossBar.setProgress(progress);

        // Меняем цвет в зависимости от оставшегося времени
        if (progress > 0.5) {
            bossBar.setColor(BarColor.GREEN);
        } else if (progress > 0.2) {
            bossBar.setColor(BarColor.YELLOW);
        } else {
            bossBar.setColor(BarColor.RED);
        }
    }

    private String formatTime(int seconds)
    {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("§eОсталось времени: §6%02d:%02d", minutes, secs);
    }
}
