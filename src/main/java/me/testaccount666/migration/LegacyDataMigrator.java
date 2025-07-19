package me.testaccount666.migration;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.executables.kit.manager.Kit;
import me.testaccount666.serversystem.commands.executables.warp.manager.Warp;
import me.testaccount666.serversystem.moderation.AbstractModeration;
import me.testaccount666.serversystem.moderation.BanModeration;
import me.testaccount666.serversystem.moderation.MuteModeration;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.OfflineUser;
import me.testaccount666.serversystem.userdata.home.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;

public class LegacyDataMigrator {
    private File _legacyDataDirectory;

    /**
     * Retrieves an offline user by UUID.
     *
     * @param uuid The UUID of the user to retrieve
     * @return Optional containing the offline user if found, empty otherwise
     */
    private Optional<OfflineUser> getOfflineUser(UUID uuid) {
        var userManager = ServerSystem.Instance.getUserManager();
        var userOptional = userManager.getUser(uuid);

        if (userOptional.isEmpty()) {
            ServerSystem.getLog().warning("Could not find user with UUID: ${uuid}");
            return Optional.empty();
        }

        return Optional.of(userOptional.get().getOfflineUser());
    }

    /**
     * Logs a message with the specified level.
     *
     * @param level   The log level
     * @param message The message to log
     */
    private void log(Level level, String message) {
        ServerSystem.getLog().log(level, message);
    }

    /**
     * Logs a message with the specified level and exception.
     *
     * @param level     The log level
     * @param message   The message to log
     * @param exception The exception to log
     */
    private void log(Level level, String message, Exception exception) {
        ServerSystem.getLog().log(level, message, exception);
    }

    /**
     * Processes database records using a generic approach.
     *
     * @param connection      The database connection
     * @param query           The SQL query to execute
     * @param recordType      The type of records being processed (for logging)
     * @param recordProcessor The function to process each record
     */
    private void processRecords(Connection connection, String query, String recordType, SqlRecordProcessor recordProcessor) {
        try (var statement = connection.createStatement();
             var resultSet = statement.executeQuery(query)) {

            var migratedCount = 0;

            while (resultSet.next()) try {
                recordProcessor.process(resultSet, migratedCount);
                migratedCount++;
            } catch (Exception exception) {
                log(Level.WARNING, "Failed to migrate a ${recordType} record: ${exception.getMessage()}", exception);
            }

            log(Level.INFO, "Successfully migrated ${migratedCount} ${recordType} records");
        } catch (SQLException exception) {
            log(Level.SEVERE, "Error querying ${recordType} database: ${exception.getMessage()}", exception);
        }
    }

    /**
     * Opens a database connection with the given URL.
     *
     * @param url       The JDBC URL
     * @param processor The function to process the connection
     */
    private void withConnection(String url, Consumer<Connection> processor) {
        try (var connection = DriverManager.getConnection(url)) {
            processor.accept(connection);
        } catch (SQLException exception) {
            log(Level.SEVERE, "Error connecting to database: ${exception.getMessage()}", exception);
        }
    }

    /**
     * Applies moderation to a user.
     *
     * @param targetUuid The UUID of the target user
     * @param moderation The moderation to apply
     */
    private void applyModeration(UUID targetUuid, AbstractModeration moderation) {
        getOfflineUser(targetUuid).ifPresent(user -> {
            if (moderation instanceof BanModeration) {
                var banManager = user.getBanManager();
                if (banManager == null) {
                    log(Level.WARNING, "Bam manager is null for user: ${targetUuid}");
                    return;
                }
                banManager.addModeration(moderation);
            }

            if (moderation instanceof MuteModeration) {
                var muteManager = user.getMuteManager();
                if (muteManager == null) {
                    log(Level.WARNING, "Mute manager is null for user: ${targetUuid}");
                    return;
                }
                muteManager.addModeration(moderation);
            }
        });
    }

    /**
     * Checks if legacy data is present that needs to be migrated.
     *
     * @return true if legacy data is present, false otherwise
     */
    public boolean isLegacyDataPresent() {
        var directory = ServerSystem.Instance.getDataFolder();
        if (!directory.exists()) return false;

        var previousVersionFile = new File(directory, "previousVersion.yml");
        if (!previousVersionFile.exists()) return true;

        var previousVersionConfig = YamlConfiguration.loadConfiguration(previousVersionFile);
        var previousVersion = previousVersionConfig.getInt("previousVersion", 0);
        return previousVersion < 300;
    }

    /**
     * Prepares for migration by renaming the current data directory to a backup directory.
     */
    public void prepareMigration() {
        var directory = ServerSystem.Instance.getDataFolder();
        if (!directory.exists()) return;

        _legacyDataDirectory = new File(directory.getParent(), "ServerSystem-LegacyData-${System.currentTimeMillis()}");
        directory.renameTo(_legacyDataDirectory);
    }

    /**
     * Migrates all legacy data.
     */
    public void migrateLegacyData() {
        migrateModerationAndEconomy();
        migrateHomes();
        migrateWarps();
        migrateKits();
    }

    /**
     * Migrates kits from legacy data.
     */
    private void migrateKits() {
        var legacyKitsFile = new File(_legacyDataDirectory, "kits.yml");
        if (!legacyKitsFile.exists()) return;
        var legacyKitsConfig = YamlConfiguration.loadConfiguration(legacyKitsFile);
        log(Level.INFO, "Legacy Kits Data detected. Migrating...");

        var kitsSection = legacyKitsConfig.getConfigurationSection("Kits");
        if (kitsSection == null) {
            log(Level.WARNING, "No kits found in legacy kits file");
            return;
        }
        var kitNames = kitsSection.getKeys(false);
        var migratedCount = 0;

        var defaultItem = new ItemStack(Material.AIR);
        var kitManager = ServerSystem.Instance.getKitManager();
        if (kitManager == null) {
            log(Level.WARNING, "Kit manager is null, skipping kits migration");
            return;
        }
        for (var kitName : kitNames)
            try {
                var offhandItem = kitsSection.getItemStack("${kitName}.40", defaultItem);
                var feetItem = kitsSection.getItemStack("${kitName}.39", defaultItem);
                var legsItem = kitsSection.getItemStack("${kitName}.38", defaultItem);
                var chestplateItem = kitsSection.getItemStack("${kitName}.37", defaultItem);
                var helmetItem = kitsSection.getItemStack("${kitName}.36", defaultItem);

                var armorContents = new ItemStack[]{helmetItem, chestplateItem, legsItem, feetItem};
                for (var index = 0; index < armorContents.length; index++) {
                    var armor = armorContents[index];
                    if (armor.getType() == Material.AIR) armor = null;
                    armorContents[index] = armor;
                }

                var inventoryContents = new ItemStack[36];
                for (var index = 0; index < 36; index++) {
                    var item = kitsSection.getItemStack("${kitName}.${index}", defaultItem);
                    if (item.getType() == Material.AIR) item = null;
                    inventoryContents[index] = item;
                }

                var cooldown = kitsSection.getLong("${kitName}.Delay", -1);
                var kit = new Kit(kitName, cooldown, offhandItem, armorContents, inventoryContents);
                kitManager.addKit(kit);
                migratedCount++;
            } catch (Exception exception) {
                log(Level.WARNING, "Failed to migrate kit '${kitName}': ${exception.getMessage()}", exception);
            }

        kitManager.saveAllKits();

        log(Level.INFO, "Successfully migrated ${migratedCount} kits from legacy data directory: ${_legacyDataDirectory.getAbsolutePath()}");
    }

    /**
     * Migrates warps from legacy data.
     */
    private void migrateWarps() {
        var legacyWarpsFile = new File(_legacyDataDirectory, "warps.yml");
        if (!legacyWarpsFile.exists()) return;
        var legacyWarpsConfig = YamlConfiguration.loadConfiguration(legacyWarpsFile);

        log(Level.INFO, "Legacy Warp Data detected. Migrating...");

        if (!legacyWarpsConfig.isConfigurationSection("Warps")) {
            log(Level.WARNING, "No warps found in legacy warps file");
            return;
        }

        var warpManager = ServerSystem.Instance.getWarpManager();
        var warpSection = legacyWarpsConfig.getConfigurationSection("Warps");
        var warpNames = warpSection.getKeys(false);
        var migratedCount = 0;

        for (var warpName : warpNames)
            try {
                var prefix = "Warps.${warpName}";

                var x = legacyWarpsConfig.getDouble("${prefix}.X");
                var y = legacyWarpsConfig.getDouble("${prefix}.Y");
                var z = legacyWarpsConfig.getDouble("${prefix}.Z");
                var yaw = (float) legacyWarpsConfig.getDouble("${prefix}.Yaw");
                var pitch = (float) legacyWarpsConfig.getDouble("${prefix}.Pitch");
                var worldName = legacyWarpsConfig.getString("${prefix}.World");

                var world = Bukkit.getWorld(worldName);
                if (world == null) {
                    log(Level.WARNING, "World '${worldName}' not found for warp '${warpName}', skipping");
                    continue;
                }

                var location = new Location(world, x, y, z, yaw, pitch);
                var warp = Warp.of(warpName, location);

                if (warp.isEmpty()) {
                    log(Level.WARNING, "Warp name '${warpName}' contains invalid characters, skipping");
                    continue;
                }

                warpManager.addWarp(warp.get());
                migratedCount++;

            } catch (Exception exception) {
                log(Level.WARNING, "Failed to migrate warp '${warpName}'", exception);
            }

        log(Level.INFO, "Successfully migrated ${migratedCount} warps");
    }

    /**
     * Migrates homes from legacy data.
     */
    private void migrateHomes() {
        var legacyHomesDirectory = new File(_legacyDataDirectory, "Homes");
        if (!legacyHomesDirectory.exists()) return;
        if (!legacyHomesDirectory.isDirectory()) return;
        var legacyHomesFiles = legacyHomesDirectory.listFiles();
        if (legacyHomesFiles == null) return;

        log(Level.INFO, "Legacy Home Data detected. Migrating...");

        var count = 0;

        for (var homesFile : legacyHomesFiles) {
            if (!homesFile.isFile()) continue;
            var uuid = UUID.fromString(homesFile.getName().replace(".yml", ""));
            var legacyHomesConfig = YamlConfiguration.loadConfiguration(homesFile);
            count += migrateHome(uuid, legacyHomesConfig);
        }

        log(Level.INFO, "Successfully migrated ${count} homes from legacy data directory: ${_legacyDataDirectory.getAbsolutePath()}");
    }

    /**
     * Migrates homes for a specific user.
     *
     * @param uuid              The UUID of the user
     * @param legacyHomesConfig The legacy homes configuration
     * @return The number of homes migrated
     */
    private int migrateHome(UUID uuid, FileConfiguration legacyHomesConfig) {
        var userOpt = getOfflineUser(uuid);
        if (userOpt.isEmpty()) return 0;

        var user = userOpt.get();

        if (!legacyHomesConfig.isConfigurationSection("Homes")) return 0;
        var homeSection = legacyHomesConfig.getConfigurationSection("Homes");
        var homeNames = homeSection.getKeys(false);
        var migratedCount = 0;

        for (var homeName : homeNames)
            try {
                var location = legacyHomesConfig.getLocation("Homes.${homeName}");
                var home = Home.of(homeName, location);
                if (home.isEmpty()) {
                    log(Level.WARNING, "Home name '${homeName}' (${uuid}) contains invalid characters, skipping");
                    continue;
                }

                user.getHomeManager().addHome(home.get());
                migratedCount++;
            } catch (Exception exception) {
                log(Level.WARNING, "Failed to migrate home '${homeName}' for ${uuid}: ${exception.getMessage()}", exception);
            }

        user.save();

        return migratedCount;
    }

    /**
     * Migrates moderation and economy data.
     */
    private void migrateModerationAndEconomy() {
        var legacyConfigFile = new File(_legacyDataDirectory, "config.yml");
        var legacyConfig = YamlConfiguration.loadConfiguration(legacyConfigFile);

        if (legacyConfig.getBoolean("mysql.use", false)) {
            log(Level.INFO, "Legacy MySQL configuration detected. Migrating...");
            migrateMySqlConfig(legacyConfig);
        }

        // Sqlite is handled below
        if (legacyConfig.getBoolean("sqlite.use", false)) log(Level.INFO, "Legacy SQLite configuration detected. Migrating...");

        if (legacyConfig.getBoolean("h2.use", false)) {
            log(Level.INFO, "Legacy H2 configuration detected. Migrating...");
            migrateH2Config(legacyConfig);
        }

        var bansFile = new File(_legacyDataDirectory, "bans.sqlite");
        if (bansFile.exists()) migrateBansFile(bansFile);

        var mutesFile = new File(_legacyDataDirectory, "mutes.sqlite");
        if (mutesFile.exists()) migrateMutesFile(mutesFile);

        var economyFile = new File(_legacyDataDirectory, "economy.sqlite");
        if (economyFile.exists()) migrateEconomyFile(economyFile);
    }

    /**
     * Migrates ban data from a SQLite file.
     *
     * @param bansFile The SQLite file containing ban data
     */
    private void migrateBansFile(File bansFile) {
        log(Level.INFO, "Migrating bans from legacy database: ${bansFile.getAbsolutePath()}");
        var url = "jdbc:sqlite:${bansFile.getAbsolutePath()}";
        withConnection(url, connection -> processBanRecords(connection, "Sqlite Ban")
        );
    }

    /**
     * Processes ban records from a database.
     *
     * @param connection The database connection
     * @param recordType The type of records being processed (for logging)
     */
    private void processBanRecords(Connection connection, String recordType) {
        processRecords(connection, "SELECT * FROM BannedPlayers", recordType, (resultSet, count) -> {
            var bannedUuidStr = resultSet.getString("BannedUUID");
            var senderUuidStr = resultSet.getString("SenderUUID");
            var reason = resultSet.getString("Reason");
            var unbanTime = resultSet.getLong("UnbanTime");

            var bannedUuid = UUID.fromString(bannedUuidStr);
            var senderUuid = parseSenderUuid(senderUuidStr);

            // Use current time as issue time since we don't have that in the legacy database
            var issueTime = System.currentTimeMillis();
            var banModeration = BanModeration.builder()
                    .issueTime(issueTime).expireTime(unbanTime)
                    .reason(reason).senderUuid(senderUuid)
                    .targetUuid(bannedUuid).build();

            applyModeration(bannedUuid, banModeration);
        });
    }

    /**
     * Parses a sender UUID string, falling back to the console UUID if invalid.
     *
     * @param senderUuidStr The sender UUID string to parse
     * @return The parsed UUID, or the console UUID if the input is invalid
     */
    private UUID parseSenderUuid(String senderUuidStr) {
        try {
            return UUID.fromString(senderUuidStr);
        } catch (IllegalArgumentException ignored) {
            // Fallback to ConsoleUser
            return ConsoleUser.CONSOLE_UUID;
        }
    }

    /**
     * Migrates mute data from a SQLite file.
     *
     * @param mutesFile The SQLite file containing mute data
     */
    private void migrateMutesFile(File mutesFile) {
        log(Level.INFO, "Migrating mutes from legacy database: ${mutesFile.getAbsolutePath()}");
        var url = "jdbc:sqlite:${mutesFile.getAbsolutePath()}";
        withConnection(url, connection -> processMuteRecords(connection, "mute")
        );
    }

    /**
     * Processes mute records from a database.
     *
     * @param connection The database connection
     * @param recordType The type of records being processed (for logging)
     */
    private void processMuteRecords(Connection connection, String recordType) {
        processRecords(connection, "SELECT * FROM MutedPlayers", recordType, (resultSet, count) -> {
            var bannedUuidStr = resultSet.getString("BannedUUID");
            var senderUuidStr = resultSet.getString("SenderUUID");
            var reason = resultSet.getString("Reason");
            var unbanTime = resultSet.getLong("UnbanTime");
            var isShadowMute = resultSet.getInt("Shadow") >= 1;

            var bannedUuid = UUID.fromString(bannedUuidStr);
            var senderUuid = parseSenderUuid(senderUuidStr);

            // Use current time as issue time since we don't have that in the legacy database
            var issueTime = System.currentTimeMillis();
            var muteModeration = MuteModeration.builder()
                    .issueTime(issueTime).expireTime(unbanTime)
                    .reason(reason).senderUuid(senderUuid)
                    .targetUuid(bannedUuid).isShadowMute(isShadowMute).build();

            applyModeration(bannedUuid, muteModeration);
        });
    }

    /**
     * Migrates economy data from a SQLite file.
     *
     * @param economyFile The SQLite file containing economy data
     */
    private void migrateEconomyFile(File economyFile) {
        log(Level.INFO, "Migrating economy from legacy database: ${economyFile.getAbsolutePath()}");
        var url = "jdbc:sqlite:${economyFile.getAbsolutePath()}";
        withConnection(url, connection -> processEconomyRecords(connection, "SELECT * FROM Economy", "economy")
        );
    }

    /**
     * Processes economy records from a database.
     *
     * @param connection The database connection
     * @param query      The SQL query to execute
     * @param recordType The type of records being processed (for logging)
     */
    private void processEconomyRecords(Connection connection, String query, String recordType) {
        processRecords(connection, query, recordType, (resultSet, count) -> {
            var ownerUuid = UUID.fromString(resultSet.getString("UUID"));
            var balance = resultSet.getBigDecimal("Balance");

            getOfflineUser(ownerUuid).ifPresent(user -> {
                var bankAccount = user.getBankAccount();
                bankAccount.setBalance(balance);
            });
        });
    }

    /**
     * Migrates data from MySQL configuration.
     *
     * @param legacyConfig The legacy configuration
     */
    private void migrateMySqlConfig(FileConfiguration legacyConfig) {
        var hostname = legacyConfig.getString("mysql.hostname", "127.0.0.1");
        var port = legacyConfig.getString("mysql.port", "3306");
        var username = legacyConfig.getString("mysql.username", "root");
        var password = legacyConfig.getString("mysql.password", "");
        var database = legacyConfig.getString("mysql.database", "ServerSystem");
        var economyEnabled = legacyConfig.getBoolean("mysql.economy.enabled", false);
        var economyServerName = legacyConfig.getString("mysql.economy.serverName", "global");
        var banSystemEnabled = legacyConfig.getBoolean("mysql.banSystem", false);

        var jdbcUrl = "jdbc:mysql://${hostname}:${port}/${database}";

        try {
            try (var connection = DriverManager.getConnection(jdbcUrl, username, password)) {
                log(Level.INFO, "Successfully connected to MySQL database. Migrating data...");

                if (economyEnabled) migrateEconomyFromMySql(connection, economyServerName);
                if (banSystemEnabled) migrateBansFromMySql(connection);
            }
        } catch (SQLException exception) {
            log(Level.WARNING, "Failed to connect to MySQL database: ${exception.getMessage()}", exception);
        }
    }

    /**
     * Migrates data from H2 configuration.
     *
     * @param legacyConfig The legacy configuration
     */
    private void migrateH2Config(FileConfiguration legacyConfig) {
        var economyEnabled = legacyConfig.getBoolean("h2.economy", false);
        var banSystemEnabled = legacyConfig.getBoolean("h2.banSystem", false);

        var h2EconomyFile = new File(_legacyDataDirectory, "economy.h2.db");
        var h2BansFile = new File(_legacyDataDirectory, "bans.h2.db");
        var h2MutesFile = new File(_legacyDataDirectory, "mutes.h2.db");

        if (economyEnabled && h2EconomyFile.exists()) {
            log(Level.INFO, "H2 economy database found. Converting to SQLite...");
            migrateH2(h2EconomyFile, "economy");
        }

        if (banSystemEnabled && h2BansFile.exists()) {
            log(Level.INFO, "H2 bans database found. Converting to SQLite...");
            migrateH2(h2BansFile, "bans");
        }

        if (banSystemEnabled && h2MutesFile.exists()) {
            log(Level.INFO, "H2 mutes database found. Converting to SQLite...");
            migrateH2(h2MutesFile, "mutes");
        }
    }

    /**
     * Migrates economy data from MySQL.
     *
     * @param connection The database connection
     * @param serverName The server name to filter by
     */
    private void migrateEconomyFromMySql(Connection connection, String serverName) {
        processEconomyRecords(connection, "SELECT * FROM Economy WHERE ServerName = '${serverName}'", "MySQL economy");
    }

    /**
     * Migrates ban data from MySQL.
     *
     * @param connection The database connection
     */
    private void migrateBansFromMySql(Connection connection) {
        processBanRecords(connection, "MySQL ban");
    }

    /**
     * Migrates data from an H2 database file.
     *
     * @param h2File The H2 database file
     * @param type   The type of data to migrate
     */
    private void migrateH2(File h2File, String type) {
        log(Level.INFO, "Migrating ${type} from H2 database: ${h2File.getAbsolutePath()}");

        var h2Path = h2File.getAbsolutePath();
        if (h2Path.endsWith(".h2.db")) h2Path = h2Path.substring(0, h2Path.length() - 6); // Remove .h2.db extension
        var url = "jdbc:h2:file:${h2Path}";

        try {
            // Load H2 driver
            Class.forName("org.h2.Driver");

            withConnection(url, connection -> {
                log(Level.INFO, "Successfully connected to H2 database.");

                if (type.equalsIgnoreCase("economy")) processEconomyRecords(connection, "SELECT * FROM Economy", "H2 economy");
                else if (type.equalsIgnoreCase("bans")) processBanRecords(connection, "H2 ban");
                else if (type.equalsIgnoreCase("mutes")) processMuteRecords(connection, "H2 mute");
            });
        } catch (ClassNotFoundException exception) {
            log(Level.SEVERE, "H2 driver not found. Make sure the H2 library is properly included: ${exception.getMessage()}", exception);
        }
    }

    /**
     * Functional interface for processing database records.
     */
    @FunctionalInterface
    private interface SqlRecordProcessor {
        void process(ResultSet resultSet, int count) throws Exception;
    }
}
