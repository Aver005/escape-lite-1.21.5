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
import org.bukkit.ChatColor;

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
        
        // Check if block is any player's spawn block
        for (Player otherPlayer : arenaPlayer.getArena().getPlayers()) {
            ArenaPlayer otherArenaPlayer = ArenaPlayer.getPlayer(otherPlayer);
            Location otherSpawnBlock = otherArenaPlayer.getSpawnBlockLocation();
            
            if (otherSpawnBlock != null && blockLoc.equals(otherSpawnBlock)) {
                if (otherPlayer.equals(player)) {
                    // Player broke their own spawn block
                    arenaPlayer.setSpawnBlockLocation(null);
                    player.sendMessage("§aВы сломали свой спавн-блок!");
                } else {
                    // Player broke another player's spawn block
                    otherArenaPlayer.setSpawnBlockLocation(null);
                    player.sendMessage("§aВы сломали спавн-блок игрока " + otherPlayer.getName() + "!");
                    // Титры для владельца блока
                    otherPlayer.sendTitle(
                        "",
                        ChatColor.RED + "Ваш спавн-блок уничтожен",
                        10, 70, 20
                    );
                    otherPlayer.sendMessage("§cВаш спавн-блок был уничтожен!");
                }
                return;
            }
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
            
            // Глобальное уведомление
            Bukkit.broadcastMessage(ChatColor.GOLD + "Игрок " + player.getName() + " установил спавн-блок!");
            
            // Титры для игрока
            player.sendTitle(
                "",
                ChatColor.GREEN + "Спавн-блок установлен",
                10, 70, 20
            );
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
            // Уведомление в чат и титры
            Bukkit.broadcastMessage("§c" + killer.getName() + " убил " + event.getEntity().getName() + "!");
            
            // Титры для убийцы
            killer.sendTitle(
                "",
                ChatColor.RED + "Вы убили " + event.getEntity().getName(),
                10, 70, 20
            );
            
            event.getEntity().sendTitle(
                "",
                ChatColor.RED + "Вас убил " + killer.getName(),
                10, 70, 20
            );
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
