package me.testaccount666.serversystem.placeholderapi

import me.testaccount666.serversystem.userdata.OfflineUser

interface Placeholder {
    fun execute(user: OfflineUser?, identifier: String, vararg arguments: String): String?

    val identifiers: Set<String>
}
