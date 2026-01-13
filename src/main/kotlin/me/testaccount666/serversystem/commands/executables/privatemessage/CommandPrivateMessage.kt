package me.testaccount666.serversystem.commands.executables.privatemessage

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.events.UserPrivateMessageEvent
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.managers.messages.MessageManager
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ComponentColor.Companion.translateToComponent
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.entity.Player
import java.util.Locale.getDefault

@ServerSystemCommand("privatemessage", ["reply", "messagetoggle", "socialspy"])
class CommandPrivateMessage : AbstractServerSystemCommand() {
    private var _privateMessageCommand: String? = null

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        val commandName = command.name.lowercase(getDefault())

        when (commandName) {
            "socialspy" -> handleSocialSpyCommand(commandSender, command, label, *arguments)
            "messagetoggle" -> handleMessageToggleCommand(commandSender, command, label, *arguments)
            "privatemessage" -> handlePrivateMessageCommand(commandSender, command, label, *arguments)
            else -> handleReplyCommand(commandSender, command, label, *arguments)
        }
    }

    private fun handleSocialSpyCommand(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "SocialSpy.Use")) return
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkOtherPermission(commandSender, "SocialSpy.Other", targetPlayer.name)) return

        val isEnabled = !targetUser.isSocialSpyEnabled

        var messagePath = if (isSelf) "SocialSpy.Success" else "SocialSpy.SuccessOther"

        messagePath += if (isEnabled) ".Enabled" else ".Disabled"

        targetUser.isSocialSpyEnabled = isEnabled
        targetUser.save()

        command(messagePath, commandSender) { target(targetPlayer.name) }.build()

        if (isSelf) return
        command("SocialSpy.Success." + (if (isEnabled) "Enabled" else "Disabled"), targetUser).build()
    }

    private fun handleMessageToggleCommand(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "PrivateMessage.Toggle.Use")) return
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkOtherPermission(commandSender, "PrivateMessage.Toggle.Other", targetPlayer.name)) return

        val acceptsMessages = !targetUser.isAcceptsMessages

        targetUser.isAcceptsMessages = acceptsMessages
        targetUser.save()

        var messagePath = if (isSelf) "MessageToggle.Success" else "MessageToggle.SuccessOther"
        messagePath = if (acceptsMessages) "${messagePath}.Enabled" else "${messagePath}.Disabled"

        command(messagePath, commandSender) { target(targetPlayer.name) }.build()

        if (isSelf) return
        command("MessageToggle.Success" + (if (acceptsMessages) "Enabled" else "Disabled"), targetUser) {
            sender(commandSender.getNameSafe())
        }.build()
    }

    private fun handleReplyCommand(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "PrivateMessage.Use")) return

        if (arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val targetUser = commandSender.replyUser

        if (targetUser == null || !isValidReplyTarget(targetUser)) {
            command("Reply.NoReply", commandSender).build()
            return
        }

        val newArguments = arrayOf(targetUser.getNameSafe()) + arguments

        sendPrivateMessage(commandSender, targetUser, label, *newArguments)
    }

    private fun handlePrivateMessageCommand(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (_privateMessageCommand == null) _privateMessageCommand = label
        if (!checkBasePermission(commandSender, "PrivateMessage.Use")) return

        if (arguments.size <= 1) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val targetUser = getTargetUser(commandSender, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        sendPrivateMessage(commandSender, targetUser, label, *arguments)
    }

    private fun sendPrivateMessage(commandSender: User, targetUser: User, label: String, vararg arguments: String) {
        val targetName = targetUser.getNameOrNull()
        if (targetName == null) {
            general("ErrorOccurred", commandSender) {
                label(label)
                target(targetUser.uuid.toString())
            }.build()
            return
        }

        val isSelf = targetUser === commandSender

        if (isSelf) {
            command("PrivateMessage.CannotSendToSelf", commandSender).build()
            return
        }

        if (!targetUser.isAcceptsMessages) {
            command("PrivateMessage.NoMessages", commandSender) { target(targetName) }.build()
            return
        }

        val message = arguments.drop(1).joinToString(" ").trim { it <= ' ' }

        val success = command("PrivateMessage.Success", commandSender) {
            format(false)
            target(targetName)
            prefix(false)
            send(false)
            blankError(true)
            postModifier {
                MessageManager.applyPlaceholders(it, commandSender, targetName, label)
                    .replace("<MESSAGE>", message)
            }
        }.build()

        val successOther = command("PrivateMessage.SuccessOther", targetUser) {
            sender(commandSender.getNameSafe())
            prefix(false)
            send(false)
            blankError(true)
            postModifier {
                MessageManager.applyPlaceholders(it, targetUser, targetName, label)
                    .replace("<MESSAGE>", message)
            }
        }.build()

        if (success.isEmpty() || successOther.isEmpty()) {
            log.warning("Couldn't find message for path Commands.PrivateMessage.Success or Commands.PrivateMessage.SuccessOther")
            general("ErrorOccurred", commandSender) {
                label(label)
                target(targetName)
            }.build()
            return
        }

        val messageEvent = UserPrivateMessageEvent(commandSender, message, targetUser)
        Bukkit.getPluginManager().callEvent(messageEvent)
        if (messageEvent.isCancelled()) return


        val successComponent = translateToComponent(success)
            .clickEvent(ClickEvent.suggestCommand("/${_privateMessageCommand} ${targetName} "))
            .asComponent()

        val successOtherComponent = translateToComponent(successOther)
            .clickEvent(ClickEvent.suggestCommand("/${_privateMessageCommand} ${commandSender.getNameOrNull()} "))
            .asComponent()


        messageEvent.recipients.forEach { recipient ->
            if (recipient === commandSender) {
                commandSender.sendMessage(successComponent)
                commandSender.replyUser = targetUser
                return@forEach
            }
            if (recipient === targetUser && targetUser.isIgnoredPlayer(commandSender.uuid)) return@forEach

            recipient.sendMessage(successOtherComponent)
            recipient.replyUser = commandSender
        }
    }

    private fun isValidReplyTarget(targetUser: User): Boolean {
        if (targetUser.commandSender == null || targetUser.getNameOrNull() == null) return false

        if (targetUser is ConsoleUser) return true

        return targetUser.getPlayer()?.isOnline ?: false
    }

    override fun getSyntaxPath(command: Command?): String {
        if (command == null) return "PrivateMessage"

        return when (val name = command.name.lowercase(getDefault())) {
            "privatemessage" -> "PrivateMessage"
            "reply" -> "Reply"
            "messagetoggle" -> "MessageToggle"
            "socialspy" -> "SocialSpy"
            else -> error("(CommandPrivateMessage) Unexpected value: ${name}")
        }
    }

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        val permissionPath = when (val name = command.name.lowercase(getDefault())) {
            "privatemessage" -> "PrivateMessage.Use"
            "reply" -> "PrivateMessage.Use"
            "messagetoggle" -> "PrivateMessage.Toggle.Use"
            "socialspy" -> "SocialSpy.Use"
            else -> error("(CommandPrivateMessage) Unexpected value: ${name}")
        }

        return hasCommandPermission(player, permissionPath, false)
    }
}
