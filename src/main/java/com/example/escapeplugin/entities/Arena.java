package com.example.escapeplugin.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.Color;

import com.example.escapeplugin.EscapePlugin;
import com.example.escapeplugin.enums.ArenaStatus;
import com.example.escapeplugin.enums.TraderType;
import com.example.escapeplugin.managers.ArenaRunnable;
import com.example.escapeplugin.managers.LootManager;

public class Arena 
{
    /* GLOBAL STATE */
    private final String ID;
    private String name;

    private int maxPlayers = 16;
    private int minPlayers = 2;

    private ArrayList<Location> prisonerSpawns;
    private ArrayList<Location> stashSpawns;
    private HashMap<TraderType, ArrayList<Location>> traderSpawns;
    private HashMap<String, Location> leverSpawns;

    private boolean allowForceChestGenerate = true;


    /* IN GAME STATE */
    private ArenaRunnable runnable;
    private ArenaStatus status;

    private ArrayList<Prisoner> prisoners;
    private ArrayList<Location> freePrisonerSpawns;
    private ArrayList<Trader> traders;
    private ArrayList<Stash> stashs;

    private ArrayList<Item> droppedItems;
    private HashMap<Location, Material> restoreBlocks;


    public Arena(String ID)
    {
        this.ID = ID.toUpperCase().strip();
        this.name = ID;
        this.runnable = new ArenaRunnable(this);
        this.status = ArenaStatus.WAITING;

        this.prisonerSpawns = new ArrayList<>();
        this.freePrisonerSpawns = new ArrayList<>();
        this.stashSpawns = new ArrayList<>();
        this.traderSpawns = new HashMap<>();
        this.leverSpawns = new HashMap<>();


        this.prisoners = new ArrayList<>();
        this.traders = new ArrayList<>();
        this.stashs = new ArrayList<>();
        this.droppedItems = new ArrayList<>();
        this.restoreBlocks = new HashMap<>();
    }

    public void join(Prisoner prisoner)
    {
        if (prisoner.isPlaying()) return;
        if (prisoners.contains(prisoner)) return;
        if (maxPlayers <= prisoners.size()) return;

        if (freePrisonerSpawns.size() == 0 && prisoners.size() == 0)
            freePrisonerSpawns = (ArrayList<Location>) prisonerSpawns.clone();

        Location freeSpawn = freePrisonerSpawns.get(0);
        prisoner.setArena(this, freeSpawn);
        prisoners.add(prisoner);
        freePrisonerSpawns.remove(0);
        setupPrisoner(prisoner);

        if (prisoners.size() != minPlayers) return;
        runnable.runTaskTimer(EscapePlugin.getInstance(), 20L, 20L);
    }

    public void leave(Prisoner prisoner)
    {
        if (prisoner.getArena().getID() != getID()) return;
        if (!prisoners.contains(prisoner)) return;

        Location freeSpawn = prisoner.getSpawn();
        freePrisonerSpawns.add(freeSpawn);
        prisoner.setArena(null, null);
        prisoners.remove(prisoner);

        prisoner.restoreState();
        runnable.removePlayer(prisoner);
    }

    public void start()
    {
        if (!isWaiting()) return;

        cleanup();
        status = ArenaStatus.PLAYING;
        prisoners.forEach(prisoner -> setupPrisoner(prisoner));

        respawnStashes();
        respawnTraders();
    }

    public void respawnStashes()
    {
        for (Stash stash : stashs) { stash.clear(); }
        stashs.clear();

        LootManager lootManager = EscapePlugin.getInstance().getLootManager();
        for (Location spawn : stashSpawns) {
            stashs.add(new Stash(spawn, lootManager));
        }
    }

    public void respawnTraders()
    {
        for (Trader trader : traders) { trader.remove(); }
        traders.clear();
        
        for (TraderType type : traderSpawns.keySet()) 
        {
            for (Location spawn : traderSpawns.get(type)) 
            {
                traders.add(new Trader(spawn, type));
            }
        }
    }

    public void stop()
    {
        cleanup();
    }

    public void cleanup()
    {
        for (Location loc : restoreBlocks.keySet()) 
        {
            loc.getBlock().setType(restoreBlocks.get(loc));
        }

        for (Item item : droppedItems) { item.remove(); }
        for (Trader trader : traders) { trader.remove(); }
        for (Stash stash : stashs) { stash.clear(); }

        restoreBlocks.clear();
        droppedItems.clear();
        traders.clear();
        stashs.clear();
    }

    public void setupPrisoner(Prisoner prisoner)
    {
        Player p = prisoner.getPlayer();

        if (isWaiting())
        {
            prisoner.saveState();
            p.setGameMode(GameMode.ADVENTURE);
            p.getInventory().clear();
            
            // Create orange leather armor (no helmet)
            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
            ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
            
            // Set armor color to orange
            LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
            chestplateMeta.setColor(Color.fromRGB(255, 165, 0));
            chestplateMeta.setDisplayName("§6Prisoner Chestplate");
            chestplate.setItemMeta(chestplateMeta);
            
            LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
            leggingsMeta.setColor(Color.fromRGB(255, 165, 0));
            leggingsMeta.setDisplayName("§6Prisoner Leggings");
            leggings.setItemMeta(leggingsMeta);
            
            LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
            bootsMeta.setColor(Color.fromRGB(255, 165, 0));
            bootsMeta.setDisplayName("§6Prisoner Boots");
            boots.setItemMeta(bootsMeta);
            
            // Set armor durability to almost broken (3-4 hits)
            chestplate.setDurability((short)(chestplate.getType().getMaxDurability() - 4));
            leggings.setDurability((short)(leggings.getType().getMaxDurability() - 4));
            boots.setDurability((short)(boots.getType().getMaxDurability() - 4));
            
            // Equip items
            p.getInventory().setChestplate(chestplate);
            p.getInventory().setLeggings(leggings);
            p.getInventory().setBoots(boots);
            prisoner.teleportToSpawn();
            return;
        }
    
        if (isPlaying())
        {
            p.setGameMode(GameMode.SURVIVAL);

            ItemStack spawnBlock = new ItemStack(Material.IRON_BLOCK);
            spawnBlock.getItemMeta().setDisplayName("§6Ваш спаси-блок");
            p.getInventory().addItem(spawnBlock);

            // Create golden pickaxe with low durability
            ItemStack pickaxe = new ItemStack(Material.GOLDEN_PICKAXE);
            pickaxe.setDurability((short)(pickaxe.getType().getMaxDurability() - 1));
            pickaxe.getItemMeta().setDisplayName("§6Ломалка");

            p.getInventory().setItemInMainHand(pickaxe);
            p.setHealth(20);
            p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);
            p.setAbsorptionAmount(13);
            return;
        }
    }

    public boolean addPrisonerSpawn(Location loc)
    {
        if (prisonerSpawns.contains(loc)) return false;

        loc.getBlock().setType(Material.BLACK_STAINED_GLASS);
        loc.clone().add(0, 1, 0).getBlock().setType(Material.BLACK_STAINED_GLASS);
        prisonerSpawns.add(loc);
        return true;
    }

    public boolean removePrisonerSpawn(Location loc)
    {
        if (!prisonerSpawns.contains(loc)) return false;

        loc.getBlock().setType(Material.AIR);
        loc.clone().add(0, 1, 0).getBlock().setType(Material.AIR);
        prisonerSpawns.remove(loc);
        return true;
    }

    public boolean addChestSpawn(Location loc)
    {
        if (stashSpawns.contains(loc)) return false;
        
        loc.getBlock().setType(Material.CHEST);
        stashSpawns.add(loc);
        return true;
    }

    public boolean removeChestSpawn(Location loc)
    {
        if (!stashSpawns.contains(loc)) return false;

        loc.getBlock().setType(Material.AIR);
        stashSpawns.remove(loc);
        return true;
    }

    public boolean addLeverSpawn(String name, Location loc)
    {
        if (leverSpawns.containsKey(name))
        {
            Location prevLoc = leverSpawns.get(name);
            if (prevLoc.equals(loc)) return false;
        }

        loc.getBlock().setType(Material.BROWN_STAINED_GLASS);
        loc.clone().add(0, 1, 0).getBlock().setType(Material.BROWN_STAINED_GLASS);
        leverSpawns.put(name, loc);
        return true;
    }

    public boolean removeLeverSpawn(String name)
    {
        if (!leverSpawns.containsKey(name)) return false;
        Location prevLoc = leverSpawns.get(name);
        prevLoc.getBlock().setType(Material.AIR);
        prevLoc.clone().add(0, 1, 0).getBlock().setType(Material.AIR);
        leverSpawns.remove(name);
        return true;
    }

    public void addRestoredBlock(Location loc, Material material)
    {
        restoreBlocks.put(loc, material);
    }

    public void addDroppedItem(Item item)
    {
        droppedItems.add(item);
    }

    public void broadcast(String message) 
    { 
        prisoners.forEach(prisoner -> prisoner.getPlayer().sendMessage(message)); 
    }

    public boolean isPlaying() { return status.equals(ArenaStatus.PLAYING); }
    public boolean isWaiting() { return status.equals(ArenaStatus.WAITING); }
    public void setStatus(ArenaStatus status) { this.status = status; }

    public String getID() { return ID; }
    public String getName() { return name; }
    public void setName(String newName) { this.name = newName; }
    
    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }
    public int getMinPlayers() { return minPlayers; }
    public void setMinPlayers(int minPlayers) { this.minPlayers = minPlayers; }
    public boolean getForceChestGenerate() { return this.allowForceChestGenerate; }
    public void setForceChestGenerate(boolean newValue) { allowForceChestGenerate = newValue; }
    
    public List<Location> getPrisonerSpawns() { return prisonerSpawns; }
    public List<Location> getStashSpawns() { return stashSpawns; }
    public HashMap<String, Location> getLeverSpawns() { return leverSpawns; }
    public HashMap<TraderType, ArrayList<Location>> getTraderSpawns() { return traderSpawns; }
    public List<Prisoner> getPrisoners() { return prisoners; }

    public void playerDead(Prisoner prisoner) 
    {
        Player p = prisoner.getPlayer();
        broadcast("§c" + p.getName() + " умер.");
        p.getWorld().strikeLightning(p.getLocation());

        if (prisoner.getSpawnBlock() != null)
        {
            p.sendTitle("§eВозрождение", "§fВы появились на своём блоке");
            p.sendMessage("§eВы появились на своём блоке.");
            p.teleport(prisoner.getSpawnBlock().getLocation());
            p.getWorld().strikeLightning(p.getLocation());
            p.setHealth(20);
            p.setAbsorptionAmount(20);
            p.getInventory().clear();
            return;
        }

        p.setHealth(20);
        p.setAbsorptionAmount(20);
        p.getInventory().clear();
        p.setGameMode(GameMode.SPECTATOR);
        p.sendTitle("§cСмерть...", "§eУ вас не было своего блока.");
    }

    public Prisoner isSpawnBlock(Location location) 
    {
        for (Prisoner prisoner : prisoners) 
        {
            SpawnBlock sb = prisoner.getSpawnBlock();
            if (sb == null) continue;
            if (sb.getLocation().equals(location)) return prisoner;
        }

        return null;
    }
}
