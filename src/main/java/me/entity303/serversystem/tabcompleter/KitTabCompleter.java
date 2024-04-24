package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.commands.ITabCompleterOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class KitTabCompleter implements ITabCompleterOverload {
    private final ServerSystem _plugin;

    public KitTabCompleter(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 1) {
            List<String> kits = new ArrayList<>(this._plugin.GetKitsManager().GetKitNames());
            List<String> tab = new ArrayList<>();
            for (var kit : kits)
                if (this._plugin.GetKitsManager().IsKitAllowed(commandSender, kit, false, true))
                    tab.add(kit);
            return tab;
        }
        return null;
    }
}
