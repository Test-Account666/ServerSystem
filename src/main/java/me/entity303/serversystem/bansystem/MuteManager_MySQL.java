package me.entity303.serversystem.bansystem;

import me.entity303.serversystem.main.ServerSystem;
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
        if (!this.checkPlayerInMYSQL(player.getUniqueId().toString())) return null;
        String SenderUUID = this.getUUIDSender(player.getUniqueId().toString());
        String mutedUUID = player.getUniqueId().toString();
        Long unmuteTime = this.getUnmuteTime(player.getUniqueId().toString());
        String unbanDate = this.convertLongToDate(this.getUnmuteTime(player.getUniqueId().toString()));
        String reason = this.getReason(player.getUniqueId().toString());
        boolean shadow = this.shadowBan(player.getUniqueId().toString());

        if (shadow) return new Mute(SenderUUID, mutedUUID, unmuteTime, unbanDate, reason, true);
        else
            return new Mute(SenderUUID, mutedUUID, unmuteTime, unbanDate, reason);
    }

    @Override
    public void removeMute(UUID mutedUUID) {
        if (!this.checkPlayerInMYSQL(mutedUUID.toString())) return;

        this.plugin.getMySQL().executeUpdate("DELETE FROM MutedPlayers WHERE BannedUUID='" + mutedUUID + "'");
    }

    @Override
    public List<String> getMutedPlayerNames() {
        List<String> playerNames = new ArrayList<>();

        ResultSet resultSet = this.plugin.getMySQL().getResult("SELECT BannedUUID from MutedPlayers");

        while (true) try {
            if (resultSet == null) break;
            if (!resultSet.next()) break;
            String uuid = resultSet.getString("BannedUUID");

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
        ResultSet rs = this.plugin.getMySQL().getResult("SELECT * FROM MutedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (rs.next()) return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private boolean shadowBan(String uuid) {
        ResultSet rs = this.plugin.getMySQL().getResult("SELECT * FROM MutedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (rs.next()) return rs.getInt("Shadow") == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getReason(String uuid) {
        ResultSet rs = this.plugin.getMySQL().getResult("SELECT * FROM MutedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (rs.next()) return rs.getString("Reason");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private Long getUnmuteTime(String uuid) {
        ResultSet rs = this.plugin.getMySQL().getResult("SELECT * FROM MutedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (rs.next()) return rs.getLong("UnbanTime");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getUUIDSender(String uuid) {
        ResultSet rs = this.plugin.getMySQL().getResult("SELECT * FROM MutedPlayers WHERE BannedUUID='" + uuid + "'");
        try {
            while (rs.next()) return rs.getString("SenderUUID");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Mute addMute(UUID mutedUUID, String senderUUID, String reason, Long howLong, TimeUnit timeUnit) {
        if (this.isMuted(Bukkit.getOfflinePlayer(mutedUUID))) this.removeMute(mutedUUID);
        long unbanTime = System.currentTimeMillis() + (howLong * timeUnit.getValue());
        if (howLong < 1) unbanTime = -1L;

        this.plugin.getMySQL().executeUpdate("INSERT INTO `MutedPlayers`" +
                " (BannedUUID, SenderUUID, Reason, Shadow, UnbanTime) VALUES ('"
                + mutedUUID + "','" + senderUUID + "',"
                + "'" + reason + "'" + "," + 0 + ",'" + unbanTime + "')");

        return new Mute(mutedUUID.toString(), senderUUID, unbanTime, this.convertLongToDate(unbanTime), reason);
    }

    @Override
    public Mute addMute(UUID mutedUUID, String senderUUID, String reason, boolean shadow, Long howLong, TimeUnit timeUnit) {
        if (this.isMuted(Bukkit.getOfflinePlayer(mutedUUID))) this.removeMute(mutedUUID);
        long unbanTime = System.currentTimeMillis() + (howLong * timeUnit.getValue());
        if (howLong < 1) unbanTime = -1L;
        this.plugin.getMySQL().executeUpdate("INSERT INTO `MutedPlayers`" +
                " (BannedUUID, SenderUUID, Reason, Shadow, UnbanTime) VALUES ('"
                + mutedUUID + "','" + senderUUID + "',"
                + "'No reason when shadow ban'" + "," + 1 + ",'" + unbanTime + "')");
        return new Mute(mutedUUID.toString(), senderUUID, unbanTime, this.convertLongToDate(unbanTime), reason, shadow);
    }

    @Override
    public String getDateFormat() {
        return this.dateFormat;
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
    public boolean isMuted(OfflinePlayer player) {
        return this.checkPlayerInMYSQL(player.getUniqueId().toString());
    }
}
