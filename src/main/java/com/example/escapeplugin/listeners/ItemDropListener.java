package com.example.escapeplugin.listeners;

import com.example.escapeplugin.arena.Arena;
import com.example.escapeplugin.arena.ArenaManager;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class ItemDropListener implements Listener {
    private final ArenaManager arenaManager;

    public ItemDropListener(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Arena arena = arenaManager.getPlayerArena(player);
        
        if (arena != null) {
            Item droppedItem = event.getItemDrop();
            arena.addDroppedItem(droppedItem);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Arena arena = arenaManager.getPlayerArena(player);
        
        if (arena != null) {
            for (ItemStack item : event.getDrops()) {
                if (item != null) {
                    // Создаем предмет в мире и добавляем его в арену
                    Item droppedItem = player.getWorld().dropItem(player.getLocation(), item);
                    arena.addDroppedItem(droppedItem);
                }
            }
            event.getDrops().clear(); // Очищаем стандартные дропы
        }
    }
}