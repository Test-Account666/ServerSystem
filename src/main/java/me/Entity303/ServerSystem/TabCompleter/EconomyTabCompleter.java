package me.Entity303.ServerSystem.TabCompleter;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EconomyTabCompleter extends ServerSystemCommand implements TabCompleter {

    public EconomyTabCompleter(ss plugin) {
        super(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            boolean ecoSet = this.isAllowed(cs, "economy.set", true);
            boolean ecoAdd = this.isAllowed(cs, "economy.Give", true);
            boolean ecoTake = this.isAllowed(cs, "economy.set", true);
            if (!ecoSet && !ecoAdd && !ecoTake) return null;
            List<String> possibleCompletions = new ArrayList<>();
            if (ecoSet)
                possibleCompletions.add("set");
            if (ecoAdd)
                possibleCompletions.add("add");
            if (ecoTake)
                possibleCompletions.add("take");
            List<String> completions = possibleCompletions.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
            return completions.isEmpty() ? possibleCompletions : completions;
        }
        return null;
    }
}
