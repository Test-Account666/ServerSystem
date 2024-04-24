package me.entity303.serversystem.bansystem;

import me.entity303.serversystem.bansystem.moderation.BanModeration;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BanManager_MySQL extends AbstractBanManager {

    public BanManager_MySQL(String dateFormat, ServerSystem plugin) {
        super(dateFormat, plugin);

    }

    @Override
    public void Close() {
        this._plugin.GetMySQL().Close();
    }

    @Override
    public boolean IsBanned(UUID uuid) {
        return this.CheckPlayerInMYSQL(uuid.toString());
    }

    @Override
    public List<String> GetBannedPlayerNames() {
        List<String> playerNames = new ArrayList<>();

        var resultSet = this._plugin.GetMySQL().GetResult("SELECT BannedUUID from BannedPlayers");

        while (true)
            try {
                if (resultSet == null)
                    break;
                if (!resultSet.next())
                    break;
                var uuid = resultSet.getString("BannedUUID");

                if (this.CheckPlayerInMYSQL(uuid))
                    playerNames.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

        return playerNames;
    }

    @Override
    public BanModeration GetBanByUUID(UUID uuid) {
        if (!this.CheckPlayerInMYSQL(uuid.toString()))
            return null;

        var bannedUUID = uuid;
        var reason = this.GetReason(uuid.toString());
        long expireTime = this.GetUnbanTime(uuid.toString());
        var senderUUID = this.GetUUIDSender(uuid.toString());
        var expireDate = this.ConvertLongToDate(expireTime);

        return new BanModeration(bannedUUID, senderUUID, expireTime, expireDate, reason);
    }

    @Override
    public BanModeration GetBanByPlayer(OfflinePlayer player) {
        return this.GetBanByUUID(player.getUniqueId());
    }

    private boolean CheckPlayerInMYSQL(String uuid) {
        var resultSet = this._plugin.GetMySQL().GetResult("SELECT * FROM BannedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (resultSet.next())
                return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private String GetReason(String uuid) {
        var resultSet = this._plugin.GetMySQL().GetResult("SELECT * FROM BannedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (resultSet.next())
                return resultSet.getString("Reason");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }


    private Long GetUnbanTime(String uuid) {
        var resultSet = this._plugin.GetMySQL().GetResult("SELECT * FROM BannedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (resultSet.next())
                return resultSet.getLong("UnbanTime");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private String GetUUIDSender(String uuid) {
        var resultSet = this._plugin.GetMySQL().GetResult("SELECT * FROM BannedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (resultSet.next())
                return resultSet.getString("SenderUUID");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public BanModeration CreateBan(UUID bannedUuid, String senderUUID, String reason, Long howLong, TimeUnit timeUnit) {
        if (this.IsBanned(bannedUuid))
            this.UnBan(bannedUuid);
        var expireTime = System.currentTimeMillis() + (howLong * timeUnit.GetValue());
        if (howLong < 1)
            expireTime = -1L;
        try {
            var query = "INSERT INTO `BannedPlayers` (BannedUUID, SenderUUID, Reason, UnbanTime) VALUES (?, ?, ?, ?)";
            var preparedStatement = this._plugin.GetMySQL().PrepareStatement(query);
            preparedStatement.setString(1, bannedUuid.toString());
            preparedStatement.setString(2, senderUUID);
            preparedStatement.setString(3, reason);
            preparedStatement.setString(4, String.valueOf(expireTime));
            preparedStatement.executeUpdate();
            preparedStatement.closeOnCompletion();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

        return new BanModeration(bannedUuid, senderUUID, expireTime, this.ConvertLongToDate(expireTime), reason);
    }

    @Override
    public void UnBan(UUID banned) {
        try {
            var query = "DELETE FROM BannedPlayers WHERE BannedUUID=?";
            var preparedStatement = this._plugin.GetMySQL().PrepareStatement(query);
            preparedStatement.setString(1, banned.toString());
            preparedStatement.executeUpdate();
            preparedStatement.closeOnCompletion();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }


}
