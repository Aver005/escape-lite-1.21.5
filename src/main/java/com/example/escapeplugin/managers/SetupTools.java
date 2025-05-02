package com.example.escapeplugin.managers;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.example.escapeplugin.entities.Arena;
import com.example.escapeplugin.enums.TraderType;

import java.util.ArrayList;
import java.util.List;

public class SetupTools implements Listener
{
    public static void giveSetupTools(Player player, Arena arena)
    {
        player.getInventory().addItem(
            createMarker(
                Material.BEACON, 
                "Маяк", 
                "Отметка игроков", 
                arena
            )
        );
        player.getInventory().addItem(
            createMarker(
                Material.CHEST, 
                "Сундук", 
                "Отметка стешей", 
                arena
            )
        );
        player.getInventory().addItem(
            createMarker(
                Material.VILLAGER_SPAWN_EGG, 
                "Яйцо призыва жителей", 
                "Отметка торговцев", 
                arena
            )
        );
        player.getInventory().addItem(
            createMarker(
                Material.LEVER, 
                "Рычаг", 
                "Отметка локаций", 
                arena
            )
        );
    }

    private static ItemStack createMarker(Material material, String name, String description, Arena arena) 
    {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(List.of(description, "ID арены: " + arena.getID()));
        item.setItemMeta(meta);
        return item;
    }


    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent e)
    {
        Player p = e.getPlayer();
        ItemStack item = e.getItemInHand();
        
        // Check if item has meta and lore
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return;
        
        List<String> lore = item.getItemMeta().getLore();
        if (lore.size() < 2) return;
        
        // Get arena ID from lore
        String arenaId = lore.get(1).replace("ID арены: ", "");
        
        // Get arena from storage
        Arena arena = ArenaStorage.get(arenaId);
        if (arena == null) return;
        
        // Handle different marker types
        Material type = item.getType();
        Location loc = e.getBlockPlaced().getLocation();
        
        if (type == Material.BEACON) {
            arena.getPrisonerSpawns().add(loc);
            p.sendMessage("§aДобавлена точка спавна заключенных");
        }
        else if (type == Material.CHEST) {
            arena.getStashSpawns().add(loc);
            p.sendMessage("§aДобавлена точка спавна стешей");
        }
        else if (type == Material.LEVER) {
            arena.getLeverSpawns().add(loc);
            p.sendMessage("§aДобавлена точка спавна рычагов");
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onVillagerEggUse(PlayerInteractEvent e)
    {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        
        ItemStack item = e.getItem();
        if (item == null || item.getType() != Material.VILLAGER_SPAWN_EGG) return;
        
        // Check if item has meta and lore
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return;
        
        List<String> lore = item.getItemMeta().getLore();
        if (lore.size() < 2) return;
        
        // Get arena ID from lore
        String arenaId = lore.get(1).replace("ID арены: ", "");
        
        // Get arena from storage
        Arena arena = ArenaStorage.get(arenaId);
        if (arena == null) return;
        
        // Add trader spawn point
        Location loc = e.getClickedBlock().getLocation().add(0, 1, 0);
        TraderType type = TraderType.COOK; // Default type
        
        if (!arena.getTraderSpawns().containsKey(type)) {
            arena.getTraderSpawns().put(type, new ArrayList<>());
        }
        arena.getTraderSpawns().get(type).add(loc);
        
        e.getPlayer().sendMessage("§aДобавлена точка спавна торговца");
        e.setCancelled(true);
    }
}
