package me.testaccount666.serversystem.commands.executables.kit.manager

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.util.*
import java.util.logging.Level

class KitManager {
    private val _kits = HashMap<String, Kit>()

    constructor() {
        val kitDirectory = Path.of(instance.dataFolder.path, "Kits").toFile()
        if (!kitDirectory.exists()) kitDirectory.mkdirs()
        else check(kitDirectory.isDirectory) { "The Kit directory is not a directory!" }

        loadKits(kitDirectory)
    }

    private fun loadKits(kitDirectory: File) {
        val kitFiles = kitDirectory.listFiles() ?: return

        kitFiles.forEach(this::loadKit)
    }

    private fun loadKit(kitFile: File) {
        val kitConfig = YamlConfiguration.loadConfiguration(kitFile)
        val name = kitConfig.getString("Name") ?: return

        val coolDown = kitConfig.getLong("Cooldown", -1)

        val inventoryContents = LinkedList<ItemStack?>()

        val inventorySection = kitConfig.getConfigurationSection("Items.Inventory")
        if (inventorySection != null) for (key in 0..41) {
            val item = inventorySection.getItemStack(key.toString())
            inventoryContents.add(item)
        }

        _kits[name] = Kit(name, coolDown, inventoryContents.toTypedArray<ItemStack?>())
    }

    val allKitNames
        get() = _kits.keys.toList()

    fun getKit(name: String) = _kits[name]

    fun kitExists(name: String) = _kits.containsKey(name)

    fun addKit(kit: Kit) {
        _kits[kit.name] = kit
    }

    fun removeKit(name: String) {
        _kits.remove(name)

        val kitFile = getKitFile(name)
        if (!kitFile.exists() || !kitFile.delete()) log.warning("Failed to delete kit file: ${kitFile.path}")
    }

    /**
     * Saves a kit to a file in the Kits directory.
     * 
     * @param kit The kit to save
     * @return true if the kit was saved successfully, false otherwise
     */
    fun saveKit(kit: Kit): Boolean {
        val kitFile = getKitFile(kit.name)
        val kitConfig = YamlConfiguration()

        kitConfig.set("Name", kit.name)
        kitConfig.set("Cooldown", kit.coolDown)

        val inventoryContents = kit.inventoryContents
        for (index in inventoryContents.indices) kitConfig.set("Items.Inventory.${index}", inventoryContents[index])

        try {
            kitConfig.save(kitFile)
            return true
        } catch (exception: IOException) {
            log.log(Level.SEVERE, "Failed to save kit '${kit.name}'", exception)
            return false
        }
    }

    /**
     * Saves all kits to files in the Kits directory.
     * 
     * @return The number of kits that were saved successfully
     */
    fun saveAllKits() = _kits.values.count { saveKit(it) }

    /**
     * Gets the file for a kit with the given name.
     * 
     * @param name The name of the kit
     * @return The file for the kit
     */
    private fun getKitFile(name: String): File {
        val kitDirectory = Path.of(instance.dataFolder.path, "Kits").toFile()
        if (!kitDirectory.exists()) kitDirectory.mkdirs()

        return File(kitDirectory, "${name}.yml")
    }
}
