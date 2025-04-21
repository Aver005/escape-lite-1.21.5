package com.example.escapeplugin.commands;

import com.example.escapeplugin.arena.Arena;
import com.example.escapeplugin.arena.ArenaManager;
import com.example.escapeplugin.gui.QuestGUI;
import com.example.escapeplugin.quests.QuestManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class EscapeCommand implements CommandExecutor
{
    private final ArenaManager arenaManager;
    private final QuestManager questManager;

    public EscapeCommand(ArenaManager arenaManager, QuestManager questManager)
    {
        this.arenaManager = arenaManager;
        this.questManager = questManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage("§cТолько игроки могут использовать эту команду!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0)
        {
            sendUsage(player);
            return true;
        }

        CommandType commandType = CommandType.fromString(args[0]);
        if (commandType == null)
        {
            player.sendMessage("§cНеизвестная команда!");
            sendUsage(player);
            return true;
        }

        if (!commandType.validateArgs(player, args)) return true;
        commandType.execute(player, args, arenaManager, questManager);
        return true;
    }

    private void sendUsage(Player player)
    {
        player.sendMessage("§eДоступные команды:");
        Arrays.stream(CommandType.values())
            .map(CommandType::getUsage)
            .forEach(player::sendMessage);
    }

    private enum CommandType
    {
        CREATE(
            "create",
            "§6/es create <название> §7- Создать арену",
            2,
            (player, args, arenaManager, questManager) ->
            {
                arenaManager.createArena(args[1]);
                player.sendMessage("§aАрена §6" + args[1] + " §aсоздана!");
            }
        ),

        JOIN(
            "join",
            "§6/es join <название> §7- Войти в арену",
            2,
            (player, args, arenaManager, questManager) ->
            {
                Arena arena = arenaManager.getArena(args[1]);
                if (arena == null)
                {
                    player.sendMessage("§cАрена не найдена!");
                    return;
                }
                arena.join(player);
            }
        ),

        ADD_CHEST(
            "addchest",
            "§6/es addchest <арена> §7- Добавить сундук",
            2,
            (player, args, arenaManager, questManager) ->
            {
                Arena arena = arenaManager.getArena(args[1]);
                if (arena == null)
                {
                    player.sendMessage("§cАрена не найдена!");
                    return;
                }
                arena.addChestLocation(player.getLocation());
                player.sendMessage("§aСундук добавлен на арену §6" + args[1] + "§a!");
            }
        ),

        ADD_TRADER(
            "addtrader",
            "§6/es addtrader <арена> <тип> §7- Добавить торговца",
            3,
            (player, args, arenaManager, questManager) ->
            {
                Arena arena = arenaManager.getArena(args[1]);
                if (arena == null)
                {
                    player.sendMessage("§cАрена не найдена!");
                    return;
                }
                arena.addTraderLocation(player.getLocation());
                player.sendMessage("§aТорговец добавлен на арену §6" + args[1] + "§a!");
            }
        ),

        QUESTS(
            "quests",
            "§6/es quests §7- Проверить квесты",
            1,
            (player, args, arenaManager, questManager) -> questManager.checkQuests(player)
        ),

        MENU(
            "menu",
            "§6/es menu §7- Открыть меню квестов",
            1,
            (player, args, arenaManager, questManager) -> QuestGUI.open(player, questManager)
        );

        private final String label;
        private final String usage;
        private final int requiredArgs;
        private final CommandExecutorCallback callback;

        CommandType(String label, String usage, int requiredArgs, CommandExecutorCallback callback)
        {
            this.label = label;
            this.usage = usage;
            this.requiredArgs = requiredArgs;
            this.callback = callback;
        }

        public String getLabel() { return label; }
        public String getUsage() { return usage; }

        public boolean validateArgs(Player player, String[] args)
        {
            if (args.length < requiredArgs)
            {
                player.sendMessage("§cНедостаточно аргументов! Используйте: " + usage);
                return false;
            }

            return true;
        }

        public void execute(
            Player player, String[] args,
            ArenaManager arenaManager,
            QuestManager questManager
        )
        {
            callback.accept(player, args, arenaManager, questManager);
        }

        public static CommandType fromString(String text)
        {
            for (CommandType type : CommandType.values())
            {
                if (!type.getLabel().equalsIgnoreCase(text)) continue;
                return type;
            }

            return null;
        }
    }

    @FunctionalInterface
    private interface CommandExecutorCallback
    {
        void accept(
            Player player, String[] args,
            ArenaManager arenaManager, QuestManager questManager
        );
    }
}
