package me.testaccount666.migration

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.executables.kit.manager.Kit
import me.testaccount666.serversystem.commands.executables.kit.manager.KitManager
import me.testaccount666.serversystem.commands.executables.warp.manager.Warp
import me.testaccount666.serversystem.commands.executables.warp.manager.WarpManager
import me.testaccount666.serversystem.moderation.AbstractModeration
import me.testaccount666.serversystem.moderation.BanModeration
import me.testaccount666.serversystem.moderation.MuteModeration
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.OfflineUser
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.userdata.home.Home
import me.testaccount666.serversystem.utils.ItemStackExtensions.Companion.isAir
import me.testaccount666.serversystem.utils.Version
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*
import java.util.function.Consumer
import java.util.logging.Level

class LegacyDataMigrator {
    private lateinit var _legacyDataDirectory: File

    /**
     * Retrieves an offline user by UUID.
     * 
     * @param uuid The UUID of the user to retrieve
     * @return Optional containing the offline user if found, empty otherwise
     */
    private fun getOfflineUser(uuid: UUID): OfflineUser? {
        val userManager = instance.registry.getService<UserManager>()
        val user = userManager.getUserOrNull(uuid)

        if (user == null) {
            log.warning("Could not find user with UUID: ${uuid}")
            return null
        }

        return user.offlineUser
    }

    /**
     * Logs a message with the specified level.
     * 
     * @param level   The log level
     * @param message The message to log
     */
    private fun log(level: Level, message: String) = log.log(level, message)

    /**
     * Logs a message with the specified level and exception.
     * 
     * @param level     The log level
     * @param message   The message to log
     * @param exception The exception to log
     */
    private fun log(level: Level, message: String, exception: Exception?) = log.log(level, message, exception)

    /**
     * Processes database records using a generic approach.
     * 
     * @param connection      The database connection
     * @param query           The SQL query to execute
     * @param recordType      The type of records being processed (for logging)
     * @param recordProcessor The function to process each record
     */
    private fun processRecords(connection: Connection, query: String, recordType: String, recordProcessor: SqlRecordProcessor) {
        try {
            connection.createStatement().use { statement ->
                statement.executeQuery(query).use { resultSet ->
                    var migratedCount = 0
                    while (resultSet.next()) try {
                        recordProcessor.process(resultSet, migratedCount)
                        migratedCount++
                    } catch (exception: Exception) {
                        log(Level.WARNING, "Failed to migrate a ${recordType} record: ${exception.message}", exception)
                    }
                    log(Level.INFO, "Successfully migrated ${migratedCount} ${recordType} records")
                }
            }
        } catch (exception: SQLException) {
            log(Level.SEVERE, "Error querying ${recordType} database: ${exception.message}", exception)
        }
    }

    /**
     * Opens a database connection with the given URL.
     * 
     * @param url       The JDBC URL
     * @param processor The function to process the connection
     */
    private fun withConnection(url: String, processor: Consumer<Connection>) {
        try {
            DriverManager.getConnection(url).use { processor.accept(it) }
        } catch (exception: SQLException) {
            log(Level.SEVERE, "Error connecting to database: ${exception.message}", exception)
        }
    }

    /**
     * Applies moderation to a user.
     * 
     * @param targetUuid The UUID of the target user
     * @param moderation The moderation to apply
     */
    private fun applyModeration(targetUuid: UUID, moderation: AbstractModeration) {
        val user = getOfflineUser(targetUuid) ?: return

        if (moderation is BanModeration) user.banManager.addModeration(moderation)
        if (moderation is MuteModeration) user.muteManager.addModeration(moderation)
    }

    val isLegacyDataPresent: Boolean
        /**
         * Checks if legacy data is present that needs to be migrated.
         * 
         * @return true if legacy data is present, false otherwise
         */
        get() {
            val directory = instance.dataFolder
            if (!directory.exists()) return false

            val previousVersionFile = File(directory, "previousVersion.yml")
            if (!previousVersionFile.exists()) return true

            val previousVersionConfig = YamlConfiguration.loadConfiguration(previousVersionFile)
            var previousVersion = previousVersionConfig.getString("previousVersion", "")!!
            if (!previousVersion.contains(".")) try {
                val verInt = previousVersion.toInt()
                if (verInt < 300) return true

                val split = previousVersion.split("".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                previousVersion = split.joinToString { "." }.trim { it <= ' ' }

                if (previousVersion.startsWith(".")) previousVersion = previousVersion.substring(1)
                if (previousVersion.endsWith(".")) previousVersion = previousVersion.substring(0, previousVersion.length - 1)
                if (previousVersion.isEmpty()) return true
            } catch (_: NumberFormatException) {
                return true
            }

            return Version(previousVersion) < Version("3.0.0")
        }

    /**
     * Prepares for migration by renaming the current data directory to a backup directory.
     */
    fun prepareMigration() {
        val directory = instance.dataFolder
        if (!directory.exists()) return

        _legacyDataDirectory = File(directory.parent, "ServerSystem-LegacyData-${System.currentTimeMillis()}")
        directory.renameTo(_legacyDataDirectory)
    }

    /**
     * Migrates all legacy data.
     */
    fun migrateLegacyData() {
        migrateModerationAndEconomy()
        migrateHomes()
        migrateWarps()
        migrateKits()
    }

    /**
     * Migrates kits from legacy data.
     */
    private fun migrateKits() {
        val legacyKitsFile = File(_legacyDataDirectory, "kits.yml")
        if (!legacyKitsFile.exists()) return
        val legacyKitsConfig = YamlConfiguration.loadConfiguration(legacyKitsFile)
        log(Level.INFO, "Legacy Kits Data detected. Migrating...")

        val kitsSection = legacyKitsConfig.getConfigurationSection("Kits")
        if (kitsSection == null) {
            log(Level.WARNING, "No kits found in legacy kits file")
            return
        }
        val kitNames = kitsSection.getKeys(false)
        var migratedCount = 0

        val defaultItem = ItemStack(Material.AIR)
        val kitManager = instance.registry.getService<KitManager>()

        for (kitName in kitNames) try {
            val offhandItem = kitsSection.getItemStack("${kitName}.40", defaultItem)
            val feetItem = kitsSection.getItemStack("${kitName}.39", defaultItem)
            val legsItem = kitsSection.getItemStack("${kitName}.38", defaultItem)
            val chestplateItem = kitsSection.getItemStack("${kitName}.37", defaultItem)
            val helmetItem = kitsSection.getItemStack("${kitName}.36", defaultItem)

            val armorContents = arrayOf(helmetItem, chestplateItem, legsItem, feetItem)
            for (index in armorContents.indices) {
                var armor = armorContents[index]
                if (armor.isAir()) armor = null
                armorContents[index] = armor
            }

            val inventoryContents = arrayOfNulls<ItemStack>(36)
            for (index in 0..35) {
                var item = kitsSection.getItemStack("${kitName}.${index}", defaultItem)
                if (item.isAir()) item = null
                inventoryContents[index] = item
            }

            val cooldown = kitsSection.getLong("${kitName}.Delay", -1)
            val kit = Kit(kitName, cooldown, offhandItem, armorContents, inventoryContents)
            kitManager.addKit(kit)
            migratedCount++
        } catch (exception: Exception) {
            log(Level.WARNING, "Failed to migrate kit '${kitName}': ${exception.message}", exception)
        }

        kitManager.saveAllKits()

        log(Level.INFO, "Successfully migrated ${migratedCount} kits from legacy data directory: ${_legacyDataDirectory.absolutePath}")
    }

    /**
     * Migrates warps from legacy data.
     */
    private fun migrateWarps() {
        val legacyWarpsFile = File(_legacyDataDirectory, "warps.yml")
        if (!legacyWarpsFile.exists()) return
        val legacyWarpsConfig = YamlConfiguration.loadConfiguration(legacyWarpsFile)

        log(Level.INFO, "Legacy Warp Data detected. Migrating...")

        if (!legacyWarpsConfig.isConfigurationSection("Warps")) {
            log(Level.WARNING, "No warps found in legacy warps file")
            return
        }

        val warpManager = instance.registry.getService<WarpManager>()
        val warpSection = legacyWarpsConfig.getConfigurationSection("Warps")
        val warpNames = warpSection?.getKeys(false) ?: HashSet()
        var migratedCount = 0

        for (warpName in warpNames) try {
            val prefix = "Warps.${warpName}"

            val x = legacyWarpsConfig.getDouble("${prefix}.X")
            val y = legacyWarpsConfig.getDouble("${prefix}.Y")
            val z = legacyWarpsConfig.getDouble("${prefix}.Z")
            val yaw = legacyWarpsConfig.getDouble("${prefix}.Yaw").toFloat()
            val pitch = legacyWarpsConfig.getDouble("${prefix}.Pitch").toFloat()
            val worldName = legacyWarpsConfig.getString("${prefix}.World") ?: ""

            val world = Bukkit.getWorld(worldName)
            if (world == null) {
                log(Level.WARNING, "World '${worldName}' not found for warp '${warpName}', skipping")
                continue
            }

            val location = Location(world, x, y, z, yaw, pitch)
            val warp = Warp.of(warpName, location)

            if (warp == null) {
                log(Level.WARNING, "Warp name '${warpName}' contains invalid characters, skipping")
                continue
            }

            warpManager.addWarp(warp)
            migratedCount++
        } catch (exception: Exception) {
            log(Level.WARNING, "Failed to migrate warp '${warpName}'", exception)
        }

        log(Level.INFO, "Successfully migrated ${migratedCount} warps")
    }

    /**
     * Migrates homes from legacy data.
     */
    private fun migrateHomes() {
        val legacyHomesDirectory = File(_legacyDataDirectory, "Homes")
        if (!legacyHomesDirectory.exists()) return
        if (!legacyHomesDirectory.isDirectory) return
        val legacyHomesFiles = legacyHomesDirectory.listFiles() ?: return

        log(Level.INFO, "Legacy Home Data detected. Migrating...")

        var count = 0

        for (homesFile in legacyHomesFiles) {
            if (!homesFile.isFile) continue
            try {
                val uuid = UUID.fromString(homesFile.name.replace(".yml", ""))
                val legacyHomesConfig = YamlConfiguration.loadConfiguration(homesFile)
                count += migrateHome(uuid, legacyHomesConfig)
            } catch (exception: IllegalArgumentException) {
                log(Level.WARNING, "Failed to migrate homes for ${homesFile.name}", exception)
            }
        }

        log(Level.INFO, "Successfully migrated ${count} homes from legacy data directory: ${_legacyDataDirectory.absolutePath}")
    }

    /**
     * Migrates homes for a specific user.
     * 
     * @param uuid              The UUID of the user
     * @param legacyHomesConfig The legacy homes configuration
     * @return The number of homes migrated
     */
    private fun migrateHome(uuid: UUID, legacyHomesConfig: FileConfiguration): Int {
        val user = getOfflineUser(uuid) ?: return 0

        if (!legacyHomesConfig.isConfigurationSection("Homes")) return 0
        val homeSection = legacyHomesConfig.getConfigurationSection("Homes")
        val homeNames = homeSection!!.getKeys(false)
        var migratedCount = 0

        for (homeName in homeNames) try {
            val location = legacyHomesConfig.getLocation("Homes.${homeName}") ?: continue
            val home = Home.of(homeName, location)
            if (home == null) {
                log(Level.WARNING, "Home name '${homeName}' (${uuid}) contains invalid characters, skipping")
                continue
            }

            user.homeManager.addHome(home)
            migratedCount++
        } catch (exception: Exception) {
            log(Level.WARNING, "Failed to migrate home '${homeName}' for ${uuid}: ${exception.message}", exception)
        }

        user.save()

        return migratedCount
    }

    /**
     * Migrates moderation and economy data.
     */
    private fun migrateModerationAndEconomy() {
        val legacyConfigFile = File(_legacyDataDirectory, "config.yml")
        val legacyConfig = YamlConfiguration.loadConfiguration(legacyConfigFile)

        if (legacyConfig.getBoolean("mysql.use", false)) {
            log(Level.INFO, "Legacy MySQL configuration detected. Migrating...")
            migrateMySqlConfig(legacyConfig)
        }

        // Sqlite is handled below
        if (legacyConfig.getBoolean("sqlite.use", false)) log(Level.INFO, "Legacy SQLite configuration detected. Migrating...")

        if (legacyConfig.getBoolean("h2.use", false)) {
            log(Level.INFO, "Legacy H2 configuration detected. Migrating...")
            migrateH2Config(legacyConfig)
        }

        val bansFile = File(_legacyDataDirectory, "bans.sqlite")
        if (bansFile.exists()) migrateBansFile(bansFile)

        val mutesFile = File(_legacyDataDirectory, "mutes.sqlite")
        if (mutesFile.exists()) migrateMutesFile(mutesFile)

        val economyFile = File(_legacyDataDirectory, "economy.sqlite")
        if (economyFile.exists()) migrateEconomyFile(economyFile)
    }

    /**
     * Migrates ban data from a SQLite file.
     * 
     * @param bansFile The SQLite file containing ban data
     */
    private fun migrateBansFile(bansFile: File) {
        log(Level.INFO, "Migrating bans from legacy database: ${bansFile.absolutePath}")
        val url = "jdbc:sqlite:${bansFile.absolutePath}"
        withConnection(url) { connection -> processBanRecords(connection, "Sqlite Ban") }
    }

    /**
     * Processes ban records from a database.
     * 
     * @param connection The database connection
     * @param recordType The type of records being processed (for logging)
     */
    private fun processBanRecords(connection: Connection, recordType: String) {
        processRecords(connection, "SELECT * FROM BannedPlayers", recordType) { resultSet, _ ->
            val bannedUuidStr = resultSet.getString("BannedUUID")
            val senderUuidStr = resultSet.getString("SenderUUID")
            val reason = resultSet.getString("Reason")
            val unbanTime = resultSet.getLong("UnbanTime")

            val bannedUuid = UUID.fromString(bannedUuidStr)
            val senderUuid = parseSenderUuid(senderUuidStr)

            // Use current time as issue time since we don't have that in the legacy database
            val issueTime = System.currentTimeMillis()
            val banModeration = BanModeration.builder()
                .issueTime(issueTime).expireTime(unbanTime)
                .reason(reason).senderUuid(senderUuid)
                .targetUuid(bannedUuid).build()
            applyModeration(bannedUuid, banModeration)
        }
    }

    /**
     * Parses a sender UUID string, falling back to the console UUID if invalid.
     * 
     * @param senderUuidStr The sender UUID string to parse
     * @return The parsed UUID, or the console UUID if the input is invalid
     */
    private fun parseSenderUuid(senderUuidStr: String): UUID {
        return try {
            UUID.fromString(senderUuidStr)
        } catch (_: IllegalArgumentException) {
            // Fallback to ConsoleUser
            ConsoleUser.CONSOLE_UUID
        }
    }

    /**
     * Migrates mute data from a SQLite file.
     * 
     * @param mutesFile The SQLite file containing mute data
     */
    private fun migrateMutesFile(mutesFile: File) {
        log(Level.INFO, "Migrating mutes from legacy database: ${mutesFile.absolutePath}")
        val url = "jdbc:sqlite:${mutesFile.absolutePath}"
        withConnection(url) { connection -> processMuteRecords(connection, "mute") }
    }

    /**
     * Processes mute records from a database.
     * 
     * @param connection The database connection
     * @param recordType The type of records being processed (for logging)
     */
    private fun processMuteRecords(connection: Connection, recordType: String) {
        processRecords(connection, "SELECT * FROM MutedPlayers", recordType) { resultSet, _ ->
            val bannedUuidStr = resultSet.getString("BannedUUID")
            val senderUuidStr = resultSet.getString("SenderUUID")
            val reason = resultSet.getString("Reason")
            val unbanTime = resultSet.getLong("UnbanTime")
            val isShadowMute = resultSet.getInt("Shadow") >= 1

            val bannedUuid = UUID.fromString(bannedUuidStr)
            val senderUuid = parseSenderUuid(senderUuidStr)

            // Use current time as issue time since we don't have that in the legacy database
            val issueTime = System.currentTimeMillis()
            val muteModeration = MuteModeration.builder()
                .issueTime(issueTime).expireTime(unbanTime)
                .reason(reason).senderUuid(senderUuid)
                .targetUuid(bannedUuid).isShadowMute(isShadowMute).build()
            applyModeration(bannedUuid, muteModeration)
        }
    }

    /**
     * Migrates economy data from a SQLite file.
     * 
     * @param economyFile The SQLite file containing economy data
     */
    private fun migrateEconomyFile(economyFile: File) {
        log(Level.INFO, "Migrating economy from legacy database: ${economyFile.absolutePath}")
        val url = "jdbc:sqlite:${economyFile.absolutePath}"
        withConnection(url) { connection -> processEconomyRecords(connection, "SELECT * FROM Economy", "economy") }
    }

    /**
     * Processes economy records from a database.
     * 
     * @param connection The database connection
     * @param query      The SQL query to execute
     * @param recordType The type of records being processed (for logging)
     */
    private fun processEconomyRecords(connection: Connection, query: String, recordType: String) {
        processRecords(connection, query, recordType) { resultSet, _ ->
            val ownerUuid = UUID.fromString(resultSet.getString("UUID"))
            val balance = resultSet.getBigDecimal("Balance")
            val user = getOfflineUser(ownerUuid) ?: return@processRecords

            val bankAccount = user.bankAccount
            bankAccount.balance = balance
        }
    }

    /**
     * Migrates data from MySQL configuration.
     * 
     * @param legacyConfig The legacy configuration
     */
    private fun migrateMySqlConfig(legacyConfig: FileConfiguration) {
        val hostname = legacyConfig.getString("mysql.hostname", "127.0.0.1")!!
        val port = legacyConfig.getString("mysql.port", "3306")!!
        val username = legacyConfig.getString("mysql.username", "root")!!
        val password = legacyConfig.getString("mysql.password", "")!!
        val database = legacyConfig.getString("mysql.database", "ServerSystem")!!
        val economyEnabled = legacyConfig.getBoolean("mysql.economy.enabled")
        val economyServerName = legacyConfig.getString("mysql.economy.serverName", "global")!!
        val banSystemEnabled = legacyConfig.getBoolean("mysql.banSystem")

        val jdbcUrl = "jdbc:mysql://${hostname}:${port}/${database}"

        try {
            DriverManager.getConnection(jdbcUrl, username, password).use { connection ->
                log(Level.INFO, "Successfully connected to MySQL database. Migrating data...")
                if (economyEnabled) migrateEconomyFromMySql(connection, economyServerName)
                if (banSystemEnabled) migrateBansFromMySql(connection)
            }
        } catch (exception: SQLException) {
            log(Level.WARNING, "Failed to connect to MySQL database: ${exception.message}", exception)
        }
    }

    /**
     * Migrates data from H2 configuration.
     * 
     * @param legacyConfig The legacy configuration
     */
    private fun migrateH2Config(legacyConfig: FileConfiguration) {
        val economyEnabled = legacyConfig.getBoolean("h2.economy")
        val banSystemEnabled = legacyConfig.getBoolean("h2.banSystem")

        val h2EconomyFile = File(_legacyDataDirectory, "economy.h2.db")
        val h2BansFile = File(_legacyDataDirectory, "bans.h2.db")
        val h2MutesFile = File(_legacyDataDirectory, "mutes.h2.db")

        if (economyEnabled && h2EconomyFile.exists()) {
            log(Level.INFO, "H2 economy database found. Converting to SQLite...")
            migrateH2(h2EconomyFile, "economy")
        }

        if (banSystemEnabled && h2BansFile.exists()) {
            log(Level.INFO, "H2 bans database found. Converting to SQLite...")
            migrateH2(h2BansFile, "bans")
        }

        if (banSystemEnabled && h2MutesFile.exists()) {
            log(Level.INFO, "H2 mutes database found. Converting to SQLite...")
            migrateH2(h2MutesFile, "mutes")
        }
    }

    /**
     * Migrates economy data from MySQL.
     * 
     * @param connection The database connection
     * @param serverName The server name to filter by
     */
    private fun migrateEconomyFromMySql(connection: Connection, serverName: String) {
        processEconomyRecords(connection, "SELECT * FROM Economy WHERE ServerName = '${serverName}'", "MySQL economy")
    }

    /**
     * Migrates ban data from MySQL.
     * 
     * @param connection The database connection
     */
    private fun migrateBansFromMySql(connection: Connection) = processBanRecords(connection, "MySQL ban")

    /**
     * Migrates data from an H2 database file.
     * 
     * @param h2File The H2 database file
     * @param type   The type of data to migrate
     */
    private fun migrateH2(h2File: File, type: String) {
        log(Level.INFO, "Migrating ${type} from H2 database: ${h2File.absolutePath}")

        var h2Path = h2File.absolutePath
        if (h2Path.endsWith(".h2.db")) h2Path = h2Path.substring(0, h2Path.length - 6) // Remove .h2.db extension

        val url = "jdbc:h2:file:${h2Path}"

        try {
            // Load H2 driver
            Class.forName("org.h2.Driver")

            withConnection(url) { connection ->
                log(Level.INFO, "Successfully connected to H2 database.")
                when {
                    type.equals("economy", ignoreCase = true) -> processEconomyRecords(connection, "SELECT * FROM Economy", "H2 economy")
                    type.equals("bans", ignoreCase = true) -> processBanRecords(connection, "H2 ban")
                    type.equals("mutes", ignoreCase = true) -> processMuteRecords(connection, "H2 mute")
                }
            }
        } catch (exception: ClassNotFoundException) {
            log(Level.SEVERE, "H2 driver not found. Make sure the H2 library is properly included: ${exception.message}", exception)
        }
    }

    /**
     * Functional interface for processing database records.
     */
    private fun interface SqlRecordProcessor {
        fun process(resultSet: ResultSet, count: Int)
    }
}
