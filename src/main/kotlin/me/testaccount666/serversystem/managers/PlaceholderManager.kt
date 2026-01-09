package me.testaccount666.serversystem.managers

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.managers.messages.MappingsData
import me.testaccount666.serversystem.userdata.User

class PlaceholderManager {
    private var _placeholders = false

    init {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI")
            _placeholders = true
        } catch (_: Throwable) {
            _placeholders = false
        }
    }

    /**
     * Replaces placeholders in the given message with dynamic values based on the provided
     * `commandSender`, `targetName`, and `label`. The method also integrates
     * with the PlaceholderAPI if enabled and the `commandSender` is a player.
     *
     * @param message       The message containing placeholders to be replaced.
     * @param commandSender The sender of the command whose name can replace placeholders such as `<SENDER>`.
     * @param targetName    The name of the target to replace the `<TARGET>` placeholder. If null, it defaults to the sender's name.
     * @param label         The command label to replace the `<LABEL>` placeholder.
     * @return The message with placeholders replaced by the corresponding values.
     */
    fun applyPlaceholders(message: String, commandSender: User, targetName: String?, label: String): String {
        var message = message
        var targetName = targetName
        if (commandSender.getName().isEmpty) {
            ServerSystem.log.warning("CommandSender (${commandSender.uuid}) has no name! This should not happen!")
            return message
        }

        if (targetName == null) targetName = commandSender.getName().get()

        message = applyColorPlaceholder(commandSender, message, "<Color:Prefix>", "Prefix")
        message = applyColorPlaceholder(commandSender, message, "<Color:Separators>", "Separator")
        message = applyColorPlaceholder(commandSender, message, "<Color:Message>", "Message")
        message = applyColorPlaceholder(commandSender, message, "<Color:Highlight>", "Highlight")
        message = applyColorPlaceholder(commandSender, message, "<Color:Error.Message>", "ErrorMessage")
        message = applyColorPlaceholder(commandSender, message, "<Color:Error.Highlight>", "ErrorHighlight")

        message = message.replace("<SENDER>", commandSender.getName().get())
            .replace("<TARGET>", targetName)
            .replace("<LABEL>", label)

        // ConsoleUser#getPlayer returns null btw
        if (_placeholders) message = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(commandSender.getPlayer(), message)

        return message
    }

    private fun applyColorPlaceholder(user: User, message: String, placeholder: String, colorId: String): String {
        val colorOptional = MappingsData.messageColors(user).getMessageColor(colorId)

        return colorOptional.map { prefixString -> message.replace(placeholder, prefixString) }
            .orElseGet {
                ServerSystem.log.warning("${colorId} color could not be found! This should not happen!")
                message
            }
    }
}