package com.example.escapeplugin.arena;

import com.example.escapeplugin.EscapePlugin;
import com.example.escapeplugin.gui.LootCategoryGUI;
import com.example.escapeplugin.gui.TraderSelectionGUI;
import com.example.escapeplugin.traders.TraderManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class SetupTools implements Listener 
{
    // Предметы для настройки
    public static final ItemStack TRADER_EGG = createSetupItem(
            Material.VILLAGER_SPAWN_EGG,
            "§aЯйцо торговца",
            "§7Кликните для выбора типа торговца");

    public static final ItemStack BEACON = createSetupItem(
            Material.BEACON,
            "§aТочка спавна игрока",
            "§7Установите для отметки точки спавна");

    public static final ItemStack CHEST = createSetupItem(
            Material.CHEST,
            "§aСундук с лутом",
            "§7Установите для выбора категории лута");

    private static ItemStack createSetupItem(Material material, String name, String... lore) 
    {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    public void giveSetupTools(Player player) 
    {
        player.getInventory().addItem(TRADER_EGG, BEACON, CHEST);
        player.sendMessage("§aВы получили предметы для настройки арены!");
    }

    @EventHandler
    public void onSetupItemUse(PlayerInteractEvent event) 
    {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null)
            return;

            
        EscapePlugin plugin = EscapePlugin.getInstance();
        ArenaManager arenaManager = plugin.getArenaManager();
        TraderManager traderManager = plugin.getTraderManager();
        Player player = event.getPlayer();
        Arena arena = arenaManager.getPlayerArena(player);
        if (arena == null) {
            player.sendMessage("§cВы должны находиться на арене для настройки!");
            return;
        }

        if (item.isSimilar(TRADER_EGG)) {
            event.setCancelled(true);
            new TraderSelectionGUI(arenaManager, traderManager, player, arena).open();
        } else if (item.isSimilar(BEACON)) {
            event.setCancelled(true);
            if (event.getClickedBlock() != null) {
                arena.addPlayerSpawn(event.getClickedBlock().getLocation());
                player.sendMessage("§aТочка спавна игрока добавлена!");
            }
        } else if (item.isSimilar(CHEST)) {
            event.setCancelled(true);
            if (event.getClickedBlock() != null) {
                new LootCategoryGUI(arenaManager, player, arena).open();
            }
        }
    }
}
