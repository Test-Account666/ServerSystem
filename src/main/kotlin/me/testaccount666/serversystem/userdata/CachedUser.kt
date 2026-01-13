package me.testaccount666.serversystem.userdata

import org.bukkit.Bukkit

open class CachedUser internal constructor(var offlineUser: OfflineUser) {
    private var _lastAccessTime: Long

    init {
        _lastAccessTime = System.currentTimeMillis()
    }

    val isOnlineUser
        get() = offlineUser is User

    val isOfflineUser
        get() = !isOnlineUser

    val isStale
        get() = System.currentTimeMillis() - _lastAccessTime > _STALE_TIME_MILLIS

    internal fun updateLastAccessTime() {
        _lastAccessTime = System.currentTimeMillis()
    }

    fun convertToOnlineUser() {
        val player = Bukkit.getPlayer(offlineUser.uuid)

        checkNotNull(player) { "Cannot convert offline user to online user!" }

        offlineUser.save()

        offlineUser = User(offlineUser)
    }

    fun convertToOfflineUser() {
        offlineUser.save()

        offlineUser = OfflineUser(offlineUser)
    }

    companion object {
        private const val _STALE_TIME_MILLIS = 1000L * 60L * 60L // One hour
    }
}