package me.testaccount666.serversystem.commands.executables.privatemessage

import me.testaccount666.serversystem.annotations.RequiredCommands
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.events.UserPrivateMessageEvent
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.event.*

@RequiredCommands([CommandPrivateMessage::class])
class ListenerSocialSpy : Listener {
    fun canRegister(requiredCommands: Set<ServerSystemCommandExecutor>): Boolean = requiredCommands.any { it is CommandPrivateMessage }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPrivateMessage(event: UserPrivateMessageEvent) {
        getService<UserManager>().cachedUsers.forEach { cachedUser ->
            if (!cachedUser.isOnlineUser) return@forEach
            val user = cachedUser.onlineUser
            if (!user.isSocialSpyEnabled) return@forEach

            val target = event.recipients.firstOrNull { it != event.sender } ?: return@forEach

            val senderName = event.sender.nameSafe
            val targetName = target.getNameOrNull()
            user.commandMsg("SocialSpy.Format") {
                sender(senderName).target(targetName)
                prefix(false)
                postModifier { it.replace("<MESSAGE>", event.message) }
            }
        }
    }
}
