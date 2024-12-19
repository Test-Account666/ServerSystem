package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.commands.ITabCompleterOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpeedTabCompleter implements ITabCompleterOverload {

    protected final ServerSystem _plugin;

    public SpeedTabCompleter(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "speed.general", true)) return Collections.singletonList("");

        if (arguments.length == 1) {
            if (this._plugin.GetPermissions().HasPermission(commandSender, "speed.general", true)) {
                if (this._plugin.GetPermissions().HasPermission(commandSender, "speed.self", true) ||
                    this._plugin.GetPermissions().HasPermission(commandSender, "speed.others", true)) {
                    return IntStream.range(1, 11).mapToObj(String::valueOf).collect(Collectors.toList());
                }
            }
        }

        if (arguments.length == 2) {
            if (this._plugin.GetPermissions().HasPermission(commandSender, "speed.general", true)) {
                if (this._plugin.GetPermissions().HasPermission(commandSender, "speed.self", true) ||
                    this._plugin.GetPermissions().HasPermission(commandSender, "speed.others", true)) {
                    var list = new ArrayList<String>();
                    if (this._plugin.GetPermissions().HasPermission(commandSender, "speed.self", true)) {
                        list.add("walk");
                        list.add("walking");
                        list.add("run");
                        list.add("running");

                        list.add("fly");
                        list.add("flying");
                        list.add("flight");
                    }

                    if (this._plugin.GetPermissions().HasPermission(commandSender, "speed.others", true)) return null;

                    var tabList = new ArrayList<String>();
                    for (var argument : list) {
                        if (!argument.toLowerCase().startsWith(arguments[1].toLowerCase())) continue;

                        tabList.add(argument);
                    }

                    if (tabList.isEmpty()) return list;
                    return tabList;
                }
            }
        }

        if (arguments.length == 3) {
            var list = new ArrayList<String>();
            if (this._plugin.GetPermissions().HasPermission(commandSender, "speed.others", true)) {
                list.add("walk");
                list.add("walking");
                list.add("run");
                list.add("running");

                list.add("fly");
                list.add("flying");
                list.add("flight");
            }

            var tabList = new ArrayList<String>();
            for (var argument : list) {
                if (!argument.toLowerCase().startsWith(arguments[2].toLowerCase())) continue;

                tabList.add(argument);
            }

            if (tabList.isEmpty()) return list;
            return tabList;
        }

        return Collections.singletonList("");
    }
}
