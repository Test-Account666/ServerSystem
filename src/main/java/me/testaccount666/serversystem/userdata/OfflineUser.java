package me.testaccount666.serversystem.userdata;

import lombok.Getter;
import lombok.Setter;
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
 * Compression can be enabled or disabled in the config.yml configuration file.
 */
public class OfflineUser {
    protected final File userFile;
    @SaveableField(path = "User.IgnoredPlayers", handler = UuidSetFieldHandler.class)
    protected final Set<UUID> ignoredPlayers = new HashSet<>();
    @SaveableField(path = "User.LastKnownName")
    protected String name;
    @Getter
    protected UUID uuid;
    protected OfflinePlayer player;
    @Getter
    protected AbstractModerationManager banManager;
    @Getter
    protected AbstractModerationManager muteManager;
    @Getter
    protected AbstractBankAccount bankAccount;
    @Getter
    protected HomeManager homeManager;
    protected FileConfiguration userConfig;
    @Getter
    @SaveableField(path = "User.LastSeen")
    protected long lastSeen;
    @Getter
    @SaveableField(path = "User.LastKnownIp")
    protected String lastKnownIp;
    @Getter
    @Setter
    @SaveableField(path = "User.LogoutPosition", handler = LocationFieldHandler.class)
    protected Location logoutPosition;
    @Getter
    @Setter
    @SaveableField(path = "User.VanishData.IsVanish")
    protected boolean isVanish;
    @Getter
    @SaveableField(path = "User.VanishData.Data", handler = VanishDataFieldHandler.class)
    protected VanishData vanishData;
    @Getter
    @Setter
    @SaveableField(path = "User.IsGodMode")
    protected boolean isGodMode;
    @Getter
    @Setter
    @SaveableField(path = "User.AcceptsTeleports")
    protected boolean acceptsTeleports;
    @Getter
    @Setter
    @SaveableField(path = "User.AcceptsMessages")
    protected boolean acceptsMessages;
    @Getter
    @Setter
    @SaveableField(path = "User.SocialSpyEnabled")
    protected boolean socialSpyEnabled;
    @Getter
    @Setter
    @SaveableField(path = "User.CommandSpyEnabled")
    protected boolean commandSpyEnabled;
    @Getter
    @Setter
    @SaveableField(path = "User.LastDeathLocation", handler = LocationFieldHandler.class)
    protected Location lastDeathLocation;
    @Getter
    @Setter
    @SaveableField(path = "User.LastTeleportLocation", handler = LocationFieldHandler.class)
    protected Location lastTeleportLocation;
    @Getter
    @Setter
    @SaveableField(path = "User.LastBackType", handler = EnumFieldHandler.class)
    protected CommandBack.BackType lastBackType;
    @Getter
    @Setter
    @SaveableField(path = "User.PlayerLanguage")
    protected String playerLanguage;
    @SaveableField(path = "User.KitCooldowns", handler = KitMapFieldHandler.class)
    protected Map<String, Long> kitCooldowns = new HashMap<>();
    @Getter
    @Setter
    @SaveableField(path = "Language.UsesDefault")
    protected boolean usesDefaultLanguage;

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
        usesDefaultLanguage = true;
        playerLanguage = "english";
        lastBackType = CommandBack.BackType.NONE;

        // PersistenceManager loads all annotated fields
        PersistenceManager.loadFields(this, userConfig);
        // Quick fix that blocks potentially wanted behavior, but eh...
        if (playerLanguage.equalsIgnoreCase(System.getProperty("user.language"))) playerLanguage = "english";
        playerLanguage = playerLanguage.toLowerCase();

        if (name == null) name = getPlayer().getName();

        homeManager = new HomeManager(this, userConfig);
        bankAccount = ServerSystem.Instance.getEconomyProvider().instantiateBankAccount(this, BigInteger.valueOf(0), userConfig);

        var moderationManager = ServerSystem.Instance.getModerationDatabaseManager();

        // Create the appropriate ban manager based on the database type
        try (var ignored = moderationManager.getConnection()) {
            banManager = switch (moderationManager) {
                case SqliteModerationDatabaseManager database -> new SqliteBanManager(uuid);
                case MySqlModerationDatabaseManager database -> new MySqlBanManager(uuid);
                default -> null;
            };
        } catch (SQLException exception) {
            throw new RuntimeException("Error loading userdata! (BanManager)", exception);
        }

        // Create the appropriate mute manager based on the database type
        try (var ignored = moderationManager.getConnection()) {
            muteManager = switch (moderationManager) {
                case SqliteModerationDatabaseManager database -> new SqliteMuteManager(uuid);
                case MySqlModerationDatabaseManager database -> new MySqlMuteManager(uuid);
                default -> null;
            };
        } catch (SQLException exception) {
            throw new RuntimeException("Error loading userdata! (MuteManager)", exception);
        }
    }

    /**
     * Checks if compression is enabled in the configuration.
     *
     * @return true if compression is enabled, false otherwise
     */
    private boolean isCompressionEnabled() {
        return ServerSystem.Instance.getConfigManager().getGeneralConfig().getBoolean("UserData.Compression.Enabled", true);
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

    public boolean isOnKitCooldown(String kitName) {
        var cooldown = kitCooldowns.getOrDefault(kitName, null);
        if (cooldown == null) return false;
        return cooldown > System.currentTimeMillis();
    }

    public long getKitCooldown(String kitName) {
        var cooldown = kitCooldowns.getOrDefault(kitName, null);
        if (cooldown == null) return -1;
        return cooldown;
    }

    public void setKitCooldown(String kitName, long cooldown) {
        kitCooldowns.put(kitName, cooldown + System.currentTimeMillis());
    }
}
