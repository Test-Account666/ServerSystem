package me.testaccount666.serversystem.managers.config

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.managers.messages.MessageManager
import me.testaccount666.serversystem.utils.FileUtils
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.Level
import kotlin.reflect.KClass

/**
 * Default implementation of ConfigReader interface that handles loading, validation,
 * and operations on YAML configuration files.
 */
open class DefaultConfigReader(
    override val file: File, protected val _plugin: Plugin,
) : ConfigReader {
    override val configuration: FileConfiguration = YamlConfiguration.loadConfiguration(file)
    protected var originalCfg: FileConfiguration? = null

    @JvmField
    protected var _newReader: DefaultConfigReader? = null

    /**
     * Creates a new DefaultConfigReader for the specified file and plugin.
     *
     * @param file   The configuration file to read
     * @param _plugin The plugin associated with this configuration
     * @throws java.io.FileNotFoundException If the default configuration cannot be found
     */
    init {
        loadDefaultConfig()

        if (!validateAndFixConfig()) createBackupAndSave()
    }

    /**
     * Logs a configuration fix with a consistent format.
     *
     * @param key     The configuration key being fixed
     * @param problem The problem description
     */
    private fun logConfigFix(key: String?, problem: String?) {
        ServerSystem.log.warning("Fixing ${problem} config entry '${key}' in file '${file.name}'")
    }

    /**
     * Loads the default configuration from the plugin's resources.
     * For messages.yml, attempts to load a language-specific version.
     *
     * @throws java.io.FileNotFoundException If the default configuration file cannot be found
     */
    @Throws(FileNotFoundException::class)
    protected open fun loadDefaultConfig() {
        val filename = file.name

        if (_plugin.getResource(filename) != null) {
            originalCfg = YamlConfiguration.loadConfiguration(InputStreamReader(_plugin.getResource(filename)))
            return
        }

        if (filename.equals("messages.yml", ignoreCase = true) || filename.equals("mappings.yml", ignoreCase = true)) {
            val language: String = configuration.getString("language", MessageManager.FALLBACK_LANGUAGE)!!
            val languageFile = "messages/${language}/${filename}"

            if (_plugin.getResource(languageFile) != null) originalCfg =
                YamlConfiguration.loadConfiguration(InputStreamReader(_plugin.getResource(languageFile)))
            else {
                originalCfg = YamlConfiguration.loadConfiguration(InputStreamReader(_plugin.getResource("messages/english/${filename}")))
                ServerSystem.log.warning("Couldn't find messages file for language '${language}', using English instead")
            }
            return
        }

        throw FileNotFoundException("Couldn't find default config file '${filename}'!")
    }

    /**
     * Validates the configuration against the default configuration.
     * Fixes missing entries and type mismatches.
     *
     * @return true if the configuration is valid (no fixes needed), false otherwise
     */
    protected open fun validateAndFixConfig(): Boolean {
        if (originalCfg == null) return true // No default config to validate against


        var isValid = true
        val typeWarnings: MutableMap<KClass<*>?, String?> = initializeTypeWarnings()
        val defaultSection = originalCfg!!.getConfigurationSection("") ?: return true


        for (key in defaultSection.getKeys(true)) {
            // Skip example entries
            if (key.lowercase().contains("example")) continue

            // Fix missing entries
            if (!configuration.isSet(key)) {
                logConfigFix(key, "missing")
                configuration.set(key, originalCfg!!.get(key))
                isValid = false
                continue
            }

            // Fix type mismatches
            val userValue = configuration.get(key)
            val defaultValue = originalCfg!!.get(key)

            if (userValue == null || defaultValue == null) continue

            val userType: Class<*> = userValue.javaClass
            val defaultType: Class<*> = defaultValue.javaClass

            if (userType.isAssignableFrom(defaultType)) continue

            // Treat String and List<String> as compatible; do not override user's value
            val userIsStringList = (userValue is MutableList<*>)
            val defaultIsStringList = (defaultValue is MutableList<*>)
            if ((userIsStringList && defaultType.kotlin == String) || (defaultIsStringList && userType.kotlin == String)) {
                // Further ensure the list (if present) contains strings or is empty
                val list = if (userIsStringList) userValue
                else defaultValue as MutableList<*>

                if (list.isEmpty() || list.stream().allMatch { line: Any? -> line == null || line is String }) continue
            }

            val warningMessage = typeWarnings[defaultType.kotlin]
            if (warningMessage != null) {
                logConfigFix(key, warningMessage)
                configuration.set(key, defaultValue)
                isValid = false
            }
        }

        return isValid
    }

    /**
     * Creates a backup of the configuration file, saves changes, and reloads the configuration.
     */
    private fun createBackupAndSave() {
        val filename = file.name
        ServerSystem.log.warning("One or more errors with your '${filename}' file were found and fixed, a backup was made before saving")

        try {
            // Create a timestamped backup file
            val dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm-ss")
            val now = LocalDateTime.now()
            val date = dtf.format(now)
            val backupFile = Path.of("plugins", "ServerSystem", "${filename}.backup-${date}").toFile()

            FileUtils.copyFile(file, backupFile)

            save()
            reload()
        } catch (exception: IOException) {
            ServerSystem.log.log(Level.SEVERE, "Failed to create backup for '${filename}', changes are not saved!", exception)
        }
    }

    override fun getObject(path: String, def: Any?): Any? {
        val configReader: DefaultConfigReader = _newReader ?: this

        configReader.ensureConfigHasValue(path)
        return configReader.configuration.get(path, def)
    }

    override fun getBoolean(path: String, def: Boolean): Boolean {
        val configReader: DefaultConfigReader = _newReader ?: this

        configReader.ensureConfigHasValue(path)
        return configReader.configuration.getBoolean(path, def)
    }

    override fun getString(path: String, def: String?): String? {
        val configReader: DefaultConfigReader = _newReader ?: this

        configReader.ensureConfigHasValue(path)

        // Allow `List<String>` to be consumed as a String by joining with line breaks
        if (configReader.configuration.isList(path)) {
            val list = configReader.configuration.getStringList(path)
            if (list.isEmpty()) return def

            return list.joinToString("\n")
        }

        return configReader.configuration.getString(path, def)
    }

    override fun getInt(path: String, def: Int): Int {
        val configReader: DefaultConfigReader = _newReader ?: this

        configReader.ensureConfigHasValue(path)
        return configReader.configuration.getInt(path, def)
    }

    override fun getLong(path: String, def: Long): Long {
        val configReader: DefaultConfigReader = _newReader ?: this

        configReader.ensureConfigHasValue(path)
        return configReader.configuration.getLong(path, def)
    }

    override fun getDouble(path: String, def: Double): Double {
        val configReader: DefaultConfigReader = _newReader ?: this

        configReader.ensureConfigHasValue(path)
        return configReader.configuration.getDouble(path, def)
    }

    override fun getItemStack(path: String, def: ItemStack?): ItemStack? {
        val configReader: DefaultConfigReader = _newReader ?: this

        configReader.ensureConfigHasValue(path)
        return configReader.configuration.getItemStack(path, def)
    }

    override fun getStringList(path: String, def: MutableList<String>): MutableList<String> {
        val configReader: DefaultConfigReader = _newReader ?: this

        configReader.ensureConfigHasValue(path)
        if (!configReader.configuration.isSet(path)) return def

        // Allow String to be consumed as List<String>
        if (configReader.configuration.isString(path)) {
            val value = configReader.configuration.getString(path)
            return if (value == null) def else mutableListOf(value)
        }

        return configReader.configuration.getStringList(path)
    }

    override fun set(path: String, `object`: Any?) {
        val configReader: DefaultConfigReader = _newReader ?: this

        configReader.configuration.set(path, `object`)
    }

    override fun save() {
        val configReader: DefaultConfigReader = _newReader ?: this

        try {
            configReader.configuration.save(configReader.file)
        } catch (exception: IOException) {
            ServerSystem.log.log(Level.SEVERE, "Failed to save configuration file '${configReader.file.name}'", exception)
        }
    }

    override fun reload() {
        val configReader: DefaultConfigReader = _newReader ?: this

        try {
            configReader.configuration.load(configReader.file)
        } catch (exception: IOException) {
            ServerSystem.log.log(Level.SEVERE, "Failed to reload configuration file '${configReader.file.name}'", exception)
        } catch (exception: InvalidConfigurationException) {
            ServerSystem.log.log(Level.SEVERE, "Failed to reload configuration file '${configReader.file.name}'", exception)
        }
    }

    override fun load(file: File?) {
        requireNotNull(file)

        try {
            _newReader = DefaultConfigReader(file, ServerSystem.instance)
        } catch (exception: Exception) {
            ServerSystem.log.log(Level.SEVERE, "Failed to load configuration file '${file.name}'", exception)
        }
    }

    override fun getConfigurationSection(path: String?): ConfigurationSection? {
        if (path == null) return null

        val configReader: DefaultConfigReader = _newReader ?: this

        configReader.ensureConfigHasValue(path)
        return configReader.configuration.getConfigurationSection(path)
    }

    override fun isConfigurationSection(path: String?): Boolean {
        if (path == null) return false

        val configReader: DefaultConfigReader = _newReader ?: this

        return configReader.configuration.isConfigurationSection(path)
    }

    /**
     * Ensures a configuration value exists by restoring it from the default config if missing.
     *
     * @param path The configuration path to check
     */
    private fun ensureConfigHasValue(path: String) {
        if (originalCfg == null) return

        // Don't do anything if the value is already set or not in the default config
        if (configuration.isSet(path) || !originalCfg!!.isSet(path)) return

        val partialPath = findClosestExistingParentPath(path)

        if (partialPath.isEmpty()) {
            // No parent exists, restore the entire section
            restoreConfigValue(path)
            return
        }

        val section = configuration.getConfigurationSection(partialPath)
        if (section == null) {
            restoreConfigValue(path)
            return
        }

        section.set(path.substring(partialPath.length + 1), originalCfg!!.get(path))
        saveAndReload()
    }

    /**
     * Finds the closest existing parent path for the given path.
     *
     * @param path The configuration path to find a parent for
     * @return The closest existing parent path, or empty string if none found
     */
    private fun findClosestExistingParentPath(path: String): String {
        if (!path.contains(".")) return ""

        val pathParts: Array<String?> = path.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val currentPath = StringBuilder()

        for (i in 0..<pathParts.size - 1) {
            if (i > 0) currentPath.append(".")
            currentPath.append(pathParts[i])

            if (configuration.isSet(currentPath.toString())) return currentPath.toString()
        }

        return ""
    }

    /**
     * Restores a configuration value from the default configuration.
     *
     * @param path The path to restore
     */
    private fun restoreConfigValue(path: String) {
        configuration.set(path, originalCfg!!.get(path))
        saveAndReload()
    }

    /**
     * Saves and reloads the configuration file.
     */
    private fun saveAndReload() {
        try {
            configuration.save(file)
            configuration.load(file)
        } catch (exception: IOException) {
            ServerSystem.log.log(Level.SEVERE, "Failed to save and reload configuration file '${file.name}'", exception)
        } catch (exception: InvalidConfigurationException) {
            ServerSystem.log.log(Level.SEVERE, "Failed to save and reload configuration file '${file.name}'", exception)
        }
    }

    companion object {
        /**
         * Static factory method to load a configuration file with a specific plugin instance.
         *
         * @param file   The configuration file to load
         * @param _plugin The plugin instance to use
         * @return A ConfigReader instance for the file
         * @throws FileNotFoundException If the default configuration cannot be found
         */
        /**
         * Static factory method to load a configuration file using the main plugin instance.
         *
         * @param file The configuration file to load
         * @return A ConfigReader instance for the file
         * @throws FileNotFoundException If the default configuration cannot be found
         */
        @JvmOverloads
        fun loadConfiguration(file: File, plugin: Plugin = ServerSystem.instance): ConfigReader = DefaultConfigReader(file, plugin)

        /**
         * Initializes a map of type warning messages for different config value types.
         *
         * @return Map of types to warning messages
         */
        private fun initializeTypeWarnings(): MutableMap<KClass<*>?, String?> {
            val typeWarnings = HashMap<KClass<*>?, String?>()
            typeWarnings[String::class] = "should be a string"
            typeWarnings[Int::class] = "should be an integer"
            typeWarnings[Long::class] = "should be a long"
            typeWarnings[Boolean::class] = "should be a boolean"
            typeWarnings[Double::class] = "should be a double"
            typeWarnings[ItemStack::class] = "should be an ItemStack"
            return typeWarnings
        }
    }
}