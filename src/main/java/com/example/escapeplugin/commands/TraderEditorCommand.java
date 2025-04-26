package com.example.escapeplugin.commands;

import com.example.escapeplugin.gui.TraderEditorGUI;
import com.example.escapeplugin.traders.TraderManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class TraderEditorCommand implements CommandExecutor, Listener {
    private final TraderManager traderManager;
    private TraderEditorGUI currentGUI;

    public TraderEditorCommand(TraderManager traderManager) {
        this.traderManager = traderManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда только для игроков");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("escape.tradereditor")) {
            player.sendMessage("§cУ вас нет прав на использование этой команды");
            return true;
        }

        currentGUI = new TraderEditorGUI(traderManager, player);
        currentGUI.open();
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (currentGUI == null || !event.getView().getTitle().equals("Редактор торговцев")) {
            return;
        }

        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        // Обработка навигации
        if (clicked.getType() == Material.ARROW) {
            if (slot == 45) {
                currentGUI.prevPage();
            } else if (slot == 53) {
                currentGUI.nextPage();
            }
            currentGUI.open();
            return;
        }

        // Обработка выбора торговца
        if (slot < 28 && clicked.getType() == Material.VILLAGER_SPAWN_EGG) {
            String traderId = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
            currentGUI.setSelectedTrader(traderId);
            currentGUI.open();
            return;
        }

        // Обработка кнопок
        switch (slot) {
            case 49: // Добавить торговца
                // TODO: Реализовать создание нового торговца
                player.sendMessage("§aСоздание нового торговца...");
                break;
            case 50: // Сохранить
                traderManager.saveTraders();
                player.sendMessage("§aИзменения сохранены!");
                break;
            case 40: // Добавить предмет
                if (currentGUI.getSelectedTrader() != null) {
                    ItemStack heldItem = player.getInventory().getItemInMainHand();
                    if (heldItem == null || heldItem.getType() == Material.AIR) {
                        player.sendMessage("§cВозьмите предмет в руку для добавления");
                        return;
                    }
                    
                    player.sendMessage("§aВведите цену предмета в изумрудах (число):");
                    player.closeInventory();
                    
                    Plugin plugin = Bukkit.getPluginManager().getPlugin("EscapePlugin");
                    player.setMetadata("trader_adding_item", new FixedMetadataValue(plugin, currentGUI.getSelectedTrader()));
                    player.setMetadata("item_to_add", new FixedMetadataValue(plugin, heldItem.clone()));
                }
                break;
        }
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("trader_adding_item") && player.hasMetadata("item_to_add")) {
            event.setCancelled(true);
            
            try {
                int price = Integer.parseInt(event.getMessage());
                if (price <= 0) {
                    player.sendMessage("§cЦена должна быть положительным числом");
                    return;
                }
                
                String traderId = player.getMetadata("trader_adding_item").get(0).asString();
                ItemStack item = (ItemStack) player.getMetadata("item_to_add").get(0).value();
                
                TraderManager.Trader trader = traderManager.getTraders().get(traderId);
                if (trader != null) {
                    trader.addItem(item, price);
                    player.sendMessage("§aПредмет успешно добавлен торговцу!");
                    currentGUI.open();
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§cВведите корректное число (например: 5)");
                return;
            } finally {
                Plugin plugin = Bukkit.getPluginManager().getPlugin("EscapePlugin");
                player.removeMetadata("trader_adding_item", plugin);
                player.removeMetadata("item_to_add", plugin);
            }
        }
    }

    public TraderEditorGUI getCurrentGUI() {
        return currentGUI;
    }
}