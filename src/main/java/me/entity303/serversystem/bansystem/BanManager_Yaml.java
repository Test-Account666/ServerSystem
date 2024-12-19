package me.entity303.serversystem.bansystem;

import me.entity303.serversystem.bansystem.moderation.BanModeration;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BanManager_Yaml extends AbstractBanManager {
    private final File _banFile;
    private final FileConfiguration _configuration;

    public BanManager_Yaml(File banFile, String dateFormat, ServerSystem plugin) {
        super(dateFormat, plugin);
        this._banFile = banFile;
        this._configuration = YamlConfiguration.loadConfiguration(banFile);
    }

    @Override
    public void Close() {

    }

    @Override
    public boolean IsBanned(UUID uuid) {
        if (!this._banFile.exists()) return false;
        return this.GetBanByUUID(uuid) != null;
    }


    @Override
    public List<String> GetBannedPlayerNames() {
        List<String> playerNameList = new ArrayList<>();
        if (this._configuration.getConfigurationSection("Banned") == null) return new ArrayList<>();
        this._configuration.getConfigurationSection("Banned").getKeys(false);

        if (this._configuration.getConfigurationSection("Banned").getKeys(false).isEmpty()) return new ArrayList<>();
        for (var uuid : this._configuration.getConfigurationSection("Banned").getKeys(false))
            playerNameList.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
        return playerNameList;
    }

    @Override
    public BanModeration GetBanByUUID(UUID uuid) {
        if (!this._banFile.exists()) return null;

        try {

            var str = "Banned." + uuid.toString() + ".";
            var senderUuid = this._configuration.getString(str + "Sender");
            var uuidBanned = uuid.toString();
            long expireTime;
            try {
                expireTime = Long.parseLong(this._configuration.getString(str + "unbanTime"));
            } catch (NumberFormatException ignored) {
                return null;
            }
            var expireDate = this.ConvertLongToDate(expireTime);
            var reason = this._configuration.getString(str + "Reason");

            if (senderUuid == null) return null;
            if (uuidBanned == null) return null;
            if (expireDate == null) return null;
            if (reason == null) return null;

            return new BanModeration(uuid, senderUuid, expireTime, expireDate, reason);
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    @Override
    public BanModeration GetBanByPlayer(OfflinePlayer player) {
        return this.GetBanByUUID(player.getUniqueId());
    }

    @Override
    public BanModeration CreateBan(UUID banned, String senderUUID, String reason, Long howLong, TimeUnit timeUnit) {
        if (this.IsBanned(banned)) this.UnBan(banned);
        var expireTime = System.currentTimeMillis() + (howLong * timeUnit.GetValue());
        if (howLong < 1) expireTime = -1L;
        this._configuration.set("Banned." + banned.toString() + ".Sender", senderUUID);
        this._configuration.set("Banned." + banned + ".Reason", reason);
        this._configuration.set("Banned." + banned + ".unbanTime", Long.toString(expireTime));

        try {
            this._configuration.save(this._banFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        var ban = new BanModeration(banned, senderUUID, expireTime, this.ConvertLongToDate(expireTime), reason);

        try {
            this._configuration.load(this._banFile);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }

        return ban;
    }

    @Override
    public void UnBan(UUID banned) {
        var ban = this.GetBanByUUID(banned);
        this._configuration.set("Banned." + banned.toString() + ".Sender", null);
        this._configuration.set("Banned." + banned + ".Reason", null);
        this._configuration.set("Banned." + banned + ".unbanTime", null);
        this._configuration.set("Banned." + banned, null);

        try {
            this._configuration.save(this._banFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        try {
            this._configuration.load(this._banFile);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
    }


}
