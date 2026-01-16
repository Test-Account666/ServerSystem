package me.testaccount666.serversystem.commands.executables.commandspy

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.userdata.UserManager.Companion.consoleUser
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
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

        for (cachedUser in instance.registry.getService<UserManager>().cachedUsers) {
            if (!cachedUser.isOnlineUser) continue

            val user = cachedUser.offlineUser as User
            if (!user.isCommandSpyEnabled) continue

            val finalCommand = command
            command("CommandSpy.Format", user) {
                prefix(false)
                target(sender)
                postModifier { it.replace("<COMMAND>", finalCommand) }
            }.build()
        }
    }
}
