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
        var result = message
        var finalTargetName = targetName
        val senderName = commandSender.getNameOrNull()
        if (senderName == null) {
            ServerSystem.log.warning("CommandSender (${commandSender.uuid}) has no name! This should not happen!")
            return result
        }

        if (finalTargetName == null) finalTargetName = senderName

        result = applyColorPlaceholder(commandSender, result, "<Color:Prefix>", "Prefix")
        result = applyColorPlaceholder(commandSender, result, "<Color:Separators>", "Separator")
        result = applyColorPlaceholder(commandSender, result, "<Color:Message>", "Message")
        result = applyColorPlaceholder(commandSender, result, "<Color:Highlight>", "Highlight")
        result = applyColorPlaceholder(commandSender, result, "<Color:Error.Message>", "ErrorMessage")
        result = applyColorPlaceholder(commandSender, result, "<Color:Error.Highlight>", "ErrorHighlight")

        result = result.replace("<SENDER>", senderName)
            .replace("<TARGET>", finalTargetName)
            .replace("<LABEL>", label)

        // ConsoleUser#getPlayer returns null btw
        if (_placeholders) result = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(commandSender.getPlayer(), result)

        return result
    }

    private fun applyColorPlaceholder(user: User, message: String, placeholder: String, colorId: String): String {
        val color = MappingsData.messageColors(user).getMessageColor(colorId)

        if (color == null) {
            ServerSystem.log.warning("${colorId} color could not be found! This should not happen!")
            return message
        }

        return message.replace(placeholder, color)
    }
}