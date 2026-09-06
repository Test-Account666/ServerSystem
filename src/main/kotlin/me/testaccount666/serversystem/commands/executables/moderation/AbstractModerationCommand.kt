package me.testaccount666.serversystem.commands.executables.moderation

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.*
import me.testaccount666.serversystem.moderation.AbstractModeration
import me.testaccount666.serversystem.moderation.AbstractModerationManager
import me.testaccount666.serversystem.userdata.*
import me.testaccount666.serversystem.utils.DurationParser
import org.bukkit.command.Command

abstract class AbstractModerationCommand<T : AbstractModeration> : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        val targetUser = getService<UserManager>().getUserOrNull(arguments[0]) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
            return
        }
        val targetOfflineUser = targetUser.offlineUser.also {
            if (it.getNameOrNull() == null) {
                commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
                return
            }
        }

        if (isRemoveModeration(command)) {
            handleRemoveModeration(command, commandSender, targetOfflineUser)
            return
        }

        val duration = DurationParser.parseDuration(arguments[1])
        if (duration == -2L) {
            commandSender.commandMsg("Moderation.InvalidDuration") { target(targetOfflineUser.nameSafe) }
            return
        }
        if (duration == 0L) {
            commandSender.commandMsg("Moderation.NotZero") { target(targetOfflineUser.nameSafe) }
            return
        }

        val currentTime = System.currentTimeMillis()
        val expireTime = if (duration == -1L) -1 else currentTime + duration

        val defaultReason = commandSender.commandMsg("Moderation.DefaultReason") {
            target(targetOfflineUser.nameSafe)
            prefix(false)
            send(false)
            blankError(true)
        }

        if (defaultReason.isEmpty()) {
            log.severe("(Command: ${command.name}) Default reason is empty! This should not happen!")
            commandSender.generalMsg("ErrorOccurred") { label(label) }
            return
        }
        var reason = defaultReason
        if (arguments.size > 2) reason = arguments.join(2)


        handleCreateModeration(command, commandSender, targetOfflineUser, expireTime, reason)
    }

    private fun handleRemoveModeration(command: Command, commandSender: User, targetUser: OfflineUser) {
        val moderationManager = getModerationManager(targetUser)
        val activeModeration = moderationManager.activeModeration ?: run {
            commandSender.commandMsg("Moderation.${type(command)}.Remove.NoActiveModeration") { target(targetUser.nameSafe) }
            return
        }

        moderationManager.removeModeration(activeModeration)
        commandSender.commandMsg("Moderation.${type(command)}.Remove.Success") { target(targetUser.nameSafe) }
        handlePostRemoveModeration(command, commandSender, targetUser)
    }

    private fun handleCreateModeration(command: Command, commandSender: User, targetUser: OfflineUser, expireTime: Long, reason: String) {
        val moderationManager = getModerationManager(targetUser)
        if (moderationManager.hasActiveModeration()) {
            commandSender.commandMsg("Moderation.${type(command)}.Add.AlreadyActiveModeration") { target(targetUser.nameSafe) }
            return
        }

        val moderation = createModeration(command, commandSender, targetUser, expireTime, reason)
        moderationManager.addModeration(moderation)
        commandSender.commandMsg("Moderation.${type(command)}.Add.Success") { target(targetUser.nameSafe) }
        handlePostModeration(command, commandSender, targetUser, moderation)
    }

    protected abstract fun handlePostRemoveModeration(command: Command, commandSender: User, targetUser: OfflineUser)

    protected abstract fun handlePostModeration(command: Command, commandSender: User, targetUser: OfflineUser, moderation: T)

    protected abstract fun createModeration(command: Command, commandSender: User, targetUser: OfflineUser, expireTime: Long, reason: String): T

    protected fun isRemoveModeration(command: Command) = command.name.startsWith("un")

    protected abstract fun getModerationManager(targetUser: OfflineUser): AbstractModerationManager<T>

    abstract fun type(command: Command?): String?
}
