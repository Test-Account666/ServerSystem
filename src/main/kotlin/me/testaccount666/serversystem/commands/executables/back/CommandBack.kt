package me.testaccount666.serversystem.commands.executables.back

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.SimpleCompletion
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.messages.MappingsData.Companion.backType
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
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

        var backType = commandSender.lastBackType
        backType = if (backType == BackType.NONE) BackType.DEATH else backType

        if (arguments.isNotEmpty()) backType = when (arguments[0].lowercase()) {
            "death" -> BackType.DEATH
            "teleport", "tp" -> BackType.TELEPORT
            else -> {
                general("InvalidArguments", commandSender) {
                    syntax(getSyntaxPath(command))
                    label(label)
                }.build()
                null
            }
        } ?: return

        val backLocation = when (backType) {
            BackType.DEATH -> commandSender.lastDeathLocation
            BackType.TELEPORT -> commandSender.lastTeleportLocation
            else -> {
                general("ErrorOccurred", commandSender) { label(label) }.build()
                throw IllegalStateException("Unexpected value: ${backType}")
            }
        } ?: run {
            command("Back.NoBackLocation", commandSender).build()
            return
        }

        commandSender.getPlayer()?.teleport(backLocation)

        val typeName = backType(commandSender).getBackTypeName(backType) ?: "ERROR"

        command("Back.Success", commandSender) {
            postModifier { it.replace("<TYPE>", typeName) }
        }.build()
    }

    enum class BackType {
        DEATH,
        TELEPORT,
        NONE
    }
}
