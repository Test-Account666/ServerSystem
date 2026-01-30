package me.testaccount666.serversystem.commands.executables.privatemessage

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.events.UserPrivateMessageEvent
import me.testaccount666.serversystem.managers.messages.MessageManager.applyPlaceholders
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ComponentColor.translateToComponent
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Bukkit
import org.bukkit.command.Command

@ServerSystemCommand("privatemessage", ["reply", "messagetoggle", "socialspy"])
class CommandPrivateMessage : AbstractServerSystemCommand() {
    private val _privateMessageCommand by lazy {
        return@lazy variantLabelMap["privatemessage"]?.first() ?: error("(CommandPrivateMessage) PrivateMessage command not found")
    }

    override fun minRequiredArguments(command: Command): Int {
        return when (command.name.lowercase()) {
            "privatemessage" -> 2
            "reply" -> 1
            "messagetoggle", "socialspy" -> 0
            else -> error("(CommandPrivateMessage) Unexpected value: ${command.name}")
        }
    }

    override fun getUsagePermission(command: Command): String {
        return when (command.name.lowercase()) {
            "privatemessage" -> "PrivateMessage.Use"
            "reply" -> "PrivateMessage.Use"
            "messagetoggle" -> "PrivateMessage.Toggle.Use"
            "socialspy" -> "SocialSpy.Use"
            else -> error("(CommandPrivateMessage) Unexpected value: ${command.name}")
        }
    }

    override fun getSyntaxPath(command: Command?): String {
        if (command == null) return "PrivateMessage"

        return when (val name = command.name.lowercase()) {
            "privatemessage" -> "PrivateMessage"
            "reply" -> "Reply"
            "messagetoggle" -> "MessageToggle"
            "socialspy" -> "SocialSpy"
            else -> error("(CommandPrivateMessage) Unexpected value: ${name}")
        }
    }

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        val commandName = command.name.lowercase()

        when (commandName) {
            "socialspy" -> handleSocialSpyCommand(commandSender, command, label, *arguments)
            "messagetoggle" -> handleMessageToggleCommand(commandSender, command, label, *arguments)
            "privatemessage" -> handlePrivateMessageCommand(commandSender, command, label, *arguments)
            else -> handleReplyCommand(commandSender, command, label, *arguments)
        }
    }

    private fun handleSocialSpyCommand(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkPermission(commandSender, "SocialSpy.Other", targetPlayer.name)) return

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
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkPermission(commandSender, "PrivateMessage.Toggle.Other", targetPlayer.name)) return

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
        val targetUser = commandSender.replyUser?.takeIf(::isValidReplyTarget) ?: run {
            command("Reply.NoReply", commandSender).build()
            return
        }

        val newArguments = arrayOf(targetUser.getNameSafe()) + arguments
        sendPrivateMessage(commandSender, targetUser, label, *newArguments)
    }

    private fun handlePrivateMessageCommand(commandSender: User, command: Command, label: String, vararg arguments: String) {
        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        sendPrivateMessage(commandSender, targetUser, label, *arguments)
    }

    private fun sendPrivateMessage(commandSender: User, targetUser: User, label: String, vararg arguments: String) {
        val targetName = targetUser.getNameOrNull() ?: run {
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

        val message = arguments.drop(1).joinToString(" ").trim()
        val success = getSuccessMessage(commandSender, targetName, label, message)
        val successOther = getSuccessOther(targetUser, commandSender, targetName, label, message)

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

    private fun getSuccessOther(targetUser: User, commandSender: User, targetName: String, label: String, message: String): String {
        return command("PrivateMessage.SuccessOther", targetUser) {
            sender(commandSender.getNameSafe())
            prefix(false)
            send(false)
            blankError(true)
            postModifier {
                applyPlaceholders(it, targetUser, targetName, label)
                    .replace("<MESSAGE>", message)
            }
        }.build()
    }

    private fun getSuccessMessage(commandSender: User, targetName: String, label: String, message: String): String {
        return command("PrivateMessage.Success", commandSender) {
            format(false)
            target(targetName)
            prefix(false)
            send(false)
            blankError(true)
            postModifier {
                applyPlaceholders(it, commandSender, targetName, label)
                    .replace("<MESSAGE>", message)
            }
        }.build()
    }

    private fun isValidReplyTarget(targetUser: User): Boolean {
        if (targetUser.commandSender == null || targetUser.getNameOrNull() == null) return false
        if (targetUser is ConsoleUser) return true

        return targetUser.getPlayer()?.isOnline ?: false
    }
}
