package me.testaccount666.serversystem.userdata;

import org.bukkit.Bukkit;

public class CachedUser {
    private static final long STALE_TIME_MILLIS = 1000 * 60 * 60; // One hour

    private OfflineUser offlineUser;
    private long lastAccessTime;

    protected CachedUser(OfflineUser offlineUser) {
        this.offlineUser = offlineUser;

        lastAccessTime = System.currentTimeMillis();
    }

    public boolean isOnlineUser() {
        return offlineUser instanceof User;
    }

    public OfflineUser getOfflineUser() {
        return offlineUser;
    }

    public boolean isStale() {
        return System.currentTimeMillis() - lastAccessTime > STALE_TIME_MILLIS;
    }

    protected void updateLastAccessTime() {
        lastAccessTime = System.currentTimeMillis();
    }

    protected void convertToOnlineUser() {
        var player = Bukkit.getPlayer(offlineUser.getUuid());

        if (player == null) throw new IllegalStateException("Cannot convert offline user to online user!");

        offlineUser = new User(offlineUser);
    }
}
