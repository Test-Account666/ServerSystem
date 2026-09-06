package me.testaccount666.serversystem.commands.executables.commandspy

import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.userdata.UserManager.Companion.consoleUser
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.server.ServerCommandEvent

class ListenerCommandSpy : Listener {
    @EventHandler
    fun onPlayerCommand(event: PlayerCommandPreprocessEvent) = sendCommandSpy(event.getPlayer().name, event.message)

    @EventHandler
    fun onConsoleCommand(event: ServerCommandEvent) = sendCommandSpy(consoleUser.getNameOrNull(), event.command)

    private fun sendCommandSpy(sender: String?, command: String) {
        var command = command
        if (!command.startsWith("/")) command = "/${command}"

        getService<UserManager>().cachedUsers.forEach { cachedUser ->
            if (!cachedUser.isOnlineUser) return@forEach

            val user = cachedUser.onlineUser
            if (!user.isCommandSpyEnabled) return@forEach

            user.commandMsg("CommandSpy.Format") {
                prefix(false)
                target(sender)
                postModifier { it.replace("<COMMAND>", command) }
            }
        }
    }
}
