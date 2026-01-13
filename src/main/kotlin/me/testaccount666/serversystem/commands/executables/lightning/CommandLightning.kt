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

        val targetUser = getTargetUser(commandSender, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        val block = if (isSelf) commandSender.getPlayer()!!.getTargetBlockExact(100) else targetPlayer.location.block
        if (block == null) {
            command("Lightning.NoTarget", commandSender).build()
            return
        }

        val effectOnly = arguments.size > 1 && "visual".startsWith(arguments[1], true)

        if (effectOnly) block.world.strikeLightningEffect(block.location)
        else block.world.strikeLightning(block.location)

        if (isSelf) general("Lightning.Success", commandSender).build()
        else general("Lightning.TargetSuccess", commandSender) { target(targetPlayer.name) }.build()
    }

    override fun getSyntaxPath(command: Command?): String = "Lightning"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Lightning.Use", false)
    }
}
