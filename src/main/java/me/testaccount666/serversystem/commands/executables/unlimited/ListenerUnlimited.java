package me.testaccount666.serversystem.commands.executables.unlimited;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.annotations.RequiredCommands;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Listener that handles events related to unlimited items functionality.
 * An unlimited item is an item that doesn't get consumed or reduced in quantity when used.
 */
@RequiredCommands(requiredCommands = CommandUnlimited.class)
public class ListenerUnlimited implements Listener {
    private CommandUnlimited _commandUnlimited;

    /**
     * Checks if this listener can be registered based on the available commands.
     *
     * @param requiredCommands Set of available command executors
     * @return true if the required CommandUnlimited is available, false otherwise
     */
    public boolean canRegister(Set<ServerSystemCommandExecutor> requiredCommands) {
        var canRegister = new AtomicBoolean(false);

        requiredCommands.forEach(command -> {
            if (!(command instanceof CommandUnlimited commandUnlimited)) return;

            _commandUnlimited = commandUnlimited;
            canRegister.set(true);
        });

        return canRegister.get();
    }

    /**
     * Handles consumption of unlimited items, preventing them from being consumed.
     */
    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        var itemInHand = event.getItem();
        if (!isUnlimited(itemInHand)) return;

        event.setReplacement(itemInHand);
    }

    /**
     * Handles placement of unlimited blocks, ensuring they don't get consumed.
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        var itemInHand = event.getItemInHand();
        if (!isUnlimited(itemInHand)) return;

        itemInHand.setAmount(itemInHand.getAmount() + 1);
    }

    /**
     * Handles dropping of unlimited items, creating a non-unlimited copy.
     */
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        var itemInHand = event.getItemDrop().getItemStack();
        if (!isUnlimited(itemInHand)) return;

        event.setCancelled(true);
        var newItem = event.getItemDrop().getWorld().dropItemNaturally(event.getItemDrop().getLocation(), itemInHand);

        var newItemStack = newItem.getItemStack();
        var newItemMeta = newItemStack.getItemMeta();
        var newDataContainer = newItemMeta.getPersistentDataContainer();
        newDataContainer.remove(_commandUnlimited.unlimitedKey);
        newItemStack.setItemMeta(newItemMeta);
    }

    /**
     * Handles bucket emptying for unlimited buckets.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        var itemInHand = event.getPlayer().getInventory().getItem(event.getHand());
        if (!isUnlimited(itemInHand)) return;

        event.setCancelled(true);
        handleBucketEmpty(event);
    }

    /**
     * Helper method to handle different types of bucket emptying.
     */
    private void handleBucketEmpty(PlayerBucketEmptyEvent event) {
        var block = event.getBlock();
        var location = block.getLocation().add(0.5, 0.5, 0.5);

        switch (event.getBucket()) {
            case WATER_BUCKET -> location.getWorld().setType(location, Material.WATER);
            case LAVA_BUCKET -> location.getWorld().setType(location, Material.LAVA);
            case POWDER_SNOW_BUCKET -> location.getWorld().setType(location, Material.POWDER_SNOW);
            case AXOLOTL_BUCKET, COD_BUCKET, SALMON_BUCKET, TADPOLE_BUCKET,
                 PUFFERFISH_BUCKET, TROPICAL_FISH_BUCKET -> handleAquaticBucketEmpty(event, location);
        }
    }

    /**
     * Helper method to handle emptying of aquatic mob buckets.
     */
    private void handleAquaticBucketEmpty(PlayerBucketEmptyEvent event, Location location) {
        location.getWorld().setType(location, Material.WATER);
        var itemInHand = event.getPlayer().getInventory().getItem(event.getHand());
        var itemMeta = itemInHand.getItemMeta();

        switch (event.getBucket()) {
            case AXOLOTL_BUCKET -> location.getWorld().spawn(location, Axolotl.class,
                    CreatureSpawnEvent.SpawnReason.BUCKET, axolotl -> handleEntitySpawn(axolotl, itemMeta));
            case COD_BUCKET -> location.getWorld().spawn(location, Cod.class,
                    CreatureSpawnEvent.SpawnReason.BUCKET, cod -> handleEntitySpawn(cod, itemMeta));
            case SALMON_BUCKET -> location.getWorld().spawn(location, Salmon.class,
                    CreatureSpawnEvent.SpawnReason.BUCKET, salmon -> handleEntitySpawn(salmon, itemMeta));
            case TADPOLE_BUCKET -> location.getWorld().spawn(location, Tadpole.class,
                    CreatureSpawnEvent.SpawnReason.BUCKET, tadpole -> handleEntitySpawn(tadpole, itemMeta));
            case PUFFERFISH_BUCKET -> location.getWorld().spawn(location, PufferFish.class,
                    CreatureSpawnEvent.SpawnReason.BUCKET, pufferfish -> handleEntitySpawn(pufferfish, itemMeta));
            case TROPICAL_FISH_BUCKET -> location.getWorld().spawn(location, TropicalFish.class,
                    CreatureSpawnEvent.SpawnReason.BUCKET, tropicalfish -> handleEntitySpawn(tropicalfish, itemMeta));
        }
    }

    /**
     * Handles entity spawning from buckets, applying metadata from the bucket.
     */
    private void handleEntitySpawn(Entity entity, ItemMeta itemMeta) {
        if (itemMeta instanceof TropicalFishBucketMeta bucketMeta && entity instanceof TropicalFish tropicalFish) if (bucketMeta.hasVariant()) {
            tropicalFish.setPattern(bucketMeta.getPattern());
            tropicalFish.setBodyColor(bucketMeta.getBodyColor());
            tropicalFish.setPatternColor(bucketMeta.getPatternColor());
        }

        if (itemMeta instanceof AxolotlBucketMeta bucketMeta && entity instanceof Axolotl axolotl)
            if (bucketMeta.hasVariant()) axolotl.setVariant(bucketMeta.getVariant());

        if (itemMeta.hasCustomName()) entity.customName(itemMeta.customName());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucketFill(PlayerBucketFillEvent event) {
        var itemInHand = event.getPlayer().getInventory().getItem(event.getHand());
        if (!isUnlimited(itemInHand)) return;

        event.setCancelled(true);

        var block = event.getBlock();
        var location = block.getLocation();

        location.getWorld().setType(location, Material.AIR);
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        var itemInHand = event.getItem();
        if (!isUnlimited(itemInHand)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onDispenserFire(BlockDispenseEvent event) {
        var itemInHand = event.getItem();

        if (!isUnlimited(itemInHand)) return;

        var dispenserBlock = event.getBlock();
        var blockState = dispenserBlock.getState();
        if (!(blockState instanceof Container container)) return;

        var blockData = dispenserBlock.getBlockData();
        if (!(blockData instanceof Directional directional)) return;
        var velocity = event.getVelocity();

        var facing = directional.getFacing();
        var facingBlock = dispenserBlock.getRelative(facing);

        var facingX = facingBlock.getX();
        var facingY = facingBlock.getY();
        var facingZ = facingBlock.getZ();

        var velocityX = velocity.getX();
        var velocityY = velocity.getY();
        var velocityZ = velocity.getZ();

        // Very quirky: when placing a block, the velocity is faked as the block’s coordinates.
        // When dropping an item, velocity is an actual direction vector.
        // Additionally, `BlockDispenseEvent` is fired AFTER removing the item from inventory, so we cannot grab the index
        if (facingX != velocityX || facingY != velocityY || facingZ != velocityZ) {
            Bukkit.getScheduler().runTaskLater(ServerSystem.Instance, () -> container.getInventory().addItem(itemInHand), 1L);
            return;
        }

        var foundIndex = findSimilarItemIndex(itemInHand, container);
        if (foundIndex == -1) return;

        Bukkit.getScheduler().runTaskLater(ServerSystem.Instance, () -> container.getInventory().setItem(foundIndex, itemInHand), 1L);
    }

    private int findSimilarItemIndex(ItemStack comparingItem, InventoryHolder inventoryHolder) {
        var foundIndex = -1;

        var maxIndex = inventoryHolder.getInventory().getSize();

        for (var index = 0; index < maxIndex; index++) {
            var itemStack = inventoryHolder.getInventory().getItem(index);
            if (itemStack == null) continue;
            if (!itemStack.isSimilar(comparingItem)) continue;

            foundIndex = index;
        }

        return foundIndex;
    }

    @EventHandler
    public void onDispenserArmorFire(BlockDispenseArmorEvent event) {
        var itemInHand = event.getItem();
        if (!isUnlimited(itemInHand)) return;

        var newItemStack = itemInHand.clone();
        var newItemMeta = newItemStack.getItemMeta();
        var newDataContainer = newItemMeta.getPersistentDataContainer();

        newDataContainer.remove(_commandUnlimited.unlimitedKey);
        newItemStack.setItemMeta(newItemMeta);

        event.setItem(newItemStack);
    }

    /**
     * Checks if an item is marked as unlimited.
     *
     * @param itemStack The item to check
     * @return true if the item is unlimited, false otherwise
     */
    private boolean isUnlimited(@Nullable ItemStack itemStack) {
        if (itemStack == null) return false;

        var itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;

        var dataContainer = itemMeta.getPersistentDataContainer();
        return dataContainer.has(_commandUnlimited.unlimitedKey);
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        var itemStack = event.getEntity().getItemStack();
        if (!isUnlimited(itemStack)) return;

        var itemMeta = itemStack.getItemMeta();
        var dataContainer = itemMeta.getPersistentDataContainer();

        dataContainer.remove(_commandUnlimited.unlimitedKey);
        itemStack.setItemMeta(itemMeta);

        event.getEntity().setItemStack(itemStack);
    }
}