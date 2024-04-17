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

public class MuteManager_MySQL extends ManagerMute {
    private final ServerSystem plugin;
    private final String dateFormat;

    public MuteManager_MySQL(ServerSystem plugin, String dateFormat) {
        super(new File("file"), dateFormat, plugin);
        this.plugin = plugin;
        this.dateFormat = dateFormat;
    }

    @Override
    public Mute getMute(OfflinePlayer player) {
        if (!this.checkPlayerInMYSQL(player.getUniqueId().toString()))
            return null;
        var SenderUUID = this.getUUIDSender(player.getUniqueId().toString());
        var mutedUUID = player.getUniqueId().toString();
        var unmuteTime = this.getUnmuteTime(player.getUniqueId().toString());
        var unbanDate = this.convertLongToDate(this.getUnmuteTime(player.getUniqueId().toString()));
        var reason = this.getReason(player.getUniqueId().toString());
        var shadow = this.shadowBan(player.getUniqueId().toString());

        if (shadow)
            return new Mute(SenderUUID, mutedUUID, unmuteTime, unbanDate, reason, true);
        else
            return new Mute(SenderUUID, mutedUUID, unmuteTime, unbanDate, reason);
    }

    @Override
    public void removeMute(UUID mutedUUID) {
        if (!this.checkPlayerInMYSQL(mutedUUID.toString()))
            return;

        this.plugin.getMySQL().executeUpdate("DELETE FROM MutedPlayers WHERE BannedUUID='" + mutedUUID + "'");
    }

    @Override
    public Mute addMute(UUID mutedUUID, String senderUUID, String reason, Long howLong, TimeUnit timeUnit) {
        if (this.isMuted(Bukkit.getOfflinePlayer(mutedUUID)))
            this.removeMute(mutedUUID);
        var unbanTime = System.currentTimeMillis() + (howLong * timeUnit.getValue());
        if (howLong < 1)
            unbanTime = -1L;

        try {
            var query = "INSERT INTO `MutedPlayers` (BannedUUID, SenderUUID, Reason, Shadow, UnbanTime) VALUES (?, ?, ?, ?, ?)";
            var preparedStatement = this.plugin.getMySQL().prepareStatement(query);
            preparedStatement.setString(1, String.valueOf(mutedUUID));
            preparedStatement.setString(2, senderUUID);
            preparedStatement.setString(3, reason);
            preparedStatement.setInt(4, 0);
            preparedStatement.setLong(5, unbanTime);
            preparedStatement.executeUpdate();
            preparedStatement.closeOnCompletion();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


        return new Mute(mutedUUID.toString(), senderUUID, unbanTime, this.convertLongToDate(unbanTime), reason);
    }

    @Override
    public Mute addMute(UUID mutedUUID, String senderUUID, String reason, boolean shadow, Long howLong, TimeUnit timeUnit) {
        if (this.isMuted(Bukkit.getOfflinePlayer(mutedUUID)))
            this.removeMute(mutedUUID);
        var unbanTime = System.currentTimeMillis() + (howLong * timeUnit.getValue());
        if (howLong < 1)
            unbanTime = -1L;
        try {
            var query = "INSERT INTO `MutedPlayers` (BannedUUID, SenderUUID, Reason, Shadow, UnbanTime) VALUES (?, ?, ?, ?, ?)";
            var preparedStatement = this.plugin.getMySQL().prepareStatement(query);
            preparedStatement.setString(1, String.valueOf(mutedUUID));
            preparedStatement.setString(2, senderUUID);
            preparedStatement.setString(3, "No reason when shadow ban");
            preparedStatement.setInt(4, 1);
            preparedStatement.setLong(5, unbanTime);
            preparedStatement.executeUpdate();
            preparedStatement.closeOnCompletion();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return new Mute(mutedUUID.toString(), senderUUID, unbanTime, this.convertLongToDate(unbanTime), reason, shadow);
    }

    @Override
    public Mute addMute(Mute mute) {
        var mutedUUID = UUID.fromString(mute.getMUTED_UUID());
        var senderUUID = mute.getSENDER_UUID();
        var reason = mute.getREASON();

        if (this.isMuted(Bukkit.getOfflinePlayer(mutedUUID)))
            this.removeMute(mutedUUID);
        long unbanTime = mute.getUNMUTE_TIME();
        if (unbanTime < 1)
            unbanTime = -1L;

        try {
            var query = "INSERT INTO `MutedPlayers` (BannedUUID, SenderUUID, Reason, Shadow, UnbanTime) VALUES (?, ?, ?, ?, ?)";
            var preparedStatement = this.plugin.getMySQL().prepareStatement(query);
            preparedStatement.setString(1, String.valueOf(mutedUUID));
            preparedStatement.setString(2, senderUUID);
            preparedStatement.setString(3, reason);
            preparedStatement.setInt(4, 0);
            preparedStatement.setLong(5, unbanTime);
            preparedStatement.executeUpdate();
            preparedStatement.closeOnCompletion();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return mute;
    }

    @Override
    public String getDateFormat() {
        return this.dateFormat;
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
    public boolean isMuted(OfflinePlayer player) {
        return this.checkPlayerInMYSQL(player.getUniqueId().toString());
    }

    @Override
    public List<String> getMutedPlayerNames() {
        List<String> playerNames = new ArrayList<>();

        var resultSet = this.plugin.getMySQL().getResult("SELECT BannedUUID from MutedPlayers");

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
    public void close() {
        this.plugin.getMySQL().close();
    }

    private boolean checkPlayerInMYSQL(String uuid) {
        var rs = this.plugin.getMySQL().getResult("SELECT * FROM MutedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (rs.next())
                return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private String getUUIDSender(String uuid) {
        var rs = this.plugin.getMySQL().getResult("SELECT * FROM MutedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (rs.next())
                return rs.getString("SenderUUID");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Long getUnmuteTime(String uuid) {
        var rs = this.plugin.getMySQL().getResult("SELECT * FROM MutedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (rs.next())
                return rs.getLong("UnbanTime");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getReason(String uuid) {
        var rs = this.plugin.getMySQL().getResult("SELECT * FROM MutedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (rs.next())
                return rs.getString("Reason");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean shadowBan(String uuid) {
        var rs = this.plugin.getMySQL().getResult("SELECT * FROM MutedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (rs.next())
                return rs.getInt("Shadow") == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
