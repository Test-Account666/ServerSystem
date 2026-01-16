package me.testaccount666.serversystem.commands.executables.teleportask

import me.testaccount666.serversystem.userdata.User

class TeleportRequest(val sender: User, val receiver: User, private val _timeout: Long, val isTeleportHere: Boolean) {
    var isCancelled: Boolean = false
    var timerId: Int = 0

    val isExpired: Boolean
        get() = System.currentTimeMillis() >= _timeout
}
