package com.example.escapeplugin.quests;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class TravelQuest extends Quest
{
    private final Location targetLocation;
    private final int radius;

    public TravelQuest(Location targetLocation)
    {
        super("Путешественник", "Доберитесь до таинственного острова", 1);
        this.radius = 5;
        this.targetLocation = targetLocation;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (progress >= targetCount) return;
        if (event.getTo().distance(targetLocation) > radius) return;
        updateProgress(event.getPlayer(), 1);
    }

    @Override
    public void onComplete(Player player)
    {
        player.sendMessage("§6§lКвест завершен! Вы получили §aкарту сокровищ§6!");
        player.getInventory().addItem(new ItemStack(Material.MAP));
    }
}
