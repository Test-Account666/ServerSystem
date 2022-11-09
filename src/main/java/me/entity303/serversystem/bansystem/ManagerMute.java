package me.entity303.serversystem.bansystem;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.List;
import java.util.UUID;

public abstract class ManagerMute {
    protected final ServerSystem plugin;

    public ManagerMute(File muteFile, String dateFormat, ServerSystem plugin) {
        this.plugin = plugin;
    }

    public abstract Mute getMute(OfflinePlayer player);

    public abstract void removeMute(UUID mutedUUID);

    public abstract Mute addMute(UUID mutedUUID, String senderUUID, String reason, Long howLong, TimeUnit timeUnit);

    public abstract Mute addMute(UUID mutedUUID, String senderUUID, String reason, boolean shadow, Long howLong, TimeUnit timeUnit);

    public abstract Mute addMute(Mute mute);

    public abstract String getDateFormat();

    public abstract String convertLongToDate(Long l);

    public abstract boolean isMuted(OfflinePlayer player);

    public abstract List<String> getMutedPlayerNames();

    public abstract void close();

    protected String getBanSystem(String action) {
        return this.plugin.getMessages().getCfg().getString("Messages.Misc.BanSystem." + action);
    }
}
