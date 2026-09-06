package me.testaccount666.serversystem.commands.executables.lightning

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

@ServerSystemCommand("lightning", [], TabCompleterLightning::class)
class CommandLightning : AbstractServerSystemCommand() {
    override fun getUsagePermission(command: Command) = "Lightning.Use"
    override fun getSyntaxPath(command: Command?) = "Lightning"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        var targetUser = getTargetUser(commandSender, arguments = arguments)
        val firstArgument = arguments.getOrNull(0) ?: ""
        if (targetUser == null && !firstArgument.startsWith("-")) {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
            return
        } else if (targetUser == null) targetUser = commandSender

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        val block = (if (isSelf) commandSender.getPlayer()!!.getTargetBlockExact(100) else targetPlayer.location.block) ?: run {
            commandSender.commandMsg("Lightning.NoTarget")
            return
        }

        val effectOnly = arguments.any { it.equals("-v", true) || it.equals("--visual", true) }

        if (effectOnly) block.world.strikeLightningEffect(block.location)
        else block.world.strikeLightning(block.location)

        if (isSelf) commandSender.commandMsg("Lightning.Success")
        else commandSender.commandMsg("Lightning.TargetSuccess") { target(targetPlayer.name) }
    }
}
