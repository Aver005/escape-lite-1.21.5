package com.example.escapeplugin.commands;

import com.example.escapeplugin.gui.LootEditorGUI;
import com.example.escapeplugin.loot.LootManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        Player player = (Player) sender;
        new LootEditorGUI(lootManager, player).open();
        return true;
    }
}