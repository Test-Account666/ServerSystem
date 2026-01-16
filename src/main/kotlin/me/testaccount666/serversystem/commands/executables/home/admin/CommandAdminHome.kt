package me.testaccount666.serversystem.commands.executables.home.admin

import me.testaccount666.serversystem.commands.executables.home.AbstractCommandHome
import me.testaccount666.serversystem.commands.executables.home.HomeType
import me.testaccount666.serversystem.managers.PermissionManager
import org.bukkit.command.Command
import org.bukkit.entity.Player

@Suppress("MoveVariableDeclarationIntoWhen")
class CommandAdminHome : AbstractCommandHome() {
    override fun getBasePermission(command: Command): String {
        val name = command.name.drop("admin".length)

        return when (name) {
            "sethome" -> "AdminHome.Set"
            "deletehome" -> "AdminHome.Delete"
            "home" -> "AdminHome.Use"
            else -> error("Invalid command name: ${command.name}")
        }
    }

    override fun argsBeforeHome(command: Command) = 1

    override fun getHomeType(command: Command): HomeType {
        val name = command.name.drop("admin".length)

        return when (name) {
            "sethome" -> HomeType.SET
            "deletehome" -> HomeType.DELETE
            "home" -> HomeType.TELEPORT
            else -> error("Invalid command name: ${command.name}")
        }
    }

    override fun getSyntaxPath(command: Command?) = "AdminHome"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return PermissionManager.hasCommandPermission(player, getBasePermission(command), false)
    }
}
