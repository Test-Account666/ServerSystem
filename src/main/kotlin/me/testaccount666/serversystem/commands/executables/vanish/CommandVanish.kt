package me.testaccount666.serversystem.commands.executables.vanish

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command
import org.bukkit.entity.Mob
import org.bukkit.metadata.FixedMetadataValue

@ServerSystemCommand("vanish", ["drop", "pickup", "interact", "message"])
class CommandVanish : AbstractServerSystemCommand() {
    val vanishPacket = VanishPacket()
    override fun getUsagePermission(command: Command) = "Vanish.Use"
    override fun getSyntaxPath(command: Command?): String {
        command ?: return "Vanish"
        if (command.name.lowercase() == "vanish") return "Vanish"
        return "Generic"
    }

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
            return
        }

        val isSelf = targetUser === commandSender

        when (command.name.lowercase()) {
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
        getCurrentState: () -> Boolean, setState: (Boolean) -> Unit,
    ) {
        var messagePath = if (isSelf) "${featureName}.Success" else "${featureName}.SuccessOther"
        val enableFeature = !getCurrentState()

        messagePath = if (enableFeature) "${messagePath}.Enabled" else "${messagePath}.Disabled"

        setState(enableFeature)
        targetUser.save()

        commandSender.commandMsg(messagePath) { target(targetUser.nameSafe) }

        if (isSelf) return

        targetUser.commandMsg("${featureName}.Success" + (if (enableFeature) "Enabled" else "Disabled")) {
            sender(commandSender.nameSafe)
        }
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

        if (enableVanish) {
            val targetPlayer = targetUser.getPlayer()!!
            targetPlayer.location.getNearbyEntitiesByType(Mob::class.java, 16.0) {
                it.target?.uniqueId == targetPlayer.uniqueId
            }.forEach { it.target = null }
        }

        commandSender.commandMsg(messagePath) { target(targetUser.nameSafe) }

        if (isSelf) return

        targetUser.commandMsg("Vanish.Success" + (if (enableVanish) "Enabled" else "Disabled")) {
            sender(commandSender.nameSafe)
        }
    }
}
