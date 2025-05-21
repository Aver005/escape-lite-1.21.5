package com.example.escapeplugin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import com.example.escapeplugin.arena.ArenaPlayer;

public class PlayerDeathListener implements Listener 
{    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) 
    {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        
        if (killer != null) 
        {
            killer.getWorld().strikeLightningEffect(killer.getLocation());
            Bukkit.broadcastMessage("§c" + killer.getName() + " убил " + player.getName() + "!");
            
            killer.sendTitle(
                "",
                ChatColor.RED + "Вы убили " + player.getName(),
                10, 70, 20
            );
            
            player.sendTitle(
                "",
                ChatColor.RED + "Вас убил " + killer.getName(),
                10, 70, 20
            );
        }
        
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer != null && arenaPlayer.isPlaying()) 
        {
            if (arenaPlayer.getSpawnBlockLocation() == null) 
            {
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage(ChatColor.RED + "Вы переведены в режим наблюдателя, так как не установили спавн-блок!");
            }
        }
    }
}