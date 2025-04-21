package com.example.escapeplugin.events;

import com.example.escapeplugin.EscapePlugin;
import com.example.escapeplugin.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BossEvent {
    public static void startWave(Arena arena, int mobCount) {
        new BukkitRunnable() {
            int spawned = 0;
            @Override
            public void run() {
                if (spawned >= mobCount) {
                    cancel();
                    return;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (arena.isPlaying(player)) {
                        Location loc = player.getLocation().add(
                                Math.random() * 10 - 5,
                                0,
                                Math.random() * 10 - 5
                        );
                        loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
                        spawned++;
                    }
                }
            }
        }.runTaskTimer(EscapePlugin.getInstance(), 0, 20); // Спавн каждую секунду
    }
}
