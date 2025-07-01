package me.testaccount666.serversystem.userdata;

import me.testaccount666.serversystem.userdata.home.HomeManager;
import me.testaccount666.serversystem.userdata.money.AbstractBankAccount;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    }

    public void save() {
        userConfig.set("User.LastKnownName", name);

        userConfig.set("User.LogoutPosition", logoutPosition);
        userConfig.set("User.IsVanish", isVanish);
        userConfig.set("User.IsGodMode", isGodMode);
        userConfig.set("User.AcceptsTeleports", acceptsTeleports);
        userConfig.set("User.AcceptsMessages", acceptsMessages);

        var ignoredPlayersList = ignoredPlayers.stream().map(UUID::toString).collect(Collectors.toList());
        userConfig.set("User.IgnoredPlayers", ignoredPlayersList);

        try {
            userConfig.save(userFile);
        } catch (Exception exception) {
            throw new RuntimeException("Error while trying to save user data for user '${getName()}' ('${getUuid()}')", exception);
        }
    }

    public OfflinePlayer getPlayer() {
        if (player == null) player = Bukkit.getOfflinePlayer(uuid);

        return player;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public UUID getUuid() {
        return uuid;
    }
}
