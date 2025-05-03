package com.example.escapeplugin.listeners;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDropItemEvent;

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
        if (!arena.isPlaying()) return;

        e.setCancelled(true);
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
}
