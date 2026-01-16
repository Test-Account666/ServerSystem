package me.testaccount666.serversystem.managers.config

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.inventory.ItemStack
import java.io.File

interface ConfigReader {
    val configuration: FileConfiguration?

    val file: File?

    fun getObject(path: String, def: Any? = null): Any?

    fun getBoolean(path: String, def: Boolean = false): Boolean

    fun getString(path: String, def: String? = null): String?

    fun getInt(path: String, def: Int = 0): Int

    fun getLong(path: String, def: Long = 0): Long

    fun getDouble(path: String, def: Double = 0.0): Double

    fun getItemStack(path: String, def: ItemStack? = null): ItemStack?

    fun getStringList(path: String, def: MutableList<String> = mutableListOf()): MutableList<String>

    fun set(path: String, `object`: Any?)

    fun save()

    fun reload()

    fun load(file: File?)

    fun getConfigurationSection(path: String?): ConfigurationSection?

    fun isConfigurationSection(path: String?): Boolean
}