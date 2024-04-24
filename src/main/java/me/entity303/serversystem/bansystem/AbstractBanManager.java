package me.entity303.serversystem.bansystem;

import me.entity303.serversystem.bansystem.moderation.BanModeration;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.UUID;

public abstract class AbstractBanManager extends AbstractModeration {

    public AbstractBanManager(String dateFormat, ServerSystem plugin) {
        super(plugin, dateFormat);
    }

    public abstract boolean IsBanned(UUID uuid);

    public abstract List<String> GetBannedPlayerNames();

    public abstract BanModeration GetBanByUUID(UUID uuid);

    public abstract BanModeration GetBanByPlayer(OfflinePlayer player);

    public abstract BanModeration CreateBan(UUID banned, String senderUUID, String reason, Long howLong, TimeUnit timeUnit);

    public abstract void UnBan(UUID banned);
}
