package me.testaccount666.serversystem.userdata.teleport

import me.testaccount666.serversystem.userdata.User

data class TeleportRequest(val sender: User, val receiver: User, private val _timeout: Long, val isTeleportHere: Boolean) {
    var isCancelled = false

    val isExpired
        get() = isCancelled || System.currentTimeMillis() >= _timeout
}