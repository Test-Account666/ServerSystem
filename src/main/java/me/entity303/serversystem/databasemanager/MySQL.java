package me.entity303.serversystem.databasemanager;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;

import java.sql.*;

public class MySQL {
    private final ServerSystem _plugin;
    private final String _hostname;
    private final String _port;
    private final String _username;
    private final String _password;
    private final String _database;
    private final boolean _mariadb;
    private int _scheduleId = -1;

    private Connection _connection;

    public MySQL(String hostname, String port, String username, String password, String database, boolean mariadb, ServerSystem plugin) {
        this._hostname = hostname;
        this._port = port;
        this._username = username;
        this._password = password;
        this._database = database;
        this._mariadb = mariadb;
        this._plugin = plugin;
    }

    public void Close() {
        if (!this.IsConnected()) return;

        try {
            this._connection.close();
            this._plugin.Info("MySQL connection successfully closed!");
        } catch (SQLException throwables) {
            this._plugin.Error("MySQL Exception:");
            throwables.printStackTrace();
        }
    }

    public boolean IsConnected() {
        if (this._connection == null) return false;

        try {
            return !this._connection.isClosed();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return false;
    }

    public void CreateTable() {
        this.ExecuteUpdate("CREATE TABLE IF NOT EXISTS BannedPlayers (BannedUUID VARCHAR(100), SenderUUID VARCHAR(100), Reason VARCHAR(100), UnbanTime BIGINT(1))");
        this.ExecuteUpdate("CREATE TABLE IF NOT EXISTS MutedPlayers (BannedUUID VARCHAR(100), SenderUUID VARCHAR(100), Reason VARCHAR(100), Shadow INT(1), " +
                           "UnbanTime BIGINT(1))");
        this.ExecuteUpdate("CREATE TABLE IF NOT EXISTS Economy (UUID VARCHAR(100), Server VARCHAR(100), Balance DECIMAL(30, 2))");
    }

    public void ExecuteUpdate(String command) {
        try {
            if (!this.IsConnected()) this.Connect();

            var statement = this._connection.createStatement();
            statement.executeUpdate(command);
            statement.closeOnCompletion();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void Connect() {
        if (this.IsConnected()) return;

        if (this._mariadb) {
            try {
                Class.forName("serversystem.libs.mariadb.org.mariadb.jdbc.Driver");
                this._connection =
                        DriverManager.getConnection("jdbc:mariadb://" + this._hostname + ":" + this._port + "/" + this._database + "?autoReconnect=true", this._username,
                                                    this._password);
            } catch (SQLException | ClassNotFoundException throwables2) {
                throwables2.printStackTrace();
            }
        } else {
            try {
                this._connection =
                        DriverManager.getConnection("jdbc:mysql://" + this._hostname + ":" + this._port + "/" + this._database + "?autoReconnect=true", this._username,
                                                    this._password);
            } catch (SQLException throwables2) {
                throwables2.printStackTrace();
            }
        }

        if (this._scheduleId != -1) Bukkit.getScheduler().cancelTask(this._scheduleId);

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this._plugin, () -> {
            if (!this.IsConnected()) return;

            try {
                var statement = this._connection.createStatement();
                var resultSet = statement.executeQuery("SELECT 1");
                resultSet.close();
                statement.closeOnCompletion();
            } catch (SQLException exception) {
                exception.printStackTrace();
                Bukkit.getScheduler().cancelTask(this._scheduleId);

                this._scheduleId = -1;

                this._plugin.Error("Error while executing keep alive command, not retrying");
            }
        }, 20 * 10, 20 * 10);
    }

    public ResultSet GetResult(String qry) {
        try {
            if (!this.IsConnected()) this.Connect();

            var statement = this._connection.createStatement();

            var resultSet = statement.executeQuery(qry);

            statement.closeOnCompletion();

            return resultSet;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public PreparedStatement PrepareStatement(String qry) {
        try {
            if (!this.IsConnected()) this.Connect();

            return this._connection.prepareStatement(qry);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
