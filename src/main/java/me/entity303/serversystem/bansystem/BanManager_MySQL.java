package me.entity303.serversystem.bansystem;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class BanManager_MySQL extends ManagerBan {
    private final ServerSystem plugin;
    private String dateFormat;

    public BanManager_MySQL(String dateFormat, ServerSystem plugin) {
        super(new File("file", "file"), dateFormat, plugin);
        this.dateFormat = dateFormat;

        this.plugin = plugin;
    }

    @Override
    public boolean isBanned(UUID uuid) {
        return this.checkPlayerInMYSQL(uuid.toString());
    }

    @Override
    public List<String> getBannedPlayerNames() {
        List<String> playerNames = new ArrayList<>();

        var resultSet = this.plugin.getMySQL().getResult("SELECT BannedUUID from BannedPlayers");

        while (true)
            try {
                if (resultSet == null)
                    break;
                if (!resultSet.next())
                    break;
                var uuid = resultSet.getString("BannedUUID");

                if (this.checkPlayerInMYSQL(uuid))
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

        if (this.checkPlayerInMYSQL(uuid.toString())) {
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

    private boolean checkPlayerInMYSQL(String uuid) {
        var rs = this.plugin.getMySQL().getResult("SELECT * FROM BannedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (rs.next())
                return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private String getReason(String uuid) {
        var rs = this.plugin.getMySQL().getResult("SELECT * FROM BannedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (rs.next())
                return rs.getString("Reason");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private Long getUnbanTime(String uuid) {
        var rs = this.plugin.getMySQL().getResult("SELECT * FROM BannedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (rs.next())
                return rs.getLong("UnbanTime");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getUUIDSender(String uuid) {
        var rs = this.plugin.getMySQL().getResult("SELECT * FROM BannedPlayers WHERE BannedUUID='" + uuid + "'");
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
            var query = "INSERT INTO `BannedPlayers` (BannedUUID, SenderUUID, Reason, UnbanTime) VALUES (?, ?, ?, ?)";
            var preparedStatement = this.plugin.getMySQL().prepareStatement(query);
            preparedStatement.setString(1, banned.toString());
            preparedStatement.setString(2, senderUUID);
            preparedStatement.setString(3, reason);
            preparedStatement.setString(4, String.valueOf(unbanTime));
            preparedStatement.executeUpdate();
            preparedStatement.closeOnCompletion();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new Ban(banned, reason, senderUUID, unbanTime, this.convertLongToDate(unbanTime));
    }

    @Override
    public void unBan(UUID banned) {
        try {
            var query = "DELETE FROM BannedPlayers WHERE BannedUUID=?";
            var preparedStatement = this.plugin.getMySQL().prepareStatement(query);
            preparedStatement.setString(1, banned.toString());
            preparedStatement.executeUpdate();
            preparedStatement.closeOnCompletion();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        this.plugin.getMySQL().close();
    }
}
