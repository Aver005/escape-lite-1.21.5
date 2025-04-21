package com.example.escapeplugin.quests;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class MiningQuest extends Quest {
    public MiningQuest() {
        super("Шахтер", "Добудьте 10 железной руды", 10);
    }

    @EventHandler
    public void onMine(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.IRON_ORE) {
            updateProgress(event.getPlayer(), 1);
        }
    }

    @Override
    public void onComplete(Player player) {
        player.sendMessage("§6§lКвест завершен! Вы получили §aжелезную кирку§6!");
        player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
    }
}
