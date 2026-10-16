package me.testaccount666.serversystem.commands.executables.privatemessage

import me.testaccount666.paperktx.extensions.ComponentExtensions.asComponent
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.events.UserPrivateMessageEvent
import me.testaccount666.serversystem.extensions.*
import me.testaccount666.serversystem.managers.messages.MessageManager.applyPlaceholders
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
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
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
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

        commandSender.commandMsg(messagePath) { target(targetPlayer.name) }

        if (isSelf) return
        targetUser.commandMsg("SocialSpy.Success." + (if (isEnabled) "Enabled" else "Disabled"))
    }

    private fun handleMessageToggleCommand(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
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

        commandSender.commandMsg(messagePath) { target(targetPlayer.name) }

        if (isSelf) return
        targetUser.commandMsg("MessageToggle.Success" + (if (acceptsMessages) "Enabled" else "Disabled")) {
            sender(commandSender.nameSafe)
        }
    }

    private fun handleReplyCommand(commandSender: User, command: Command, label: String, vararg arguments: String) {
        val targetUser = commandSender.replyUser?.takeIf(::isValidReplyTarget) ?: run {
            commandSender.commandMsg("Reply.NoReply")
            return
        }

        val newArguments = arrayOf(targetUser.nameSafe) + arguments
        sendPrivateMessage(commandSender, targetUser, label, *newArguments)
    }

    private fun handlePrivateMessageCommand(commandSender: User, command: Command, label: String, vararg arguments: String) {
        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
            return
        }

        sendPrivateMessage(commandSender, targetUser, label, *arguments)
    }

    private fun sendPrivateMessage(commandSender: User, targetUser: User, label: String, vararg arguments: String) {
        val targetName = targetUser.getNameOrNull() ?: run {
            commandSender.generalMsg("ErrorOccurred") {
                label(label)
                target(targetUser.uuid.toString())
            }
            return
        }

        val isSelf = targetUser === commandSender

        if (isSelf) {
            commandSender.commandMsg("PrivateMessage.CannotSendToSelf")
            return
        }

        if (!targetUser.isAcceptsMessages) {
            commandSender.commandMsg("PrivateMessage.NoMessages") { target(targetName) }
            return
        }

        val message = arguments.join(1)
        val success = getSuccessMessage(commandSender, targetName, label, message)
        val successOther = getSuccessOther(targetUser, commandSender, targetName, label, message)

        if (success.isEmpty() || successOther.isEmpty()) {
            log.warning("Couldn't find message for path Commands.PrivateMessage.Success or Commands.PrivateMessage.SuccessOther")
            commandSender.generalMsg("ErrorOccurred") {
                label(label)
                target(targetName)
            }
            return
        }

        val messageEvent = UserPrivateMessageEvent(commandSender, message, targetUser)
        Bukkit.getPluginManager().callEvent(messageEvent)
        if (messageEvent.isCancelled()) return


        val successComponent = success.asComponent()
            .clickEvent(ClickEvent.suggestCommand("/${_privateMessageCommand} ${targetName} "))
            .asComponent()

        val successOtherComponent = successOther.asComponent()
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
        return targetUser.commandMsg("PrivateMessage.SuccessOther") {
            sender(commandSender.nameSafe)
            prefix(false)
            send(false)
            blankError(true)
            postModifier {
                applyPlaceholders(it, targetUser, targetName, label)
                    .replace("<MESSAGE>", message)
            }
        }
    }

    private fun getSuccessMessage(commandSender: User, targetName: String, label: String, message: String): String {
        return commandSender.commandMsg("PrivateMessage.Success") {
            format(false)
            target(targetName)
            prefix(false)
            send(false)
            blankError(true)
            postModifier {
                applyPlaceholders(it, commandSender, targetName, label)
                    .replace("<MESSAGE>", message)
            }
        }
    }

    private fun isValidReplyTarget(targetUser: User): Boolean {
        if (targetUser.commandSender == null || targetUser.getNameOrNull() == null) return false
        if (targetUser is ConsoleUser) return true

        return targetUser.isOnline
    }
}
