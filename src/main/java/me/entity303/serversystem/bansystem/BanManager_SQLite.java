package me.entity303.serversystem.bansystem;

import me.entity303.serversystem.bansystem.moderation.BanModeration;
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

public class BanManager_SQLite extends AbstractBanManager {
    private Connection _connection;

    public BanManager_SQLite(String dateFormat, ServerSystem plugin) {
        super(dateFormat, plugin);
        this.Open();
        try {
            this._connection.createStatement()
                            .executeUpdate(
                                    "CREATE TABLE IF NOT EXISTS BannedPlayers (BannedUUID VARCHAR(100), SenderUUID VARCHAR(100), Reason VARCHAR(100), " +
                                    "UnbanTime BIGINT(1))");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void Open() {
        if (this.Initialize())
            try {
                this._connection = DriverManager.getConnection("jdbc:sqlite:" + new File("plugins//ServerSystem", "bans.sqlite").getAbsolutePath());
            } catch (SQLException var2) {
                this._plugin.Error("Could not establish an SQLite connection, SQLException: " + var2.getMessage());
            }
    }

    protected boolean Initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
            return true;
        } catch (ClassNotFoundException var2) {
            this._plugin.Error("Class not found in initialize(): " + var2);
            return false;
        }
    }

    @Override
    public void Close() {
        try {
            this._connection.close();
        } catch (Exception ignored) {
        }
    }

    @Override
    public boolean IsBanned(UUID uuid) {
        return this.CheckPlayerInSQLite(uuid.toString());
    }

    @Override
    public List<String> GetBannedPlayerNames() {
        List<String> playerNames = new ArrayList<>();

        ResultSet resultSet = null;
        try {
            resultSet = this._connection.createStatement().executeQuery("SELECT BannedUUID from BannedPlayers");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        while (true)
            try {
                if (resultSet == null)
                    break;
                if (!resultSet.next())
                    break;
                var uuid = resultSet.getString("BannedUUID");

                if (this.CheckPlayerInSQLite(uuid))
                    playerNames.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

        return playerNames;
    }

    @Override
    public BanModeration GetBanByUUID(UUID uuid) {
        if (!this.CheckPlayerInSQLite(uuid.toString()))
            return null;

        var reason = this.GetReason(uuid.toString());
        long expireTime = this.GetUnbanTime(uuid.toString());
        var senderUUID = this.GetUUIDSender(uuid.toString());
        var expireDate = this.ConvertLongToDate(expireTime);

        return new BanModeration(uuid, senderUUID, expireTime, expireDate, reason);
    }

    @Override
    public BanModeration GetBanByPlayer(OfflinePlayer player) {
        return this.GetBanByUUID(player.getUniqueId());
    }

    private boolean CheckPlayerInSQLite(String uuid) {
        ResultSet resultSet = null;
        try {
            resultSet = this._connection.createStatement().executeQuery("SELECT * FROM BannedPlayers WHERE BannedUUID='" + uuid + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            while (resultSet.next())
                return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private String GetReason(String uuid) {
        ResultSet resultSet = null;
        try {
            resultSet = this._connection.createStatement().executeQuery("SELECT * FROM BannedPlayers WHERE BannedUUID='" + uuid + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            while (resultSet.next())
                return resultSet.getString("Reason");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }


    private Long GetUnbanTime(String uuid) {
        ResultSet resultSet = null;
        try {
            resultSet = this._connection.createStatement().executeQuery("SELECT * FROM BannedPlayers WHERE BannedUUID='" + uuid + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            while (resultSet.next())
                return resultSet.getLong("UnbanTime");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private String GetUUIDSender(String uuid) {
        ResultSet resultSet = null;
        try {
            resultSet = this._connection.createStatement().executeQuery("SELECT * FROM BannedPlayers WHERE BannedUUID='" + uuid + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            while (resultSet.next())
                return resultSet.getString("SenderUUID");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public BanModeration CreateBan(UUID banned, String senderUUID, String reason, Long howLong, TimeUnit timeUnit) {
        if (this.IsBanned(banned))
            this.UnBan(banned);
        var expireTime = System.currentTimeMillis() + (howLong * timeUnit.GetValue());
        if (howLong < 1)
            expireTime = -1L;
        try {
            var query = "INSERT INTO `BannedPlayers` (BannedUUID, SenderUUID, Reason, UnbanTime) VALUES (?, ?, ?, ?)";
            var preparedStatement = this._connection.prepareStatement(query);
            preparedStatement.setString(1, banned.toString());
            preparedStatement.setString(2, senderUUID);
            preparedStatement.setString(3, reason);
            preparedStatement.setString(4, String.valueOf(expireTime));
            preparedStatement.executeUpdate();
            preparedStatement.closeOnCompletion();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new BanModeration(banned, senderUUID, expireTime, this.ConvertLongToDate(expireTime), reason);
    }

    @Override
    public void UnBan(UUID banned) {
        try {
            var query = "DELETE FROM BannedPlayers WHERE BannedUUID=?";
            var preparedStatement = this._connection.prepareStatement(query);
            preparedStatement.setString(1, banned.toString());
            preparedStatement.executeUpdate();
            preparedStatement.closeOnCompletion();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


}
