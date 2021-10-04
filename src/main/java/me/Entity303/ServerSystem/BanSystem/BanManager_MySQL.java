package me.Entity303.ServerSystem.BanSystem;

import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class BanManager_MySQL extends ManagerBan {
    private final ss plugin;
    private String dateFormat;

    public BanManager_MySQL(String dateFormat, ss plugin) {
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

        ResultSet resultSet = this.plugin.getMySQL().getResult("SELECT BannedUUID from BannedPlayers");

        while (true) try {
            if (resultSet == null) break;
            if (!resultSet.next()) break;
            String uuid = resultSet.getString("BannedUUID");

            if (this.checkPlayerInMYSQL(uuid)) playerNames.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
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
        } else return null;

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
        if (l < 1) return this.getBanSystem("PermaBan");
        Calendar c = Calendar.getInstance();

        c.setTimeInMillis(l);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:kk:mm:ss");
        String[] dates = dateFormat.format(c.getTime()).split(":");

        String year = dates[0];
        String month = dates[1];
        String day = dates[2];
        String hour = dates[3];
        String minute = dates[4];
        String second = dates[5];

        if (month.chars().count() == 1) month = "0" + month;

        if (day.chars().count() == 1) day = "0" + day;

        if (hour.chars().count() == 1) hour = "0" + hour;

        if (minute.chars().count() == 1) minute = "0" + minute;

        if (second.chars().count() == 1) second = "0" + second;

        return this.getDateFormat().
                replace("<YEAR>", year).
                replace("<MONTH>", month).
                replace("<DAY>", day).
                replace("<HOUR>", hour).
                replace("<MINUTE>", minute).
                replace("<SECOND>", second);
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
        ResultSet rs = this.plugin.getMySQL().getResult("SELECT * FROM BannedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (rs.next()) return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private String getReason(String uuid) {
        ResultSet rs = this.plugin.getMySQL().getResult("SELECT * FROM BannedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (rs.next()) return rs.getString("Reason");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private Long getUnbanTime(String uuid) {
        ResultSet rs = this.plugin.getMySQL().getResult("SELECT * FROM BannedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (rs.next()) return rs.getLong("UnbanTime");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getUUIDSender(String uuid) {
        ResultSet rs = this.plugin.getMySQL().getResult("SELECT * FROM BannedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (rs.next()) return rs.getString("SenderUUID");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Ban createBan(UUID banned, String senderUUID, String reason, Long howLong, TimeUnit timeUnit) {
        if (this.isBanned(banned)) this.unBan(banned);
        long unbanTime = System.currentTimeMillis() + (howLong * timeUnit.getValue());
        if (howLong < 1) unbanTime = -1L;
        this.plugin.getMySQL().executeUpdate("INSERT INTO `BannedPlayers` (BannedUUID, SenderUUID, Reason, UnbanTime) VALUES ('" + banned + "','" + senderUUID + "','" + reason + "','" + unbanTime + "')");
        return new Ban(banned, reason, senderUUID, unbanTime, this.convertLongToDate(unbanTime));
    }

    @Override
    public void unBan(UUID banned) {
        this.plugin.getMySQL().executeUpdate("DELETE FROM BannedPlayers WHERE BannedUUID='" + banned.toString() + "'");
    }

    @Override
    public void close() {
        this.plugin.getMySQL().close();
    }
}
