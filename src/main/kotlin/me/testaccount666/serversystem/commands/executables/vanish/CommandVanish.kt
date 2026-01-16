package me.testaccount666.serversystem.commands.executables.vanish

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import java.util.Locale.getDefault
import java.util.function.BooleanSupplier
import java.util.function.Consumer

@ServerSystemCommand("vanish", ["drop", "pickup", "interact", "message"])
class CommandVanish : AbstractServerSystemCommand() {
    val vanishPacket = VanishPacket()

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Vanish.Use")) return
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val isSelf = targetUser === commandSender

        when (command.name.lowercase(getDefault())) {
            "vanish" -> handleVanishCommand(commandSender, targetUser, isSelf)
            "drop" -> handleDropCommand(commandSender, targetUser, isSelf)
            "pickup" -> handlePickupCommand(commandSender, targetUser, isSelf)
            "interact" -> handleInteractCommand(commandSender, targetUser, isSelf)
            "message" -> handleMessageCommand(commandSender, targetUser, isSelf)
        }
    }

    private fun handleDropCommand(commandSender: User, targetUser: User, isSelf: Boolean) {
        handleToggleCommand(
            commandSender, targetUser, isSelf,
            "Drop", { targetUser.vanishData.canDrop },
            { targetUser.vanishData.canDrop = it }
        )
    }


    private fun handlePickupCommand(commandSender: User, targetUser: User, isSelf: Boolean) {
        handleToggleCommand(
            commandSender, targetUser, isSelf,
            "Pickup", { targetUser.vanishData.canPickup },
            { targetUser.vanishData.canPickup = it }
        )
    }

    private fun handleToggleCommand(
        commandSender: User, targetUser: User, isSelf: Boolean, featureName: String?,
        getCurrentState: BooleanSupplier, setState: Consumer<Boolean>
    ) {
        var messagePath = if (isSelf) "${featureName}.Success" else "${featureName}.SuccessOther"
        val enableFeature = !getCurrentState.asBoolean

        messagePath = if (enableFeature) "${messagePath}.Enabled" else "${messagePath}.Disabled"

        setState.accept(enableFeature)
        targetUser.save()

        command(messagePath, commandSender) { target(targetUser.getNameSafe()) }.build()

        if (isSelf) return

        command("${featureName}.Success" + (if (enableFeature) "Enabled" else "Disabled"), targetUser) {
            sender(commandSender.getNameSafe())
        }.build()
    }


    private fun handleInteractCommand(commandSender: User, targetUser: User, isSelf: Boolean) {
        handleToggleCommand(
            commandSender, targetUser, isSelf,
            "Interact", { targetUser.vanishData.canInteract },
            { targetUser.vanishData.canInteract = it }
        )
    }

    private fun handleMessageCommand(commandSender: User, targetUser: User, isSelf: Boolean) {
        handleToggleCommand(
            commandSender, targetUser, isSelf,
            "Message", { targetUser.vanishData.canMessage },
            { targetUser.vanishData.canMessage = it }
        )
    }

    private fun handleVanishCommand(commandSender: User, targetUser: User, isSelf: Boolean) {
        var messagePath = if (isSelf) "Vanish.Success" else "Vanish.SuccessOther"
        val enableVanish = !targetUser.isVanish

        messagePath = if (enableVanish) "${messagePath}.Enabled" else "${messagePath}.Disabled"

        targetUser.getPlayer()!!.isSleepingIgnored = enableVanish
        targetUser.getPlayer()!!.setMetadata("vanished", FixedMetadataValue(instance, enableVanish))
        targetUser.isVanish = enableVanish
        targetUser.save()

        vanishPacket.sendVanishPacket(targetUser)

        command(messagePath, commandSender) { target(targetUser.getNameSafe()) }.build()

        if (isSelf) return

        command("Vanish.Success" + (if (enableVanish) "Enabled" else "Disabled"), targetUser) {
            sender(commandSender.getNameSafe())
        }.build()
    }

    override fun getSyntaxPath(command: Command?) = "Vanish"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Vanish.Use", false)
    }
}
