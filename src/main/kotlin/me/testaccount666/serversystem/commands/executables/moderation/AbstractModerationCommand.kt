package me.testaccount666.serversystem.commands.executables.moderation

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.moderation.AbstractModeration
import me.testaccount666.serversystem.moderation.AbstractModerationManager
import me.testaccount666.serversystem.userdata.OfflineUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.utils.DurationParser
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command

abstract class AbstractModerationCommand<T : AbstractModeration> : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, command)) return
        if (arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        if (!isRemoveModeration(command) && arguments.size < 2) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val targetUser = instance.registry.getService<UserManager>().getUserOrNull(arguments[0])
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }
        val targetOfflineUser = targetUser.offlineUser
        if (targetOfflineUser.getNameOrNull() == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        if (isRemoveModeration(command)) {
            handleRemoveModeration(command, commandSender, targetOfflineUser)
            return
        }

        val duration = DurationParser.parseDuration(arguments[1])
        if (duration == -2L) {
            command("Moderation.InvalidDuration", commandSender) { target(targetOfflineUser.getNameSafe()) }.build()
            return
        }
        if (duration == 0L) {
            command("Moderation.NotZero", commandSender) { target(targetOfflineUser.getNameSafe()) }.build()
            return
        }

        val currentTime = System.currentTimeMillis()
        val expireTime = if (duration == -1L) -1 else currentTime + duration

        val defaultReason = command("Moderation.DefaultReason", commandSender) {
            target(targetOfflineUser.getNameSafe())
            prefix(false)
            send(false)
            blankError(true)
        }.build()

        if (defaultReason.isEmpty()) {
            log.severe("(Command: ${command.name}) Default reason is empty! This should not happen!")
            general("ErrorOccurred", commandSender) { label(label) }.build()
            return
        }
        var reason = defaultReason
        if (arguments.size > 2) reason = arguments.drop(2).joinToString(" ")


        handleCreateModeration(command, commandSender, targetOfflineUser, expireTime, reason)
    }

    private fun handleRemoveModeration(command: Command, commandSender: User, targetUser: OfflineUser) {
        val moderationManager = getModerationManager(targetUser)
        val activeModeration = moderationManager.activeModeration
        if (activeModeration == null) {
            command("Moderation.${type(command)}.Remove.NoActiveModeration", commandSender) { target(targetUser.getNameSafe()) }.build()
            return
        }

        moderationManager.removeModeration(activeModeration)
        command("Moderation.${type(command)}.Remove.Success", commandSender) { target(targetUser.getNameSafe()) }.build()
        handlePostRemoveModeration(command, commandSender, targetUser)
    }

    private fun handleCreateModeration(command: Command, commandSender: User, targetUser: OfflineUser, expireTime: Long, reason: String) {
        val moderationManager = getModerationManager(targetUser)
        if (moderationManager.hasActiveModeration()) {
            command("Moderation.${type(command)}.Add.AlreadyActiveModeration", commandSender) { target(targetUser.getNameSafe()) }.build()
            return
        }

        val moderation = createModeration(command, commandSender, targetUser, expireTime, reason)
        moderationManager.addModeration(moderation)
        command("Moderation.${type(command)}.Add.Success", commandSender) { target(targetUser.getNameSafe()) }.build()
        handlePostModeration(command, commandSender, targetUser, moderation)
    }

    protected abstract fun handlePostRemoveModeration(command: Command, commandSender: User, targetUser: OfflineUser)

    protected abstract fun handlePostModeration(command: Command, commandSender: User, targetUser: OfflineUser, moderation: T)

    protected abstract fun createModeration(command: Command, commandSender: User, targetUser: OfflineUser, expireTime: Long, reason: String): T

    protected abstract fun checkBasePermission(commandSender: User, command: Command): Boolean

    protected fun isRemoveModeration(command: Command): Boolean = command.name.startsWith("un")

    protected abstract fun getModerationManager(targetUser: OfflineUser): AbstractModerationManager<T>

    abstract fun type(command: Command?): String?
}
