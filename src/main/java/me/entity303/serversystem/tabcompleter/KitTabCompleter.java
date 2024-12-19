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
        if (arguments.length != 1) return null;

        var kits = new ArrayList<>(this._plugin.GetKitsManager().GetKitNames());
        var tab = new ArrayList<String>();
        for (var kit : kits) {
            if (!this._plugin.GetKitsManager().IsKitAllowed(commandSender, kit, false, true)) continue;

            tab.add(kit);
        }
        return tab;
    }
}
