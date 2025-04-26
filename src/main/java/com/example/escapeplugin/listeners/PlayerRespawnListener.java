package com.example.escapeplugin.listeners;

import com.example.escapeplugin.arena.ArenaPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        
        if (arenaPlayer.isPlaying()) {
            // Respawn at spawn block if available, otherwise at arena spawn
            Location respawnLocation = arenaPlayer.getSpawn();
            if (respawnLocation != null) {
                event.setRespawnLocation(respawnLocation);
                player.sendMessage("§aВы возродились у своего спавн-блока!");
            } else {
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage("§cВаш спавн-блок уничтожен! Вы переведены в режим наблюдателя.");
            }
        }
    }
}