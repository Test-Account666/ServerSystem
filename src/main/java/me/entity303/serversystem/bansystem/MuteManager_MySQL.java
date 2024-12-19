package me.entity303.serversystem.bansystem;

import me.entity303.serversystem.bansystem.moderation.MuteModeration;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MuteManager_MySQL extends AbstractMuteManager {

    public MuteManager_MySQL(ServerSystem plugin, String dateFormat) {
        super(dateFormat, plugin);
    }

    @Override
    public MuteModeration GetMute(OfflinePlayer player) {
        if (!this.CheckPlayerInMYSQL(player.getUniqueId().toString())) return null;
        var senderUUID = this.GetUUIDSender(player.getUniqueId().toString());
        var mutedUUID = player.getUniqueId();
        var unmuteTime = this.GetUnmuteTime(player.getUniqueId().toString());
        var unbanDate = this.ConvertLongToDate(this.GetUnmuteTime(player.getUniqueId().toString()));
        var reason = this.GetReason(player.getUniqueId().toString());
        var shadow = this.IsShadowMute(player.getUniqueId().toString());

        return new MuteModeration(mutedUUID, senderUUID, unmuteTime, unbanDate, reason, shadow);
    }

    @Override
    public void RemoveMute(UUID mutedUUID) {
        if (!this.CheckPlayerInMYSQL(mutedUUID.toString())) return;

        this._plugin.GetMySQL().ExecuteUpdate("DELETE FROM MutedPlayers WHERE BannedUUID='" + mutedUUID + "'");
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

        if (this.IsMuted(Bukkit.getOfflinePlayer(mutedUUID))) this.RemoveMute(mutedUUID);
        long unbanTime = mute.GetExpireTime();
        if (unbanTime < 1) unbanTime = -1L;

        try {
            var query = "INSERT INTO `MutedPlayers` (BannedUUID, SenderUUID, Reason, Shadow, UnbanTime) VALUES (?, ?, ?, ?, ?)";
            var preparedStatement = this._plugin.GetMySQL().PrepareStatement(query);
            preparedStatement.setString(1, String.valueOf(mutedUUID));
            preparedStatement.setString(2, senderUUID);
            preparedStatement.setString(3, reason);
            preparedStatement.setInt(4, mute.IsShadow()? 1 : 0);
            preparedStatement.setLong(5, unbanTime);
            preparedStatement.executeUpdate();
            preparedStatement.closeOnCompletion();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    @Override
    public boolean IsMuted(OfflinePlayer player) {
        return this.CheckPlayerInMYSQL(player.getUniqueId().toString());
    }

    @Override
    public List<String> GetMutedPlayerNames() {
        List<String> playerNames = new ArrayList<>();

        var resultSet = this._plugin.GetMySQL().GetResult("SELECT BannedUUID from MutedPlayers");

        while (true) try {
            if (resultSet == null) break;
            if (!resultSet.next()) break;
            var uuid = resultSet.getString("BannedUUID");

            if (this.CheckPlayerInMYSQL(uuid)) playerNames.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return playerNames;
    }

    private boolean CheckPlayerInMYSQL(String uuid) {
        var resultSet = this._plugin.GetMySQL().GetResult("SELECT * FROM MutedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (resultSet.next()) return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private String GetUUIDSender(String uuid) {
        var resultSet = this._plugin.GetMySQL().GetResult("SELECT * FROM MutedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (resultSet.next()) return resultSet.getString("SenderUUID");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private Long GetUnmuteTime(String uuid) {
        var resultSet = this._plugin.GetMySQL().GetResult("SELECT * FROM MutedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (resultSet.next()) return resultSet.getLong("UnbanTime");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private String GetReason(String uuid) {
        var resultSet = this._plugin.GetMySQL().GetResult("SELECT * FROM MutedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (resultSet.next()) return resultSet.getString("Reason");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private boolean IsShadowMute(String uuid) {
        var resultSet = this._plugin.GetMySQL().GetResult("SELECT * FROM MutedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (resultSet.next()) return resultSet.getInt("Shadow") == 1;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    @Override
    public void Close() {
        this._plugin.GetMySQL().Close();
    }
}
