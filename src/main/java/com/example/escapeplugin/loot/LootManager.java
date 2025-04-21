package com.example.escapeplugin.loot;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.*;

public class LootManager
{
    private final Map<Material, Integer> commonLoot = new HashMap<>();
    private final Map<Material, Integer> rareLoot = new HashMap<>();

    public LootManager() {
        setupLootTables();
    }

    private void setupLootTables() {
        // Обычный лут (60% шанс)
        commonLoot.put(Material.IRON_INGOT, 3);
        commonLoot.put(Material.BREAD, 5);
        commonLoot.put(Material.STONE_PICKAXE, 1);

        // Редкий лут (20% шанс)
        rareLoot.put(Material.DIAMOND, 1);
        rareLoot.put(Material.GOLDEN_APPLE, 1);
        rareLoot.put(Material.ENDER_PEARL, 2);
    }

    public void fillChest(Inventory chest) {
        Random random = new Random();
        // Очистка сундука
        chest.clear();

        // Заполнение обычным лутом
        for (Map.Entry<Material, Integer> entry : commonLoot.entrySet()) {
            if (random.nextDouble() < 0.6) { // 60% шанс
                chest.addItem(new ItemStack(entry.getKey(), entry.getValue()));
            }
        }

        // Заполнение редким лутом (20% шанс)
        if (random.nextDouble() < 0.2) {
            Material rareItem = (Material) rareLoot.keySet().toArray()[random.nextInt(rareLoot.size())];
            chest.addItem(new ItemStack(rareItem, rareLoot.get(rareItem)));
        }
    }
}
