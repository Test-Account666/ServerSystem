package me.entity303.serversystem.databasemanager;

import me.entity303.serversystem.main.ServerSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL {
    private final ServerSystem plugin;
    private final String hostname;
    private final String port;
    private final String username;
    private final String password;
    private final String database;

    private Connection con;

    public MySQL(String hostname, String port, String username, String password, String database, ServerSystem plugin) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
        this.plugin = plugin;
    }

    public void connect() {
        if (!this.isConnected()) try {
            this.con = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database + "?autoReconnect=true", this.username, this.password);
        } catch (SQLException throwables2) {
            throwables2.printStackTrace();
        }
    }

    public void close() {
        if (this.isConnected()) try {
            this.con.close();
            this.plugin.log("MySQL connection successfully closed!");
        } catch (SQLException throwables) {
            this.plugin.error("MySQL Exception:");
            throwables.printStackTrace();
        }
    }


    public boolean isConnected() {
        return this.con != null;
    }

    public void executeUpdate(String cmd) {
        try {
            this.con.createStatement().executeUpdate(cmd);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void executeQuery(String cmd) {
        if (this.isConnected()) try {
            this.con.createStatement().executeQuery(cmd);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void createTable() {
        this.executeUpdate("CREATE TABLE IF NOT EXISTS BannedPlayers (BannedUUID VARCHAR(100), SenderUUID VARCHAR(100), Reason VARCHAR(100), UnbanTime BIGINT(1))");
        this.executeUpdate("CREATE TABLE IF NOT EXISTS MutedPlayers (BannedUUID VARCHAR(100), SenderUUID VARCHAR(100), Reason VARCHAR(100), Shadow INT(1), UnbanTime BIGINT(1))");
        this.executeUpdate("CREATE TABLE IF NOT EXISTS Economy (UUID VARCHAR(100), Server VARCHAR(100), Balance DECIMAL(30, 2))");
    }

    public void execute(String cmd) {
        if (this.isConnected()) try {
            this.con.createStatement().execute(cmd);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public ResultSet getResult(String qry) {
        if (this.isConnected()) try {
            return this.con.createStatement().executeQuery(qry);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
