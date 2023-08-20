package me.entity303.serversystem.databasemanager;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LegacyWarpManager {
    private final ServerSystem plugin;
    private Connection connection;

    public LegacyWarpManager(ServerSystem plugin) {
        this.plugin = plugin;
        if (!this.open())
            return;

        try {
            this.connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS warps (\n" +
                    "  Name VARCHAR(100),\n" +
                    "  World VARCHAR(100),\n" +
                    "  X DECIMAL(100, 30),\n" +
                    "  Y DECIMAL(100, 30),\n" +
                    "  Z DECIMAL(100, 30),\n" +
                    "  Yaw DECIMAL(100, 30),\n" +
                    "  Pitch DECIMAL(100, 30)\n" +
                    ")");
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
        if (!new File("plugins//ServerSystem", "warps.h2.mv.db").exists())
            return false;

        if (this.initialize()) try {
            this.connection = DriverManager.getConnection("jdbc:h2:file:" + new File("plugins//ServerSystem", "warps.h2").getAbsolutePath());
            return true;
        } catch (SQLException var2) {
            this.plugin.error("Could not establish an H2 connection, SQLException: " + var2.getMessage());
            return false;
        }
        else return false;
    }

    public void addWarp(String name, Location location) {
        if (this.doesWarpExist(name)) return;
        name = name.toLowerCase();
        try {
            String x = this.trimString(String.valueOf(location.getX()));
            String y = this.trimString(String.valueOf(location.getY()));
            String z = this.trimString(String.valueOf(location.getZ()));
            String yaw = this.trimString(String.valueOf(location.getYaw()));
            String pitch = this.trimString(String.valueOf(location.getPitch()));
            this.connection.createStatement().executeUpdate("INSERT INTO warps (Name, World, X, Y, Z, Yaw, Pitch) VALUES ('" + name + "','" + location.getWorld().getName() + "','" + x + "','" + y + "','" + z + "','" + yaw + "','" + pitch + "')");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private String trimString(String string) {
        return string.split("\\.")[1].length() > 100 ? string.split("\\.")[0] + "." + string.split("\\.")[1].substring(100) : string;
    }

    public Location getWarp(String name) {
        name = name.toLowerCase();
        Location location = null;
        ResultSet resultSet = null;
        try {
            resultSet = this.connection.createStatement().executeQuery("SELECT * FROM warps WHERE Name='" + name + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        while (true) try {
            if (!resultSet.next()) break;
            double x = resultSet.getDouble("X");
            double y = resultSet.getDouble("Y");
            double z = resultSet.getDouble("Z");
            double yaw = resultSet.getDouble("Yaw");
            double pitch = resultSet.getDouble("Pitch");
            String worldName = resultSet.getString("World");
            location = new Location(Bukkit.getWorld(worldName), x, y, z, (float) yaw, (float) pitch);
            break;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return location;
    }

    public void deleteWarp(String name) {
        name = name.toLowerCase();
        try {
            this.connection.createStatement().executeUpdate("DELETE FROM warps WHERE Name='" + name + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean doesWarpExist(String name) {
        name = name.toLowerCase();
        ResultSet rs = null;
        try {
            rs = this.connection.createStatement().executeQuery("SELECT * FROM warps WHERE Name='" + name + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            while (rs.next()) return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public List<String> getWarps() {
        List<String> warps = new ArrayList<>();

        ResultSet resultSet = null;

        try {
            resultSet = this.connection.createStatement().executeQuery("SELECT Name FROM warps");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        if (resultSet != null) while (true) {
            try {
                if (!resultSet.next()) break;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            try {
                if (warps.contains(resultSet.getString("Name"))) continue;
                warps.add(resultSet.getString("Name"));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return warps;
    }

    public void close() {
        try {
            this.connection.close();
        } catch (Exception ignored) {
        }
    }
}
