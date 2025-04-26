package com.example.escapeplugin.gui;

import com.example.escapeplugin.arena.Arena;
import com.example.escapeplugin.arena.ArenaManager;
import com.example.escapeplugin.arena.SetupTools;
import com.example.escapeplugin.traders.TraderManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class TraderSelectionGUI implements Listener {
    private final ArenaManager arenaManager;
    private final TraderManager traderManager;
    private final Player player;
    private final Arena arena;

    public TraderSelectionGUI(ArenaManager arenaManager, TraderManager traderManager, Player player, Arena arena) {
        this.arenaManager = arenaManager;
        this.traderManager = traderManager;
        this.player = player;
        this.arena = arena;
    }

    public void open() {
        Inventory gui = Bukkit.createInventory(null, 9, "§aВыберите тип торговца");

        // Добавляем варианты торговцев
        gui.setItem(2, createTraderItem("weapons", "§6Торговец оружием", Material.IRON_SWORD));
        gui.setItem(4, createTraderItem("food", "§6Торговец едой", Material.GOLDEN_APPLE));
        gui.setItem(6, createTraderItem("tools", "§6Торговец инструментами", Material.IRON_PICKAXE));

        player.openInventory(gui);
    }

    private ItemStack createTraderItem(String type, String name, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList("§7Тип: " + type));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§aВыберите тип торговца")) return;
        if (event.getCurrentItem() == null) return;

        event.setCancelled(true);
        player.closeInventory();

        String traderType = event.getCurrentItem().getItemMeta().getLore().get(0).replace("§7Тип: ", "");
        arena.addTraderLocation(player.getLocation(), traderType);
        player.sendMessage("§aТорговец типа §6" + traderType + "§a добавлен на арену!");
        
        // Возвращаем яйцо торговца в инвентарь
        player.getInventory().addItem(SetupTools.TRADER_EGG);
    }
}