package me.entity303.serversystem.utils;

import me.entity303.serversystem.main.ServerSystem;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WantsTP {
    private final ServerSystem plugin;
    Connection connection;

    public WantsTP(ServerSystem plugin) {
        this.plugin = plugin;
        this.open();
        try {
            this.connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS wantsTP (UUID VARCHAR(100), wants VARCHAR(5))");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    protected boolean initialize() {
        try {
            Class.forName("org.h2.Driver");
            return true;
        } catch (ClassNotFoundException var2) {
            this.plugin.error("H2 driver class missing: " + var2.getMessage() + ".");
            return false;
        }
    }

    public boolean open() {
        if (this.initialize()) try {
            this.connection = DriverManager.getConnection("jdbc:h2:file:" + new File("plugins//ServerSystem", "wantstp.h2").getAbsolutePath());
            return true;
        } catch (SQLException var2) {
            this.plugin.error("Could not establish an H2 connection, SQLException: " + var2.getMessage());
            return false;
        }
        else return false;
    }

    public void update(String query) {
        try {
            this.connection.createStatement().executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ResultSet query(String query) {
        try {
            return this.connection.createStatement().executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            this.connection.close();
        } catch (SQLException ignored) {
        }
    }
}
