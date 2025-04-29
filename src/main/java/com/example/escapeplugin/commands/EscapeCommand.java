package com.example.escapeplugin.commands;

import com.example.escapeplugin.EscapePlugin;
import com.example.escapeplugin.arena.Arena;
import com.example.escapeplugin.arena.ArenaManager;
import com.example.escapeplugin.arena.ArenaPlayer;
import com.example.escapeplugin.arena.SetupTools;
import com.example.escapeplugin.gui.QuestGUI;
import com.example.escapeplugin.loot.LootManager;
import com.example.escapeplugin.quests.QuestManager;
import com.example.escapeplugin.traders.TraderManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class EscapeCommand implements CommandExecutor
{
    private final ArenaManager arenaManager;
    private final QuestManager questManager;
    private final LootManager lootManager;
    private final TraderManager traderManager;

    public EscapeCommand()
    {
        this.arenaManager = EscapePlugin.getInstance().getArenaManager();
        this.questManager = EscapePlugin.getInstance().getQuestManager();
        this.lootManager = EscapePlugin.getInstance().getLootManager();
        this.traderManager = EscapePlugin.getInstance().getTraderManager();
    }

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
                Arena arena = EscapePlugin.getInstance().getArenaManager().getArena(args[1]);
                if (arena == null)
                {
                    player.sendMessage("§cАрена не найдена!");
                    return;
                }
                
                SetupTools setupTools = new SetupTools();
                setupTools.giveSetupTools(player);
                player.sendMessage("§aИспользуйте полученные предметы для настройки арены!");
            }
        ),

        CREATE(
            "create",
            "§6/es create <название> §7- Создать арену",
            2,
            (player, args) ->
            {
                EscapePlugin.getInstance().getArenaManager().createArena(args[1]);
                player.sendMessage("§aАрена §6" + args[1] + "§a создана!");
            }
        ),

        REMOVE(
            "remove",
            "§6/es remove <название> §7- Удалить арену",
            2,
            (player, args) ->
            {
                EscapePlugin.getInstance().getArenaManager().removeArena(args[1]);
                player.sendMessage("§aАрена §6" + args[1] + "§a удалена!");
            }
        ),

        JOIN(
            "join",
            "§6/es join <название> §7- Войти в арену",
            2,
            (player, args) ->
            {
                Arena arena = EscapePlugin.getInstance().getArenaManager().getArena(args[1]);
                if (arena == null)
                {
                    player.sendMessage("§cАрена не найдена!");
                    return;
                }

                arena.join(player);
            }
        ),

        LEAVE(
            "leave",
            "§6/es leave §7- Выйти с арены",
            1,
            (player, args) ->
            {
                ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
                if (arenaPlayer == null) return;

                Arena arena = arenaPlayer.getArena();
                if (arena == null)
                {
                    player.sendMessage("§cВы не в игре.");
                    return;
                }

                arena.leave(player);
            }
        ),

        ADD_CHEST(
            "addchest",
            "§6/es addchest <арена> §7- Добавить сундук",
            2,
            (player, args) ->
            {
                Arena arena = EscapePlugin.getInstance().getArenaManager().getArena(args[1]);
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
            (player, args) ->
            {
                Arena arena = EscapePlugin.getInstance().getArenaManager().getArena(args[1]);
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
            (player, args) -> EscapePlugin.getInstance().getQuestManager().checkQuests(player)
        ),

        MENU(
            "menu",
            "§6/es menu §7- Открыть меню квестов",
            1,
            (player, args) -> QuestGUI.open(player, EscapePlugin.getInstance().getQuestManager())
        ),

        MIN_PLAYERS(
            "setmin",
            "§6/es setmin <арена> <количество> §7- Открыть меню квестов",
            3,
            (player, args) ->
            {
                Arena arena = EscapePlugin.getInstance().getArenaManager().getArena(args[1]);
                if (arena == null)
                {
                    player.sendMessage("§cАрена не найдена!");
                    return;
                }
                int count = Integer.parseInt(args[2]);
                arena.setMinPlayersToStart(count);
                player.sendMessage("§aМинимальное количество игроков для арены §6" + args[1] + "§a установлено!");
            }
        ),

        DELETE_SPAWN(
            "deletespawn",
            "§6/es deletespawn <арена> §7- Удалить точку спавна",
            2,
            (player, args) ->
            {
                Arena arena = EscapePlugin.getInstance().getArenaManager().getArena(args[1]);
                if (arena == null)
                {
                    player.sendMessage("§cАрена не найдена!");
                    return;
                }
                SetupTools setupTools = new SetupTools();
                setupTools.deleteSpawnPoint(arena, player.getLocation());
                player.sendMessage("§aТочка спавна удалена с арены §6" + args[1] + "§a!");
            }
        ),

        DELETE_TRADER(
            "deletetrader",
            "§6/es deletetrader <арена> §7- Удалить торговца",
            2,
            (player, args) ->
            {
                Arena arena = EscapePlugin.getInstance().getArenaManager().getArena(args[1]);
                if (arena == null)
                {
                    player.sendMessage("§cАрена не найдена!");
                    return;
                }
                SetupTools setupTools = new SetupTools();
                setupTools.deleteTraderPoint(arena, player.getLocation());
                player.sendMessage("§aТорговец удален с арены §6" + args[1] + "§a!");
            }
        ),

        DELETE_CHEST(
            "deletechest",
            "§6/es deletechest <арена> §7- Удалить сундук",
            2,
            (player, args) ->
            {
                Arena arena = EscapePlugin.getInstance().getArenaManager().getArena(args[1]);
                if (arena == null)
                {
                    player.sendMessage("§cАрена не найдена!");
                    return;
                }
                SetupTools setupTools = new SetupTools();
                setupTools.deleteChestPoint(arena, player.getLocation());
                player.sendMessage("§aСундук удален с арены §6" + args[1] + "§a!");
            }
        ),

        RELOAD(
            "reload",
            "§6/es reload §7- Перезагрузить конфигурации торговцев",
            1,
            (player, args) ->
            {
                if (!player.hasPermission("escape.reload"))
                {
                    player.sendMessage("§cУ вас нет прав на использование этой команды!");
                    return;
                }

                try {
                    // Reload trader configs
                    EscapePlugin.getInstance().getTraderManager().loadTraders();
                    player.sendMessage("§aКонфигурации торговцев успешно перезагружены!");
                } catch (Exception e) {
                    player.sendMessage("§cОшибка при перезагрузке конфигураций: " + e.getMessage());
                }
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
