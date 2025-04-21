package com.example.escapeplugin.quests;

import org.bukkit.entity.Player;
import java.util.*;

public class QuestManager
{
    private final Map<Player, List<Quest>> activeQuests = new HashMap<>();

    public void assignQuest(Player player, Quest quest) {
        if (!activeQuests.containsKey(player)) {
            activeQuests.put(player, new ArrayList<>());
        }
        activeQuests.get(player).add(quest);
        player.sendMessage("§eНовый квест: §6" + quest.name + "§e - " + quest.description);
    }

    public void checkQuests(Player player) {
        if (activeQuests.containsKey(player)) {
            player.sendMessage("§aАктивные квесты:");
            for (Quest quest : activeQuests.get(player)) {
                player.sendMessage("§6- " + quest.name + ": §e" + quest.progress + "§6/§e" + quest.targetCount);
            }
        }
    }

    public boolean hasQuests(Player p) { return activeQuests.containsKey(p); }
    public List<Quest> getQuests(Player p) { return activeQuests.get(p); }
}
