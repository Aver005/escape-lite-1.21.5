package com.example.escapeplugin.entities;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import com.example.escapeplugin.enums.TraderType;

public class Trader
{
    private Villager villager;
    private TraderType type;
    private Location location;

    public Trader(Location location, TraderType type) 
    {
        this.location = location;
        this.type = type;
        this.villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        this.villager.setAI(false);
        this.villager.setInvulnerable(true);
        this.villager.setProfession(type.getProfession());
    }

    public void remove() 
    {
        if (villager == null) return;
        villager.remove();
    }

    public TraderType getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public Villager getVillager() {
        return villager;
    }
}
