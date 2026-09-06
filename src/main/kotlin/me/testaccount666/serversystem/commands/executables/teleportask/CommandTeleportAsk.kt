package me.testaccount666.serversystem.commands.executables.teleportask

import me.testaccount666.paperktx.extensions.ComponentExtensions.asComponent
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.managers.messages.MessageManager.applyPlaceholders
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.teleport.TeleportRequest
import me.testaccount666.serversystem.userdata.teleport.TeleportRunnable.Companion.teleportSmart
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.command.Command

@ServerSystemCommand("teleportask", ["teleporthereask", "teleportaccept", "teleportdeny", "teleporttoggle"])
class CommandTeleportAsk : AbstractServerSystemCommand() {
    override fun minRequiredArguments(command: Command): Int {
        return when (command.name.lowercase()) {
            "teleportask", "teleporthereask" -> 1
            else -> 0
        }
    }

    override fun getUsagePermission(command: Command): String {
        return when (command.name) {
            "teleportask" -> "TeleportAsk.Use"
            "teleportaccept" -> "TeleportAccept.Use"
            "teleportdeny" -> "TeleportDeny.Use"
            "teleporthereask" -> "TeleportHereAsk.Use"
            "teleporttoggle" -> "TeleportToggle.Use"
            else -> error("(CommandTeleportAsk;getUsagePermission) Unknown command name: ${command.name}")
        }
    }

    override fun getSyntaxPath(command: Command?): String {
        if (command != null && command.name.equals("teleporttoggle", true)) return "TeleportToggle"
        return "TeleportAsk"
    }

    val activeTeleportRequests = HashSet<TeleportRequest>()

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        when (command.name) {
            "teleportask" -> handleTeleportAsk(commandSender, label, *arguments)
            "teleportaccept" -> handleTeleportAccept(commandSender)
            "teleportdeny" -> handleTeleportDeny(commandSender)
            "teleporthereask" -> handleTeleportHereAsk(commandSender, label, *arguments)
            "teleporttoggle" -> handleTeleportToggle(commandSender, command, label, *arguments)
        }
    }

    /**
     * Validates a target player for teleport commands
     *
     * @param commandSender The user sending the command
     * @param arguments     Command arguments containing target player name
     * @return The target User if valid, null if validation failed
     */
    private fun validateTargetPlayer(commandSender: User, vararg arguments: String): User? {
        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
            return null
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (isSelf) {
            commandSender.commandMsg("TeleportAsk.CannotTeleportSelf")
            return null
        }

        if (!targetUser.isAcceptsTeleports) {
            commandSender.commandMsg("TeleportAsk.NoTeleport") { target(targetPlayer.name) }
            return null
        }

        return targetUser
    }

    private fun handleTeleportAsk(commandSender: User, label: String, vararg arguments: String) {
        val targetUser = validateTargetPlayer(commandSender, *arguments) ?: return
        val targetPlayer = targetUser.getPlayer()!!
        val timeOut = System.currentTimeMillis() + (1000 * 60 * 2) // Two minutes

        commandSender.commandMsg("TeleportAsk.Success") { target(targetPlayer.name) }

        if (targetUser.isIgnoredPlayer(commandSender.uuid)) return

        targetUser.commandMsg("TeleportAsk.SuccessOther") { sender(commandSender.nameSafe) }

        val teleportRequest = TeleportRequest(commandSender, targetUser, timeOut, false)
        targetUser.teleportRequest = teleportRequest
        sendAcceptDenyButtons(commandSender, targetUser, label)
    }

    private fun handleTeleportHereAsk(commandSender: User, label: String, vararg arguments: String) {
        val targetUser = validateTargetPlayer(commandSender, *arguments) ?: return
        val targetPlayer = targetUser.getPlayer()!!
        val timeOut = System.currentTimeMillis() + (1000 * 60 * 2) // Two minutes

        commandSender.commandMsg("TeleportHereAsk.Success") { target(targetPlayer.name) }

        if (targetUser.isIgnoredPlayer(commandSender.uuid)) return

        targetUser.commandMsg("TeleportHereAsk.SuccessOther") { sender(commandSender.nameSafe) }

        val teleportRequest = TeleportRequest(commandSender, targetUser, timeOut, true)
        targetUser.teleportRequest = teleportRequest
        sendAcceptDenyButtons(commandSender, targetUser, label)
    }


    private fun sendAcceptDenyButtons(commandSender: User, targetUser: User, label: String) {
        val targetPlayer = targetUser.getPlayer()!!

        val acceptButton = targetUser.commandMsg("TeleportAsk.Buttons.Accept.Name") {
            format(false)
            send(false)
            prefix(false)
            blankError(true)
            postModifier { applyPlaceholders(it, commandSender, targetPlayer.name, label) }
        }

        val denyButton = targetUser.commandMsg("TeleportAsk.Buttons.Deny.Name") {
            format(false)
            send(false)
            prefix(false)
            blankError(true)
            postModifier { applyPlaceholders(it, commandSender, targetPlayer.name, label) }
        }

        if (acceptButton.isEmpty() || denyButton.isEmpty()) {
            log.warning(
                "Couldn't find accept or deny button for ${
                    targetUser.getNameOrNull()
                } in the language file. Please check the language file for errors."
            )
            targetUser.generalMsg("ErrorOccurred") { label(label) }
            return
        }

        val acceptButtonTooltip = targetUser.commandMsg("TeleportAsk.Buttons.Accept.Tooltip") {
            format(false)
            prefix(false)
            send(false)
            blankError(true)
            postModifier { applyPlaceholders(it, commandSender, targetPlayer.name, label) }
        }

        val denyButtonTooltip = targetUser.commandMsg("TeleportAsk.Buttons.Deny.Tooltip") {
            format(false)
            prefix(false)
            send(false)
            blankError(true)
            postModifier { applyPlaceholders(it, commandSender, targetPlayer.name, label) }
        }

        if (acceptButtonTooltip.isEmpty() || denyButtonTooltip.isEmpty()) {
            log.warning(
                "Couldn't find accept or deny button tooltip for ${
                    targetUser.getNameOrNull()
                } in the language file. Please check the language file for errors."
            )
            targetUser.generalMsg("ErrorOccurred") { label(label) }
            return
        }

        val acceptComponent = createMessageComponent(
            acceptButton,
            acceptButtonTooltip,
            ClickEvent.callback { handleTeleportAccept(targetUser) }
        )

        val denyComponent = createMessageComponent(
            denyButton,
            denyButtonTooltip,
            ClickEvent.callback { handleTeleportDeny(targetUser) }
        )

        targetPlayer.sendMessage(acceptComponent)
        targetPlayer.sendMessage(denyComponent)
    }

    /**
     * Validates a teleport request for accept/deny commands
     *
     * @param commandSender    The user who is accepting/denying
     * @return The teleport request if valid, null otherwise
     */
    private fun validateTeleportRequest(commandSender: User): TeleportRequest? {
        val teleportRequest = commandSender.teleportRequest?.takeUnless(TeleportRequest::isExpired) ?: run {
            commandSender.commandMsg("TeleportAccept.NoRequest")
            return null
        }

        val requester = teleportRequest.sender
        if (requester.getPlayer() == null || !requester.getPlayer()!!.isOnline) {
            commandSender.commandMsg("TeleportAccept.NoRequest")
            return null
        }

        return teleportRequest
    }

    private fun handleTeleportAccept(commandSender: User) {
        val teleportRequest = validateTeleportRequest(commandSender) ?: return
        teleportRequest.isCancelled = true

        val requester = teleportRequest.sender
        commandSender.teleportRequest = null

        requester.commandMsg("TeleportAccept.SuccessOther") { target(commandSender.nameSafe) }

        val teleporter = if (teleportRequest.isTeleportHere) commandSender else requester
        val target = if (teleportRequest.isTeleportHere) requester else commandSender

        executeTeleport(teleporter, target)
    }

    private fun handleTeleportDeny(commandSender: User) {
        val teleportRequest = validateTeleportRequest(commandSender) ?: return

        val requester = teleportRequest.sender
        commandSender.teleportRequest = null

        commandSender.commandMsg("TeleportDeny.Success") { target(requester.nameSafe) }
        requester.commandMsg("TeleportDeny.SuccessOther") { target(commandSender.nameSafe) }
    }

    /**
     * Executes the teleport with animation and notification
     *
     * @param teleporter The user who is teleporting
     * @param target     The target user to teleport to
     */
    private fun executeTeleport(teleporter: User, target: User) {
        val targetLocation = target.getPlayer()!!.location

        teleporter.teleportSmart(
            targetLocation,
            hasCommandPermission(teleporter, "TeleportAsk.InstantTeleport", false),
            { teleporter.commandMsg("TeleportAsk.StartingTeleporting") { target(target.nameSafe) } },
            { teleporter.commandMsg("TeleportAsk.TeleportFinished") { target(target.nameSafe) } },
            { teleporter.commandMsg("TeleportAsk.Moved") }
        )
    }


    /**
     * Creates an interactive message component with hover text and click action
     *
     * @param text        The button text
     * @param hoverText   The text to show when hovering over the button
     * @param clickAction The action to perform when clicked
     * @return The formatted component
     */
    private fun createMessageComponent(text: String?, hoverText: String?, clickAction: ClickEvent?): Component {
        if (text == null || hoverText == null || clickAction == null) return Component.empty()

        return text.asComponent()
            .hoverEvent(HoverEvent.showText(hoverText.asComponent()))
            .clickEvent(clickAction)
            .asComponent()
    }

    private fun handleTeleportToggle(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkPermission(commandSender, "TeleportToggle.Other", targetPlayer.name)) return

        val acceptsTeleports = !targetUser.isAcceptsTeleports

        var messagePath = if (isSelf) "TeleportToggle.Success" else "TeleportToggle.SuccessOther"
        messagePath = if (acceptsTeleports) "${messagePath}.Enabled" else "${messagePath}.Disabled"

        targetUser.isAcceptsTeleports = acceptsTeleports
        targetUser.save()

        commandSender.commandMsg(messagePath) { target(targetPlayer.name) }

        if (isSelf) return

        targetUser.commandMsg("TeleportToggle.Success" + (if (acceptsTeleports) "Enabled" else "Disabled")) {
            sender(commandSender.nameSafe)
        }
    }
}
