package com.example.escapeplugin.commands;

import com.example.escapeplugin.entities.Arena;
import com.example.escapeplugin.entities.Prisoner;
import com.example.escapeplugin.enums.ArenaStatus;
import com.example.escapeplugin.managers.ArenaStorage;
import com.example.escapeplugin.managers.PrisonerStorage;
import com.example.escapeplugin.managers.SetupTools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class EscapeCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender instanceof Player player))
        {
            sender.sendMessage("§cТолько игроки могут использовать эту команду!");
            return true;
        }

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
        commandType.execute(player, args);
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
        SETUP(
            "setup",
            "§6/es setup <название> §7- Получить предметы для настройки арены",
            2,
            (player, args) ->
            {
                String name = args[1].toUpperCase().strip();
                Arena arena = ArenaStorage.get(name);

                if (arena == null)
                {
                    player.sendMessage("§cАрена не найдена!");
                    return;
                }

                SetupTools.giveSetupTools(player, arena);
                player.sendMessage("§aИспользуйте полученные предметы для настройки арены!");
            }
        ),

        CREATE(
            "create",
            "§6/es create <название> §7- Создать арену",
            2,
            (player, args) ->
            {
                String name = args[1].toUpperCase().strip();
                ArenaStorage.create(name);
                player.sendMessage("§aАрена §6" + name + "§a создана!");
            }
        ),

        REMOVE(
            "remove",
            "§6/es remove <название> §7- Удалить арену",
            2,
            (player, args) ->
            {
                String name = args[1].toUpperCase().strip();
                Arena arena = ArenaStorage.get(name);

                if (arena == null)
                {
                    player.sendMessage("§cАрена не найдена!");
                    return;
                }

                ArenaStorage.remove(arena);
                player.sendMessage("§aАрена §6" + name + "§a удалена!");
            }
        ),

        JOIN(
            "join",
            "§6/es join <название> §7- Войти в арену",
            2,
            (player, args) ->
            {
                String name = args[1].toUpperCase().strip();
                Arena arena = ArenaStorage.get(name);

                if (arena == null)
                {
                    player.sendMessage("§cАрена не найдена!");
                    return;
                }

                Prisoner prisoner = PrisonerStorage.get(player);
                arena.join(prisoner);
            }
        ),

        LEAVE(
            "leave",
            "§6/es leave §7- Выйти с арены",
            2,
            (player, args) ->
            {
                String name = args[1].toUpperCase().strip();
                Arena arena = ArenaStorage.get(name);
                
                if (arena == null)
                {
                    player.sendMessage("§cАрена не найдена!");
                    return;
                }

                Prisoner prisoner = PrisonerStorage.get(player);
                arena.leave(prisoner);
            }
        ),
        
        ENABLE(
            "enable",
            "§6/es enabel <Имя арены> §7- Выйти с арены",
            2,
            (player, args) ->
            {
                String name = args[1].toUpperCase().strip();
                Arena arena = ArenaStorage.get(name);
                
                if (arena == null)
                {
                    player.sendMessage("§cАрена не найдена!");
                    return;
                }

                arena.setStatus(ArenaStatus.WAITING);
            }
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

        public void execute(Player player, String[] args)
        {
            callback.accept(player, args);
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
        void accept(Player player, String[] args);
    }
}
