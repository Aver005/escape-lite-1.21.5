package com.example.escapeplugin.enums;

import org.bukkit.Material;
import org.bukkit.entity.Villager;

public enum TraderType {
    COOK("Повар", Material.BREAD, Villager.Profession.BUTCHER),
    GUNMAKER("Оружейник", Material.IRON_SWORD, Villager.Profession.WEAPONSMITH),
    TOOLMAKER("Инструментальщик", Material.IRON_PICKAXE, Villager.Profession.TOOLSMITH),
    MAGICIAN("Маг", Material.ENCHANTED_BOOK, Villager.Profession.CLERIC),
    TRAPPER("Капканщик", Material.TRIPWIRE_HOOK, Villager.Profession.LEATHERWORKER),
    MYSTERIOUS("Таинственный", Material.ENDER_EYE, Villager.Profession.NITWIT);

    private final String displayName;
    private final Material icon;
    private final Villager.Profession profession;

    TraderType(String displayName, Material icon, Villager.Profession profession) {
        this.displayName = displayName;
        this.icon = icon;
        this.profession = profession;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public Material getIcon() {
        return icon;
    }

    public Villager.Profession getProfession() {
        return profession;
    }
}
