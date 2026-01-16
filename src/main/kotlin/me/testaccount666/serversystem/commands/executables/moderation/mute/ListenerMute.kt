package me.testaccount666.serversystem.commands.executables.moderation.mute

import io.papermc.paper.event.player.AsyncChatEvent
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.annotations.RequiredCommands
import me.testaccount666.serversystem.commands.executables.moderation.ModerationUtils
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.events.UserPrivateMessageEvent
import me.testaccount666.serversystem.moderation.MuteModeration
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.utils.DurationParser.parseDate
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

@RequiredCommands([CommandMute::class])
class ListenerMute : Listener {
    fun canRegister(requiredCommands: Set<ServerSystemCommandExecutor>): Boolean = requiredCommands.any { it is CommandMute }

    @EventHandler
    fun onPlayerChat(event: AsyncChatEvent) {
        val user = instance.registry.getService<UserManager>().getUserOrNull(event.getPlayer()) ?: return
        if (user.isOfflineUser) return
        val onlineUser = user.offlineUser as User
        val muteManager = onlineUser.muteManager

        val muteModeration = muteManager.activeModeration ?: return

        handleMute(event, onlineUser, muteModeration)
        handleShadowMute(event, muteModeration)
    }

    @EventHandler
    fun onPrivateMessage(event: UserPrivateMessageEvent) {
        val user = event.sender
        if (user is ConsoleUser) return
        val muteManager = user.muteManager

        val muteModeration = muteManager.activeModeration ?: return

        handleMute(event, user, muteModeration)
        handleShadowMute(event, muteModeration)
    }

    private fun handleMute(event: Cancellable, user: User, muteModeration: MuteModeration) {
        if (muteModeration.isShadowMute) return
        event.isCancelled = true

        val senderName = ModerationUtils.findSenderName(muteModeration) ?: muteModeration.senderUuid.toString()

        command("Moderation.Mute.Muted", user) {
            sender(senderName)
            target(user.getNameSafe())
            postModifier {
                it.replace("<REASON>", muteModeration.reason)
                    .replace("<DATE>", parseDate(muteModeration.expireTime, user))
            }
        }.build()
    }

    private fun handleShadowMute(event: AsyncChatEvent, muteModeration: MuteModeration) {
        if (!muteModeration.isShadowMute) return

        event.viewers().removeIf { audience ->
            if (audience !is Player) return@removeIf false
            audience !== event.getPlayer()
        }
    }

    private fun handleShadowMute(event: UserPrivateMessageEvent, muteModeration: MuteModeration) {
        if (!muteModeration.isShadowMute) return

        event.recipients.removeIf { user ->
            if (user is ConsoleUser) return@removeIf false
            user !== event.sender
        }
    }
}
