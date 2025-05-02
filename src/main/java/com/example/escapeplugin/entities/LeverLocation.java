package com.example.escapeplugin.entities;

import org.bukkit.Location;

public class LeverLocation {
    private Location location;
    private String name;

    public LeverLocation(Location location, String name) {
        this.location = location;
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }
}