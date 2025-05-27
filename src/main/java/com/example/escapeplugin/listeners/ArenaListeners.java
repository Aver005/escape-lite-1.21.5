package com.example.escapeplugin.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;

import com.example.escapeplugin.entities.Arena;
import com.example.escapeplugin.entities.Prisoner;
import com.example.escapeplugin.managers.PrisonerStorage;

public class ArenaListeners implements Listener
{
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e)
    {
        Prisoner prisoner = PrisonerStorage.get(e.getPlayer());
        Arena arena = prisoner.getArena();
        if (arena == null) return;
        if (!arena.isPlaying()) return;

        Material material = e.getBlock().getType();

        if (material.equals(Material.IRON_BLOCK))
        {
            if (prisoner.getSpawnBlock().getLocation().equals(e.getBlock().getLocation()))
            {
                e.setCancelled(true);
                e.getBlock().setType(Material.AIR);
                prisoner.setSpawnBlock(null);
                ItemStack spawnBlock = new ItemStack(Material.IRON_BLOCK);
                spawnBlock.getItemMeta().setDisplayName("§6Ваш спаси-блок");
                e.getPlayer().getInventory().addItem(spawnBlock);
                return;
            }

            Prisoner who = arena.isSpawnBlock(e.getBlock().getLocation());
            if (who != null)
            {
                e.setCancelled(true);
                arena.broadcast("§c" + e.getPlayer().getName() + " §eуничтожил спаси-блок §b" + who.getPlayer().getName());
                e.getBlock().setType(Material.AIR);
                who.setSpawnBlock(null);
                who.getPlayer().sendMessage("§cВаш спаси-блок уничтожен.");
                who.getPlayer().sendMessage("§eТеперь Вы не возродитесь.");
                who.getPlayer().sendTitle("§cПоломка", "§eВам спаси-блок сломали");
                return;
            }

            arena.addRestoredBlock(e.getBlock().getLocation(), material);
            return;
        }

        if (material.name().endsWith("_LEAVES"))
        {
            arena.addRestoredBlock(e.getBlock().getLocation(), material);
            return;
        }
        
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e)
    {
        Prisoner prisoner = PrisonerStorage.get(e.getPlayer());
        Arena arena = prisoner.getArena();
        if (arena == null) return;

        Block b = e.getBlock();
        Material material = b.getType();

        if (arena.isWaiting())
        {
            e.setCancelled(true);
            return;
        }

        if (arena.isPlaying())
        {
            if (material.equals(Material.IRON_BLOCK))
            {
                if (e.getItemInHand().getItemMeta().getDisplayName().equals("§6Ваш спаси-блок"))
                {
                    prisoner.setSpawnBlock(b);
                    prisoner.getPlayer().sendMessage("§aВы разместили свой спаси-блок.");
                    return;
                }
            }

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(EntityDropItemEvent e)
    {
        if (!e.getEntityType().equals(EntityType.PLAYER)) return;
        Prisoner prisoner = PrisonerStorage.get((Player) e.getEntity());
        Arena arena = prisoner.getArena();
        if (arena == null) return;
        if (!arena.isPlaying()) return;

        arena.addDroppedItem(e.getItemDrop());
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e)
    {
        if (!e.getEntity().getType().equals(EntityType.PLAYER)) return;
        Player p = (Player) e.getEntity();
        Prisoner prisoner = PrisonerStorage.get(p);
        Arena arena = prisoner.getArena();
        if (arena == null) return;
        if (!arena.isPlaying()) return;

        double dmg = e.getDamage();
        if (dmg >= p.getHealth()) 
        {
            arena.playerDead(prisoner);
            e.setCancelled(true);
        }
    }
}
