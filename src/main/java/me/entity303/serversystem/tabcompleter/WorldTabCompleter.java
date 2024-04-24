package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.commands.ITabCompleterOverload;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class WorldTabCompleter implements ITabCompleterOverload {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (command.getName().equalsIgnoreCase("time")) {
            var possibleTimes = new ArrayList<>(List.of("day", "noon", "night", "tag", "mittag", "nacht"));
            if (arguments.length == 1) {
                List<String> tabCompletion = new ArrayList<>(possibleTimes);

                tabCompletion.removeIf(time -> !time.startsWith(arguments[0].toLowerCase()));

                return tabCompletion.isEmpty()? possibleTimes : tabCompletion;
            }

            if (arguments.length != 2)
                return null;

            return this.GetWorlds(arguments[1]);
        }

        if (command.getName().equalsIgnoreCase("day")) {
            if (arguments.length != 1)
                return null;

            return this.GetWorlds(arguments[0]);
        }

        if (command.getName().equalsIgnoreCase("noon")) {
            if (arguments.length != 1)
                return null;

            return this.GetWorlds(arguments[0]);
        }
        if (command.getName().equalsIgnoreCase("night")) {
            if (arguments.length != 1)
                return null;

            return this.GetWorlds(arguments[0]);

        }
        if (command.getName().equalsIgnoreCase("sun")) {
            if (arguments.length != 1)
                return null;

            return this.GetWorlds(arguments[0]);

        }
        if (command.getName().equalsIgnoreCase("rain")) {
            if (arguments.length != 1)
                return null;

            return this.GetWorlds(arguments[0]);
        }

        if (!command.getName().equalsIgnoreCase("weather"))
            return null;

        if (arguments.length == 1) {
            var weatherStates = List.of("storm", "sturm", "regen", "rain", "sun", "sonne", "clear", "klar");

            var tabCompletion = new ArrayList<>(weatherStates);

            tabCompletion.removeIf(weatherState -> !weatherState.startsWith(arguments[0].toLowerCase()));

            return tabCompletion.isEmpty()? weatherStates : tabCompletion;
        }

        if (arguments.length != 2)
            return null;

        return this.GetWorlds(arguments[1]);

    }

    private List<String> GetWorlds(String argument) {
        var worlds = Bukkit.getWorlds().stream().map(World::getName).toList();
        List<String> tabCompletion = new ArrayList<>(worlds);

        tabCompletion.removeIf(world -> !world.toLowerCase().startsWith(argument.toLowerCase()));

        return tabCompletion.isEmpty()? worlds : tabCompletion;
    }
}
