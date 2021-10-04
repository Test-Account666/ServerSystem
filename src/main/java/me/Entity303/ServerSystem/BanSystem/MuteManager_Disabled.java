package me.Entity303.ServerSystem.BanSystem;

import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MuteManager_Disabled extends ManagerMute {

    public MuteManager_Disabled(File muteFile, String dateFormat, ss plugin) {
        super(muteFile, dateFormat, plugin);
    }

    @Override
    public Mute getMute(OfflinePlayer player) {
        return null;
    }

    @Override
    public void removeMute(UUID mutedUUID) {

    }

    @Override
    public Mute addMute(UUID mutedUUID, String senderUUID, String reason, Long howLong, TimeUnit timeUnit) {
        return new Mute(mutedUUID.toString(), senderUUID, howLong, "", reason);
    }

    @Override
    public Mute addMute(UUID mutedUUID, String senderUUID, String reason, boolean shadow, Long howLong, TimeUnit timeUnit) {
        return new Mute(mutedUUID.toString(), senderUUID, howLong, "", reason, shadow);
    }

    @Override
    public String getDateFormat() {
        return "";
    }

    @Override
    public String convertLongToDate(Long l) {
        return "";
    }

    @Override
    public boolean isMuted(OfflinePlayer player) {
        return false;
    }

    @Override
    public List<String> getMutedPlayerNames() {
        return new ArrayList<>();
    }

    @Override
    public void close() {
    }
}
