package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.tabcompleter.WorldTabCompleter;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class WeatherCommand extends CommandUtils implements CommandExecutorOverload {

    public WeatherCommand(ServerSystem plugin) {
        super(plugin);

        this.plugin.getCommandManager().registerCommand("sun", new SunCommand(this.plugin, this), new WorldTabCompleter());
        this.plugin.getCommandManager().registerCommand("rain", new RainCommand(this.plugin, this), new WorldTabCompleter());
    }

    @SuppressWarnings("DuplicatedCode")
    public void ExecuteWeather(String weather, CommandSender commandSender, Command command, String commandLabel, String... arguments) {
        if (arguments.length == 0)
            arguments = new String[] { weather };
        else if (arguments.length == 1)
            arguments = new String[] { weather, arguments[0] };
        else {
            List<String> argumentList = new LinkedList<>();

            Collections.addAll(argumentList, arguments);

            argumentList.add(0, weather);

            arguments = argumentList.toArray(new String[0]);
        }

        this.onCommand(commandSender, command, commandLabel, arguments);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "weather")) {
            var permission = this.plugin.getPermissions().getPermission("weather");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Weather"));
            return true;
        }

        if (arguments.length == 1) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(
                        this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Weather"));
                return true;
            }
            this.handleWeatherCommand(commandSender, arguments[0], ((Player) commandSender).getWorld(), command, commandLabel);
            return true;
        }

        if (arguments.length == 2) {
            var world = Bukkit.getWorld(arguments[1]);
            if (world == null) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                             .getMessage(commandLabel, command, commandSender, null,
                                                                                                         "Weather.NoWorld")
                                                                                             .replace("<WORLD>", arguments[1]));
                return true;
            }
            this.handleWeatherCommand(commandSender, arguments[0], world, command, commandLabel);
            return true;
        }

        return false;
    }

    private void handleWeatherCommand(CommandSender commandSender, String argument, World world, Command command, String commandLabel) {
        var weatherState = switch (argument.toLowerCase()) {
            case "storm", "sturm", "regen", "rain" -> "storm";
            case "sun", "sonne", "clear", "klar" -> "clear";
            default -> "unknown";
        };

        if ("unknown".equals(weatherState)) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Weather"));
            return;
        }

        var isStorm = "storm".equals(weatherState);

        world.setStorm(isStorm);

        var messageKey = isStorm? "Weather.RainStarted" : "Weather.RainStopped";
        var message = this.plugin.getMessages().getPrefix() +
                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, messageKey).replace("<WORLD>", world.getName());
        commandSender.sendMessage(message);
    }


}
