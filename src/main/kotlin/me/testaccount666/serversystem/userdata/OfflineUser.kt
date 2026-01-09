package me.testaccount666.serversystem.userdata

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.commands.executables.back.CommandBack
import me.testaccount666.serversystem.managers.config.ConfigurationManager
import me.testaccount666.serversystem.managers.database.moderation.AbstractModerationDatabaseManager
import me.testaccount666.serversystem.managers.database.moderation.MySqlModerationDatabaseManager
import me.testaccount666.serversystem.managers.database.moderation.SqliteModerationDatabaseManager
import me.testaccount666.serversystem.managers.messages.MessageManager
import me.testaccount666.serversystem.moderation.AbstractModerationManager
import me.testaccount666.serversystem.moderation.ban.MySqlBanManager
import me.testaccount666.serversystem.moderation.ban.SqliteBanManager
import me.testaccount666.serversystem.moderation.mute.MySqlMuteManager
import me.testaccount666.serversystem.moderation.mute.SqliteMuteManager
import me.testaccount666.serversystem.userdata.home.HomeManager
import me.testaccount666.serversystem.userdata.money.AbstractBankAccount
import me.testaccount666.serversystem.userdata.money.EconomyProvider
import me.testaccount666.serversystem.userdata.persistence.*
import me.testaccount666.serversystem.userdata.vanish.VanishData
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.*
import java.math.BigInteger
import java.sql.SQLException
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Represents an offline user.
 * This class provides functionality for managing user data that persists
 * even when the player is offline.
 *
 *
 * User data can be optionally compressed using GZIP compression to reduce disk space usage.
 * Compression can be enabled or disabled in the config.yml configuration file.
 */
open class OfflineUser(val userFile: File) {
    @SaveableField(path = "User.IgnoredPlayers", handler = UuidSetFieldHandler::class)
    val ignoredPlayers: MutableSet<UUID> = HashSet()

    @SaveableField(path = "User.LastKnownName")
    protected var name: String? = null

    lateinit var uuid: UUID
        protected set
    open var player: OfflinePlayer? = null
        get() {
            if (field == null) field = Bukkit.getOfflinePlayer(uuid)

            return field
        }
    var banManager: AbstractModerationManager? = null
        protected set
    var muteManager: AbstractModerationManager? = null
        protected set
    var bankAccount: AbstractBankAccount? = null
        protected set
    var homeManager: HomeManager? = null
        protected set
    protected lateinit var userConfig: FileConfiguration

    @SaveableField(path = "User.LastSeen")
    var lastSeen: Long = 0
        protected set

    @SaveableField(path = "User.LastKnownIp")
    var lastKnownIp: String? = null
        protected set

    @SaveableField(path = "User.LogoutPosition", handler = LocationFieldHandler::class)
    var logoutPosition: Location? = null

    @SaveableField(path = "User.VanishData.IsVanish")
    var isVanish: Boolean = false

    @SaveableField(path = "User.VanishData.Data", handler = VanishDataFieldHandler::class)
    var vanishData: VanishData? = null
        protected set

    @SaveableField(path = "User.IsGodMode")
    var isGodMode: Boolean = false

    @SaveableField(path = "User.AcceptsTeleports")
    var isAcceptsTeleports: Boolean = false

    @SaveableField(path = "User.AcceptsMessages")
    var isAcceptsMessages: Boolean = false

    @SaveableField(path = "User.SocialSpyEnabled")
    var isSocialSpyEnabled: Boolean = false

    @SaveableField(path = "User.CommandSpyEnabled")
    var isCommandSpyEnabled: Boolean = false

    @SaveableField(path = "User.LastDeathLocation", handler = LocationFieldHandler::class)
    var lastDeathLocation: Location? = null

    @SaveableField(path = "User.LastTeleportLocation", handler = LocationFieldHandler::class)
    var lastTeleportLocation: Location? = null

    @SaveableField(path = "User.LastBackType", handler = EnumFieldHandler::class)
    var lastBackType: CommandBack.BackType? = null

    @SaveableField(path = "User.PlayerLanguage")
    lateinit var playerLanguage: String

    @SaveableField(path = "User.KitCooldowns", handler = KitMapFieldHandler::class)
    protected var kitCooldowns: MutableMap<String, Long> = HashMap()

    @SaveableField(path = "Language.UsesDefault")
    var isUsesDefaultLanguage: Boolean = false

    init {
        loadBasicData()
    }

    internal constructor(onlineUser: OfflineUser) : this(onlineUser.userFile)

    /**
     * Checks if a file is compressed with GZIP.
     *
     * @param file The file to check
     * @return true if the file is compressed with GZIP, false otherwise
     */
    private fun isCompressed(file: File): Boolean {
        if (!file.exists() || file.length() < 2) return false

        try {
            FileInputStream(file).use { fis ->
                val signature = ByteArray(2)
                if (fis.read(signature) != 2) return false
                // Check for GZIP magic number (0x1F8B)
                return (signature[0] == 0x1F.toByte() && signature[1] == 0x8B.toByte())
            }
        } catch (exception: IOException) {
            return false
        }
    }

    /**
     * Loads a YAML configuration from a file, decompressing it if necessary.
     *
     * @param file The file to load
     * @return The loaded YAML configuration
     */
    private fun loadYamlConfiguration(file: File): FileConfiguration {
        // File is not compressed, load it normally
        if (!isCompressed(file)) return YamlConfiguration.loadConfiguration(file)

        // File is compressed, decompress it first
        try {
            FileInputStream(file).use { fileInputStream ->
                GZIPInputStream(fileInputStream).use { gzipInputStream ->
                    InputStreamReader(gzipInputStream).use { inputReader ->
                        return YamlConfiguration.loadConfiguration(inputReader)
                    }
                }
            }
        } catch (_: IOException) {
            // If decompression fails, try loading normally as fallback
            return YamlConfiguration.loadConfiguration(file)
        }
    }

    protected open fun loadBasicData() {
        userConfig = loadYamlConfiguration(userFile)

        uuid = UUID.fromString(userFile.name.replace(".yml.gz", ""))

        // Set default values before loading from config
        name = null
        lastSeen = System.currentTimeMillis()
        lastKnownIp = "Unknown"
        isVanish = false
        vanishData = VanishData(false, false, false, false)
        isGodMode = false
        this.isAcceptsTeleports = true
        this.isAcceptsMessages = true
        this.isSocialSpyEnabled = false
        this.isCommandSpyEnabled = false
        this.isUsesDefaultLanguage = true
        playerLanguage = MessageManager.defaultLanguage!!
        lastBackType = CommandBack.BackType.NONE

        // PersistenceManager loads all annotated fields
        PersistenceManager.loadFields(this, userConfig)
        // Quick fix that blocks potentially wanted behavior, but eh...
        if (playerLanguage.equals(System.getProperty("user.language"), ignoreCase = true)) playerLanguage = MessageManager.defaultLanguage!!
        playerLanguage = playerLanguage.lowercase(Locale.getDefault())

        if (name == null) name = player?.name

        homeManager = HomeManager(this, userConfig)
        bankAccount =
            ServerSystem.instance.registry.getService<EconomyProvider>()
                .instantiateBankAccount(this, BigInteger.valueOf(0), userConfig)

        val moderationManager = ServerSystem.instance.registry.getService<AbstractModerationDatabaseManager>()

        // Create the appropriate ban manager based on the database type
        try {
            moderationManager.getConnection().use { _ ->

                banManager = when (moderationManager) {
                    is SqliteModerationDatabaseManager -> SqliteBanManager(uuid)
                    is MySqlModerationDatabaseManager -> MySqlBanManager(uuid)
                    else -> null
                }
            }
        } catch (exception: SQLException) {
            throw RuntimeException("Error loading userdata! (BanManager)", exception)
        }

        // Create the appropriate mute manager based on the database type
        try {
            moderationManager.getConnection().use { _ ->
                muteManager = when (moderationManager) {
                    is SqliteModerationDatabaseManager -> SqliteMuteManager(uuid)
                    is MySqlModerationDatabaseManager -> MySqlMuteManager(uuid)
                    else -> null
                }
            }
        } catch (exception: SQLException) {
            throw RuntimeException("Error loading userdata! (MuteManager)", exception)
        }
    }

    private val isCompressionEnabled: Boolean
        /**
         * Checks if compression is enabled in the configuration.
         *
         * @return true if compression is enabled, false otherwise
         */
        get() = ServerSystem.instance.registry.getService<ConfigurationManager>().generalConfig!!
            .getBoolean("UserData.Compression.Enabled", true)

    /**
     * Saves a YAML configuration to a file with compression.
     *
     * @param config The YAML configuration to save
     * @param file   The file to save to
     * @throws IOException If an I/O error occurs
     */
    @Throws(IOException::class)
    private fun saveCompressedYamlConfiguration(config: FileConfiguration, file: File) {
        FileOutputStream(file).use { fileOutputStream ->
            GZIPOutputStream(fileOutputStream).use { gzipOutputStream ->
                OutputStreamWriter(gzipOutputStream).use { streamWriter ->
                    streamWriter.write(config.saveToString())
                    streamWriter.flush()
                }
            }
        }
    }

    /**
     * Saves the user's data to their configuration file.
     * This method should be called whenever user data is modified.
     * If compression is enabled in the configuration, the data will be compressed.
     *
     * @throws RuntimeException if there is an error saving the user data
     */
    open fun save() {
        // PersistenceManager saves all annotated fields
        PersistenceManager.saveFields(this, userConfig)

        try {
            if (this.isCompressionEnabled) saveCompressedYamlConfiguration(userConfig, userFile)
            else userConfig.save(userFile)
        } catch (exception: Exception) {
            throw RuntimeException("Error while trying to save user data for user '${getName()}' ('${uuid}')", exception)
        }
    }

    /**
     * Gets the name of this user.
     *
     * @return An Optional containing the name of this user, or an empty Optional if the name is not available
     */
    open fun getName(): Optional<String> = Optional.ofNullable(name)

    fun isIgnoredPlayer(uuid: UUID?): Boolean = uuid in ignoredPlayers

    fun addIgnoredPlayer(uuid: UUID) = ignoredPlayers.add(uuid)

    fun removeIgnoredPlayer(uuid: UUID) = ignoredPlayers.remove(uuid)

    fun isOnKitCooldown(kitName: String?): Boolean {
        val cooldown = kitCooldowns.getOrDefault(kitName, null) ?: return false
        return cooldown > System.currentTimeMillis()
    }

    fun getKitCooldown(kitName: String?): Long {
        val cooldown = kitCooldowns.getOrDefault(kitName, null) ?: return -1
        return cooldown
    }

    fun setKitCooldown(kitName: String, cooldown: Long) {
        kitCooldowns[kitName] = cooldown + System.currentTimeMillis()
    }
}