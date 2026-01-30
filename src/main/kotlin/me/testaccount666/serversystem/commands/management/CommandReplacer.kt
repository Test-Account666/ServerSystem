package me.testaccount666.serversystem.commands.management

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.managers.config.ConfigurationManager
import me.testaccount666.serversystem.utils.tuples.Tuple
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection

class CommandReplacer {
    fun replaceCommands() {
        val configManager = instance.registry.getService<ConfigurationManager>()
        val replaceConfig = configManager.commandReplaceConfig

        if (!replaceConfig.getBoolean("ReplacedCommands.Enabled", false)) return

        val replaceSection = replaceConfig.getConfigurationSection("ReplacedCommands.Replace") ?: return

        for (identifier in replaceSection.getKeys(false)) {
            val section = replaceSection.getConfigurationSection(identifier) ?: run {
                instance.logger.warning("Invalid command replacement identifier '${identifier}' in config.yml (Found no section?!)")
                continue
            }

            val (success, message) = replaceCommands(identifier, section)
            if (success) log.info(message)
            else log.warning(message)
        }

        instance.registry.getService<CommandManager>().syncCommands()
    }

    private fun replaceCommands(identifier: String, section: ConfigurationSection): Tuple<Boolean, String> {
        val commandManager = instance.registry.getService<CommandManager>()

        val fromCommandName = section.getString("From.Command") ?: return Tuple(
            false,
            "Invalid command replacement with identifier '${identifier}': 'From Command' is null!"
        )

        val fromPluginName = section.getString("From.Plugin") ?: return Tuple(
            false,
            "Invalid command replacement with identifier '${identifier}': 'From Plugin' is null!"
        )


        val (success, message) = verifyPlugin(fromPluginName)
        if (!success) return Tuple(
            false,
            "Invalid command replacement with identifier '${identifier}': ${message} (FromPlugin)"
        )

        val fromCommand = commandManager.getCommand(
            "${
                fromPluginName.lowercase()
            }:${fromCommandName.lowercase()}"
        ) ?: return Tuple(
            false,
            "Invalid command replacement with identifier '${identifier}': ${fromPluginName}:${fromCommandName} (Command not found in plugin '${fromPluginName}')"
        )


        val toCommandName = section.getString("To.Command") ?: return Tuple(
            false,
            "Invalid command replacement with identifier '${identifier}': 'To Command' is null!"
        )

        commandManager.getCommand(toCommandName.lowercase()) ?: return Tuple(
            false,
            "Invalid command replacement with identifier '${identifier}': ${toCommandName} (Command not found!)"
        )


        val commandMap = commandManager.commandMap
        commandMap.remove(toCommandName.lowercase())
        commandMap[toCommandName.lowercase()] = fromCommand

        return Tuple(true, "Replaced command '${toCommandName}' with '${fromPluginName}:${fromCommandName}'")
    }

    private fun verifyPlugin(pluginName: String): Tuple<Boolean, String?> {
        val pluginManager = Bukkit.getPluginManager()
        val plugin =
            pluginManager.getPlugin(pluginName) ?: return Tuple(false, "Invalid plugin name '${pluginName}' (Plugin not found!)")

        if (!plugin.isEnabled) return Tuple(false, "Invalid plugin name '${pluginName}' (Plugin is not enabled!)")

        return Tuple(true, null)
    }
}
