package com.example.escapeplugin.quests;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class KillQuest extends Quest
{
    private final int count = 10;

    public KillQuest(int count)
    {
        super("Охотник", "Убейте " + count + " игроков", count);
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event)
    {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        updateProgress(killer, 1);
    }

    @Override
    public void onComplete(Player player)
    {
        player.sendMessage("§6§lКвест завершен! Вы получили §a5 алмазов§6!");
        player.getInventory().addItem(new ItemStack(Material.DIAMOND, 5));
    }
}
