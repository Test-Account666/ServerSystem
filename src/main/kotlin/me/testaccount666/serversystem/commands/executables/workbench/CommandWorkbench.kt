package me.testaccount666.serversystem.commands.executables.workbench

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player
import java.util.Locale.getDefault

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
        val commandName = capitalizeFirstLetter(command.name)

        val permissionPath = "${commandName}.Use"
        if (!checkBasePermission(commandSender, permissionPath)) return

        val opener = _menuOpeners[commandName] ?: return
        opener(player)
    }

    private fun capitalizeFirstLetter(input: String) = input.replaceFirstChar { it.uppercaseChar() }


    override fun getSyntaxPath(command: Command?): String {
        throw UnsupportedOperationException("Workbench command doesn't have an available syntax!")
    }

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        val permissionMap = mapOf(
            "workbench" to "Workbench.Use",
            "anvil" to "Anvil.Use",
            "smithing" to "Smithing.Use",
            "loom" to "Loom.Use",
            "grindstone" to "Grindstone.Use",
            "cartography" to "Cartography.Use",
            "stonecutter" to "Stonecutter.Use"
        )

        val permissionPath = permissionMap[command.name.lowercase(getDefault())] ?: return false

        return hasCommandPermission(player, permissionPath, false)
    }
}
