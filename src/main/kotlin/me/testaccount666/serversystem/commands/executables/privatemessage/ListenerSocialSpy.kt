package me.testaccount666.serversystem.commands.executables.privatemessage

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.annotations.RequiredCommands
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.events.UserPrivateMessageEvent
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

@RequiredCommands([CommandPrivateMessage::class])
class ListenerSocialSpy : Listener {
    fun canRegister(requiredCommands: Set<ServerSystemCommandExecutor>): Boolean = requiredCommands.any { it is CommandPrivateMessage }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPrivateMessage(event: UserPrivateMessageEvent) {
        instance.registry.getService<UserManager>().cachedUsers.forEach { cachedUser ->
            if (!cachedUser.isOnlineUser) return@forEach
            val user = cachedUser.offlineUser as User
            if (!user.isSocialSpyEnabled) return@forEach

            val target = event.recipients.firstOrNull { recipient -> recipient != event.sender } ?: return@forEach

            val senderName = event.sender.getNameSafe()
            val targetName = target.getNameOrNull()
            command("SocialSpy.Format", user) {
                sender(senderName).target(targetName)
                prefix(false)
                postModifier { it.replace("<MESSAGE>", event.message) }
            }.build()
        }
    }
}
