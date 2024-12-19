package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.commands.ITabCompleterOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerSystemTabCompleter implements ITabCompleterOverload {

    protected final ServerSystem _plugin;

    public ServerSystemTabCompleter(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "serversystem.use", true)) return Collections.singletonList("");
        if (arguments.length != 1) return Collections.singletonList("");

        var options = new ArrayList<String>();
        if (this._plugin.GetPermissions().HasPermission(commandSender, "serversystem.version", true)) options.add("version");
        if (this._plugin.GetPermissions().HasPermission(commandSender, "serversystem.reload", true)) options.add("reload");
        if (this._plugin.GetPermissions().HasPermission(commandSender, "serversystem.update", true)) options.add("update");

        var tab = new ArrayList<String>();

        for (var option : options) {
            if (!option.toLowerCase().startsWith(arguments[0].toLowerCase())) continue;

            tab.add(option);
        }
        return !tab.isEmpty()? tab : options;

    }
}
