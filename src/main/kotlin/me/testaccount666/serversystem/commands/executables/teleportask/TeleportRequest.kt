package me.testaccount666.serversystem.commands.executables.teleportask

import me.testaccount666.serversystem.userdata.User

data class TeleportRequest(val sender: User, val receiver: User, private val _timeout: Long, val isTeleportHere: Boolean) {
    var isCancelled = false
    var timerId = 0

    val isExpired
        get() = System.currentTimeMillis() >= _timeout
}
