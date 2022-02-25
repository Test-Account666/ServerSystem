package me.entity303.serversystem.bansystem;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BanManager_Disabled extends ManagerBan {

    public BanManager_Disabled(File banFile, String dateFormat, ServerSystem plugin) {
        super(banFile, dateFormat, plugin);
    }

    @Override
    public List<String> getBannedPlayerNames() {
        return new ArrayList<>();
    }

    @Override
    public boolean isBanned(UUID uuid) {
        return false;
    }

    @Override
    public Ban getBanByUUID(UUID uuid) {
        return null;
    }

    @Override
    public Ban getBanByUUIDString(String uuid) {
        return null;
    }

    @Override
    public Ban getBanByPlayer(OfflinePlayer player) {
        return null;
    }

    @Override
    public String convertLongToDate(Long l) {
        return "";
    }

    @Override
    public String getDateFormat() {
        return "";
    }

    @Override
    public void setDateFormat(String dateFormat) {
    }

    @Override
    public Ban createBan(UUID banned, String senderUUID, String reason, Long howLong, TimeUnit timeUnit) {
        return new Ban(banned, reason, senderUUID, howLong, "");
    }

    @Override
    public void unBan(UUID banned) {
    }

    @Override
    public void close() {
    }

    @Override
    protected String getBanSystem(String action) {
        return "";
    }
}
