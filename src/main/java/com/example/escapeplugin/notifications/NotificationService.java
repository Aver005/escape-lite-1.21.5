package com.example.escapeplugin.notifications;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class NotificationService {
    public void notifyPlayers(String message) {
        Bukkit.broadcastMessage(message);
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendTitle("", ChatColor.GOLD + "Сундуки перезаполнены", 10, 70, 20);
        });
    }

    public void notifyPlayers(String message, String title) {
        Bukkit.broadcastMessage(message);
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendTitle("", ChatColor.GOLD + title, 10, 70, 20);
        });
    }
}