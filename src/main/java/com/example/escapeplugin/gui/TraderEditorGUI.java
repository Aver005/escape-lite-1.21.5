package com.example.escapeplugin.gui;

import com.example.escapeplugin.traders.TraderManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class TraderEditorGUI {
    private final TraderManager traderManager;
    private final Player player;
    private int currentPage = 0;
    private String selectedTrader = null;
    
    public TraderEditorGUI(TraderManager traderManager, Player player) {
        this.traderManager = traderManager;
        this.player = player;
    }
    
    public void open() {
        Inventory inv = Bukkit.createInventory(null, 54, "Редактор торговцев");
        
        // Список торговцев (левая часть)
        List<String> traderIds = new ArrayList<>(traderManager.getTraders().keySet());
        int startIndex = currentPage * 28;
        for (int i = 0; i < 28 && startIndex + i < traderIds.size(); i++) {
            String traderId = traderIds.get(startIndex + i);
            ItemStack traderItem = new ItemStack(Material.VILLAGER_SPAWN_EGG);
            ItemMeta meta = traderItem.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + traderId);
            meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Кликните для редактирования",
                ChatColor.GRAY + "Shift+Клик для удаления"
            ));
            traderItem.setItemMeta(meta);
            inv.setItem(i, traderItem);
        }
        
        // Кнопки навигации
        if (currentPage > 0) {
            ItemStack prevButton = createButton(Material.ARROW, "§aПредыдущая страница", "§7Кликните для перехода");
            inv.setItem(45, prevButton);
        }
        if (startIndex + 28 < traderIds.size()) {
            ItemStack nextButton = createButton(Material.ARROW, "§aСледующая страница", "§7Кликните для перехода");
            inv.setItem(53, nextButton);
        }
        
        // Форма редактирования (правая часть)
        if (selectedTrader != null) {
            // Поля для редактирования имени
            ItemStack nameItem = new ItemStack(Material.NAME_TAG);
            ItemMeta nameMeta = nameItem.getItemMeta();
            nameMeta.setDisplayName("§eИмя торговца");
            nameMeta.setLore(Arrays.asList(
                "§7Текущее: §f" + traderManager.getTraderName(selectedTrader),
                "§7Кликните чтобы изменить"
            ));
            nameItem.setItemMeta(nameMeta);
            inv.setItem(30, nameItem);
            
            // Список предметов
            List<ItemStack> items = traderManager.getTraderItems(selectedTrader);
            for (int i = 0; i < 9 && i < items.size(); i++) {
                inv.setItem(31 + i, items.get(i));
            }
            
            // Кнопка добавления предмета
            ItemStack addItemButton = createButton(Material.EMERALD, "§aДобавить предмет", "§7Кликните чтобы добавить новый предмет");
            inv.setItem(40, addItemButton);
        }
        
        // Кнопка добавления нового торговца
        ItemStack addButton = createButton(Material.EMERALD_BLOCK, "§aДобавить торговца", "§7Кликните чтобы создать нового торговца");
        inv.setItem(49, addButton);
        
        // Кнопка сохранения
        ItemStack saveButton = createButton(Material.REDSTONE_BLOCK, "§cСохранить изменения", "§7Кликните чтобы сохранить всех торговцев");
        inv.setItem(50, saveButton);
        
        player.openInventory(inv);
    }
    
    private ItemStack createButton(Material material, String name, String lore) {
        ItemStack button = new ItemStack(material);
        ItemMeta meta = button.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        button.setItemMeta(meta);
        return button;
    }
    
    public void setSelectedTrader(String traderId) {
        this.selectedTrader = traderId;
    }
    
    public void nextPage() {
        currentPage++;
    }
    
    public void prevPage() {
        currentPage = Math.max(0, currentPage - 1);
    }
    
    public String getSelectedTrader() {
        return selectedTrader;
    }
}