package com.example.escapeplugin.commands;

import com.example.escapeplugin.arena.Arena;
import com.example.escapeplugin.arena.ArenaManager;
import com.example.escapeplugin.gui.QuestGUI;
import com.example.escapeplugin.quests.QuestManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EscapeCommand implements CommandExecutor {
    private final ArenaManager arenaManager;
    private final QuestManager questManager;

    public EscapeCommand(ArenaManager arenaManager, QuestManager questManager) {
        this.arenaManager = arenaManager;
        this.questManager = questManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cТолько игроки могут использовать эту команду!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§eИспользование: §6/es create <название> §7- Создать арену");
            player.sendMessage("§eИспользование: §6/es join <название> §7- Войти в арену");
            return true;
        }

        Arena arena = null;

        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length < 2) {
                    player.sendMessage("§cУкажите название арены: §6/es create <название>");
                    return true;
                }
                arenaManager.createArena(args[1]);
                player.sendMessage("§aАрена §6" + args[1] + " §aсоздана!");
                break;

            case "join":
                if (args.length < 2) {
                    player.sendMessage("§cУкажите название арены: §6/es join <название>");
                    return true;
                }
                arena = arenaManager.getArena(args[1]);
                if (arena == null) {
                    player.sendMessage("§cАрена не найдена!");
                    return true;
                }
                arena.join(player);
                break;
            case "addchest":
                if (args.length < 2) {
                    player.sendMessage("§cИспользуйте: §6/es addchest <арена>");
                    return true;
                }
                arena = arenaManager.getArena(args[1]);
                if (arena == null) {
                    player.sendMessage("§cАрена не найдена!");
                    return true;
                }
                arena.addChestLocation(player.getLocation());
                player.sendMessage("§aСундук добавлен на арену §6" + args[1] + "§a!");
                break;

            case "addtrader":
                if (args.length < 3) {
                    player.sendMessage("§cИспользуйте: §6/es addtrader <арена> <тип>");
                    return true;
                }
                arena = arenaManager.getArena(args[1]);
                if (arena == null) {
                    player.sendMessage("§cАрена не найдена!");
                    return true;
                }
                arena.addTraderLocation(player.getLocation());
                player.sendMessage("§aТорговец добавлен на арену §6" + args[1] + "§a!");
                break;

            case "quests":
                questManager.checkQuests(player);
                break;

            case "menu":
                QuestGUI.open(player, questManager);
                break;

            default:
                player.sendMessage("§cНеизвестная команда!");
                break;
        }
        return true;
    }
}
