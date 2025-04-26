package com.example.escapeplugin.listeners;

import com.example.escapeplugin.EscapePlugin;
import com.example.escapeplugin.arena.ArenaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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
        Player player = event.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        
        // Если игрок не в матче - разрешаем стандартное поведение
        if (!arenaPlayer.isPlaying()) return;
        
        // Проверяем, является ли блок спавн-блоком игрока
        Location blockLoc = event.getBlock().getLocation();
        Location spawnBlockLoc = arenaPlayer.getSpawnBlockLocation();
        
        if (spawnBlockLoc != null &&
            blockLoc.equals(spawnBlockLoc)) {
            // Игрок ломает свой спавн-блок - разрешаем
            arenaPlayer.setSpawnBlockLocation(null);
            player.sendMessage("§aВы сломали свой спавн-блок!");
            return;
        }
        
        // Запрещаем ломать другие блоки во время матча
        event.setCancelled(true);
        player.sendMessage("§cВы не можете ломать блоки во время матча!");
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        
        // Если игрок не в матче - разрешаем стандартное поведение
        if (!arenaPlayer.isPlaying()) return;
        
        // Проверяем, является ли блок спавн-блоком
        if (event.getBlock().getType() == Material.BEDROCK) {
            // Устанавливаем новый спавн-блок
            arenaPlayer.setSpawnBlockLocation(event.getBlock().getLocation());
            player.sendMessage("§aВы установили новый спавн-блок!");
            return;
        }
        
        // Запрещаем ставить другие блоки во время матча
        event.setCancelled(true);
        player.sendMessage("§cВы не можете ставить блоки во время матча!");
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
    public void onLeverActivate(PlayerInteractEvent event)
    {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK &&
                event.getClickedBlock().getType() == Material.LEVER)
        {
            // Спавн частиц
            event.getPlayer().spawnParticle(
                Particle.FLAME,
                event.getClickedBlock().getLocation(),
                30, 0.5, 0.5, 0.5
            );
        }
    }

}
