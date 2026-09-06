package me.testaccount666.serversystem.commands.executables.ignore

import io.papermc.paper.event.player.AsyncChatEvent
import me.testaccount666.serversystem.annotations.RequiredCommands
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

@RequiredCommands([CommandIgnore::class])
class ListenerIgnore : Listener {

    fun canRegister(requiredCommands: Set<ServerSystemCommandExecutor>) = requiredCommands.any { it is CommandIgnore }

    @EventHandler
    fun dropIgnoreMessages(event: AsyncChatEvent) {
        val uuid = event.player.uniqueId

        event.viewers().removeIf {
            val player = it as? Player ?: return@removeIf false
            val user = getService<UserManager>().getUserOrNull(player) ?: return@removeIf false

            user.offlineUser.isIgnoredPlayer(uuid)
        }
    }
}