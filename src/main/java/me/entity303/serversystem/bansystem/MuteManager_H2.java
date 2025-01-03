package me.entity303.serversystem.bansystem;

import me.entity303.serversystem.bansystem.moderation.MuteModeration;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MuteManager_H2 extends AbstractMuteManager {
    private Connection _connection;

    public MuteManager_H2(ServerSystem plugin, String dateFormat) {
        super(dateFormat, plugin);
        this.Open();
        try {
            this._connection.createStatement()
                            .executeUpdate("CREATE TABLE IF NOT EXISTS MutedPlayers (BannedUUID VARCHAR(100), SenderUUID VARCHAR(100), Reason VARCHAR(100), " +
                                           "Shadow INT, UnbanTime BIGINT)");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void Open() {
        if (!this.Initialize()) return;

        try {
            this._connection = DriverManager.getConnection("jdbc:h2:file:" + new File("plugins//ServerSystem", "mutes.h2").getAbsolutePath());
        } catch (SQLException var2) {
            this._plugin.Error("Could not establish an H2 connection, SQLException: " + var2.getMessage());
        }
    }

    protected boolean Initialize() {
        try {
            Class.forName("org.h2.Driver");
            return true;
        } catch (ClassNotFoundException var2) {
            this._plugin.Error("H2 driver class missing: " + var2.getMessage() + ".");
            return false;
        }
    }

    @Override
    public MuteModeration GetMute(OfflinePlayer player) {
        if (!this.CheckPlayerInH2(player.getUniqueId().toString())) return null;

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
        if (!this.CheckPlayerInH2(mutedUUID.toString())) return;

        try {
            var query = "DELETE FROM MutedPlayers WHERE BannedUUID=?";
            var preparedStatement = this._connection.prepareStatement(query);
            preparedStatement.setString(1, String.valueOf(mutedUUID));
            preparedStatement.executeUpdate();
            preparedStatement.closeOnCompletion();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
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
            var query = "INSERT INTO MutedPlayers (BannedUUID, SenderUUID, Reason, Shadow, UnbanTime) VALUES (?, ?, ?, ?, ?)";
            var preparedStatement = this._connection.prepareStatement(query);
            preparedStatement.setString(1, String.valueOf(mutedUUID));
            preparedStatement.setString(2, senderUUID);
            preparedStatement.setString(3, reason);
            preparedStatement.setInt(4, mute.IsShadow()? 1 : 0);
            preparedStatement.setLong(5, unbanTime);
            preparedStatement.executeUpdate();
            preparedStatement.closeOnCompletion();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public boolean IsMuted(OfflinePlayer player) {
        return this.CheckPlayerInH2(player.getUniqueId().toString());
    }

    @Override
    public List<String> GetMutedPlayerNames() {
        List<String> playerNames = new ArrayList<>();

        ResultSet resultSet = null;
        try {
            //noinspection SqlResolve
            resultSet = this._connection.createStatement().executeQuery("SELECT BannedUUID from MutedPlayers");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        while (true) try {
            if (resultSet == null) break;
            if (!resultSet.next()) break;
            var uuid = resultSet.getString("BannedUUID");

            if (this.CheckPlayerInH2(uuid)) playerNames.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return playerNames;
    }

    private boolean CheckPlayerInH2(String uuid) {
        ResultSet resultSet = null;
        try {
            resultSet = this._connection.createStatement().executeQuery("SELECT * FROM MutedPlayers WHERE BannedUUID='" + uuid + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            while (resultSet.next()) return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private String GetUUIDSender(String uuid) {
        ResultSet resultSet = null;
        try {
            resultSet = this._connection.createStatement().executeQuery("SELECT * FROM MutedPlayers WHERE BannedUUID='" + uuid + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            while (resultSet.next()) return resultSet.getString("SenderUUID");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private Long GetUnmuteTime(String uuid) {
        ResultSet resultSet = null;
        try {
            resultSet = this._connection.createStatement().executeQuery("SELECT * FROM MutedPlayers WHERE BannedUUID='" + uuid + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            while (resultSet.next()) return resultSet.getLong("UnbanTime");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private String GetReason(String uuid) {
        ResultSet resultSet = null;
        try {
            resultSet = this._connection.createStatement().executeQuery("SELECT * FROM MutedPlayers WHERE BannedUUID='" + uuid + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            while (resultSet.next()) return resultSet.getString("Reason");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private boolean IsShadowMute(String uuid) {
        ResultSet resultSet = null;
        try {
            resultSet = this._connection.createStatement().executeQuery("SELECT * FROM MutedPlayers WHERE BannedUUID='" + uuid + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            while (resultSet.next()) return resultSet.getInt("Shadow") == 1;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    @Override
    public void Close() {
        try {
            this._connection.close();
        } catch (Exception ignored) {
        }
    }
}
