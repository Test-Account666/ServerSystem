package me.testaccount666.serversystem.commands.executables.workbench

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.userdata.User
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
    private val _permissionMap = mapOf(
        "workbench" to "Workbench.Use",
        "anvil" to "Anvil.Use",
        "smithing" to "Smithing.Use",
        "loom" to "Loom.Use",
        "grindstone" to "Grindstone.Use",
        "cartography" to "Cartography.Use",
        "stonecutter" to "Stonecutter.Use"
    )

    override fun getUsagePermission(command: Command) = _permissionMap[command.name.lowercase()] ?: error("Invalid command name: ${command.name}")
    override fun getSyntaxPath(command: Command?) = "Generic"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!isPlayer(commandSender)) return
        val player = commandSender.getPlayer()!!

        val commandName = command.name.replaceFirstChar { it.uppercaseChar() }

        _menuOpeners[commandName]?.invoke(player)
    }
}
