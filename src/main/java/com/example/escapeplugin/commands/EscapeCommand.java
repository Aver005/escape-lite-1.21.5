package com.example.escapeplugin.commands;

import com.example.escapeplugin.EscapePlugin;
import com.example.escapeplugin.entities.Arena;
import com.example.escapeplugin.entities.Prisoner;
import com.example.escapeplugin.enums.ArenaStatus;
import com.example.escapeplugin.enums.LootRarity;
import com.example.escapeplugin.managers.ArenaStorage;
import com.example.escapeplugin.managers.LootManager;
import com.example.escapeplugin.managers.PrisonerStorage;
import com.example.escapeplugin.managers.SetupTools;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.io.IOException;
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
            "§6/es enable <Имя арены> §7- Включить арену",
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
        ),
        
        DISABLE(
            "disable",
            "§6/es disable <Имя арены> §7- Выключить арену",
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

                arena.setStatus(ArenaStatus.DISABLED);
            }
        ),
        
        SAVE(
            "save",
            "§6/es save <Имя арены> §7- Сохранить арену",
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

                ArenaStorage.saveArena(arena);
                player.sendMessage("§bАрена сохранена");
            }
        ),
        
        LOAD(
            "load",
            "§6/es load <Имя арены> §7- Загрузить арену",
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

                ArenaStorage.loadArena(arena);
                player.sendMessage("§bАрена загружена");
            }
        ),
        
        ADDLOOT(
            "addloot",
            "§6/es addloot <редкость> <шанс> §7- Добавить лут из руки",
            3,
            (player, args) ->
            {
                try 
                {
                    
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (item == null || item.getType().isAir()) 
                    {
                        player.sendMessage("§cВозьмите предмет в руку!");
                        return;
                    }

                    String key = args[1].toUpperCase() + "_" + item.getType();
                    LootRarity rarity = LootRarity.valueOf(args[1].toUpperCase());
                    double chance = Double.parseDouble(args[2]);
                    
                    LootManager lootManager = EscapePlugin.getInstance().getLootManager();
                    lootManager.addLootItem(key, item, rarity, chance);
                    player.sendMessage("§aПредмет добавлен в лут!");
                } 
                catch (IllegalArgumentException e) 
                {
                    player.sendMessage("§cНекорректные аргументы! Используйте: ARTIFACT/LEGENDARY/EPIC/RARE/COMMON");
                } 
                catch (Exception e) 
                {
                    player.sendMessage("§cОшибка при добавлении лута: " + e.getMessage());
                }
            }
        ),
        
        SAVELOOT(
            "saveloot",
            "§6/es saveloot §7- Сохранить лут",
            1,
            (player, args) ->
            {
                LootManager lootManager = EscapePlugin.getInstance().getLootManager();
                try { lootManager.saveLootConfig(); } 
                catch (IOException e) { e.printStackTrace(); }
            }
        ),
        
        LOADLOOT(
            "loadloot",
            "§6/es loadloot §7- Загрузить лут",
            1,
            (player, args) ->
            {
                LootManager lootManager = EscapePlugin.getInstance().getLootManager();
                lootManager.loadLootConfig();
            }
        ),
        
        CREATEITEM(
            "createitem",
            "§6/es createitem [урон] [цвет] [название] [описание] §7- Создать предмет с параметрами",
            2,
            (player, args) -> 
            {
                try 
                {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    ItemMeta meta = item.getItemMeta();
                    
                    // Durability
                    if (args.length > 1) 
                    {
                        int damage = Integer.parseInt(args[1]);
                        Damageable dmg = (Damageable) meta; 
                        dmg.setDamage(damage);
                    }
                    
                    // Color (for leather armor)
                    if (args.length > 2 && meta instanceof LeatherArmorMeta) 
                    {
                        int color = Integer.parseInt(args[2].replace("#", ""), 16);
                        ((LeatherArmorMeta) meta).setColor(Color.fromRGB(color));
                    }
                    
                    // Display name
                    if (args.length > 3) 
                    {
                        meta.setDisplayName(args[3].replace("_", " "));
                    }
                    
                    // Lore
                    if (args.length > 4) 
                    {
                        String[] loreLines = args[4].split("\\|");
                        meta.setLore(Arrays.asList(loreLines));
                    }
                    
                    item.setItemMeta(meta);
                } 
                catch (IllegalArgumentException e) 
                {
                    player.sendMessage("§cНекорректные параметры! Используйте: /es createitem [урон] [цвет] [название] [описание]");
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
