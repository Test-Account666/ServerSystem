package me.testaccount666.serversystem.commands.executables.moderation.ban

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.annotations.RequiredCommands
import me.testaccount666.serversystem.commands.executables.moderation.ModerationUtils
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.userdata.UserManager.Companion.consoleUser
import me.testaccount666.serversystem.utils.DurationParser.parseDate
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

@RequiredCommands([CommandBan::class])
class ListenerBan : Listener {
    fun canRegister(requiredCommands: Set<ServerSystemCommandExecutor>): Boolean = requiredCommands.any { it is CommandBan }

    @EventHandler
    fun onLogin(event: AsyncPlayerPreLoginEvent) {
        val cachedUser = getService<UserManager>().getUserOrNull(event.uniqueId) ?: run {
            log.severe("(ListenerBan) User not found! This should not happen!")
            return
        }
        val user = cachedUser.offlineUser
        val banManager = user.banManager

        val banModeration = banManager.activeModeration ?: return

        val senderName = ModerationUtils.findSenderName(banModeration) ?: banModeration.senderUuid.toString()
        val parsedDuration = banModeration.expireTime

        val unbanDate = parseDate(parsedDuration, user)
        val kickMessage = consoleUser.commandMsg("Moderation.Ban.Kick") {
            sender(senderName)
            language(user.playerLanguage)
            target(user.getNameOrNull())
            prefix(false)
            send(false)
            blankError(true)
            postModifier {
                it.replace("<DATE>", unbanDate)
                    .replace("<REASON>", banModeration.reason)
            }
        }

        if (kickMessage.isEmpty()) {
            log.severe("(CommandBan) Kick message is empty! This should not happen!")
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, "Error occurred!")
            return
        }

        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, kickMessage)
        log.info("(ListenerBan) Disallowed ${event.name} (${event.uniqueId}) for ${banModeration.reason}")
    }
}
