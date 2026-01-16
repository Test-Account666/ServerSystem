package me.testaccount666.serversystem.commands.executables.inventorysee.offline

import de.tr7zw.nbtapi.NBT
import de.tr7zw.nbtapi.handler.NBTHandlers
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.executables.inventorysee.utils.InventorySeeUtils
import me.testaccount666.serversystem.utils.BiDirectionalHashMap
import me.testaccount666.serversystem.utils.ItemStackExtensions.Companion.isAir
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.Inventory
import java.io.IOException
import java.nio.file.Path
import java.util.*
import java.util.logging.Level

class InventoryLoader {
    val inventoryMap: BiDirectionalHashMap<UUID, Inventory> = BiDirectionalHashMap()

    fun loadOfflineInventory(offlinePlayer: OfflinePlayer): Inventory? {
        if (!offlinePlayer.hasPlayedBefore()) return null

        val playerUUID = offlinePlayer.uniqueId
        val cachedInventory = inventoryMap.getValue(playerUUID)
        if (cachedInventory != null) return cachedInventory

        val playerDataFile = Path.of(Bukkit.getWorlds().first().worldFolder.path, "playerdata", "${playerUUID}.dat").toFile()

        if (!playerDataFile.exists()) {
            log.warning("Player data file not found for ${offlinePlayer.name} (${offlinePlayer.uniqueId})")
            return null
        }

        try {
            val fileHandle = NBT.getFileHandle(playerDataFile)

            val inventoryTag = fileHandle.getCompoundList("Inventory") ?: return null
            val inventory = Bukkit.createInventory(null, 54, "${offlinePlayer.name}'s Inventory")

            for (index in 0..<inventoryTag.size()) {
                val itemTag = inventoryTag.get(index)
                if (itemTag == null || itemTag.toString().equals("{}", true)) continue

                val slot = itemTag.getByte("Slot")

                val itemStack = NBT.itemStackFromNBT(itemTag)
                inventory.setItem(slot.toInt(), itemStack)
            }

            val equipmentTag = fileHandle.getCompound("equipment")
            val equipmentSlotList = arrayOf("feet", "legs", "chest", "head", "offhand")

            if (equipmentTag != null) for (index in equipmentSlotList.indices) {
                val equipmentSlot = equipmentSlotList[index]
                val itemTag = equipmentTag.getCompound(equipmentSlot)
                if (itemTag == null || itemTag.toString().equals("{}", true)) continue

                val itemStack = NBT.itemStackFromNBT(itemTag)

                inventory.setItem(36 + index, itemStack)
            }

            addSectionDecorators(inventory)

            inventoryMap.put(playerUUID, inventory)
            return inventory
        } catch (exception: IOException) {
            log.log(Level.SEVERE, "Failed to load inventory for ${offlinePlayer.name} (${offlinePlayer.uniqueId})", exception)
            return null
        }
    }

    fun saveOfflineInventory(playerUUID: UUID, inventory: Inventory) {
        val offlinePlayer = Bukkit.getOfflinePlayer(playerUUID)
        val playerDataFile = Path.of(Bukkit.getWorlds().first().worldFolder.path, "playerdata", "${playerUUID}.dat").toFile()

        if (!playerDataFile.exists()) {
            log.warning("Player data file not found for ${offlinePlayer.name} (${offlinePlayer.uniqueId})")
            return
        }

        try {
            val fileHandle = NBT.getFileHandle(playerDataFile)

            val inventoryTag = fileHandle.getCompoundList("Inventory")
            inventoryTag.clear()

            for (slot in 0..40) {
                val item = inventory.getItem(slot)
                if (item.isAir()) continue

                val itemTag = NBT.itemStackToNBT(item)
                itemTag.setByte("Slot", slot.toByte())
                inventoryTag.addCompound(itemTag)
            }

            var equipmentTag = fileHandle.getCompound("equipment")
            val equipmentSlotList = arrayOf("feet", "legs", "chest", "head", "offhand")

            val addTag = equipmentTag == null
            equipmentTag = equipmentTag ?: NBT.createNBTObject()

            var added = false
            for (i in equipmentSlotList.indices) {
                val equipmentSlot = equipmentSlotList[i]
                val slotIndex = 36 + i
                val item = inventory.getItem(slotIndex)
                if (item.isAir()) {
                    equipmentTag.removeKey(equipmentSlot)
                    continue
                }

                added = true
                equipmentTag.setItemStack(equipmentSlot, item)
            }

            if (addTag && added) fileHandle.set("equipment", equipmentTag, NBTHandlers.STORE_READWRITE_TAG)

            fileHandle.save()

            log.info("Saved inventory for ${offlinePlayer.name} (${offlinePlayer.uniqueId})")
        } catch (exception: IOException) {
            log.log(Level.SEVERE, "Failed to save inventory for ${offlinePlayer.name} (${offlinePlayer.uniqueId})", exception)
        }
    }

    fun addSectionDecorators(displayInventory: Inventory) = InventorySeeUtils.addSectionDecorators(displayInventory, true)
}
