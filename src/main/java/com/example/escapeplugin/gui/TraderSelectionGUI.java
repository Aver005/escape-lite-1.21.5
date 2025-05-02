package com.example.escapeplugin.gui;

import java.util.ArrayList;
import java.util.List;
import com.example.escapeplugin.entities.Arena;
import com.example.escapeplugin.managers.ArenaStorage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.example.escapeplugin.enums.TraderType;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class TraderSelectionGUI implements Listener 
{
    private static final String TITLE = "Выберите тип торговца";
    private static final Map<Player, Location> pendingTraderLocations = new HashMap<>();

    public static void open(Player player, Location location) 
    {
        Inventory inv = Bukkit.createInventory(null, 9, TITLE);
        
        for (TraderType type : TraderType.values()) 
        {
            ItemStack item = new ItemStack(type.getIcon());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(type.getDisplayName());
            item.setItemMeta(meta);
            
            inv.addItem(item);
        }
        
        pendingTraderLocations.put(player, location);
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) 
    {
        if (!e.getView().getTitle().equals(TITLE)) return;
        if (!(e.getWhoClicked() instanceof Player)) return;
        
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        ItemStack clicked = e.getCurrentItem();
        
        if (clicked == null || !clicked.hasItemMeta()) return;
        
        Location loc = pendingTraderLocations.get(player);
        if (loc == null) return;
        
        for (TraderType type : TraderType.values()) 
        {
            if (clicked.getType() == type.getIcon() && 
                clicked.getItemMeta().getDisplayName().equals(type.getDisplayName())) 
            {
                // Get arena from player's hand (similar to SetupTools)
                ItemStack item = player.getInventory().getItemInHand();
                if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return;
                
                List<String> lore = item.getItemMeta().getLore();
                if (lore.size() < 2) return;
                
                String arenaId = lore.get(1).replace("ID арены: ", "");
                Arena arena = ArenaStorage.get(arenaId);
                if (arena == null) return;
                
                // Add trader spawn point
                if (!arena.getTraderSpawns().containsKey(type)) {
                    arena.getTraderSpawns().put(type, new ArrayList<>());
                }
                arena.getTraderSpawns().get(type).add(loc);
                
                player.sendMessage("§aДобавлен торговец: " + type.getDisplayName());
                player.closeInventory();
                pendingTraderLocations.remove(player);
                break;
            }
        }
    }
}