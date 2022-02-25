package me.entity303.serversystem.bansystem;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class BanManager extends ManagerBan {
    private final File banFile;
    private final FileConfiguration cfg;
    private String dateFormat;

    public BanManager(File banFile, String dateFormat, ServerSystem plugin) {
        super(banFile, dateFormat, plugin);
        this.banFile = banFile;
        this.cfg = YamlConfiguration.loadConfiguration(banFile);
        this.dateFormat = dateFormat;
    }

    @Override
    public List<String> getBannedPlayerNames() {
        List<String> playerNameList = new ArrayList<>();
        if (this.cfg.getConfigurationSection("Banned") == null) return new ArrayList<>();
        this.cfg.getConfigurationSection("Banned").getKeys(false);

        if (this.cfg.getConfigurationSection("Banned").getKeys(false).size() <= 0) return new ArrayList<>();
        for (String uuid : this.cfg.getConfigurationSection("Banned").getKeys(false))
            playerNameList.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
        return playerNameList;
    }

    @Override
    public boolean isBanned(UUID uuid) {
        if (!this.banFile.exists()) return false;
        return this.getBanByUUID(uuid) != null;
    }

    @Override
    public Ban getBanByUUID(UUID uuid) {
        if (!this.banFile.exists()) return null;
        try {
            Ban ban;
            String uuidSender;
            String uuidBanned;
            long unban_time;
            String unban_date;
            String reason;

            String str = "Banned." + uuid.toString() + ".";
            uuidSender = this.cfg.getString(str + "Sender");
            uuidBanned = uuid.toString();
            try {
                unban_time = Long.parseLong(this.cfg.getString(str + "unbanTime"));
            } catch (NumberFormatException ignored) {
                return null;
            }
            unban_date = this.convertLongToDate(unban_time);
            reason = this.cfg.getString(str + "Reason");

            if (uuidSender == null) return null;
            if (uuidBanned == null) return null;
            if (unban_date == null) return null;
            if (reason == null) return null;
            ban = new Ban(uuid, reason, uuidSender, unban_time, unban_date);

            return ban;
        } catch (NullPointerException ignored) {
            return null;
        }
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

    public File getBanFile() {
        return this.banFile;
    }

    public FileConfiguration getCfg() {
        return this.cfg;
    }

    @Override
    public Ban createBan(UUID banned, String senderUUID, String reason, Long howLong, TimeUnit timeUnit) {
        if (this.isBanned(banned)) this.unBan(banned);
        long unbanTime = System.currentTimeMillis() + (howLong * timeUnit.getValue());
        if (howLong < 1) unbanTime = -1L;
        this.getCfg().set("Banned." + banned.toString() + ".Sender", senderUUID);
        this.getCfg().set("Banned." + banned + ".Reason", reason);
        this.getCfg().set("Banned." + banned + ".unbanTime", Long.toString(unbanTime));

        try {
            this.getCfg().save(this.banFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Ban ban = new Ban(banned, reason, senderUUID, unbanTime, this.convertLongToDate(unbanTime));

        try {
            this.getCfg().load(this.banFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        return ban;
    }

    @Override
    public void unBan(UUID banned) {
        Ban ban = this.getBanByUUID(banned);
        this.getCfg().set("Banned." + banned.toString() + ".Sender", null);
        this.getCfg().set("Banned." + banned + ".Reason", null);
        this.getCfg().set("Banned." + banned + ".unbanTime", null);
        this.getCfg().set("Banned." + banned, null);

        try {
            this.getCfg().save(this.banFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.getCfg().load(this.banFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {

    }
}
