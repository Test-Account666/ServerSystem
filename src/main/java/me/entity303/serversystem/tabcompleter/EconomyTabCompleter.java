package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EconomyTabCompleter extends CommandUtils implements TabCompleter {

    public EconomyTabCompleter(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            var ecoSet = this.plugin.getPermissions().hasPermission(cs, "economy.set", true);
            var ecoAdd = this.plugin.getPermissions().hasPermission(cs, "economy.Give", true);
            var ecoTake = this.plugin.getPermissions().hasPermission(cs, "economy.set", true);
            if (!ecoSet && !ecoAdd && !ecoTake)
                return null;
            List<String> possibleCompletions = new ArrayList<>();
            if (ecoSet)
                possibleCompletions.add("set");
            if (ecoAdd)
                possibleCompletions.add("add");
            if (ecoTake)
                possibleCompletions.add("take");
            var completions = possibleCompletions.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
            return completions.isEmpty()? possibleCompletions : completions;
        }
        return null;
    }
}
