package com.example.escapeplugin.entities;

import org.bukkit.inventory.ItemStack;
import com.example.escapeplugin.enums.LootRarity;

public class LootItem 
{
    private final ItemStack item;
    private final LootRarity rarity;
    private final double chance;

    public LootItem(ItemStack item, LootRarity rarity, double chance) 
    {
        this.item = item;
        this.rarity = rarity;
        this.chance = chance;
    }

    public ItemStack getItem() {
        return item.clone();
    }

    public LootRarity getRarity() {
        return rarity;
    }

    public double getChance() {
        return chance;
    }
}