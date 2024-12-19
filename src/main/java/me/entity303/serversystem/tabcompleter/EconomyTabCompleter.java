package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.commands.ITabCompleterOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EconomyTabCompleter implements ITabCompleterOverload {
    protected final ServerSystem _plugin;

    public EconomyTabCompleter(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 1) {
            var ecoSet = this._plugin.GetPermissions().HasPermission(commandSender, "economy.set", true);
            var ecoAdd = this._plugin.GetPermissions().HasPermission(commandSender, "economy.Give", true);
            var ecoTake = this._plugin.GetPermissions().HasPermission(commandSender, "economy.set", true);
            if (!ecoSet && !ecoAdd && !ecoTake) return null;
            List<String> possibleCompletions = new ArrayList<>();
            if (ecoSet) possibleCompletions.add("set");
            if (ecoAdd) possibleCompletions.add("add");
            if (ecoTake) possibleCompletions.add("take");
            var completions = possibleCompletions.stream().filter(argument -> argument.toLowerCase().startsWith(arguments[0].toLowerCase())).collect(Collectors.toList());
            return completions.isEmpty()? possibleCompletions : completions;
        }
        return null;
    }
}
