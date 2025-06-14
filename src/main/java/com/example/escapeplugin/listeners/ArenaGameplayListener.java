package com.example.escapeplugin.listeners;

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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.ChatColor;

public class ArenaGameplayListener implements Listener
{
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) 
    {
        Player player = event.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);    
        if (!arenaPlayer.isPlaying()) return;
    
        Location blockLoc = event.getBlock().getLocation();
        
        for (Player otherPlayer : arenaPlayer.getArena().getPlayers()) 
        {
            ArenaPlayer otherArenaPlayer = ArenaPlayer.getPlayer(otherPlayer);
            Location otherSpawnBlock = otherArenaPlayer.getSpawnBlockLocation();
            
            if (otherSpawnBlock != null && blockLoc.equals(otherSpawnBlock)) 
            {
                if (otherPlayer.equals(player)) 
                {
                    arenaPlayer.setSpawnBlockLocation(null);
                    player.sendMessage("§aВы сломали свой спавн-блок!");
                    event.setDropItems(false);
                    player.getInventory().addItem(null)
                    return;
                } 

                otherArenaPlayer.setSpawnBlockLocation(null);
                player.sendMessage("§aВы сломали спавн-блок игрока " + otherPlayer.getName() + "!");
                otherPlayer.sendTitle(
                    "",
                    ChatColor.RED + "Ваш спавн-блок уничтожен",
                    10, 70, 20
                );
                otherPlayer.sendMessage("§cВаш спавн-блок был уничтожен!");
                event.setDropItems(false);
                return;
            }
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) 
    {
        Player player = event.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (!arenaPlayer.isPlaying()) return;
        
        if (event.getBlock().getType().equals(Material.IRON_BLOCK)) 
        {
            arenaPlayer.setSpawnBlockLocation(event.getBlock().getLocation());
            player.sendMessage("§aВы установили новый спавн-блок!");
            
            Bukkit.broadcastMessage(ChatColor.GOLD + "Игрок " + player.getName() + " установил спавн-блок!");
            
            player.sendTitle(
                "",
                ChatColor.GREEN + "Спавн-блок установлен",
                10, 70, 20
            );
            return;
        }
        
        event.setCancelled(true);
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
