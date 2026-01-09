package me.testaccount666.serversystem.commands.management;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.config.ConfigurationManager;
import me.testaccount666.serversystem.utils.tuples.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

public class CommandReplacer {
    public void replaceCommands() {
        var configManager = ServerSystem.getInstance().getRegistry().getService(ConfigurationManager.class);
        var replaceConfig = configManager.getCommandReplaceConfig();

        if (!replaceConfig.getBoolean("ReplacedCommands.Enabled", false)) return;

        var replaceSection = replaceConfig.getConfigurationSection("ReplacedCommands.Replace");
        if (replaceSection == null) return;

        for (var identifier : replaceSection.getKeys(false)) {
            var section = replaceSection.getConfigurationSection(identifier);
            if (section == null) {
                ServerSystem.getInstance().getLogger().warning("Invalid command replacement identifier '${identifier}' in config.yml (Found no section?!)");
                continue;
            }

            var result = replaceCommands(identifier, section);
            if (result.first) ServerSystem.Companion.getLog().info(result.second);
            else ServerSystem.Companion.getLog().warning(result.second);
        }

        var commandManager = ServerSystem.getInstance().getRegistry().getService(CommandManager.class);
        commandManager.syncCommands();
    }

    private Tuple<Boolean, String> replaceCommands(String identifier, ConfigurationSection section) {
        var commandManager = ServerSystem.getInstance().getRegistry().getService(CommandManager.class);

        var fromCommandName = section.getString("From.Command");
        if (fromCommandName == null)
            return new Tuple<>(false, "Invalid command replacement with identifier '${identifier}': 'From Command' is null!");

        var fromPluginName = section.getString("From.Plugin");
        if (fromPluginName == null)
            return new Tuple<>(false, "Invalid command replacement with identifier '${identifier}': 'From Plugin' is null!");


        var fromPluginResult = verifyPlugin(fromPluginName);
        if (!fromPluginResult.first)
            return new Tuple<>(false, "Invalid command replacement with identifier '${identifier}': ${fromPluginResult.second} (FromPlugin)");

        var fromCommand = commandManager.getCommand("${fromPluginName.toLowerCase()}:${fromCommandName.toLowerCase()}");
        if (fromCommand.isEmpty()) return new Tuple<>(false,
                "Invalid command replacement with identifier '${identifier}': ${fromPluginName}:${fromCommandName} (Command not found in plugin '${fromPluginName}')");


        var toCommandName = section.getString("To.Command");
        if (toCommandName == null)
            return new Tuple<>(false, "Invalid command replacement with identifier '${identifier}': 'To Command' is null!");

        var toCommand = commandManager.getCommand(toCommandName.toLowerCase());
        if (toCommand.isEmpty()) return new Tuple<>(false,
                "Invalid command replacement with identifier '${identifier}': ${toCommandName} (Command not found!)");

        var commandMap = commandManager.getCommandMap();
        commandMap.remove(toCommandName.toLowerCase());
        commandMap.put(toCommandName.toLowerCase(), fromCommand.get());

        return new Tuple<>(true, "Replaced command '${toCommandName}' with '${fromPluginName}:${fromCommandName}'");
    }

    private Tuple<Boolean, String> verifyPlugin(String pluginName) {
        var pluginManager = Bukkit.getPluginManager();
        var plugin = pluginManager.getPlugin(pluginName);

        if (plugin == null) return new Tuple<>(false, "Invalid plugin name '${pluginName}' (Plugin not found!)");
        if (!plugin.isEnabled()) return new Tuple<>(false, "Invalid plugin name '${pluginName}' (Plugin is not enabled!)");

        return new Tuple<>(true, null);
    }
}
