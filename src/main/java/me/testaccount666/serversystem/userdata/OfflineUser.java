package me.testaccount666.serversystem.userdata;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.executables.back.CommandBack;
import me.testaccount666.serversystem.managers.database.moderation.MySqlModerationDatabaseManager;
import me.testaccount666.serversystem.managers.database.moderation.SqliteModerationDatabaseManager;
import me.testaccount666.serversystem.moderation.AbstractModerationManager;
import me.testaccount666.serversystem.moderation.ban.MySqlBanManager;
import me.testaccount666.serversystem.moderation.ban.SqliteBanManager;
import me.testaccount666.serversystem.moderation.mute.MySqlMuteManager;
import me.testaccount666.serversystem.moderation.mute.SqliteMuteManager;
import me.testaccount666.serversystem.userdata.home.HomeManager;
import me.testaccount666.serversystem.userdata.money.AbstractBankAccount;
import me.testaccount666.serversystem.userdata.persistence.*;
import me.testaccount666.serversystem.userdata.vanish.VanishData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Represents an offline user.
 * This class provides functionality for managing user data that persists
 * even when the player is offline.
 * <p>
 * User data can be optionally compressed using GZIP compression to reduce disk space usage.
 * Compression can be enabled or disabled in the userdata.yml configuration file.
 */
public class OfflineUser {
    protected final File userFile;
    @SaveableField(path = "User.IgnoredPlayers", handler = UuidSetFieldHandler.class)
    protected final Set<UUID> ignoredPlayers = new HashSet<>();
    @SaveableField(path = "User.LastKnownName")
    protected String name;
    protected UUID uuid;
    protected OfflinePlayer player;
    protected AbstractModerationManager banManager;
    protected AbstractModerationManager muteManager;
    protected AbstractBankAccount bankAccount;
    protected HomeManager homeManager;
    protected FileConfiguration userConfig;
    @SaveableField(path = "User.LastSeen")
    protected long lastSeen;
    @SaveableField(path = "User.LastKnownIp")
    protected String lastKnownIp;
    @SaveableField(path = "User.LogoutPosition", handler = LocationFieldHandler.class)
    protected Location logoutPosition;
    @SaveableField(path = "User.VanishData.IsVanish")
    protected boolean isVanish;
    @SaveableField(path = "User.VanishData.Data", handler = VanishDataFieldHandler.class)
    protected VanishData vanishData;
    @SaveableField(path = "User.IsGodMode")
    protected boolean isGodMode;
    @SaveableField(path = "User.AcceptsTeleports")
    protected boolean acceptsTeleports;
    @SaveableField(path = "User.AcceptsMessages")
    protected boolean acceptsMessages;
    @SaveableField(path = "User.SocialSpyEnabled")
    protected boolean socialSpyEnabled;
    @SaveableField(path = "User.CommandSpyEnabled")
    protected boolean commandSpyEnabled;
    @SaveableField(path = "User.LastDeathLocation", handler = LocationFieldHandler.class)
    protected Location lastDeathLocation;
    @SaveableField(path = "User.LastTeleportLocation", handler = LocationFieldHandler.class)
    protected Location lastTeleportLocation;
    @SaveableField(path = "User.LastBackType", handler = EnumFieldHandler.class)
    protected CommandBack.BackType lastBackType;
    @SaveableField(path = "User.PlayerLanguage")
    protected String playerLanguage;

    protected OfflineUser(File userFile) {
        this.userFile = userFile;

        loadBasicData();
    }

    //Warning can be safely ignored
    @SuppressWarnings("CopyConstructorMissesField")
    protected OfflineUser(OfflineUser onlineUser) {
        this(onlineUser.userFile);
    }

    /**
     * Checks if a file is compressed with GZIP.
     *
     * @param file The file to check
     * @return true if the file is compressed with GZIP, false otherwise
     */
    private boolean isCompressed(File file) {
        if (!file.exists() || file.length() < 2) return false;

        try (var fis = new FileInputStream(file)) {
            var signature = new byte[2];
            if (fis.read(signature) != 2) return false;
            // Check for GZIP magic number (0x1F8B)
            return (signature[0] == (byte) 0x1F && signature[1] == (byte) 0x8B);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Loads a YAML configuration from a file, decompressing it if necessary.
     *
     * @param file The file to load
     * @return The loaded YAML configuration
     */
    private FileConfiguration loadYamlConfiguration(File file) {
        // File is not compressed, load it normally
        if (!isCompressed(file)) return YamlConfiguration.loadConfiguration(file);

        // File is compressed, decompress it first
        try (var fileInputStream = new FileInputStream(file);
             var gzipInputStream = new GZIPInputStream(fileInputStream);
             var inputReader = new InputStreamReader(gzipInputStream)) {

            return YamlConfiguration.loadConfiguration(inputReader);
        } catch (IOException e) {
            // If decompression fails, try loading normally as fallback
            return YamlConfiguration.loadConfiguration(file);
        }
    }

    protected void loadBasicData() {
        userConfig = loadYamlConfiguration(userFile);

        uuid = UUID.fromString(userFile.getName().replace(".yml.gz", ""));

        // Set default values before loading from config
        name = null;
        lastSeen = System.currentTimeMillis();
        lastKnownIp = "Unknown";
        isVanish = false;
        vanishData = new VanishData(false, false, false, false);
        isGodMode = false;
        acceptsTeleports = true;
        acceptsMessages = true;
        socialSpyEnabled = false;
        commandSpyEnabled = false;
        playerLanguage = System.getProperty("user.language");
        lastBackType = CommandBack.BackType.NONE;

        // PersistenceManager loads all annotated fields
        PersistenceManager.loadFields(this, userConfig);

        if (name == null) name = getPlayer().getName();

        homeManager = new HomeManager(this, userConfig);
        bankAccount = ServerSystem.Instance.getEconomyProvider().instantiateBankAccount(this, BigInteger.valueOf(0), userConfig);

        var moderationManager = ServerSystem.Instance.getModerationDatabaseManager();
        if (moderationManager instanceof SqliteModerationDatabaseManager) try {
            banManager = new SqliteBanManager(uuid, moderationManager.getConnection());
        } catch (SQLException exception) {
            throw new RuntimeException("Error loading userdata! (BanManager)", exception);
        }
        else if (moderationManager instanceof MySqlModerationDatabaseManager) try {
            banManager = new MySqlBanManager(uuid, moderationManager.getConnection());
        } catch (SQLException exception) {
            throw new RuntimeException("Error loading userdata! (BanManager)", exception);
        }

        if (moderationManager instanceof SqliteModerationDatabaseManager) try {
            muteManager = new SqliteMuteManager(uuid, moderationManager.getConnection());
        } catch (SQLException exception) {
            throw new RuntimeException("Error loading userdata! (MuteManager)", exception);
        }
        else if (moderationManager instanceof MySqlModerationDatabaseManager) try {
            muteManager = new MySqlMuteManager(uuid, moderationManager.getConnection());
        } catch (SQLException exception) {
            throw new RuntimeException("Error loading userdata! (MuteManager)", exception);
        }
    }

    public AbstractBankAccount getBankAccount() {
        return bankAccount;
    }

    /**
     * Checks if compression is enabled in the configuration.
     *
     * @return true if compression is enabled, false otherwise
     */
    private boolean isCompressionEnabled() {
        return ServerSystem.Instance.getConfigManager().getUserDataConfig().getBoolean("Compression.Enabled", true);
    }

    /**
     * Saves a YAML configuration to a file with compression.
     *
     * @param config The YAML configuration to save
     * @param file   The file to save to
     * @throws IOException If an I/O error occurs
     */
    private void saveCompressedYamlConfiguration(FileConfiguration config, File file) throws IOException {
        try (var fileOutputStream = new FileOutputStream(file);
             var gzipOutputStream = new GZIPOutputStream(fileOutputStream);
             var streamWriter = new OutputStreamWriter(gzipOutputStream)) {
            streamWriter.write(config.saveToString());
            streamWriter.flush();
        }
    }

    /**
     * Saves the user's data to their configuration file.
     * This method should be called whenever user data is modified.
     * If compression is enabled in the configuration, the data will be compressed.
     *
     * @throws RuntimeException if there is an error saving the user data
     */
    public void save() {
        // PersistenceManager saves all annotated fields
        PersistenceManager.saveFields(this, userConfig);

        try {
            if (isCompressionEnabled()) saveCompressedYamlConfiguration(userConfig, userFile);
            else userConfig.save(userFile);
        } catch (Exception exception) {
            throw new RuntimeException("Error while trying to save user data for user '${getName()}' ('${getUuid()}')", exception);
        }
    }

    /**
     * Gets the OfflinePlayer object associated with this user.
     *
     * @return The OfflinePlayer object for this user
     */
    public OfflinePlayer getPlayer() {
        if (player == null) player = Bukkit.getOfflinePlayer(uuid);

        return player;
    }

    /**
     * Gets the name of this user.
     *
     * @return An Optional containing the name of this user, or an empty Optional if the name is not available
     */
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    /**
     * Gets the UUID of this user.
     *
     * @return The UUID of this user
     */
    public UUID getUuid() {
        return uuid;
    }

    public boolean isVanish() {
        return isVanish;
    }

    public void setVanish(boolean vanish) {
        isVanish = vanish;
    }

    public VanishData getVanishData() {
        return vanishData;
    }

    public boolean isAcceptsMessages() {
        return acceptsMessages;
    }

    public void setAcceptsMessages(boolean acceptsMessages) {
        this.acceptsMessages = acceptsMessages;
    }

    public boolean isAcceptsTeleports() {
        return acceptsTeleports;
    }

    public void setAcceptsTeleports(boolean acceptsTeleports) {
        this.acceptsTeleports = acceptsTeleports;
    }

    public boolean isGodMode() {
        return isGodMode;
    }

    public void setGodMode(boolean godMode) {
        isGodMode = godMode;
    }

    public Set<UUID> getIgnoredPlayers() {
        return Collections.unmodifiableSet(ignoredPlayers);
    }

    public boolean isIgnoredPlayer(UUID uuid) {
        return ignoredPlayers.contains(uuid);
    }

    public void addIgnoredPlayer(UUID uuid) {
        ignoredPlayers.add(uuid);
    }

    public void removeIgnoredPlayer(UUID uuid) {
        ignoredPlayers.remove(uuid);
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public String getLastKnownIp() {
        return lastKnownIp;
    }

    public Location getLogoutPosition() {
        return logoutPosition;
    }

    public void setLogoutPosition(Location logoutPosition) {
        this.logoutPosition = logoutPosition;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public String getPlayerLanguage() {
        return playerLanguage;
    }

    public void setPlayerLanguage(String playerLanguage) {
        this.playerLanguage = playerLanguage;
    }

    public boolean isSocialSpyEnabled() {
        return socialSpyEnabled;
    }

    public void setSocialSpyEnabled(boolean socialSpyEnabled) {
        this.socialSpyEnabled = socialSpyEnabled;
    }

    public boolean isCommandSpyEnabled() {
        return commandSpyEnabled;
    }

    public void setCommandSpyEnabled(boolean commandSpyEnabled) {
        this.commandSpyEnabled = commandSpyEnabled;
    }

    public Location getLastDeathLocation() {
        return lastDeathLocation;
    }

    public void setLastDeathLocation(Location lastDeathLocation) {
        this.lastDeathLocation = lastDeathLocation;
    }

    public Location getLastTeleportLocation() {
        return lastTeleportLocation;
    }

    public void setLastTeleportLocation(Location lastTeleportLocation) {
        this.lastTeleportLocation = lastTeleportLocation;
    }

    public CommandBack.BackType getLastBackType() {
        return lastBackType;
    }

    public void setLastBackType(CommandBack.BackType lastBackType) {
        this.lastBackType = lastBackType;
    }

    public AbstractModerationManager getBanManager() {
        return banManager;
    }

    public AbstractModerationManager getMuteManager() {
        return muteManager;
    }
}
