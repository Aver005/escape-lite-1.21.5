package com.example.escapeplugin.gui;

import com.example.escapeplugin.quests.Quest;
import com.example.escapeplugin.quests.QuestManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;

public class QuestGUI
{
    public static void open(Player player, QuestManager questManager)
    {
        Inventory gui = Bukkit.createInventory(null, 27, "§6Квесты");

        // Заполнение границ
        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = border.getItemMeta();
        meta.setDisplayName(" ");
        border.setItemMeta(meta);
        for (int i = 0; i < 27; i++) {
            if (i < 9 || i > 17 || i % 9 == 0 || i % 9 == 8) {
                gui.setItem(i, border);
            }
        }

        // Добавление квестов
        if (questManager.hasQuests(player))
        {
            int slot = 10;
            for (Quest quest : questManager.getQuests(player))
            {
                ItemStack questItem = new ItemStack(Material.MAP);
                ItemMeta questMeta = questItem.getItemMeta();
                questMeta.setDisplayName("§e" + quest.getName());
                questMeta.setLore(Arrays.asList(
                        "§7" + quest.getDescription(),
                        "§aПрогресс: §e" + quest.getProgress() + "§6/§e" + quest.getTargetCount()
                ));
                questItem.setItemMeta(questMeta);
                gui.setItem(slot++, questItem);
            }
        }
        else
        {
            ItemStack noQuests = new ItemStack(Material.BARRIER);
            ItemMeta noMeta = noQuests.getItemMeta();
            noMeta.setDisplayName("§cНет активных квестов");
            noQuests.setItemMeta(noMeta);
            gui.setItem(13, noQuests);
        }

        player.openInventory(gui);
    }
}
