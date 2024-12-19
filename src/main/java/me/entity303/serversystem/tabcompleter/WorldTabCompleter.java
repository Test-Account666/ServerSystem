package me.entity303.serversystem.tabcompleter;

import me.entity303.serversystem.commands.ITabCompleterOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class WorldTabCompleter implements ITabCompleterOverload {
    private static final List<String> POSSIBLE_TIMES = List.of("day", "noon", "night");
    private static final List<String> WEATHER_STATES = List.of("storm", "rain", "sun", "clear");

    protected final ServerSystem _plugin;

    public WorldTabCompleter(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (command.getName().equalsIgnoreCase("time")) {
            if (arguments.length == 1) {
                var tabCompletion = new ArrayList<>(POSSIBLE_TIMES);

                tabCompletion.removeIf(time -> !time.startsWith(arguments[0].toLowerCase()));

                return tabCompletion.isEmpty()? POSSIBLE_TIMES : tabCompletion;
            }

            if (arguments.length != 2) return null;

            return this.GetWorlds(arguments[1]);
        }

        if (command.getName().equalsIgnoreCase("day")) {
            if (arguments.length != 1) return null;

            return this.GetWorlds(arguments[0]);
        }

        if (command.getName().equalsIgnoreCase("noon")) {
            if (arguments.length != 1) return null;

            return this.GetWorlds(arguments[0]);
        }
        if (command.getName().equalsIgnoreCase("night")) {
            if (arguments.length != 1) return null;

            return this.GetWorlds(arguments[0]);

        }
        if (command.getName().equalsIgnoreCase("sun")) {
            if (arguments.length != 1) return null;

            return this.GetWorlds(arguments[0]);

        }
        if (command.getName().equalsIgnoreCase("rain")) {
            if (arguments.length != 1) return null;

            return this.GetWorlds(arguments[0]);
        }

        if (!command.getName().equalsIgnoreCase("weather")) return null;

        if (arguments.length == 1) {
            var tabCompletion = new ArrayList<>(WEATHER_STATES);

            tabCompletion.removeIf(weatherState -> !weatherState.startsWith(arguments[0].toLowerCase()));

            return tabCompletion.isEmpty()? WEATHER_STATES : tabCompletion;
        }

        if (arguments.length != 2) return null;

        return this.GetWorlds(arguments[1]);

    }

    private List<String> GetWorlds(String argument) {
        var worlds = Bukkit.getWorlds().stream().map(World::getName).toList();
        var tabCompletion = new ArrayList<>(worlds);

        tabCompletion.removeIf(world -> !world.toLowerCase().startsWith(argument.toLowerCase()));

        return tabCompletion.isEmpty()? worlds : tabCompletion;
    }
}
