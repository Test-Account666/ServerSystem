package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.commands.ITabCompleterOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class DeleteKitTabCompleter implements ITabCompleterOverload {

    protected final ServerSystem _plugin;

    public DeleteKitTabCompleter(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 1)
            if (this._plugin.GetPermissions().HasPermission(commandSender, "deletekit", true))
                return this._plugin.GetKitsManager().GetKitNames();
        return Collections.singletonList("");
    }
}
