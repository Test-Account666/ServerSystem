package me.entity303.serversystem.bansystem;

import me.entity303.serversystem.bansystem.moderation.MuteModeration;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.UUID;

public abstract class AbstractMuteManager extends AbstractModeration {

    public AbstractMuteManager(String dateFormat, ServerSystem plugin) {
        super(plugin, dateFormat);
    }

    public abstract MuteModeration GetMute(OfflinePlayer player);

    public abstract void RemoveMute(UUID mutedUUID);

    public abstract MuteModeration CreateMute(UUID mutedUUID, String senderUUID, String reason, Long howLong, TimeUnit timeUnit);

    public abstract MuteModeration CreateMute(UUID mutedUUID, String senderUUID, String reason, boolean shadow, Long howLong, TimeUnit timeUnit);

    public abstract void CreateMute(MuteModeration mute);

    public abstract boolean IsMuted(OfflinePlayer player);

    public abstract List<String> GetMutedPlayerNames();
}
