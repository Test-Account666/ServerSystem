package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.commands.ITabCompleterOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpeedTabCompleter extends CommandUtils implements ITabCompleterOverload {

    public SpeedTabCompleter(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "speed.general", true))
            return Collections.singletonList("");
        if (arguments.length == 1)
            if (this._plugin.GetPermissions().HasPermission(commandSender, "speed.general", true))
                if (this._plugin.GetPermissions().HasPermission(commandSender, "speed.self", true) ||
                    this._plugin.GetPermissions().HasPermission(commandSender, "speed.others", true))
                    return IntStream.range(1, 11).mapToObj(String::valueOf).collect(Collectors.toList());

        if (arguments.length == 2)
            if (this._plugin.GetPermissions().HasPermission(commandSender, "speed.general", true))
                if (this._plugin.GetPermissions().HasPermission(commandSender, "speed.self", true) ||
                    this._plugin.GetPermissions().HasPermission(commandSender, "speed.others", true)) {
                    List<String> list = new ArrayList<>();
                    List<String> tabList;
                    if (this._plugin.GetPermissions().HasPermission(commandSender, "speed.self", true)) {
                        list.add("walk");
                        list.add("laufen");
                        list.add("walking");
                        list.add("lauf");
                        list.add("run");
                        list.add("running");
                        list.add("gehen");

                        list.add("fly");
                        list.add("flying");
                        list.add("flight");
                        list.add("flug");
                        list.add("fliegen");
                    }

                    if (this._plugin.GetPermissions().HasPermission(commandSender, "speed.others", true))
                        return null;

                    tabList = list.stream().filter(argument -> argument.toLowerCase().startsWith(arguments[1].toLowerCase())).collect(Collectors.toList());

                    if (tabList.isEmpty())
                        return list;
                    return tabList;
                }

        if (arguments.length == 3) {
            List<String> list = new ArrayList<>();
            List<String> tabList;
            if (this._plugin.GetPermissions().HasPermission(commandSender, "speed.others", true)) {
                list.add("walk");
                list.add("laufen");
                list.add("walking");
                list.add("lauf");
                list.add("run");
                list.add("running");
                list.add("gehen");

                list.add("fly");
                list.add("flying");
                list.add("flight");
                list.add("flug");
                list.add("fliegen");
            }

            tabList = list.stream().filter(argument -> argument.toLowerCase().startsWith(arguments[2].toLowerCase())).collect(Collectors.toList());

            if (tabList.isEmpty())
                return list;
            return tabList;
        }
        return Collections.singletonList("");
    }
}
