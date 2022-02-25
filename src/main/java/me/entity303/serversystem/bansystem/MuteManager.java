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
import java.util.stream.Collectors;

public class MuteManager extends ManagerMute {
    private final File muteFile;
    private final FileConfiguration cfg;
    private final String dateFormat;

    public MuteManager(File muteFile, String dateFormat, ServerSystem plugin) {
        super(muteFile, dateFormat, plugin);
        this.muteFile = muteFile;
        this.cfg = YamlConfiguration.loadConfiguration(muteFile);
        this.dateFormat = dateFormat;
    }

    @Override
    public void removeMute(UUID mutedUUID) {
        if (!this.isMuted(Bukkit.getOfflinePlayer(mutedUUID))) return;
        this.cfg.set("Muted." + mutedUUID, null);

        this.save();

        try {
            Mute nullMute = new Mute(null, null, null, null, null);
        } catch (Exception ignored) {
        }

        this.reload();
    }

    @Override
    public Mute addMute(UUID mutedUUID, String senderUUID, String reason, Long howLong, TimeUnit timeUnit) {
        if (this.isMuted(Bukkit.getOfflinePlayer(mutedUUID))) this.removeMute(mutedUUID);
        long unbanTime = System.currentTimeMillis() + (howLong * timeUnit.getValue());
        if (howLong < 1) unbanTime = -1L;
        this.cfg.set("Muted." + mutedUUID + ".Sender", senderUUID);
        this.cfg.set("Muted." + mutedUUID + ".Reason", reason);
        this.cfg.set("Muted." + mutedUUID + ".Shadow", false);
        this.cfg.set("Muted." + mutedUUID + ".UnmuteTime", Long.toString(unbanTime));

        this.save();

        Mute mute = new Mute(mutedUUID.toString(), senderUUID, unbanTime, this.convertLongToDate(unbanTime), reason);

        this.reload();
        return mute;
    }

    @Override
    public Mute addMute(UUID mutedUUID, String senderUUID, String reason, boolean shadow, Long howLong, TimeUnit timeUnit) {
        if (this.isMuted(Bukkit.getOfflinePlayer(mutedUUID))) this.removeMute(mutedUUID);
        long unbanTime = System.currentTimeMillis() + (howLong * timeUnit.getValue());
        if (howLong < 1) unbanTime = -1L;
        this.cfg.set("Muted." + mutedUUID + ".Sender", senderUUID);
        this.cfg.set("Muted." + mutedUUID + ".Reason", "No reason when shadow");
        this.cfg.set("Muted." + mutedUUID + ".Shadow", shadow);
        this.cfg.set("Muted." + mutedUUID + ".UnmuteTime", Long.toString(unbanTime));

        this.save();

        Mute mute = new Mute(mutedUUID.toString(), senderUUID, unbanTime, this.convertLongToDate(unbanTime), reason, shadow);

        this.reload();
        return mute;
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
        if (!this.muteFile.exists()) return false;
        return this.getMute(player) != null;
    }

    @Override
    public List<String> getMutedPlayerNames() {
        if (this.cfg.getConfigurationSection("Muted") == null) return new ArrayList<>();
        this.cfg.getConfigurationSection("Muted").getKeys(false);
        if (this.cfg.getConfigurationSection("Muted").getKeys(false).size() <= 0) return new ArrayList<>();

        try {
            return this.cfg.getConfigurationSection("Muted").getKeys(false).stream().map(uuid -> Bukkit.getOfflinePlayer(UUID.fromString(uuid))).map(OfflinePlayer::getName).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void close() {

    }

    @Override
    public Mute getMute(OfflinePlayer player) {
        return this.getMute(player.getUniqueId());
    }

    public Mute getMute(UUID uuid) {
        if (!this.muteFile.exists()) return null;
        try {
            Mute mute;
            String uuidSender;
            String uuidMuted;
            long unmute_time;
            String unmute_date;
            String reason;
            boolean shadow;

            String str = "Muted." + uuid.toString() + ".";
            uuidSender = this.cfg.getString(str + "Sender");
            uuidMuted = uuid.toString();
            try {
                unmute_time = Long.parseLong(this.cfg.getString(str + "UnmuteTime"));
            } catch (NumberFormatException ignored) {
                return null;
            }
            unmute_date = this.convertLongToDate(unmute_time);
            reason = this.cfg.getString(str + "Reason");
            shadow = this.cfg.getBoolean(str + "Shadow");

            if (uuidSender == null) return null;
            if (uuidMuted == null) return null;
            if (unmute_date == null) return null;
            if (reason == null) return null;

            if (shadow) mute = new Mute(uuidSender, uuidMuted, unmute_time, unmute_date, reason, true);
            else
                mute = new Mute(uuidSender, uuidMuted, unmute_time, unmute_date, reason);

            return mute;
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    public void reload() {
        try {
            this.cfg.load(this.muteFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            this.cfg.save(this.muteFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
