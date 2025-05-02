package com.example.escapeplugin.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.Color;

import com.example.escapeplugin.EscapePlugin;
import com.example.escapeplugin.enums.ArenaStatus;
import com.example.escapeplugin.enums.TraderType;
import com.example.escapeplugin.managers.ArenaRunnable;

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


    /* IN GAME STATE */
    private ArenaRunnable runnable;
    private ArenaStatus status;

    private ArrayList<Prisoner> prisoners;
    private ArrayList<Location> freePrisonerSpawns;
    private ArrayList<Trader> traders;
    private ArrayList<Stash> stashs;
    private ArrayList<ItemStack> droppedItems;
    private HashMap<Location, Material> restoreBlocks;


    public Arena(String ID)
    {
        this.ID = ID.toUpperCase().strip();
        this.name = ID;
        this.runnable = new ArenaRunnable(this);
        this.status = ArenaStatus.DISABLED;

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

        status = ArenaStatus.PLAYING;
        prisoners.forEach(prisoner -> setupPrisoner(prisoner));
    }

    public void stop()
    {
        
    }

    public void cleanup()
    {
        
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
            // Create golden pickaxe with low durability
            ItemStack pickaxe = new ItemStack(Material.GOLDEN_PICKAXE);
            pickaxe.setDurability((short)(pickaxe.getType().getMaxDurability() - 2));
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
        prisonerSpawns.add(loc);
        return true;
    }

    public boolean removePrisonerSpawn(Location loc)
    {
        if (!prisonerSpawns.contains(loc)) return false;
        prisonerSpawns.remove(loc);
        return true;
    }

    public boolean addChestSpawn(Location loc)
    {
        if (stashSpawns.contains(loc)) return false;
        stashSpawns.add(loc);
        return true;
    }

    public boolean removeChestSpawn(Location loc)
    {
        if (!stashSpawns.contains(loc)) return false;
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

        leverSpawns.put(name, loc);
        return true;
    }

    public boolean removeLeverSpawn(String name)
    {
        if (!leverSpawns.containsKey(name)) return false;
        leverSpawns.remove(name);
        return true;
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
    
    public List<Location> getPrisonerSpawns() { return prisonerSpawns; }
    public List<Location> getStashSpawns() { return stashSpawns; }
    public HashMap<String, Location> getLeverSpawns() { return leverSpawns; }
    public HashMap<TraderType, ArrayList<Location>> getTraderSpawns() { return traderSpawns; }
    public List<Prisoner> getPrisoners() { return prisoners; }
}
