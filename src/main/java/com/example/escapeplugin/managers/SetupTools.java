package com.example.escapeplugin.managers;

import java.util.HashMap;
import java.util.Map;

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
import com.example.escapeplugin.gui.TraderSelectionGUI;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.List;

public class SetupTools implements Listener {
    private final Map<Player, Location> pendingLeverNames = new HashMap<>();
    public static void giveSetupTools(Player player, Arena arena)
    {
        player.getInventory().addItem(
            createMarker(
                Material.BEACON, 
                "Отметка спавна игроков", 
                "Отметка игроков", 
                arena
            )
        );
        player.getInventory().addItem(
            createMarker(
                Material.CHEST, 
                "Отметка стешей", 
                "Отметка стешей", 
                arena
            )
        );
        player.getInventory().addItem(
            createMarker(
                Material.VILLAGER_SPAWN_EGG, 
                "Отметка торговцев", 
                "Отметка торговцев", 
                arena
            )
        );
        player.getInventory().addItem(
            createMarker(
                Material.LEVER, 
                "Отметка локаций", 
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
        
        if (type == Material.BEACON) 
        {
            if (arena.addPrisonerSpawn(loc))
            {
                e.setCancelled(true);
                p.sendMessage("§aДобавлена точка спавна заключенных");
                return;
            }

            if (arena.removePrisonerSpawn(loc))
            {
                e.setCancelled(true);
                p.sendMessage("§eТочка спавна убрана.");
                return;
            }
        }
        else if (type == Material.CHEST) 
        {
            if (arena.addChestSpawn(loc))
            {
                e.setCancelled(true);
                p.sendMessage("§aСундук добавлен");
                return;
            }

            if (arena.removeChestSpawn(loc))
            {
                e.setCancelled(true);
                p.sendMessage("§eСундук убран");
                return;
            }
        }
        else if (type == Material.LEVER) 
        {
            pendingLeverNames.put(p, loc);
            p.sendMessage("§eВведите название для этой локации в чат:");
            p.sendMessage("§7(Используйте cancel чтобы отменить)");
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
        
        // Open trader type selection GUI
        Location loc = e.getClickedBlock().getLocation().add(0, 1, 0);
        TraderSelectionGUI.open(e.getPlayer(), loc);
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent e) 
    {
        Player p = e.getPlayer();
        if (!pendingLeverNames.containsKey(p)) return;
        
        Location loc = pendingLeverNames.get(p);
        PlainTextComponentSerializer plainSerializer = PlainTextComponentSerializer.plainText();
        String name = plainSerializer.serialize(e.message());
        
        // Check for cancel command
        if (name.equalsIgnoreCase("cancel")) 
        {
            p.sendMessage("§cДобавление локации отменено");
            pendingLeverNames.remove(p);
            e.setCancelled(true);
            return;
        }
        
        // Get arena from storage (similar to onBlockPlaced)
        ItemStack item = p.getInventory().getItemInHand();
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return;
        
        List<String> lore = item.getItemMeta().getLore();
        if (lore.size() < 2) return;
        
        String arenaId = lore.get(1).replace("ID арены: ", "");
        Arena arena = ArenaStorage.get(arenaId);
        if (arena == null) return;
        
        if (arena.addLeverSpawn(name, loc))
        {
            p.sendMessage("§aДобавлена локация '" + name + "'");
            pendingLeverNames.remove(p);
            e.setCancelled(true);
            return;
        }
        
        if (arena.removeLeverSpawn(name))
        {
            p.sendMessage("§eЛокация удалена '" + name + "'");
            pendingLeverNames.remove(p);
            e.setCancelled(true);
            return;
        }
    }
}
