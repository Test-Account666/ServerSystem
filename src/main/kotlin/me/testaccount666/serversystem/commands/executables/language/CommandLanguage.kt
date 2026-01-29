package me.testaccount666.serversystem.commands.executables.language

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.managers.messages.MessageManager.defaultLanguage
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.command.Command
import org.bukkit.entity.Player

@ServerSystemCommand("language", [], TabCompleterLanguages::class)
class CommandLanguage : AbstractServerSystemCommand() {
    private val _messagesDirectory = ServerSystem.instance.dataPath.resolve("messages").toFile()

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Language.Use")) return
        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        if (arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val languages = _messagesDirectory.listFiles() ?: arrayOf()
        var selectedLanguage = defaultLanguage

        for (file in languages) {
            val fileName = file.name
            if (!fileName.startsWith(arguments[0], true)) continue

            selectedLanguage = fileName
        }

        commandSender.playerLanguage = selectedLanguage.lowercase()
        commandSender.isUsesDefaultLanguage = false
        commandSender.save()
        val chars = selectedLanguage.toCharArray()
        chars[0] = chars[0].uppercaseChar()
        selectedLanguage = String(chars)

        command("Language.Changed", commandSender) {
            postModifier { it.replace("<LANGUAGE>", selectedLanguage) }
        }.build()
    }

    override fun getSyntaxPath(command: Command?) = "Language"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Language.Use", false)
    }
}
