package me.testaccount666.serversystem.commands.executables.ip

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command

@ServerSystemCommand("ip")
class CommandIp : AbstractServerSystemCommand() {
    override fun minRequiredArguments(command: Command) = 1
    override fun getUsagePermission(command: Command) = "Ip.Use"
    override fun getSyntaxPath(command: Command?) = "Ip"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        val targetUser = getTargetUser(commandSender, returnSender = false, arguments = arguments) ?: run {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()

        command("Ip.Success", commandSender) {
            target(targetPlayer!!.name)
            postModifier { it.replace("<IP>", targetPlayer.address!!.address.hostAddress) }
        }.build()
    }
}
