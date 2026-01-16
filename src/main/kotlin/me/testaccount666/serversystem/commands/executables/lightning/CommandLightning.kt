package me.testaccount666.serversystem.commands.executables.lightning

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player

@ServerSystemCommand("lightning", [], TabCompleterLightning::class)
class CommandLightning : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Lightning.Use")) return
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        var targetUser = getTargetUser(commandSender, arguments = arguments)
        val firstArgument = if (arguments.isEmpty()) "" else arguments[0]
        if (targetUser == null && !firstArgument.startsWith("-")) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        } else if (targetUser == null) targetUser = commandSender

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        val block = if (isSelf) commandSender.getPlayer()!!.getTargetBlockExact(100) else targetPlayer.location.block
        if (block == null) {
            command("Lightning.NoTarget", commandSender).build()
            return
        }

        val effectOnly = arguments.any { it.equals("-v", true) || it.equals("--visual", true) }

        if (effectOnly) block.world.strikeLightningEffect(block.location)
        else block.world.strikeLightning(block.location)

        if (isSelf) command("Lightning.Success", commandSender).build()
        else command("Lightning.TargetSuccess", commandSender) { target(targetPlayer.name) }.build()
    }

    override fun getSyntaxPath(command: Command?) = "Lightning"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Lightning.Use", false)
    }
}
