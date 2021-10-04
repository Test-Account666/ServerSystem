package me.Entity303.ServerSystem.BanSystem;

import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.List;
import java.util.UUID;

public abstract class ManagerBan {
    protected final ss plugin;

    public ManagerBan(File banFile, String dateFormat, ss plugin) {
        this.plugin = plugin;
    }

    public abstract boolean isBanned(UUID uuid);

    public abstract List<String> getBannedPlayerNames();

    public abstract Ban getBanByUUID(UUID uuid);

    public abstract Ban getBanByUUIDString(String uuid);

    public abstract Ban getBanByPlayer(OfflinePlayer player);

    public abstract String convertLongToDate(Long l);

    public abstract String getDateFormat();

    public abstract void setDateFormat(String dateFormat);

    public abstract Ban createBan(UUID banned, String senderUUID, String reason, Long howLong, TimeUnit timeUnit);

    public abstract void unBan(UUID banned);

    public abstract void close();

    protected String getBanSystem(String action) {
        return this.plugin.getMessages().getCfg().getString("Messages.Misc.BanSystem." + action);
    }
}
