package me.testaccount666.serversystem.commands.executables.unlimited

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.annotations.RequiredCommands
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Container
import org.bukkit.block.data.Directional
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDispenseArmorEvent
import org.bukkit.event.block.BlockDispenseEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.ItemSpawnEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.AxolotlBucketMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.TropicalFishBucketMeta

/**
 * Listener that handles events related to unlimited items functionality.
 * An unlimited item is an item that doesn't get consumed or reduced in quantity when used.
 */
@RequiredCommands([CommandUnlimited::class])
class ListenerUnlimited : Listener {
    private lateinit var _commandUnlimited: CommandUnlimited

    /**
     * Checks if this listener can be registered based on the available commands.
     * 
     * @param requiredCommands Set of available command executors
     * @return true if the required CommandUnlimited is available, false otherwise
     */
    fun canRegister(requiredCommands: Set<ServerSystemCommandExecutor>): Boolean {
        _commandUnlimited = requiredCommands.firstOrNull { it is CommandUnlimited } as? CommandUnlimited ?: return false
        return true
    }

    /**
     * Handles consumption of unlimited items, preventing them from being consumed.
     */
    @EventHandler
    fun onConsume(event: PlayerItemConsumeEvent) {
        val itemInHand = event.item
        if (!isUnlimited(itemInHand)) return

        event.replacement = itemInHand
    }

    /**
     * Handles placement of unlimited blocks, ensuring they don't get consumed.
     */
    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val itemInHand = event.getItemInHand()
        if (!isUnlimited(itemInHand)) return

        itemInHand.amount += 1
    }

    /**
     * Handles dropping of unlimited items, creating a non-unlimited copy.
     */
    @EventHandler
    fun onItemDrop(event: PlayerDropItemEvent) {
        val itemInHand = event.itemDrop.itemStack
        if (!isUnlimited(itemInHand)) return

        event.isCancelled = true
        val newItem = event.itemDrop.world.dropItemNaturally(event.itemDrop.location, itemInHand)

        val newItemStack = newItem.itemStack
        val newItemMeta = newItemStack.itemMeta
        val newDataContainer = newItemMeta.persistentDataContainer
        newDataContainer.remove(_commandUnlimited.unlimitedKey)
        newItemStack.setItemMeta(newItemMeta)
    }

    /**
     * Handles bucket emptying for unlimited buckets.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBucketEmpty(event: PlayerBucketEmptyEvent) {
        val itemInHand = event.getPlayer().inventory.getItem(event.hand)
        if (!isUnlimited(itemInHand)) return

        event.isCancelled = true
        handleBucketEmpty(event)
    }

    /**
     * Helper method to handle different types of bucket emptying.
     */
    private fun handleBucketEmpty(event: PlayerBucketEmptyEvent) {
        val block = event.block
        val location = block.location.add(0.5, 0.5, 0.5)

        when (event.bucket) {
            Material.WATER_BUCKET -> location.world.setType(location, Material.WATER)
            Material.LAVA_BUCKET -> location.world.setType(location, Material.LAVA)
            Material.POWDER_SNOW_BUCKET -> location.world.setType(location, Material.POWDER_SNOW)
            Material.AXOLOTL_BUCKET, Material.COD_BUCKET, Material.SALMON_BUCKET,
            Material.TADPOLE_BUCKET, Material.PUFFERFISH_BUCKET, Material.TROPICAL_FISH_BUCKET -> handleAquaticBucketEmpty(event, location)

            else -> Unit
        }
    }

    /**
     * Helper method to handle emptying of aquatic mob buckets.
     */
    private fun handleAquaticBucketEmpty(event: PlayerBucketEmptyEvent, location: Location) {
        location.world.setType(location, Material.WATER)
        val itemInHand = event.getPlayer().inventory.getItem(event.hand)
        val itemMeta = itemInHand.itemMeta

        when (event.bucket) {
            Material.AXOLOTL_BUCKET -> location.world.spawn(
                location, Axolotl::class.java,
                CreatureSpawnEvent.SpawnReason.BUCKET
            ) { handleEntitySpawn(it, itemMeta) }

            Material.COD_BUCKET -> location.world.spawn(
                location, Cod::class.java,
                CreatureSpawnEvent.SpawnReason.BUCKET
            ) { handleEntitySpawn(it, itemMeta) }

            Material.SALMON_BUCKET -> location.world.spawn(
                location, Salmon::class.java,
                CreatureSpawnEvent.SpawnReason.BUCKET
            ) { handleEntitySpawn(it, itemMeta) }

            Material.TADPOLE_BUCKET -> location.world.spawn(
                location, Tadpole::class.java,
                CreatureSpawnEvent.SpawnReason.BUCKET
            ) { handleEntitySpawn(it, itemMeta) }

            Material.PUFFERFISH_BUCKET -> location.world.spawn(
                location, PufferFish::class.java,
                CreatureSpawnEvent.SpawnReason.BUCKET
            ) { handleEntitySpawn(it, itemMeta) }

            Material.TROPICAL_FISH_BUCKET -> location.world.spawn(
                location, TropicalFish::class.java,
                CreatureSpawnEvent.SpawnReason.BUCKET
            ) { handleEntitySpawn(it, itemMeta) }

            else -> Unit
        }
    }

    /**
     * Handles entity spawning from buckets, applying metadata from the bucket.
     */
    private fun handleEntitySpawn(entity: Entity, itemMeta: ItemMeta) {
        if ((itemMeta is TropicalFishBucketMeta && entity is TropicalFish) && itemMeta.hasVariant()) {
            entity.pattern = itemMeta.pattern
            entity.bodyColor = itemMeta.bodyColor
            entity.patternColor = itemMeta.patternColor
        }

        if ((itemMeta is AxolotlBucketMeta && entity is Axolotl) && itemMeta.hasVariant()) entity.variant = itemMeta.variant

        if (itemMeta.hasCustomName()) entity.customName(itemMeta.customName())
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBucketFill(event: PlayerBucketFillEvent) {
        val itemInHand = event.getPlayer().inventory.getItem(event.hand)
        if (!isUnlimited(itemInHand)) return

        event.isCancelled = true

        val block = event.block
        val location = block.location

        location.world.setType(location, Material.AIR)
    }

    @EventHandler
    fun onItemDamage(event: PlayerItemDamageEvent) {
        val itemInHand = event.item
        if (!isUnlimited(itemInHand)) return

        event.isCancelled = true
    }

    @EventHandler
    fun onDispenserFire(event: BlockDispenseEvent) {
        val itemInHand = event.item

        if (!isUnlimited(itemInHand)) return

        val dispenserBlock = event.getBlock()
        val blockState = dispenserBlock.state
        if (blockState !is Container) return

        val blockData = dispenserBlock.blockData
        if (blockData !is Directional) return
        val velocity = event.velocity

        val facing = blockData.facing
        val facingBlock = dispenserBlock.getRelative(facing)

        val facingX = facingBlock.x
        val facingY = facingBlock.y
        val facingZ = facingBlock.z

        val velocityX = velocity.getX()
        val velocityY = velocity.getY()
        val velocityZ = velocity.getZ()

        // Very quirky: when placing a block, the velocity is faked as the block’s coordinates.
        // When dropping an item, velocity is an actual direction vector.
        // Additionally, `BlockDispenseEvent` is fired AFTER removing the item from inventory, so we cannot grab the index
        if (facingX.toDouble() != velocityX || facingY.toDouble() != velocityY || facingZ.toDouble() != velocityZ) {
            Bukkit.getScheduler().runTaskLater(instance, Runnable { blockState.inventory.addItem(itemInHand) }, 1L)
            return
        }

        val foundIndex = findSimilarItemIndex(itemInHand, blockState)
        if (foundIndex == -1) return

        Bukkit.getScheduler().runTaskLater(instance, Runnable { blockState.inventory.setItem(foundIndex, itemInHand) }, 1L)
    }

    private fun findSimilarItemIndex(comparingItem: ItemStack?, inventoryHolder: InventoryHolder): Int {
        var foundIndex = -1

        val maxIndex = inventoryHolder.inventory.size

        for (index in 0..<maxIndex) {
            val itemStack = inventoryHolder.inventory.getItem(index) ?: continue
            if (!itemStack.isSimilar(comparingItem)) continue

            foundIndex = index
        }

        return foundIndex
    }

    @EventHandler
    fun onDispenserArmorFire(event: BlockDispenseArmorEvent) {
        val itemInHand = event.item
        if (!isUnlimited(itemInHand)) return

        val newItemStack = itemInHand.clone()
        val newItemMeta = newItemStack.itemMeta
        val newDataContainer = newItemMeta.persistentDataContainer

        newDataContainer.remove(_commandUnlimited.unlimitedKey)
        newItemStack.setItemMeta(newItemMeta)

        event.item = newItemStack
    }

    /**
     * Checks if an item is marked as unlimited.
     * 
     * @param itemStack The item to check
     * @return true if the item is unlimited, false otherwise
     */
    private fun isUnlimited(itemStack: ItemStack?): Boolean {
        val itemMeta = itemStack?.itemMeta ?: return false

        val dataContainer = itemMeta.persistentDataContainer
        return dataContainer.has(_commandUnlimited.unlimitedKey)
    }

    @EventHandler
    fun onItemSpawn(event: ItemSpawnEvent) {
        val itemStack = event.getEntity().itemStack
        if (!isUnlimited(itemStack)) return

        val itemMeta = itemStack.itemMeta
        val dataContainer = itemMeta.persistentDataContainer

        dataContainer.remove(_commandUnlimited.unlimitedKey)
        itemStack.setItemMeta(itemMeta)

        event.getEntity().itemStack = itemStack
    }
}