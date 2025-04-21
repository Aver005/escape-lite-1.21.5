package com.example.escapeplugin.game;

public class WaveEffects
{
    public static final WaveEffects DEFAULT = new WaveEffects(
        false, false, false, false
    );

    private final boolean fireResistant;
    private final boolean invisible;
    private final boolean hasEquipment;
    private final boolean hasBoss;

    public WaveEffects(boolean fireResistant, boolean invisible, boolean hasEquipment, boolean hasBoss)
    {
        this.fireResistant = fireResistant;
        this.invisible = invisible;
        this.hasEquipment = hasEquipment;
        this.hasBoss = hasBoss;
    }

    public WaveEffects withBossEffects() {
        return new WaveEffects(true, false, true, true);
    }

    // Геттеры
    public boolean isFireResistant() { return fireResistant; }
    public boolean isInvisible() { return invisible; }
    public boolean hasEquipment() { return hasEquipment; }
    public boolean hasBoss() { return hasBoss; }
}