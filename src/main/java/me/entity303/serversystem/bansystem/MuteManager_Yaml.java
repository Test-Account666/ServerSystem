package me.entity303.serversystem.bansystem;

import me.entity303.serversystem.bansystem.moderation.MuteModeration;
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
import java.util.stream.Collectors;

public class MuteManager_Yaml extends AbstractMuteManager {
    private final File _muteFile;
    private final FileConfiguration _configuration;

    public MuteManager_Yaml(File muteFile, String dateFormat, ServerSystem plugin) {
        super(dateFormat, plugin);
        this._muteFile = muteFile;
        this._configuration = YamlConfiguration.loadConfiguration(muteFile);
    }

    @Override
    public MuteModeration GetMute(OfflinePlayer player) {
        return this.GetMute(player.getUniqueId());
    }

    @Override
    public void RemoveMute(UUID mutedUUID) {
        if (!this.IsMuted(Bukkit.getOfflinePlayer(mutedUUID))) return;

        this._configuration.set("Muted." + mutedUUID, null);

        this.Save();

        this.Reload();
    }

    @Override
    public MuteModeration CreateMute(UUID mutedUUID, String senderUUID, String reason, Long howLong, TimeUnit timeUnit) {
        return this.CreateMute(mutedUUID, senderUUID, reason, false, howLong, timeUnit);
    }

    @Override
    public MuteModeration CreateMute(UUID mutedUUID, String senderUUID, String reason, boolean shadow, Long howLong, TimeUnit timeUnit) {
        if (this.IsMuted(Bukkit.getOfflinePlayer(mutedUUID))) this.RemoveMute(mutedUUID);

        var expireTime = System.currentTimeMillis() + (howLong * timeUnit.GetValue());
        if (howLong < 1) expireTime = -1L;

        var mute = new MuteModeration(mutedUUID, senderUUID, expireTime, this.ConvertLongToDate(expireTime), reason, shadow);

        this.CreateMute(mute);

        return mute;
    }

    @Override
    public void CreateMute(MuteModeration mute) {
        var mutedUUID = mute.GetUuid();
        var senderUUID = mute.GetSenderUuid();
        var reason = mute.GetReason();
        var shadow = mute.IsShadow();
        long unbanTime = mute.GetExpireTime();

        if (this.IsMuted(Bukkit.getOfflinePlayer(mutedUUID))) this.RemoveMute(mutedUUID);

        this._configuration.set("Muted." + mutedUUID + ".Sender", senderUUID);
        this._configuration.set("Muted." + mutedUUID + ".Reason", reason);
        this._configuration.set("Muted." + mutedUUID + ".Shadow", shadow);
        this._configuration.set("Muted." + mutedUUID + ".UnmuteTime", Long.toString(unbanTime));

        this.Save();

        this.Reload();
    }

    @Override
    public boolean IsMuted(OfflinePlayer player) {
        if (!this._muteFile.exists()) return false;
        return this.GetMute(player) != null;
    }

    @Override
    public List<String> GetMutedPlayerNames() {
        if (this._configuration.getConfigurationSection("Muted") == null) return new ArrayList<>();
        this._configuration.getConfigurationSection("Muted").getKeys(false);
        if (this._configuration.getConfigurationSection("Muted").getKeys(false).isEmpty()) return new ArrayList<>();

        try {
            return this._configuration.getConfigurationSection("Muted")
                                      .getKeys(false)
                                      .stream()
                                      .map(uuid -> Bukkit.getOfflinePlayer(UUID.fromString(uuid)))
                                      .map(OfflinePlayer::getName)
                                      .collect(Collectors.toList());
        } catch (Exception exception) {
            exception.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void Close() {

    }

    public MuteModeration GetMute(UUID uuid) {
        if (!this._muteFile.exists()) return null;
        try {
            var str = "Muted." + uuid.toString() + ".";
            var senderUuid = this._configuration.getString(str + "Sender");
            long expireTime;
            try {
                expireTime = Long.parseLong(this._configuration.getString(str + "UnmuteTime"));
            } catch (NumberFormatException ignored) {
                return null;
            }
            var expireDate = this.ConvertLongToDate(expireTime);
            var reason = this._configuration.getString(str + "Reason");
            var shadow = this._configuration.getBoolean(str + "Shadow");

            if (senderUuid == null) return null;
            if (expireDate == null) return null;
            if (reason == null) return null;

            return new MuteModeration(uuid, senderUuid, expireTime, expireDate, reason, shadow);
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    public void Reload() {
        try {
            this._configuration.load(this._muteFile);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
    }

    public void Save() {
        try {
            this._configuration.save(this._muteFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
