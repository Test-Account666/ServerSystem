package me.testaccount666.serversystem.userdata;

import org.bukkit.Bukkit;

public class CachedUser {
    private static final long _STALE_TIME_MILLIS = 1000 * 60 * 60; // One hour

    private OfflineUser _offlineUser;
    private long _lastAccessTime;

    protected CachedUser(OfflineUser offlineUser) {
        _offlineUser = offlineUser;

        _lastAccessTime = System.currentTimeMillis();
    }

    public boolean isOnlineUser() {
        return _offlineUser instanceof User;
    }

    public boolean isOfflineUser() {
        return !isOnlineUser();
    }

    public OfflineUser getOfflineUser() {
        return _offlineUser;
    }

    public boolean isStale() {
        return System.currentTimeMillis() - _lastAccessTime > _STALE_TIME_MILLIS;
    }

    protected void updateLastAccessTime() {
        _lastAccessTime = System.currentTimeMillis();
    }

    public void convertToOnlineUser() {
        var player = Bukkit.getPlayer(_offlineUser.getUuid());

        if (player == null) throw new IllegalStateException("Cannot convert offline user to online user!");

        _offlineUser.save();

        _offlineUser = new User(_offlineUser);
    }

    public void convertToOfflineUser() {
        _offlineUser.save();

        _offlineUser = new OfflineUser(_offlineUser);
    }
}
