package com.example.escapeplugin.quests;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class Quest implements Listener
{
    protected String name;
    protected String description;
    protected int targetCount;
    protected int progress;

    public Quest(String name, String description, int targetCount)
    {
        this.name = name;
        this.description = description;
        this.targetCount = targetCount;
        this.progress = 0;
    }

    public abstract void onComplete(Player player);

    public void updateProgress(Player player, int amount)
    {
        progress += amount;
        player.sendMessage("§aПрогресс квеста '" + name + "': §e" + progress + "§6/§e" + targetCount);
        if (progress < targetCount) return;
        onComplete(player);
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getTargetCount() { return targetCount; }
    public int getProgress() { return progress; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setTargetCount(int targetCount) { this.targetCount = targetCount; }
    public void setProgress(int progress) { this.progress = progress; }
}
