package me.testaccount666.serversystem.placeholderapi.executables

import me.testaccount666.serversystem.managers.PermissionManager
import me.testaccount666.serversystem.placeholderapi.Placeholder
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.OfflineUser
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Bukkit

class OnlinePlayersPlaceholder : Placeholder {
    override fun execute(user: OfflineUser?, identifier: String, vararg arguments: String): String {
        val count = Bukkit.getOnlinePlayers().stream().filter { player -> !player.hasMetadata("vanished") }.count()
        if ((user !is User) || user is ConsoleUser) return count.toString()

        val player = user.getPlayer()
        if (PermissionManager.hasCommandPermission(player!!, "Vanish.Show", false)) return Bukkit.getOnlinePlayers().size.toString()

        return count.toString()
    }

    override val identifiers = mutableSetOf("onlineplayers")
}
