package me.entity303.serversystem.databasemanager;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;

import java.sql.*;

@SuppressWarnings("SqlSourceToSinkFlow") public class MySQL {
    private final ServerSystem plugin;
    private final String hostname;
    private final String port;
    private final String username;
    private final String password;
    private final String database;
    private final boolean mariadb;
    private int scheduleId = -1;

    private Connection con;

    public MySQL(String hostname, String port, String username, String password, String database, boolean mariadb, ServerSystem plugin) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
        this.mariadb = mariadb;
        this.plugin = plugin;
    }

    public void close() {
        if (!this.isConnected())
            return;

        try {
            this.con.close();
            this.plugin.log("MySQL connection successfully closed!");
        } catch (SQLException throwables) {
            this.plugin.error("MySQL Exception:");
            throwables.printStackTrace();
        }
    }

    public boolean isConnected() {
        if (this.con == null)
            return false;

        try {
            return !this.con.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void executeQuery(String cmd) {
        try {
            if (!this.isConnected())
                this.connect();

            this.con.createStatement().executeQuery(cmd);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void connect() {
        if (this.isConnected())
            return;

        if (this.mariadb)
            try {
                Class.forName("serversystem.libs.mariadb.org.mariadb.jdbc.Driver");
                this.con = DriverManager.getConnection("jdbc:mariadb://" + this.hostname + ":" + this.port + "/" + this.database + "?autoReconnect=true",
                                                       this.username, this.password);
            } catch (SQLException | ClassNotFoundException throwables2) {
                throwables2.printStackTrace();
            }
        else
            try {
                this.con = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database + "?autoReconnect=true",
                                                       this.username, this.password);
            } catch (SQLException throwables2) {
                throwables2.printStackTrace();
            }

        if (this.scheduleId != -1)
            Bukkit.getScheduler().cancelTask(this.scheduleId);

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this.plugin, () -> {
            if (!this.isConnected())
                return;

            try {
                var statement = this.con.createStatement();
                var resultSet = statement.executeQuery("SELECT 1");
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
                Bukkit.getScheduler().cancelTask(this.scheduleId);

                this.scheduleId = -1;

                this.plugin.error("Error while executing keep alive command, not retrying");
            }
        }, 20 * 10, 20 * 10);
    }

    public void createTable() {
        this.executeUpdate(
                "CREATE TABLE IF NOT EXISTS BannedPlayers (BannedUUID VARCHAR(100), SenderUUID VARCHAR(100), Reason VARCHAR(100), UnbanTime BIGINT(1))");
        this.executeUpdate(
                "CREATE TABLE IF NOT EXISTS MutedPlayers (BannedUUID VARCHAR(100), SenderUUID VARCHAR(100), Reason VARCHAR(100), Shadow INT(1), UnbanTime BIGINT(1))");
        this.executeUpdate("CREATE TABLE IF NOT EXISTS Economy (UUID VARCHAR(100), Server VARCHAR(100), Balance DECIMAL(30, 2))");
    }

    public void executeUpdate(String cmd) {
        try {
            if (!this.isConnected())
                this.connect();

            this.con.createStatement().executeUpdate(cmd);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void execute(String cmd) {
        try {
            if (!this.isConnected())
                this.connect();

            this.con.createStatement().execute(cmd);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public ResultSet getResult(String qry) {
        try {
            if (!this.isConnected())
                this.connect();

            return this.con.createStatement().executeQuery(qry);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public PreparedStatement prepareStatement(String qry) {
        try {
            if (!this.isConnected())
                this.connect();

            return this.con.prepareStatement(qry);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
