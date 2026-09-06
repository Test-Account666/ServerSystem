package me.testaccount666.serversystem.commands.executables.back

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.SimpleCompletion
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.managers.messages.MappingsData.Companion.backType
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

@ServerSystemCommand(
    "back", simpleCompletions = [
        SimpleCompletion(0, ["teleport", "death"])
    ]
)
class CommandBack : AbstractServerSystemCommand() {
    override fun getUsagePermission(command: Command) = "Back.Use"
    override fun getSyntaxPath(command: Command?) = "Back"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!isPlayer(commandSender)) return

        val backType = arguments.firstOrNull()?.lowercase()?.let {
            when (it) {
                "death" -> BackType.DEATH
                "teleport", "tp" -> BackType.TELEPORT
                else -> {
                    commandSender.generalMsg("InvalidArguments") {
                        syntax(getSyntaxPath(command))
                        label(label)
                    }
                    BackType.NONE
                }
            }
        } ?: (commandSender.lastBackType.takeUnless { it == BackType.NONE } ?: BackType.DEATH)
        if (backType == BackType.NONE) return

        val backLocation = when (backType) {
            BackType.DEATH -> commandSender.lastDeathLocation
            BackType.TELEPORT -> commandSender.lastTeleportLocation
        } ?: run {
            commandSender.commandMsg("Back.NoBackLocation")
            return
        }

        commandSender.getPlayer()?.teleport(backLocation)

        val typeName = backType(commandSender).getBackTypeName(backType) ?: "ERROR"
        commandSender.commandMsg("Back.Success") {
            postModifier { it.replace("<TYPE>", typeName) }
        }
    }

    enum class BackType {
        DEATH,
        TELEPORT,
        NONE
    }
}
