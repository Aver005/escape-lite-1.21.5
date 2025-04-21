package com.example.escapeplugin.listeners;

import com.example.escapeplugin.EscapePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockBreakListener implements Listener
{
    private final EscapePlugin plugin;

    public BlockBreakListener(EscapePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.getPlayer().sendMessage("Вы сломали блок!");
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            // Эффект молнии
            killer.getWorld().strikeLightningEffect(killer.getLocation());
            // Уведомление в чат
            Bukkit.broadcastMessage("§c" + killer.getName() + " убил " + event.getEntity().getName() + "!");
        }
    }

    @EventHandler
    public void onLeverActivate(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK &&
                event.getClickedBlock().getType() == Material.LEVER) {
            // Спавн частиц
            event.getPlayer().spawnParticle(
                    Particle.FLAME,
                    event.getClickedBlock().getLocation(),
                    30, 0.5, 0.5, 0.5
            );
        }
    }

}
