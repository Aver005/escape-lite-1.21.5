package com.example.escapeplugin.quests;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class MobHunterQuest extends Quest
{
    private final EntityType targetMob;

    public MobHunterQuest(int count, EntityType targetMob)
    {
        super("Охотник на монстров", "Убейте " + count + " криперов", count);
        this.targetMob = targetMob;
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event)
    {
        if (event.getEntity().getKiller() == null) return;
        if (event.getEntityType() != targetMob) return;
        updateProgress(event.getEntity().getKiller(), 1);
    }

    @Override
    public void onComplete(Player player)
    {
        player.sendMessage("§6§lКвест завершен! Вы получили §a3 динамита§6!");
        player.getInventory().addItem(new ItemStack(Material.TNT, 3));
    }
}
