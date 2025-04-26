package com.example.escapeplugin.arena;

import org.bukkit.Location;

public class TraderLocation {
    private final Location location;
    private final String traderType;

    public TraderLocation(Location location, String traderType) {
        this.location = location;
        this.traderType = traderType;
    }

    public Location getLocation() {
        return location;
    }

    public String getTraderType() {
        return traderType;
    }
}