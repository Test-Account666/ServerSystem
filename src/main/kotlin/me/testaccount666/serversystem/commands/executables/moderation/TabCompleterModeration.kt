package me.testaccount666.serversystem.commands.executables.moderation

import com.destroystokyo.paper.profile.PlayerProfile
import io.papermc.paper.ban.BanListType
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.commands.interfaces.getService
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.CachedUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import org.bukkit.BanEntry
import org.bukkit.Bukkit
import org.bukkit.command.Command

class TabCompleterModeration : ServerSystemTabCompleter {
    override fun tabComplete(commandSender: User, command: Command, label: String, vararg arguments: String): List<String>? {
        val commandName = command.name.lowercase()
        if (!hasPermission(commandSender, commandName)) return listOf()

        return when (arguments.size) {
            1 -> handlePlayerNameCompletion(commandName, arguments[0])
            2 -> handleTimeCompletion(arguments[1], commandName)
            else -> null
        }
    }

    private fun hasPermission(commandSender: User, commandName: String): Boolean {
        val permissionNode = when (commandName) {
            "mute" -> "Moderation.Mute.Use"
            "unmute" -> "Moderation.Mute.Remove"
            "shadowmute" -> "Moderation.Mute.Shadow"
            "ban" -> "Moderation.Ban.Use"
            "unban" -> "Moderation.Ban.Remove"
            else -> null
        }
        return hasCommandPermission(commandSender, permissionNode, false)
    }

    private fun handlePlayerNameCompletion(commandName: String, argument: String): List<String> {
        val possibleCompletions = getPlayerNames(commandName)
        if (argument.isEmpty()) return possibleCompletions

        return possibleCompletions.filter { it.startsWith(argument, true) }
    }

    private fun getPlayerNames(commandName: String): List<String> {
        if (commandName.equals("unban", true)) {
            return Bukkit.getServer().getBanList(BanListType.PROFILE)
                .getEntries<BanEntry<PlayerProfile>>()
                .map { it.getBanTarget() }.mapNotNull { it.name }
        }

        if (commandName.equals("unmute", true)) {
            return getService<UserManager>()
                .cachedUsers.asSequence()
                .map(CachedUser::offlineUser).filter { it.muteManager.hasActiveModeration() }
                .mapNotNull { it.getNameOrNull() }.toList()
        }

        return Bukkit.getOfflinePlayers().mapNotNull { it.name }
    }

    private fun handleTimeCompletion(argument: String, commandName: String): List<String>? {
        if (isRemoveCommand(commandName)) return null
        if (argument.isEmpty()) return defaultTimeSuggestions

        val suggestions = mutableListOf<String>()

        if (_PERMANENT.startsWith(argument, true)) suggestions.add(_PERMANENT)

        if (argument.matches(".*\\d$".toRegex()) || argument.matches("\\d+".toRegex())) {
            suggestions.addAll(getTimeSuggestions(argument))
        } else {
            suggestions.addAll(getMatchingTimeUnits(argument))
        }

        return suggestions
    }

    private val defaultTimeSuggestions by lazy { _NUMBERS + _PERMANENT }

    private fun getTimeSuggestions(argument: String) = _NUMBERS.map { argument + it } + _TIME_UNITS.map { argument + it }

    private fun getMatchingTimeUnits(argument: String) = _TIME_UNITS.filter { it.startsWith(argument, true) }

    private fun isRemoveCommand(commandName: String) = commandName.startsWith("un", true)

    companion object {
        private val _TIME_UNITS = listOf("s", "m", "h", "d", "w", "mo", "y")
        private val _NUMBERS = (0 until 10).map { it.toString() }

        private const val _PERMANENT = "permanent"
    }
}
