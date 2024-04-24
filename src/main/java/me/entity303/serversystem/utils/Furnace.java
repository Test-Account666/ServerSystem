package me.entity303.serversystem.utils;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;

public class Furnace extends CommandUtils {
    private final Map<Material, String> _furnaceMap;

    public Furnace(ServerSystem plugin) {
        super(plugin);
        this._furnaceMap = new EnumMap<>(Material.class);

        try {
            this._furnaceMap.put(Material.getMaterial("PORKCHOP"), "COOKED_PORKCHOP");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("PORK"), "GRILLED_PORK");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("BEEF"), "COOKED_BEEF");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("RAW_BEEF"), "COOKED_BEEF");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("CHICKEN"), "COOKED_CHICKEN");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("CHICKEN"), "RAW_CHICKEN");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("COD"), "COOKED_COD");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("RAW_FISH"), "COOKED_FISH");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("SALMON"), "COOKED_SALMON");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.POTATO, "BAKED_POTATO");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("POTATOES"), "BAKED_POTATO");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("POTATO_ITEM"), "BAKED_POTATO");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.MUTTON, "COOKED_MUTTON");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.RABBIT, "COOKED_RABBIT");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("KELP"), "DRIED_KELP");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.IRON_ORE, "IRON_INGOT");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.GOLD_ORE, "GOLD_INGOT");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("ANCIENT_DEBRIS"), "NETHERITE_INGOT");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.SAND, "GLASS");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.SANDSTONE, "SMOOTH_SANDSTONE");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.RED_SANDSTONE, "SMOOTH_RED_SANDSTONE");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.QUARTZ_BLOCK, "SMOOTH_QUARTZ");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.CLAY_BALL, "BRICK");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.NETHERRACK, "NETHER_BRICK");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("NETHER_BRICKS"), "CRACKED_NETHER_BRICKS");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.CLAY, "TERRACOTTA");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("STONE_BRICKS"), "CRACKED_STONE_BRICKS");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("SMOOTH_BRICK"), "SMOOTH_BRICK");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.CACTUS, "CACTUS_GREEN");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("ACACIA_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("BIRCH_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("DARK_OAK_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("JUNGLE_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("OAK_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("SPRUCE_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("STRIPPED_ACACIA_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("STRIPPED_BIRCH_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("LOG2"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("STRIPPED_DARK_OAK_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("STRIPPED_JUNGLE_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("STRIPPED_OAK_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("STRIPPED_SPRUCE_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("ACACIA_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("BIRCH_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("DARK_OAK_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("JUNGLE_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("OAK_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("SPRUCE_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("STRIPPED_ACACIA_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("STRIPPED_BIRCH_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("STRIPPED_DARK_OAK_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("STRIPPED_JUNGLE_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("STRIPPED_OAK_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("STRIPPED_SPRUCE_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("CHORUS_FRUIT"), "POPPED_CHORUS_FRUIT");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("WET_SPONGE"), "SPONGE");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("SEA_PICKLE"), "LIME_DYE");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.DIAMOND_ORE, "DIAMOND");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.LAPIS_ORE, "LAPIS_LAZULI");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.REDSTONE_ORE, "REDSTONE");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.COAL_ORE, "COAL");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.EMERALD_ORE, "EMERALD");
        } catch (Exception ignore) {

        }
        try {
            this._furnaceMap.put(Material.getMaterial("NETHER_GOLD_ORE"), "GOLD_INGOT");
        } catch (Exception ignore) {

        }

        try {
            this._furnaceMap.put(Material.getMaterial("NETHER_QUARTZ_ORE"), "QUARTZ");
        } catch (Exception ignore) {

        }

        try {
            this._furnaceMap.put(Material.getMaterial("QUARTZ_ORE"), "QUARTZ");
        } catch (Exception ignore) {

        }
    }

    public ItemStack GetResult(ItemStack input) {
        if (this._furnaceMap.get(input.getType()) == null) {
            var recipeIterator = Bukkit.recipeIterator();
            while (recipeIterator.hasNext()) {
                var recipe = recipeIterator.next();
                if (recipe instanceof FurnaceRecipe furnaceRecipe) {
                    if (furnaceRecipe.getInput().getType() == input.getType()) {
                        this._furnaceMap.put(furnaceRecipe.getInput().getType(), furnaceRecipe.getResult().getType().name());
                        return furnaceRecipe.getResult();
                    }
                }
            }
            return null;
        }

        var type = this._furnaceMap.get(input.getType()).toUpperCase();

        return new ItemStack(Material.getMaterial(type));
    }
}
