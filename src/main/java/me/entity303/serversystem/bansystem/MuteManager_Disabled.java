package me.entity303.serversystem.bansystem;

import me.entity303.serversystem.bansystem.moderation.MuteModeration;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MuteManager_Disabled extends AbstractMuteManager {

    public MuteManager_Disabled(String dateFormat, ServerSystem plugin) {
        super(dateFormat, plugin);
    }

    @Override
    public MuteModeration GetMute(OfflinePlayer player) {
        return null;
    }

    @Override
    public void RemoveMute(UUID mutedUUID) {

    }

    @Override
    public MuteModeration CreateMute(UUID mutedUUID, String senderUUID, String reason, Long howLong, TimeUnit timeUnit) {
        return new MuteModeration(mutedUUID, senderUUID, howLong, "", reason, false);
    }

    @Override
    public MuteModeration CreateMute(UUID mutedUUID, String senderUUID, String reason, boolean shadow, Long howLong, TimeUnit timeUnit) {
        return new MuteModeration(mutedUUID, senderUUID, howLong, "", reason, shadow);
    }

    @Override
    public void CreateMute(MuteModeration mute) {
    }

    @Override
    public boolean IsMuted(OfflinePlayer player) {
        return false;
    }

    @Override
    public List<String> GetMutedPlayerNames() {
        return new ArrayList<>();
    }

    @Override
    public String ConvertLongToDate(Long longDate) {
        return "";
    }

    @Override
    public void Close() {
    }
}
