package com.example.escapeplugin.commands;

import com.example.escapeplugin.EscapePlugin;
import com.example.escapeplugin.gui.LootEditorGUI;
import com.example.escapeplugin.loot.LootManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LootEditorCommand implements CommandExecutor {
    private final LootManager lootManager;

    public LootEditorCommand(LootManager lootManager) {
        this.lootManager = lootManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        if (!sender.hasPermission("escape.looteditor")) {
            sender.sendMessage("You don't have permission to use this command!");
            return true;
        }

        if(args.length <= 0) {
            sender.sendMessage("Usage: /looteditor <category>");
            return true;
        }

        String category = args[0];


        Player player = (Player) sender;
        Block targetBlock = player.getTargetBlock(Set.of(Material.CHEST), 7);
        if(targetBlock.getType() != Material.CHEST) {
            sender.sendMessage("You must look on the chest!");
            return true;
        }



        lootManager.saveItemFromInventory(((Chest) targetBlock).getBlockInventory(), category);

//        new LootEditorGUI(EscapePlugin.getInstance().getLootManager(), player).open();
        return true;
    }
}