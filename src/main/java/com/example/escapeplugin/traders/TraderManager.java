package com.example.escapeplugin.traders;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import java.util.ArrayList;
import java.util.List;

public class TraderManager {
    public void spawnTrader(Location loc, String type) {
        Villager trader = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
        trader.setCustomName("§eТорговец (" + type + ")");
        trader.setProfession(Profession.NITWIT); // Без профессии
        trader.setAI(false);
        trader.setInvulnerable(true);

        // Настройка торговли
        List<MerchantRecipe> recipes = new ArrayList<>();
        switch (type.toLowerCase()) {
            case "оружие":
                recipes.add(new MerchantRecipe(new ItemStack(Material.IRON_SWORD), 0, 1, false));
                recipes.get(0).addIngredient(new ItemStack(Material.IRON_INGOT, 5));
                break;
            case "еда":
                recipes.add(new MerchantRecipe(new ItemStack(Material.GOLDEN_APPLE), 0, 1, false));
                recipes.get(0).addIngredient(new ItemStack(Material.GOLD_INGOT, 3));
                break;
        }
        trader.setRecipes(recipes);
    }
}
