package me.testaccount666.serversystem.commands.executables.language

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.messages.MessageManager.defaultLanguage
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import org.bukkit.command.Command

@ServerSystemCommand("language", [], TabCompleterLanguages::class)
class CommandLanguage : AbstractServerSystemCommand() {
    private val _messagesDirectory = ServerSystem.instance.dataPath.resolve("messages").toFile()

    override fun minRequiredArguments(command: Command) = 1
    override fun getUsagePermission(command: Command) = "Language.Use"
    override fun getSyntaxPath(command: Command?) = "Language"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!isPlayer(commandSender)) return

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
}
