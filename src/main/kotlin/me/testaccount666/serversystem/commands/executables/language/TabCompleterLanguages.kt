package me.testaccount666.serversystem.commands.executables.language

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command
import java.io.File
import java.nio.file.Path

class TabCompleterLanguages : ServerSystemTabCompleter {
    private val _messagesDirectory: File = Path.of("plugins", "ServerSystem", "messages").toFile()

    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String> {
        if (arguments.size != 1) return listOf()

        val files = _messagesDirectory.listFiles()?.mapNotNull { it.name } ?: listOf()
        return files.filter { it.startsWith(arguments[0], true) }
    }
}
