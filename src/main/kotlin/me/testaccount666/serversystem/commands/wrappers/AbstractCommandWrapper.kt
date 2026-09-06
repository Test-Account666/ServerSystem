package me.testaccount666.serversystem.commands.wrappers

import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.userdata.UserManager.Companion.consoleUser
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class AbstractCommandWrapper {
    internal fun resolveCommandUser(commandSender: CommandSender): User? {
        if (commandSender !is Player) return consoleUser

        val cachedUser = getService<UserManager>().getUserOrNull(commandSender) ?: return null
        if (cachedUser.isOfflineUser) return null

        return cachedUser.onlineUser
    }
}
