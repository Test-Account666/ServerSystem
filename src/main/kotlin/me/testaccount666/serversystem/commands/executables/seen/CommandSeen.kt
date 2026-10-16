package me.testaccount666.serversystem.commands.executables.seen

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.*
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.command.Command
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@ServerSystemCommand("seen", [], TabCompleterSeen::class)
class CommandSeen : AbstractServerSystemCommand() {
    override fun minRequiredArguments(command: Command) = 1
    override fun getUsagePermission(command: Command) = "Seen.Use"
    override fun getSyntaxPath(command: Command?) = "Seen"


    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        val cachedUser = getService<UserManager>().getUserOrNull(arguments[0]) ?: run {
            log.warning("(CommandSeen) User '${arguments[0]}' is not cached! This should not happen!")
            commandSender.generalMsg("ErrorOccurred") { label(label) }
            return
        }

        val targetUser = cachedUser.offlineUser.also {
            if (it.getNameOrNull() == null) {
                commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
                return
            }
        }

        val lastSeen = if (targetUser is User) System.currentTimeMillis() else targetUser.lastSeen
        val formattedDate = parseDate(lastSeen)

        commandSender.commandMsg("Seen.Success") {
            target(targetUser.getNameOrNull())
            postModifier {
                it.replace("<DATE>", formattedDate)
                    .replace("<IP>", targetUser.lastKnownIp ?: "???")
            }
        }
    }

    private fun parseDate(dateMillis: Long): String {
        return Instant.ofEpochMilli(dateMillis)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
    }
}
