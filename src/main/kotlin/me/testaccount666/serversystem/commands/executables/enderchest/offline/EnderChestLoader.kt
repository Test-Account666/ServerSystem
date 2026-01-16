package me.testaccount666.serversystem.commands.executables.enderchest.offline

import de.tr7zw.nbtapi.NBT
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.utils.BiDirectionalHashMap
import me.testaccount666.serversystem.utils.ItemStackExtensions.Companion.isAir
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.Inventory
import java.io.IOException
import java.nio.file.Path
import java.util.*
import java.util.logging.Level

class EnderChestLoader {
    val inventoryMap = BiDirectionalHashMap<UUID, Inventory>()

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
            val enderItemsTag = fileHandle.getCompoundList("EnderItems") ?: return null

            val inventory = Bukkit.createInventory(null, 27, "${offlinePlayer.name}'s Ender Chest")

            for (index in 0..<enderItemsTag.size()) {
                val itemTag = enderItemsTag.get(index) ?: continue

                val slot = itemTag.getByte("Slot")
                val itemStack = NBT.itemStackFromNBT(itemTag)

                inventory.setItem(slot.toInt(), itemStack)
            }

            inventoryMap.put(playerUUID, inventory)
            return inventory
        } catch (exception: IOException) {
            log.log(Level.SEVERE, "Failed to load ender chest for ${offlinePlayer.name} (${offlinePlayer.uniqueId})", exception)
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
            val enderItemsTag = fileHandle.getCompoundList("EnderItems") ?: return

            enderItemsTag.clear()
            for (slot in 0..26) {
                val item = inventory.getItem(slot)
                if (item.isAir()) continue

                val itemTag = NBT.itemStackToNBT(item)
                itemTag.setByte("Slot", slot.toByte())
                enderItemsTag.addCompound(itemTag)
            }

            fileHandle.save()

            log.info("Saved ender chest for ${offlinePlayer.name} (${offlinePlayer.uniqueId})")
        } catch (exception: IOException) {
            log.log(Level.SEVERE, "Failed to save ender chest for ${offlinePlayer.name} (${offlinePlayer.uniqueId})", exception)
        }
    }
}
