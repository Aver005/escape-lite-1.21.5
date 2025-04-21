package com.example.escapeplugin.events;

import com.example.escapeplugin.EscapePlugin;
import com.example.escapeplugin.arena.Arena;
import com.example.escapeplugin.game.WaveEffects;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BossEvent
{
    private static final Map<EntityType, Double> MOB_WEIGHTS = new HashMap<EntityType, Double>() {{
        put(EntityType.ZOMBIE, 0.3);
        put(EntityType.SKELETON, 0.25);
        put(EntityType.SPIDER, 0.2);
        put(EntityType.CREEPER, 0.15);
        put(EntityType.WITCH, 0.1);
    }};

    private static final List<EntityType> BOSS_TYPES = Arrays.asList(
            EntityType.WITHER_SKELETON,
            EntityType.ENDERMAN,
            EntityType.BLAZE,
            EntityType.PIGLIN_BRUTE
    );

    /**
     * Начинает волну мобов с расширенными настройками
     *
     * @param arena Арена, на которой происходит событие
     * @param mobCount Базовое количество мобов
     * @param difficulty Уровень сложности (от 1 до 5)
     * @param specialBossChance Шанс появления босса (0.0-1.0)
     * @param waveEffects Специальные эффекты для волны
     */
    public static void startWave(
        Arena arena, int mobCount, int difficulty,
        double specialBossChance, WaveEffects waveEffects
    )
    {
        announceWaveStart(arena, difficulty);

        new BukkitRunnable()
        {
            int spawned = 0;
            boolean bossSpawned = false;

            @Override
            public void run()
            {
                if (spawned >= mobCount * difficulty && (!waveEffects.hasBoss() || bossSpawned))
                {
                    arena.broadcast("§aВолна завершена!");
                    cancel();
                    return;
                }

                for (Player player : arena.getPlayers())
                {
                    // Шанс спавна босса
                    if (!bossSpawned && waveEffects.hasBoss() &&
                            ThreadLocalRandom.current().nextDouble() < specialBossChance)
                    {
                        spawnBoss(arena, player.getLocation(), difficulty);
                        bossSpawned = true;
                        continue;
                    }

                    if (spawned < mobCount * difficulty)
                    {
                        spawnRandomMob(arena, player.getLocation(), difficulty, waveEffects);
                        spawned++;
                    }
                }
            }
        }.runTaskTimer(EscapePlugin.getInstance(), 0, 20 - difficulty * 3L);
    }

    private static void announceWaveStart(Arena arena, int difficulty)
    {
        String title = "§4§lВолна " + difficulty;
        String subtitle = "§cСложность: " + "★".repeat(difficulty);

        arena.getPlayers().forEach(player -> {
            player.sendTitle(title, subtitle, 10, 70, 20);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.8f);
        });
        arena.broadcast("§6Начинается волна сложности §e" + difficulty);
    }

    private static void spawnRandomMob(Arena arena, Location playerLoc, int difficulty, WaveEffects effects)
    {
        Location spawnLoc = calculateSpawnLocation(playerLoc);

        // Выбираем моба по весам
        EntityType mobType = getWeightedRandomMob();
        LivingEntity entity = (LivingEntity) spawnLoc.getWorld().spawnEntity(spawnLoc, mobType);

        // Усиливаем моба в зависимости от сложности
        enhanceMob(entity, difficulty, effects);

        // Эффекты при спавне
        spawnLoc.getWorld().spawnParticle(Particle.SMOKE, spawnLoc, 30, 0.5, 0.5, 0.5, 0.1);
        spawnLoc.getWorld().playSound(spawnLoc, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 0.8f, 1.0f);
    }

    private static void spawnBoss(Arena arena, Location center, int difficulty)
    {
        Location spawnLoc = calculateSpawnLocation(center);
        EntityType bossType = BOSS_TYPES.get(ThreadLocalRandom.current().nextInt(BOSS_TYPES.size()));
        LivingEntity boss = (LivingEntity) spawnLoc.getWorld().spawnEntity(spawnLoc, bossType);

        // Улучшаем босса
        boss.setCustomName("§4§lБосс Волны");
        boss.setCustomNameVisible(true);
        boss.setGlowing(true);

        // Больше усилений для босса
        enhanceMob(boss, difficulty + 2, WaveEffects.DEFAULT.withBossEffects());

        // Эпические эффекты появления
        spawnLoc.getWorld().spawnParticle(Particle.FLAME, spawnLoc, 100, 1, 1, 1, 0.3);
        spawnLoc.getWorld().playSound(spawnLoc, Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0f, 0.7f);

        arena.broadcast("§4§lПоявился босс!");
    }

    private static Location calculateSpawnLocation(Location playerLoc)
    {
        return playerLoc.clone().add(
            ThreadLocalRandom.current().nextDouble() * 20 - 10,
            0,
            ThreadLocalRandom.current().nextDouble() * 20 - 10
        );
    }

    private static EntityType getWeightedRandomMob()
    {
        double totalWeight = MOB_WEIGHTS.values().stream().mapToDouble(Double::doubleValue).sum();
        double random = ThreadLocalRandom.current().nextDouble() * totalWeight;

        for (Map.Entry<EntityType, Double> entry : MOB_WEIGHTS.entrySet())
        {
            if (random < entry.getValue()) {
                return entry.getKey();
            }
            random -= entry.getValue();
        }
        return EntityType.ZOMBIE; // fallback
    }

    private static void enhanceMob(LivingEntity entity, int difficulty, WaveEffects effects)
    {
        // Базовые усиления по сложности
        double healthMultiplier = 1 + (difficulty * 0.3);
        entity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(
            entity.getAttribute(Attribute.MAX_HEALTH).getBaseValue() * healthMultiplier
        );
        entity.setHealth(entity.getAttribute(Attribute.MAX_HEALTH).getBaseValue());

        entity.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(
            entity.getAttribute(Attribute.ATTACK_DAMAGE).getBaseValue() * (1 + difficulty * 0.2)
        );

        // Специальные эффекты волны
        if (effects.isFireResistant())
        {
            entity.addPotionEffect(
                new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0)
            );
        }

        if (effects.isInvisible())
        {
            entity.addPotionEffect(
                new PotionEffect(PotionEffectType.INVISIBILITY, 200, 0)
            );
        }

        if (effects.hasEquipment())
        {
            entity.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
            entity.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
        }
    }
}
