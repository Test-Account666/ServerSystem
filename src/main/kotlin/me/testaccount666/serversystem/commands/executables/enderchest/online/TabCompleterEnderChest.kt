package me.testaccount666.serversystem.commands.executables.enderchest.online

import me.testaccount666.serversystem.commands.executables.enderchest.offline.TabCompleterOfflineEnderChest
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.Command

class TabCompleterEnderChest : ServerSystemTabCompleter {
    private val _offlineEnderChest = TabCompleterOfflineEnderChest()

    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String>? {
        if (command.name.startsWith("offline", true)) return _offlineEnderChest.tabComplete(
            commandSender, command, label, *arguments
        )

        return if (arguments.size <= 1) null else listOf()
    }
}