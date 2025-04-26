package com.example.escapeplugin.gui;

import com.example.escapeplugin.loot.LootManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class LootEditorGUI {
    private final LootManager lootManager;
    private final Player player;
    private Inventory inventory;

    public LootEditorGUI(LootManager lootManager, Player player) {
        this.lootManager = lootManager;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 54, "Loot Editor");
    }

    public void open() {
        updateInventory();
        player.openInventory(inventory);
    }

    private void updateInventory() {
        inventory.clear();
        addControlButtons();
        displayCategories();
    }

    private void addControlButtons() {
        // Кнопка добавления новой категории
        ItemStack addCategory = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta addMeta = addCategory.getItemMeta();
        addMeta.setDisplayName("§aAdd Category");
        addMeta.setLore(Arrays.asList("§7Click to add new loot category"));
        addCategory.setItemMeta(addMeta);
        inventory.setItem(49, addCategory);

        // Кнопка сохранения
        ItemStack save = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta saveMeta = save.getItemMeta();
        saveMeta.setDisplayName("§cSave and Exit");
        saveMeta.setLore(Arrays.asList("§7Click to save changes"));
        save.setItemMeta(saveMeta);
        inventory.setItem(53, save);
    }

    private void displayCategories() {
        int slot = 0;
        for (String category : lootManager.getCategories()) {
            ItemStack categoryItem = new ItemStack(Material.CHEST);
            ItemMeta meta = categoryItem.getItemMeta();
            meta.setDisplayName("§e" + category);
            meta.setLore(Arrays.asList(
                    "§7Click to edit items",
                    "§7Shift+Click to delete"
            ));
            categoryItem.setItemMeta(meta);
            inventory.setItem(slot++, categoryItem);
        }
    }

    public void handleClick(int slot, boolean isShiftClick) {
        if (slot == 49) {
            // Добавление категории
            player.closeInventory();
            player.sendMessage("§aEnter new category name in chat:");
            // TODO: Добавить обработчик ввода
        } else if (slot == 53) {
            // Сохранение и выход
            player.closeInventory();
        } else if (slot < lootManager.getCategories().size()) {
            // Редактирование категории
            String category = (String) lootManager.getCategories().toArray()[slot];
            if (isShiftClick) {
                lootManager.removeCategory(category);
                updateInventory();
            } else {
                openCategoryEditor(category);
            }
        }
    }

    private void openCategoryEditor(String category) {
        Inventory categoryInv = Bukkit.createInventory(null, 54, "Editing: " + category);
        // TODO: Реализовать редактор предметов в категории
        player.openInventory(categoryInv);
    }
}