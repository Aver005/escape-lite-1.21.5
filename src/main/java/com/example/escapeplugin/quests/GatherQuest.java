package com.example.escapeplugin.quests;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class GatherQuest extends Quest
{
    private final Material targetMaterial;

    public GatherQuest(Material targetMaterial, int count)
    {
        super("Шахтер", "Соберите " + count + " алмазов", count);
        this.targetMaterial = targetMaterial;
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event)
    {
        Entity ent = event.getEntity();
        if (!(ent instanceof Player)) return;
        if (event.getItem().getItemStack().getType() != targetMaterial) return;
        updateProgress((Player) ent, 1);
    }

    @Override
    public void onComplete(Player player)
    {
        player.sendMessage("§6§lКвест завершен! Вы получили §aжелезную кирку с удачей III§6!");
        ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE);
        // Здесь можно добавить enchantments через ItemMeta
        player.getInventory().addItem(pickaxe);
    }
}
