package me.testaccount666.serversystem.extensions

import me.testaccount666.serversystem.managers.PermissionManager
import me.testaccount666.serversystem.userdata.*
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

val User.isOnline get() = player?.isOnline ?: false

fun User.hasPermission(permission: String, sendFailInfo: Boolean = true) =
    PermissionManager.hasPermission(this, permission, sendFailInfo)

fun User.hasCommandPermission(permission: String, sendFailInfo: Boolean = true) =
    PermissionManager.hasCommandPermission(this, permission, sendFailInfo)

fun Player.asUser(): User? {
    val cachedUser = getService<UserManager>().getUserOrNull(this) ?: return null
    if (cachedUser.isOfflineUser) return null
    return cachedUser.onlineUser
}

fun OfflinePlayer.asOfflineUser(): OfflineUser? {
    val cachedUser = getService<UserManager>().getUserOrNull(uniqueId) ?: return null
    return cachedUser.offlineUser
}
