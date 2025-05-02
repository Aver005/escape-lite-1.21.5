package com.example.escapeplugin.enums;

import org.bukkit.Material;

public enum TraderType {
    COOK("Повар", Material.BREAD),
    GUNMAKER("Оружейник", Material.IRON_SWORD),
    TOOLMAKER("Инструментальщик", Material.IRON_PICKAXE),
    MAGICIAN("Маг", Material.ENCHANTED_BOOK),
    TRAPPER("Капканщик", Material.TRIPWIRE_HOOK),
    MYSTERIOUS("Таинственный", Material.ENDER_EYE);

    private final String displayName;
    private final Material icon;

    TraderType(String displayName, Material icon) {
        this.displayName = displayName;
        this.icon = icon;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public Material getIcon() {
        return icon;
    }
}
