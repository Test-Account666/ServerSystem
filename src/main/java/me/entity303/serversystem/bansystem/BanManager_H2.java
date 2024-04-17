package me.entity303.serversystem.bansystem;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class BanManager_H2 extends ManagerBan {
    private final ServerSystem plugin;
    private Connection connection;
    private String dateFormat;

    public BanManager_H2(String dateFormat, ServerSystem plugin) {
        super(new File("file", "file"), dateFormat, plugin);
        this.dateFormat = dateFormat;
        this.plugin = plugin;
        this.open();
        try {
            this.connection.createStatement()
                           .executeUpdate(
                                   "CREATE TABLE IF NOT EXISTS BannedPlayers (BannedUUID VARCHAR(100), SenderUUID VARCHAR(100), Reason VARCHAR(100), UnbanTime BIGINT)");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean open() {
        if (this.initialize())
            try {
                this.connection = DriverManager.getConnection("jdbc:h2:file:" + new File("plugins//ServerSystem", "bans.h2").getAbsolutePath());
                return true;
            } catch (SQLException var2) {
                this.plugin.error("Could not establish an H2 connection, SQLException: " + var2.getMessage());
                return false;
            }
        else
            return false;
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

    @Override
    public boolean isBanned(UUID uuid) {
        return this.checkPlayerInH2(uuid.toString());
    }

    @Override
    public List<String> getBannedPlayerNames() {
        List<String> playerNames = new ArrayList<>();

        ResultSet resultSet = null;
        try {
            resultSet = this.connection.createStatement().executeQuery("SELECT BannedUUID from BannedPlayers");
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

                if (this.checkPlayerInH2(uuid))
                    playerNames.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
            } catch (SQLException e) {
                e.printStackTrace();
            }

        return playerNames;
    }

    @Override
    public Ban getBanByUUID(UUID uuid) {
        UUID bannedUUID;
        String senderUUID;
        String reason;
        long unbanTime;
        String unbanDate;

        if (this.checkPlayerInH2(uuid.toString())) {
            bannedUUID = uuid;
            reason = this.getReason(uuid.toString());
            unbanTime = this.getUnbanTime(uuid.toString());
            senderUUID = this.getUUIDSender(uuid.toString());
            unbanDate = this.convertLongToDate(unbanTime);
        } else
            return null;

        return new Ban(bannedUUID, reason, senderUUID, unbanTime, unbanDate);
    }

    @Override
    public Ban getBanByUUIDString(String uuid) {
        return this.getBanByUUID(UUID.fromString(uuid));
    }

    @Override
    public Ban getBanByPlayer(OfflinePlayer player) {
        return this.getBanByUUID(player.getUniqueId());
    }

    @Override
    public String convertLongToDate(Long l) {
        if (l < 1)
            return this.getBanSystem("PermaBan");
        var c = Calendar.getInstance();

        c.setTimeInMillis(l);

        var dateFormat = new SimpleDateFormat("yyyy:MM:dd:kk:mm:ss");
        var dates = dateFormat.format(c.getTime()).split(":");

        var year = dates[0];
        var month = dates[1];
        var day = dates[2];
        var hour = dates[3];
        var minute = dates[4];
        var second = dates[5];

        if (month.chars().count() == 1)
            month = "0" + month;

        if (day.chars().count() == 1)
            day = "0" + day;

        if (hour.chars().count() == 1)
            hour = "0" + hour;

        if (minute.chars().count() == 1)
            minute = "0" + minute;

        if (second.chars().count() == 1)
            second = "0" + second;

        return this.getDateFormat()
                   .replace("<YEAR>", year)
                   .replace("<MONTH>", month)
                   .replace("<DAY>", day)
                   .replace("<HOUR>", hour)
                   .replace("<MINUTE>", minute)
                   .replace("<SECOND>", second);
    }

    @Override
    public String getDateFormat() {
        return this.dateFormat;
    }

    @Override
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    private boolean checkPlayerInH2(String uuid) {
        ResultSet rs = null;
        try {
            rs = this.connection.createStatement().executeQuery("SELECT * FROM BannedPlayers WHERE BannedUUID='" + uuid + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            while (rs.next())
                return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private String getReason(String uuid) {
        ResultSet rs = null;
        try {
            rs = this.connection.createStatement().executeQuery("SELECT * FROM BannedPlayers WHERE BannedUUID='" + uuid + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            while (rs.next())
                return rs.getString("Reason");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private Long getUnbanTime(String uuid) {
        ResultSet rs = null;
        try {
            rs = this.connection.createStatement().executeQuery("SELECT * FROM BannedPlayers WHERE BannedUUID='" + uuid + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            while (rs.next())
                return rs.getLong("UnbanTime");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getUUIDSender(String uuid) {
        ResultSet rs = null;
        try {
            rs = this.connection.createStatement().executeQuery("SELECT * FROM BannedPlayers WHERE BannedUUID='" + uuid + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            while (rs.next())
                return rs.getString("SenderUUID");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Ban createBan(UUID banned, String senderUUID, String reason, Long howLong, TimeUnit timeUnit) {
        if (this.isBanned(banned))
            this.unBan(banned);
        var unbanTime = System.currentTimeMillis() + (howLong * timeUnit.getValue());
        if (howLong < 1)
            unbanTime = -1L;
        try {
            var query = "INSERT INTO BANNEDPLAYERS (BannedUUID, SenderUUID, Reason, UnbanTime) VALUES (?, ?, ?, ?)";
            var preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setString(1, String.valueOf(banned));
            preparedStatement.setString(2, senderUUID);
            preparedStatement.setString(3, reason);
            preparedStatement.setLong(4, unbanTime);
            preparedStatement.executeUpdate();
            preparedStatement.closeOnCompletion();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new Ban(banned, reason, senderUUID, unbanTime, this.convertLongToDate(unbanTime));
    }

    @Override
    public void unBan(UUID banned) {
        try {
            var statement = this.connection.createStatement();
            var query = "DELETE FROM BannedPlayers WHERE BannedUUID=?";
            var preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setString(1, banned.toString());
            preparedStatement.executeUpdate();
            preparedStatement.closeOnCompletion();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            this.connection.close();
        } catch (Exception ignored) {
        }
    }
}
