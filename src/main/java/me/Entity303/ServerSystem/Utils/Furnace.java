package me.Entity303.ServerSystem.Utils;

import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class Furnace extends MessageUtils {
    private final Map<Material, String> furnaceMap;

    public Furnace(ss plugin) {
        super(plugin);
        this.furnaceMap = new HashMap<>();

        try {
            this.furnaceMap.put(Material.getMaterial("PORKCHOP"), "COOKED_PORKCHOP");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("PORK"), "GRILLED_PORK");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("BEEF"), "COOKED_BEEF");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("RAW_BEEF"), "COOKED_BEEF");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("CHICKEN"), "COOKED_CHICKEN");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("CHICKEN"), "RAW_CHICKEN");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("COD"), "COOKED_COD");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("RAW_FISH"), "COOKED_FISH");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("SALMON"), "COOKED_SALMON");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.POTATO, "BAKED_POTATO");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("POTATOES"), "BAKED_POTATO");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("POTATO_ITEM"), "BAKED_POTATO");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.MUTTON, "COOKED_MUTTON");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.RABBIT, "COOKED_RABBIT");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("KELP"), "DRIED_KELP");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.IRON_ORE, "IRON_INGOT");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.GOLD_ORE, "GOLD_INGOT");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("ANCIENT_DEBRIS"), "NETHERITE_INGOT");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.SAND, "GLASS");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.SANDSTONE, "SMOOTH_SANDSTONE");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.RED_SANDSTONE, "SMOOTH_RED_SANDSTONE");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.QUARTZ_BLOCK, "SMOOTH_QUARTZ");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.CLAY_BALL, "BRICK");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.NETHERRACK, "NETHER_BRICK");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("NETHER_BRICKS"), "CRACKED_NETHER_BRICKS");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.CLAY, "TERRACOTTA");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("STONE_BRICKS"), "CRACKED_STONE_BRICKS");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("SMOOTH_BRICK"), "SMOOTH_BRICK");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.CACTUS, "CACTUS_GREEN");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("ACACIA_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("BIRCH_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("DARK_OAK_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("JUNGLE_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("OAK_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("SPRUCE_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("STRIPPED_ACACIA_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("STRIPPED_BIRCH_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("LOG2"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("STRIPPED_DARK_OAK_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("STRIPPED_JUNGLE_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("STRIPPED_OAK_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("STRIPPED_SPRUCE_LOG"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("ACACIA_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("BIRCH_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("DARK_OAK_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("JUNGLE_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("OAK_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("SPRUCE_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("STRIPPED_ACACIA_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("STRIPPED_BIRCH_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("STRIPPED_DARK_OAK_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("STRIPPED_JUNGLE_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("STRIPPED_OAK_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("STRIPPED_SPRUCE_WOOD"), "CHARCOAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("CHORUS_FRUIT"), "POPPED_CHORUS_FRUIT");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("WET_SPONGE"), "SPONGE");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("SEA_PICKLE"), "LIME_DYE");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.DIAMOND_ORE, "DIAMOND");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.LAPIS_ORE, "LAPIS_LAZULI");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.REDSTONE_ORE, "REDSTONE");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.COAL_ORE, "COAL");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.EMERALD_ORE, "EMERALD");
        } catch (Exception ignore) {

        }
        try {
            this.furnaceMap.put(Material.getMaterial("NETHER_GOLD_ORE"), "GOLD_INGOT");
        } catch (Exception ignore) {

        }

        try {
            this.furnaceMap.put(Material.getMaterial("NETHER_QUARTZ_ORE"), "QUARTZ");
        } catch (Exception ignore) {

        }

        try {
            this.furnaceMap.put(Material.getMaterial("QUARTZ_ORE"), "QUARTZ");
        } catch (Exception ignore) {

        }
    }

    public ItemStack getResult(ItemStack input) {
        if (this.furnaceMap.get(input.getType()) == null) return null;

        String type = this.furnaceMap.get(input.getType()).toUpperCase();

        if (type.equalsIgnoreCase("TERRACOTTA") && !this.plugin.getVersionManager().isTerracotta())
            type = "HARDENED_CLAY";

        if (type.equalsIgnoreCase("CACTUS_GREEN") && !this.plugin.getVersionManager().isV113())
            return new ItemStack(Material.INK_SAC, (short) 2);

        if (type.equalsIgnoreCase("LIME_DYE") && !this.plugin.getVersionManager().isV113())
            return new ItemStack(Material.INK_SAC, (short) 10);

        if (type.equalsIgnoreCase("LAPIS_LAZULI") && !this.plugin.getVersionManager().isV113())
            return new ItemStack(Material.INK_SAC, (short) 4);

        if (type.equalsIgnoreCase("SMOOTH_BRICK") && !this.plugin.getVersionManager().isV113())
            return new ItemStack(Material.getMaterial("SMOOTH_BRICK"), (short) 2);

        return new ItemStack(Material.getMaterial(type));
    }
}
