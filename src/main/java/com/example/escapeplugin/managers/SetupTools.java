package com.example.escapeplugin.managers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.example.escapeplugin.entities.Arena;

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
        
    }
}
