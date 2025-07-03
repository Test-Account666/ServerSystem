package me.testaccount666.serversystem.userdata;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.userdata.home.HomeManager;
import me.testaccount666.serversystem.userdata.money.AbstractBankAccount;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents an offline user.
 * This class provides functionality for managing user data that persists
 * even when the player is offline.
 */
public class OfflineUser {
    protected String name;
    protected UUID uuid;
    protected OfflinePlayer player;
    protected AbstractBankAccount bankAccount;
    protected HomeManager homeManager;
    protected File userFile;
    protected FileConfiguration userConfig;
    protected Location logoutPosition;
    protected boolean isVanish;
    protected boolean isGodMode;
    protected boolean acceptsTeleports;
    protected boolean acceptsMessages;
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

    protected void loadBasicData() {
        userConfig = YamlConfiguration.loadConfiguration(userFile);

        uuid = UUID.fromString(userFile.getName().replace(".yml", ""));
        name = userConfig.getString("User.LastKnownName", null);

        if (name == null) name = getPlayer().getName();

        logoutPosition = userConfig.getLocation("User.LogoutPosition", null);
        isVanish = userConfig.getBoolean("User.IsVanish", false);
        isGodMode = userConfig.getBoolean("User.IsGodMode", false);
        acceptsTeleports = userConfig.getBoolean("User.AcceptsTeleports", true);
        acceptsMessages = userConfig.getBoolean("User.AcceptsMessages", true);

        //TODO: Make default language configurable instead of depending on system properties
        playerLanguage = userConfig.getString("User.PlayerLanguage", System.getProperty("user.language"));

        var ignoredPlayersList = userConfig.getStringList("User.IgnoredPlayers");
        ignoredPlayers.addAll(ignoredPlayersList.stream().map(UUID::fromString).collect(Collectors.toSet()));

        homeManager = new HomeManager(this, userFile, userConfig);

        bankAccount = ServerSystem.Instance.getEconomyManager().instantiateBankAccount(this, BigInteger.valueOf(0), userFile, userConfig);
    }

    public AbstractBankAccount getBankAccount() {
        return bankAccount;
    }

    /**
     * Saves the user's data to their configuration file.
     * This method should be called whenever user data is modified.
     *
     * @throws RuntimeException if there is an error saving the user data
     */
    public void save() {
        userConfig.set("User.LastKnownName", name);

        userConfig.set("User.LogoutPosition", logoutPosition);
        userConfig.set("User.IsVanish", isVanish);
        userConfig.set("User.IsGodMode", isGodMode);
        userConfig.set("User.AcceptsTeleports", acceptsTeleports);
        userConfig.set("User.AcceptsMessages", acceptsMessages);
        userConfig.set("User.PlayerLanguage", playerLanguage);

        var ignoredPlayersList = ignoredPlayers.stream().map(UUID::toString).collect(Collectors.toList());
        userConfig.set("User.IgnoredPlayers", ignoredPlayersList);

        bankAccount.save();

        try {
            userConfig.save(userFile);
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

    public void setLogoutPosition(Location logoutPosition) {
        this.logoutPosition = logoutPosition;
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
}
