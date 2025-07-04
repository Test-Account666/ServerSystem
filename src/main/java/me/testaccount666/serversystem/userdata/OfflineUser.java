package me.testaccount666.serversystem.userdata;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.userdata.home.HomeManager;
import me.testaccount666.serversystem.userdata.money.AbstractBankAccount;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
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
    protected String name;
    protected UUID uuid;
    protected OfflinePlayer player;
    protected AbstractBankAccount bankAccount;
    protected HomeManager homeManager;
    protected File userFile;
    protected FileConfiguration userConfig;
    protected long lastSeen;
    protected String lastKnownIp;
    protected Location logoutPosition;
    protected boolean isVanish;
    protected boolean isGodMode;
    protected boolean acceptsTeleports;
    protected boolean acceptsMessages;
    protected boolean socialSpyEnabled;
    protected boolean commandSpyEnabled;
    protected String playerLanguage;
    protected Set<UUID> ignoredPlayers = new HashSet<>();

    //TODO: Add more data. Homes, for example.

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
        try (var fis = new FileInputStream(file);
             var gzis = new GZIPInputStream(fis);
             var reader = new InputStreamReader(gzis)) {

            return YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            // If decompression fails, try loading normally as fallback
            return YamlConfiguration.loadConfiguration(file);
        }
    }

    protected void loadBasicData() {
        userConfig = loadYamlConfiguration(userFile);

        uuid = UUID.fromString(userFile.getName().replace(".yml.gz", ""));
        name = userConfig.getString("User.LastKnownName", null);

        if (name == null) name = getPlayer().getName();

        logoutPosition = userConfig.getLocation("User.LogoutPosition", null);
        lastSeen = userConfig.getLong("User.LastSeen", System.currentTimeMillis());
        lastKnownIp = userConfig.getString("User.LastKnownIp", "Unknown");
        isVanish = userConfig.getBoolean("User.IsVanish", false);
        isGodMode = userConfig.getBoolean("User.IsGodMode", false);
        acceptsTeleports = userConfig.getBoolean("User.AcceptsTeleports", true);
        acceptsMessages = userConfig.getBoolean("User.AcceptsMessages", true);
        socialSpyEnabled = userConfig.getBoolean("User.SocialSpyEnabled", false);
        commandSpyEnabled = userConfig.getBoolean("User.CommandSpyEnabled", false);

        //TODO: Make default language configurable instead of depending on system properties
        playerLanguage = userConfig.getString("User.PlayerLanguage", System.getProperty("user.language"));

        var ignoredPlayersList = userConfig.getStringList("User.IgnoredPlayers");
        ignoredPlayers.addAll(ignoredPlayersList.stream().map(UUID::fromString).collect(Collectors.toSet()));

        homeManager = new HomeManager(this, userConfig);

        bankAccount = ServerSystem.Instance.getEconomyManager().instantiateBankAccount(this, BigInteger.valueOf(0), userConfig);
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
     * Saves a YAML configuration to a file without compression.
     *
     * @param config The YAML configuration to save
     * @param file   The file to save to
     * @throws IOException If an I/O error occurs
     */
    private void saveYamlConfiguration(FileConfiguration config, File file) throws IOException {
        config.save(file);
    }

    /**
     * Saves the user's data to their configuration file.
     * This method should be called whenever user data is modified.
     * If compression is enabled in the configuration, the data will be compressed.
     *
     * @throws RuntimeException if there is an error saving the user data
     */
    public void save() {
        userConfig.set("User.LastKnownName", name);

        userConfig.set("User.LogoutPosition", logoutPosition);
        userConfig.set("User.LastSeen", lastSeen);
        userConfig.set("User.LastKnownIp", lastKnownIp);
        userConfig.set("User.IsVanish", isVanish);
        userConfig.set("User.IsGodMode", isGodMode);
        userConfig.set("User.AcceptsTeleports", acceptsTeleports);
        userConfig.set("User.AcceptsMessages", acceptsMessages);
        userConfig.set("User.PlayerLanguage", playerLanguage);
        userConfig.set("User.SocialSpyEnabled", socialSpyEnabled);
        userConfig.set("User.CommandSpyEnabled", commandSpyEnabled);

        var ignoredPlayersList = ignoredPlayers.stream().map(UUID::toString).collect(Collectors.toList());
        userConfig.set("User.IgnoredPlayers", ignoredPlayersList);

        try {
            if (isCompressionEnabled()) saveCompressedYamlConfiguration(userConfig, userFile);
            else saveYamlConfiguration(userConfig, userFile);
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
}
