package me.testaccount666.serversystem.commands.executables.unlimited

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.UseRemainder.useRemainder
import me.testaccount666.paperktx.extensions.*
import me.testaccount666.paperktx.scheduler.skedule.okkero.schedule
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.annotations.RequiredCommands
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Container
import org.bukkit.block.data.Directional
import org.bukkit.entity.*
import org.bukkit.event.*
import org.bukkit.event.block.*
import org.bukkit.event.entity.ItemSpawnEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.*

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
        _commandUnlimited = requiredCommands.firstNotNullOfOrNull { it as? CommandUnlimited } ?: return false
        return true
    }

    /**
     * Handles consumption of unlimited items, preventing them from being consumed.
     */
    @EventHandler
    fun onConsume(event: PlayerItemConsumeEvent) {
        val itemInHand = event.item.takeIf(::isUnlimited) ?: return
        event.replacement = itemInHand
    }

    /**
     * Handles placement of unlimited blocks, ensuring they don't get consumed.
     */
    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val itemInHand = event.itemInHand.takeIf(::isUnlimited) ?: return
        itemInHand.setData(DataComponentTypes.USE_REMAINDER, useRemainder(itemInHand))
    }

    /**
     * Handles dropping of unlimited items, creating a non-unlimited copy.
     */
    @EventHandler
    fun onItemDrop(event: PlayerDropItemEvent) {
        val itemInHand = event.itemDrop.itemStack.takeIf(::isUnlimited) ?: return

        event.isCancelled = true
        event.itemDrop.world.dropItem(event.itemDrop.location, itemInHand) { item ->
            item.velocity = event.itemDrop.velocity
            item.itemStack.editPersistentDataContainer { it.remove(_commandUnlimited.unlimitedKey) }
        }
    }

    /**
     * Handles bucket emptying for unlimited buckets.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBucketEmpty(event: PlayerBucketEmptyEvent) {
        event.getPlayer().inventory[event.hand].takeIf(::isUnlimited) ?: return

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
            Material.TADPOLE_BUCKET, Material.PUFFERFISH_BUCKET, Material.TROPICAL_FISH_BUCKET,
                -> handleAquaticBucketEmpty(event, location)

            else -> Unit
        }
    }

    /**
     * Helper method to handle emptying of aquatic mob buckets.
     */
    private fun handleAquaticBucketEmpty(event: PlayerBucketEmptyEvent, location: Location) {
        location.world.setType(location, Material.WATER)
        val itemInHand = event.getPlayer().inventory[event.hand]
        val itemMeta = itemInHand.itemMeta

        when (event.bucket) {
            Material.AXOLOTL_BUCKET -> location.spawn<Axolotl> { handleEntitySpawn(this, itemMeta) }
            Material.COD_BUCKET -> location.spawn<Cod> { handleEntitySpawn(this, itemMeta) }
            Material.SALMON_BUCKET -> location.spawn<Salmon> { handleEntitySpawn(this, itemMeta) }
            Material.TADPOLE_BUCKET -> location.spawn<Tadpole> { handleEntitySpawn(this, itemMeta) }
            Material.PUFFERFISH_BUCKET -> location.spawn<PufferFish> { handleEntitySpawn(this, itemMeta) }
            Material.TROPICAL_FISH_BUCKET -> location.spawn<TropicalFish> { handleEntitySpawn(this, itemMeta) }

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
        event.getPlayer().inventory[event.hand].takeIf(::isUnlimited) ?: return

        event.isCancelled = true
        event.block.location.run { world.setType(this, Material.AIR) }
    }

    @EventHandler
    fun onItemDamage(event: PlayerItemDamageEvent) {
        event.item.takeIf(::isUnlimited) ?: return
        event.isCancelled = true
    }

    @EventHandler
    fun onDispenserFire(event: BlockDispenseEvent) {
        val itemInHand = event.item.takeIf(::isUnlimited) ?: return

        val dispenserBlock = event.getBlock()
        val blockState = dispenserBlock.state as? Container ?: return

        val blockData = dispenserBlock.blockData as? Directional ?: return

        val facing = blockData.facing
        val facingBlock = dispenserBlock.getRelative(facing)

        val facingX = facingBlock.x
        val facingY = facingBlock.y
        val facingZ = facingBlock.z

        val (velocityX, velocityY, velocityZ) = event.velocity

        instance.schedule {
            // Very quirky: when placing a block, the velocity is faked as the block’s coordinates.
            // When dropping an item, velocity is an actual direction vector.
            // Additionally, `BlockDispenseEvent` is fired AFTER removing the item from inventory, so we cannot grab the index
            if (facingX.toDouble() != velocityX || facingY.toDouble() != velocityY || facingZ.toDouble() != velocityZ) {
                waitFor(1L)
                blockState.inventory.addItem(itemInHand)
                return@schedule
            }

            val foundIndex = findSimilarItemIndex(itemInHand, blockState)
            if (foundIndex == -1) return@schedule

            waitFor(1L)
            blockState.inventory[foundIndex] = itemInHand
        }
    }

    private fun findSimilarItemIndex(comparingItem: ItemStack?, inventoryHolder: InventoryHolder): Int {
        var foundIndex = -1

        val maxIndex = inventoryHolder.inventory.size

        for (index in 0..<maxIndex) {
            val itemStack = inventoryHolder.inventory[index].takeUnless { it.isAir() } ?: continue
            if (!itemStack.isSimilar(comparingItem)) continue

            foundIndex = index
        }

        return foundIndex
    }

    @EventHandler
    fun onDispenserArmorFire(event: BlockDispenseArmorEvent) {
        val itemInHand = event.item.takeIf(::isUnlimited) ?: return

        event.item = itemInHand.clone().edit { persistentDataContainer.remove(_commandUnlimited.unlimitedKey) }
    }

    /**
     * Checks if an item is marked as unlimited.
     *
     * @param itemStack The item to check
     * @return true if the item is unlimited, false otherwise
     */
    private fun isUnlimited(itemStack: ItemStack?): Boolean {
        val itemMeta = itemStack?.itemMeta ?: return false

        return itemMeta.persistentDataContainer.has(_commandUnlimited.unlimitedKey)
    }

    @EventHandler
    fun onItemSpawn(event: ItemSpawnEvent) {
        val itemStack = event.getEntity().itemStack.takeIf(::isUnlimited) ?: return

        event.getEntity().itemStack = itemStack.edit { persistentDataContainer.remove(_commandUnlimited.unlimitedKey) }
    }
}