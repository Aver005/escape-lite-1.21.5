package com.example.escapeplugin.entities;

import org.bukkit.Location;
import org.bukkit.Material;

public class SpawnBlock 
{
    private Material material;
    private Location location;

    public SpawnBlock(Material material, Location location) 
    {
        this.material = material;
        this.location = location;
    }

    public void place(Location l) 
    {
        location = l;
        location.getBlock().setType(material);
    }

    public Material getMaterial() 
    {
        return material;
    }

    public Location getLocation() 
    {
        return location;
    }
}
