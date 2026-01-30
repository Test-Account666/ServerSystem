package me.testaccount666.serversystem.commands.executables.teleportask

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.managers.messages.MessageManager.applyPlaceholders
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ComponentColor.translateToComponent
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
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
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return null
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (isSelf) {
            command("TeleportAsk.CannotTeleportSelf", commandSender).build()
            return null
        }

        if (!targetUser.isAcceptsTeleports) {
            command("TeleportAsk.NoTeleport", commandSender) { target(targetPlayer.name) }.build()
            return null
        }

        return targetUser
    }

    private fun handleTeleportAsk(commandSender: User, label: String, vararg arguments: String) {
        val targetUser = validateTargetPlayer(commandSender, *arguments) ?: return
        val targetPlayer = targetUser.getPlayer()!!
        val timeOut = System.currentTimeMillis() + (1000 * 60 * 2) // Two minutes

        command("TeleportAsk.Success", commandSender) { target(targetPlayer.name) }.build()

        if (targetUser.isIgnoredPlayer(commandSender.uuid)) return

        command("TeleportAsk.SuccessOther", targetUser) { sender(commandSender.getNameSafe()) }.build()

        val teleportRequest = TeleportRequest(commandSender, targetUser, timeOut, false)
        targetUser.teleportRequest = teleportRequest
        sendAcceptDenyButtons(commandSender, targetUser, label)
    }

    private fun handleTeleportHereAsk(commandSender: User, label: String, vararg arguments: String) {
        val targetUser = validateTargetPlayer(commandSender, *arguments) ?: return
        val targetPlayer = targetUser.getPlayer()!!
        val timeOut = System.currentTimeMillis() + (1000 * 60 * 2) // Two minutes

        command("TeleportHereAsk.Success", commandSender) { target(targetPlayer.name) }.build()

        if (targetUser.isIgnoredPlayer(commandSender.uuid)) return

        command("TeleportHereAsk.SuccessOther", targetUser) { sender(commandSender.getNameSafe()) }.build()

        val teleportRequest = TeleportRequest(commandSender, targetUser, timeOut, true)
        targetUser.teleportRequest = teleportRequest
        sendAcceptDenyButtons(commandSender, targetUser, label)
    }


    private fun sendAcceptDenyButtons(commandSender: User, targetUser: User, label: String) {
        val targetPlayer = targetUser.getPlayer()!!

        val acceptButton = command("TeleportAsk.Buttons.Accept.Name", targetUser) {
            format(false)
            send(false)
            prefix(false)
            blankError(true)
            postModifier { applyPlaceholders(it, commandSender, targetPlayer.name, label) }
        }.build()

        val denyButton = command("TeleportAsk.Buttons.Deny.Name", targetUser) {
            format(false)
            send(false)
            prefix(false)
            blankError(true)
            postModifier { applyPlaceholders(it, commandSender, targetPlayer.name, label) }
        }.build()

        if (acceptButton.isEmpty() || denyButton.isEmpty()) {
            log.warning(
                "Couldn't find accept or deny button for ${
                    targetUser.getNameOrNull()
                } in the language file. Please check the language file for errors."
            )
            general("ErrorOccurred", targetUser) { label(label) }.build()
            return
        }

        val acceptButtonTooltip = command("TeleportAsk.Buttons.Accept.Tooltip", targetUser) {
            format(false)
            prefix(false)
            send(false)
            blankError(true)
            postModifier { applyPlaceholders(it, commandSender, targetPlayer.name, label) }
        }.build()

        val denyButtonTooltip = command("TeleportAsk.Buttons.Deny.Tooltip", targetUser) {
            format(false)
            prefix(false)
            send(false)
            blankError(true)
            postModifier { applyPlaceholders(it, commandSender, targetPlayer.name, label) }
        }.build()

        if (acceptButtonTooltip.isEmpty() || denyButtonTooltip.isEmpty()) {
            log.warning(
                "Couldn't find accept or deny button tooltip for ${
                    targetUser.getNameOrNull()
                } in the language file. Please check the language file for errors."
            )
            general("ErrorOccurred", targetUser) { label(label) }.build()
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
            command("TeleportAccept.NoRequest", commandSender).build()
            return null
        }

        val requester = teleportRequest.sender
        if (requester.getPlayer() == null || !requester.getPlayer()!!.isOnline) {
            command("TeleportAccept.NoRequest", commandSender).build()
            return null
        }

        return teleportRequest
    }

    private fun handleTeleportAccept(commandSender: User) {
        val teleportRequest = validateTeleportRequest(commandSender) ?: return

        val requester = teleportRequest.sender
        commandSender.teleportRequest = null

        command("TeleportAccept.SuccessOther", requester) { target(commandSender.getNameSafe()) }.build()

        val teleporter = if (teleportRequest.isTeleportHere) commandSender else requester
        val target = if (teleportRequest.isTeleportHere) requester else commandSender

        val canInstantTeleport = hasCommandPermission(teleporter, "TeleportAsk.InstantTeleport", false)

        if (canInstantTeleport) {
            executeTeleport(teleporter, target)
            return
        }

        command("TeleportAsk.StartingTeleporting", teleporter) { target(target.getNameSafe()) }.build()
        startTeleportTimer(teleporter, target, teleportRequest)
    }

    private fun handleTeleportDeny(commandSender: User) {
        val teleportRequest = validateTeleportRequest(commandSender) ?: return

        val requester = teleportRequest.sender
        commandSender.teleportRequest = null

        command("TeleportDeny.Success", commandSender) { target(requester.getNameSafe()) }.build()
        command("TeleportDeny.SuccessOther", requester) { target(commandSender.getNameSafe()) }.build()
    }

    private fun startTeleportTimer(teleporter: User, target: User, teleportRequest: TeleportRequest) {
        val teleporterPlayer = teleporter.getPlayer()
        val targetPlayer = target.getPlayer()

        activeTeleportRequests.add(teleportRequest)
        (Bukkit.getScheduler().scheduleSyncDelayedTask(instance, {
            if (teleporterPlayer == null || !teleporterPlayer.isOnline) return@scheduleSyncDelayedTask
            if (targetPlayer == null || !targetPlayer.isOnline) return@scheduleSyncDelayedTask

            executeTeleport(teleporter, target)
            activeTeleportRequests.remove(teleportRequest)
        }, 20L * 5)).also { teleportRequest.timerId = it }
    }

    /**
     * Executes the teleport with animation and notification
     *
     * @param teleporter The user who is teleporting
     * @param target     The target user to teleport to
     */
    private fun executeTeleport(teleporter: User, target: User) {
        val targetLocation = target.getPlayer()!!.location

        playAnimation(targetLocation)
        teleporter.getPlayer()!!.teleport(targetLocation)
        playAnimation(targetLocation)

        command("TeleportAsk.TeleportFinished", teleporter) { target(target.getNameSafe()) }.build()
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

        return translateToComponent(text)
            .hoverEvent(HoverEvent.showText(translateToComponent(hoverText)))
            .clickEvent(clickAction)
            .asComponent()
    }

    /**
     * Plays a teleportation animation effect at the given location
     *
     * @param location The location to play the animation at
     */
    private fun playAnimation(location: Location) {
        location.world.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f)
        location.world.spawnParticle(Particle.PORTAL, location, 100, 0.5, 0.5, 0.5, 0.05)
    }

    private fun handleTeleportToggle(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
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

        command(messagePath, commandSender) { target(targetPlayer.name) }.build()

        if (isSelf) return

        command("TeleportToggle.Success" + (if (acceptsTeleports) "Enabled" else "Disabled"), targetUser) {
            sender(commandSender.getNameSafe())
        }.build()
    }
}
