package com.example.escapeplugin.gui;

import com.example.escapeplugin.arena.Arena;
import com.example.escapeplugin.arena.ArenaManager;
import com.example.escapeplugin.arena.SetupTools;
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

public class LootCategoryGUI implements Listener {
    private final ArenaManager arenaManager;
    private final Player player;
    private final Arena arena;

    public LootCategoryGUI(ArenaManager arenaManager, Player player, Arena arena) {
        this.arenaManager = arenaManager;
        this.player = player;
        this.arena = arena;
    }

    public void open() {
        Inventory gui = Bukkit.createInventory(null, 9, "§aВыберите категорию лута");

        // Добавляем варианты категорий
        gui.setItem(2, createCategoryItem("COMMON", "§aОбычный лут", Material.IRON_INGOT));
        gui.setItem(4, createCategoryItem("RARE", "§9Редкий лут", Material.GOLD_INGOT));
        gui.setItem(6, createCategoryItem("EPIC", "§5Эпический лут", Material.DIAMOND));

        player.openInventory(gui);
    }

    private ItemStack createCategoryItem(String category, String name, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList("§7Категория: " + category));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§aВыберите категорию лута")) return;
        if (event.getCurrentItem() == null) return;

        event.setCancelled(true);
        player.closeInventory();

        String category = event.getCurrentItem().getItemMeta().getLore().get(0).replace("§7Категория: ", "");
        arena.addChestLocation(player.getLocation(), category);
        player.sendMessage("§aКатегория лута §6" + category + "§a выбрана для сундука!");
        
        // Возвращаем сундук в инвентарь
        player.getInventory().addItem(SetupTools.CHEST);
    }
}