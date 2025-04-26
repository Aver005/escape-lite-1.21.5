package com.example.escapeplugin.arena;

import org.bukkit.Location;

public class ChestLocation {
    private final Location location;
    private final String lootCategory;

    public ChestLocation(Location location, String lootCategory) {
        this.location = location;
        this.lootCategory = lootCategory;
    }

    public Location getLocation() {
        return location;
    }

    public String getLootCategory() {
        return lootCategory;
    }
}