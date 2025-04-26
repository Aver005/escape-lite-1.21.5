package com.example.escapeplugin.arena;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.attribute.Attribute;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Collection;

public class ArenaPlayer
{
    private static  HashMap<String, ArenaPlayer> playersMap = new HashMap<>();

    public static ArenaPlayer getPlayer(Player p)
    {
        if (p == null) return null;
        String name = p.getName();
        if (playersMap.containsKey(name)) {
            return playersMap.get(name);
        }
        ArenaPlayer player = new ArenaPlayer(p);
        playersMap.put(name, player);
        return player;
    }

    private Player player;
    private Arena activeArena = null;
    private Location activeSpawn = null;
    private Location spawnBlockLocation = null;
    private PlayerState savedState = null;

    public ArenaPlayer(Player p) { this.player = p; }

    public void join(Arena arena, Location spawn)
    {
        // Сохраняем текущее состояние игрока
        savePlayerState();

        // Устанавливаем новое состояние
        activeArena = arena;
        activeSpawn = spawn;

        // Очищаем и сбрасываем состояние
        resetPlayerForArena();

        // Телепортируем на арену
        player.teleport(spawn);
    }

    public void leave()
    {
        if (!isPlaying()) return;

        // Восстанавливаем сохранённое состояние
        restorePlayerState();

        // Очищаем данные арены
        activeArena = null;
        activeSpawn = null;
    }

    private void savePlayerState() {
        savedState = new PlayerState(player);
    }

    private void restorePlayerState()
    {
        if (savedState == null) return;
        savedState.restore(player);
    }

    private void resetPlayerForArena()
    {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);
        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    // Метод для сохранения состояния в YAML
    public void saveToYaml(YamlConfiguration config, String path)
    {
        if (savedState == null) return;
        savedState.saveToYaml(config, path);
    }

    // Метод для загрузки состояния из YAML
    public void loadFromYaml(YamlConfiguration config, String path) {
        savedState = PlayerState.loadFromYaml(config, path);
    }

    public boolean isPlaying() { return activeArena != null; }
    public Player getPlayer() { return player; }
    public Arena getArena() { return activeArena; }
    public Location getSpawn() {
        return spawnBlockLocation != null ? spawnBlockLocation : activeSpawn;
    }
    public Location getSpawnBlockLocation() { return spawnBlockLocation; }
    public void setSpawnBlockLocation(Location location) { this.spawnBlockLocation = location; }

    // Внутренний класс для хранения состояния игрока
    private static class PlayerState
    {
        private Location location;
        private String inventoryBase64;
        private String armorBase64;
        private Collection<PotionEffect> effects;
        private double health;
        private int foodLevel;
        private float exp;
        private int level;
        private float walkSpeed;
        private float flySpeed;

        public PlayerState(Player player)
        {
            if (player == null) return;
            this.location = player.getLocation();

            // Сериализация инвентаря в Base64
            this.inventoryBase64 = itemStackArrayToBase64(player.getInventory().getContents());
            this.armorBase64 = itemStackArrayToBase64(player.getInventory().getArmorContents());

            this.effects = player.getActivePotionEffects();
            this.health = player.getHealth();
            this.foodLevel = player.getFoodLevel();
            this.exp = player.getExp();
            this.level = player.getLevel();
            this.walkSpeed = player.getWalkSpeed();
            this.flySpeed = player.getFlySpeed();
        }

        public void restore(Player player)
        {
            player.teleport(location);

            // Восстановление инвентаря
            player.getInventory().setContents(base64ToItemStackArray(inventoryBase64));
            player.getInventory().setArmorContents(base64ToItemStackArray(armorBase64));

            // Очистка эффектов перед восстановлением
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }

            // Восстановление эффектов
            player.addPotionEffects(effects);

            // Восстановление атрибутов
            player.setHealth(Math.min(health, player.getAttribute(Attribute.MAX_HEALTH).getValue()));
            player.setFoodLevel(foodLevel);
            player.setExp(exp);
            player.setLevel(level);
            player.setWalkSpeed(walkSpeed);
            player.setFlySpeed(flySpeed);
        }

        public void saveToYaml(YamlConfiguration config, String path)
        {
            config.set(path + ".location", location);
            config.set(path + ".inventory", inventoryBase64);
            config.set(path + ".armor", armorBase64);
            config.set(path + ".effects", effects);
            config.set(path + ".health", health);
            config.set(path + ".food", foodLevel);
            config.set(path + ".exp", exp);
            config.set(path + ".level", level);
            config.set(path + ".walkSpeed", walkSpeed);
            config.set(path + ".flySpeed", flySpeed);
        }

        public static PlayerState loadFromYaml(YamlConfiguration config, String path)
        {
            if (!config.contains(path)) return null;

            PlayerState state = new PlayerState(null);
            state.location = config.getLocation(path + ".location");
            state.inventoryBase64 = config.getString(path + ".inventory");
            state.armorBase64 = config.getString(path + ".armor");
            state.effects = (Collection<PotionEffect>) config.getList(path + ".effects");
            state.health = config.getDouble(path + ".health");
            state.foodLevel = config.getInt(path + ".food");
            state.exp = (float) config.getDouble(path + ".exp");
            state.level = config.getInt(path + ".level");
            state.walkSpeed = (float) config.getDouble(path + ".walkSpeed");
            state.flySpeed = (float) config.getDouble(path + ".flySpeed");

            return state;
        }

        // Методы для работы с Base64
        private static String itemStackArrayToBase64(ItemStack[] items)
        {
            try
            {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

                dataOutput.writeInt(items.length);
                for (ItemStack item : items) {
                    dataOutput.writeObject(item);
                }

                dataOutput.close();
                return Base64Coder.encodeLines(outputStream.toByteArray());
            }
            catch (Exception e)
            {
                throw new IllegalStateException("Unable to save item stacks.", e);
            }
        }

        private static ItemStack[] base64ToItemStackArray(String data)
        {
            if (data == null || data.isEmpty()) return new ItemStack[0];

            try
            {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                ItemStack[] items = new ItemStack[dataInput.readInt()];

                for (int i = 0; i < items.length; i++) {
                    items[i] = (ItemStack) dataInput.readObject();
                }

                dataInput.close();
                return items;
            }
            catch (Exception e)
            {
                throw new IllegalStateException("Unable to load item stacks.", e);
            }
        }
    }
}
