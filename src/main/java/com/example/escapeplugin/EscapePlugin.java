package com.example.escapeplugin;

import com.example.escapeplugin.arena.ArenaManager;
import com.example.escapeplugin.commands.EscapeCommand;
import com.example.escapeplugin.listeners.BlockBreakListener;
import com.example.escapeplugin.loot.LootManager;
import com.example.escapeplugin.quests.*;
import com.example.escapeplugin.traders.TraderManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class EscapePlugin extends JavaPlugin
{
    private static EscapePlugin instance;
    private ArenaManager arenaManager;
    private QuestManager questManager;
    private LootManager lootManager;
    private TraderManager traderManager;

    @Override
    public void onEnable()
    {
        // Создаем папку плагина, если её нет
        saveDefaultConfig();

        // Инициализация менеджера арен
        instance = this;
        arenaManager = new ArenaManager(this);
        questManager = new QuestManager();
        lootManager = new LootManager();
        traderManager = new TraderManager();

        Arrays.asList(
            // Квесты на убийство игроков (PvP)
            new KillQuest(3), // Охотник (убить 3 игроков)
            new KillQuest(5), // Охотник-эксперт (убить 5 игроков)
            new KillQuest(10), // Легендарный дуэлянт (убить 10 игроков)

            // Квесты на убийство мобов
            new MobHunterQuest(5, EntityType.ZOMBIE), // Истребитель зомби (5 зомби)
            new MobHunterQuest(3, EntityType.SKELETON), // Охотник на скелетов (3 скелета)
            new MobHunterQuest(1, EntityType.ENDER_DRAGON), // Убийца дракона (1 эндер-дракон)
            new MobHunterQuest(3, EntityType.WITHER), // Истребитель иссушителей (3 иссушителя)
            new MobHunterQuest(10, EntityType.CREEPER), // Взрывной эксперт (10 криперов)
            new MobHunterQuest(5, EntityType.ENDERMAN), // Охотник на эндерменов (5 эндерменов)
            new MobHunterQuest(20, EntityType.SPIDER), // Паучья чума (20 пауков)

            // Квесты на сбор ресурсов
            new GatherQuest(Material.DIAMOND, 5), // Алмазный искатель (5 алмазов)
            new GatherQuest(Material.EMERALD, 10), // Искатель изумрудов (10 изумрудов)
            new GatherQuest(Material.IRON_INGOT, 20), // Железный магнат (20 железных слитков)
            new GatherQuest(Material.GOLD_INGOT, 15), // Золотой запас (15 золотых слитков)
            new GatherQuest(Material.NETHERITE_SCRAP, 4), // Незеритовый охотник (4 незерита)
            new GatherQuest(Material.BLAZE_ROD, 3), // Ловец огня (3 стержня ифрита)
            new GatherQuest(Material.ENDER_PEARL, 8), // Коллекционер жемчуга (8 жемчугов Эндера)

            // Квесты на добычу (копание блоков)
            new MiningQuest(Material.DIAMOND_ORE, 3), // Алмазный шахтер (3 алмазные руды)
            new MiningQuest(Material.EMERALD_ORE, 5), // Изумрудный шахтер (5 изумрудных руд)
            new MiningQuest(Material.ANCIENT_DEBRIS, 2), // Незеритовый копатель (2 древних обломка)
            new MiningQuest(Material.REDSTONE_ORE, 15), // Красный электрик (15 редстоуновых руд)
            new MiningQuest(Material.LAPIS_ORE, 10), // Синий геолог (10 лазуритовых руд)

            // Квесты на путешествия (достижение локаций)
            new TravelQuest(new Location(null, 100, 70, -200)), // Таинственный остров (x=100, z=-200)
            new TravelQuest(new Location(null, 0, 80, 1000)), // Дальние земли (x=0, z=1000)
            new TravelQuest(new Location(null, -300, 65, 500)), // Затерянный каньон (x=-300, z=500)
            new TravelQuest(new Location(null, 200, 120, -800)), // Горная вершина (x=200, z=-800)
            new TravelQuest(new Location(null, 1500, 70, 1500)), // Край света (x=1500, z=1500)

            // Специальные квесты
            new MobHunterQuest(1, EntityType.ELDER_GUARDIAN), // Охотник на древних стражей (1 древний страж)
            new MobHunterQuest(3, EntityType.WITHER_SKELETON), // Охотник на иссушенных скелетов (3 скелета-иссушителя)
            new GatherQuest(Material.TOTEM_OF_UNDYING, 1), // Коллекционер тотемов (1 тотем бессмертия)
            new GatherQuest(Material.HEART_OF_THE_SEA, 1), // Сердце океана (1 сердце моря)
            new GatherQuest(Material.ECHO_SHARD, 3), // Искатель эха (3 осколка эха)
            new GatherQuest(Material.DISC_FRAGMENT_5, 1) // Археолог (1 фрагмент диска 5)
        ).forEach(
            quest -> getServer().getPluginManager().registerEvents(quest, this)
        );


        // Регистрация команд и событий
        getCommand("es").setExecutor(new EscapeCommand(arenaManager, questManager));
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);

        getLogger().info("Плагин Escape успешно загружен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Плагин Escape выгружен.");
    }

    public static EscapePlugin getInstance()
    {
        return instance;
    }
}
