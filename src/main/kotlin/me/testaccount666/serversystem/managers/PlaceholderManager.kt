package me.testaccount666.serversystem.managers

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.managers.messages.MappingsData
import me.testaccount666.serversystem.userdata.User

private val _COLOR_KEYS = listOf(
    "<Color:Prefix>" to "Prefix",
    "<Color:Separators>" to "Separator",
    "<Color:Message>" to "Message",
    "<Color:Highlight>" to "Highlight",
    "<Color:Error.Message>" to "ErrorMessage",
    "<Color:Error.Highlight>" to "ErrorHighlight"
)
private val _placeholderApi by lazy { runCatching { Class.forName("me.clip.placeholderapi.PlaceholderAPI") }.isSuccess }

class PlaceholderManager {
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
        val senderName = commandSender.nameSafe
        val finalTargetName = targetName ?: senderName

        result = _COLOR_KEYS.fold(result) { acc, (placeholder, key) ->
            applyColorPlaceholder(commandSender, acc, placeholder, key)
        }

        val placeholders = listOf(
            "<SENDER>" to senderName,
            "<TARGET>" to finalTargetName,
            "<LABEL>" to label
        )

        result = placeholders.fold(result) { acc, (placeholder, value) ->
            acc.replace(placeholder, value)
        }

        // ConsoleUser#getPlayer returns null btw
        if (_placeholderApi) result = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(commandSender.getPlayer(), result)

        return result
    }

    private fun applyColorPlaceholder(user: User, message: String, placeholder: String, colorId: String): String {
        val color = MappingsData.messageColors(user).getMessageColor(colorId) ?: run {
            ServerSystem.log.warning("${colorId} color could not be found! This should not happen!")
            return message
        }

        return message.replace(placeholder, color)
    }
}