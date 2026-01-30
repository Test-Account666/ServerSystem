package me.testaccount666.serversystem.commands.executables.suicide

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command
import org.bukkit.damage.DamageSource
import org.bukkit.damage.DamageType

@ServerSystemCommand("suicide")
class CommandSuicide : AbstractServerSystemCommand() {
    override fun getUsagePermission(command: Command) = "Suicide.Use"
    override fun getSyntaxPath(command: Command?) = "Generic"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!isPlayer(commandSender)) return

        commandSender.getPlayer()!!.damage(Double.MAX_VALUE, DamageSource.builder(DamageType.GENERIC_KILL).build())
    }
}
