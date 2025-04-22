package com.example.escapeplugin.quests;

import com.example.escapeplugin.EscapePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuestLoader
{
    private final EscapePlugin plugin;
    private FileConfiguration config;

    public QuestLoader(EscapePlugin plugin)
    {
        this.plugin = plugin;
        loadQuestsConfig();
    }

    public void loadQuestsConfig()
    {
        File configFile = new File(plugin.getDataFolder(), "quests.yml");
        if (!configFile.exists()) plugin.saveResource("quests.yml", false);
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public List<Quest> loadQuests()
    {
        List<Quest> quests = new ArrayList<>();
        List<Map<?, ?>> questsList = config.getMapList("quests");

        for (Map<?, ?> questMap : questsList)
        {
            try
            {
                Quest quest = parseQuest(questMap);
                if (quest == null) continue;
                quests.add(quest);
            }
            catch (Exception e)
            {
                plugin.getLogger().warning(
                    "Ошибка загрузки квеста: " + e.getMessage()
                );
            }
        }

        return quests;
    }

    private Quest parseQuest(Map<?, ?> questMap)
    {
        String type = ((String) questMap.get("type")).toUpperCase();
        String name = (String) questMap.get("name");
        int targetCount = (int) questMap.get("targetCount");

        switch (type)
        {
            case "KILL":
                return new KillQuest(name, targetCount);

            case "MOB_HUNT":
                EntityType mobType = EntityType.valueOf((String) questMap.get("mobType"));
                return new MobHunterQuest(name, targetCount, mobType);

            case "GATHER":
                Material material = Material.valueOf((String) questMap.get("material"));
                return new GatherQuest(name, material, targetCount);

            case "MINING":
                Material blockType = Material.valueOf((String) questMap.get("material"));
                return new MiningQuest(name, blockType, targetCount);

            case "TRAVEL":
                World world = Bukkit.getWorld((String) questMap.get("world"));
                int x = (int) questMap.get("x");
                int y = (int) questMap.get("y");
                int z = (int) questMap.get("z");
                return new TravelQuest(name, new Location(world, x, y, z));

            default:
                plugin.getLogger().warning("Неизвестный тип квеста: " + type);
                return null;
        }
    }

    public void registerQuests()
    {
        loadQuests().forEach(quest ->
            Bukkit.getPluginManager().registerEvents(quest, plugin)
        );
    }
}
