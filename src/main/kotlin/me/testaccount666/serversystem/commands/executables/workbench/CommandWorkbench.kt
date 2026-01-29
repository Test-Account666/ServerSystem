package me.testaccount666.serversystem.commands.executables.workbench

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player

@ServerSystemCommand("workbench", ["anvil", "smithing", "loom", "grindstone", "cartography", "stonecutter"])
class CommandWorkbench : AbstractServerSystemCommand() {
    private val _menuOpeners: Map<String, (Player) -> Unit> by lazy {
        mapOf(
            "Workbench" to MenuUtils::openWorkbench,
            "Anvil" to MenuUtils::openAnvil,
            "Smithing" to MenuUtils::openSmithing,
            "Loom" to MenuUtils::openLoom,
            "Grindstone" to MenuUtils::openGrindstone,
            "Cartography" to MenuUtils::openCartography,
            "Stonecutter" to MenuUtils::openStonecutter
        )
    }

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        val player = commandSender.getPlayer()!!
        val commandName = command.name.replaceFirstChar { it.uppercaseChar() }

        val permissionPath = "${commandName}.Use"
        if (!checkBasePermission(commandSender, permissionPath)) return

        _menuOpeners[commandName]?.invoke(player)
    }


    override fun getSyntaxPath(command: Command?): String {
        throw UnsupportedOperationException("Workbench command doesn't have an available syntax!")
    }

    private val _permissionMap = mapOf(
        "workbench" to "Workbench.Use",
        "anvil" to "Anvil.Use",
        "smithing" to "Smithing.Use",
        "loom" to "Loom.Use",
        "grindstone" to "Grindstone.Use",
        "cartography" to "Cartography.Use",
        "stonecutter" to "Stonecutter.Use"
    )

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        val permissionPath = _permissionMap[command.name.lowercase()] ?: return false

        return hasCommandPermission(player, permissionPath, false)
    }
}
