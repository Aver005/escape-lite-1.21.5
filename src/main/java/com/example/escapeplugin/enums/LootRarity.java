package com.example.escapeplugin.enums;

public enum LootRarity {
    ARTIFACT(1, 0.04),
    LEGENDARY(2, 0.10),
    EPIC(5, 0.14),
    RARE(Integer.MAX_VALUE, 0.32),
    COMMON(Integer.MAX_VALUE, 0.40);

    private final int maxPerChest;
    private final double baseChance;

    LootRarity(int maxPerChest, double baseChance) 
    {
        this.maxPerChest = maxPerChest;
        this.baseChance = baseChance;
    }

    public int getMaxPerChest() {
        return maxPerChest;
    }

    public double getBaseChance() {
        return baseChance;
    }
}