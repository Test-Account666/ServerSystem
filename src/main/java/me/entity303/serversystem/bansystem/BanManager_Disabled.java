package me.entity303.serversystem.bansystem;

import me.entity303.serversystem.bansystem.moderation.BanModeration;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BanManager_Disabled extends AbstractBanManager {

    public BanManager_Disabled(File banFile, String dateFormat, ServerSystem plugin) {
        super(dateFormat, plugin);
    }

    @Override
    public boolean IsBanned(UUID uuid) {
        return false;
    }

    @Override
    public List<String> GetBannedPlayerNames() {
        return new ArrayList<>();
    }

    @Override
    public BanModeration GetBanByUUID(UUID uuid) {
        return null;
    }

    @Override
    public BanModeration GetBanByPlayer(OfflinePlayer player) {
        return null;
    }

    @Override
    public BanModeration CreateBan(UUID banned, String senderUUID, String reason, Long howLong, TimeUnit timeUnit) {
        return new BanModeration(banned, senderUUID, howLong, "", reason);
    }

    @Override
    public void UnBan(UUID banned) {
    }

    @Override
    public String ConvertLongToDate(Long longDate) {
        return "";
    }

    @Override
    protected String GetPermanentBanName() {
        return "";
    }

    @Override
    public void Close() {
    }
}
