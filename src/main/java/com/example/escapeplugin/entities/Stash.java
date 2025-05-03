package com.example.escapeplugin.entities;

import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import com.example.escapeplugin.managers.LootManager;
import java.util.List;

public class Stash 
{
    private Inventory inventory;
    private Location location;

    public Stash(Location location, LootManager lootManager) 
    {
        this.location = location;
        if (location.getBlock().getState() instanceof Chest) 
        {
            this.inventory = ((Chest) location.getBlock().getState()).getInventory();
            generateLoot(lootManager);
        }
    }

    private void generateLoot(LootManager lootManager) 
    {
        if (inventory == null) return;
        
        inventory.clear();
        List<ItemStack> loot = lootManager.generateLoot();
        for (ItemStack item : loot) 
        {
            int slot = (int) (Math.random() * inventory.getSize());
            inventory.setItem(slot, item);
        }
    }

    public void clear() 
    {
        if (inventory == null) return;
        inventory.clear();
    }

    public Location getLocation() {
        return location;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
