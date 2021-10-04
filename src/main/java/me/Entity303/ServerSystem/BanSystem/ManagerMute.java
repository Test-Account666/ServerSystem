package me.Entity303.ServerSystem.BanSystem;

import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.List;
import java.util.UUID;

public abstract class ManagerMute {
    protected final ss plugin;

    public ManagerMute(File muteFile, String dateFormat, ss plugin) {
        this.plugin = plugin;
    }

    public abstract Mute getMute(OfflinePlayer player);

    public abstract void removeMute(UUID mutedUUID);

    public abstract Mute addMute(UUID mutedUUID, String senderUUID, String reason, Long howLong, TimeUnit timeUnit);

    public abstract Mute addMute(UUID mutedUUID, String senderUUID, String reason, boolean shadow, Long howLong, TimeUnit timeUnit);

    public abstract String getDateFormat();

    public abstract String convertLongToDate(Long l);

    public abstract boolean isMuted(OfflinePlayer player);

    public abstract List<String> getMutedPlayerNames();

    public abstract void close();

    protected String getBanSystem(String action) {
        return this.plugin.getMessages().getCfg().getString("Messages.Misc.BanSystem." + action);
    }
}
