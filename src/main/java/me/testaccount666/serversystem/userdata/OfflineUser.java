package me.testaccount666.serversystem.userdata;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;

public class OfflineUser {
    protected final String name;
    protected final UUID uuid;
    protected OfflinePlayer player;

    //TODO: Add more data. Homes, for example.

    protected OfflineUser(File userFile) {
        var fileConfig = YamlConfiguration.loadConfiguration(userFile);

        uuid = UUID.fromString(userFile.getName().replace(".yml", ""));
        name = fileConfig.getString("User.LastKnownName", "Unknown");
    }

    public OfflinePlayer getPlayer() {
        if (player == null) player = Bukkit.getOfflinePlayer(uuid);

        return player;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }
}
