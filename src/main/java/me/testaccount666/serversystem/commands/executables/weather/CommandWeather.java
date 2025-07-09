package me.testaccount666.serversystem.commands.executables.weather;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

@ServerSystemCommand(name = "weather", variants = {"sun", "storm", "rain"}, tabCompleter = TabCompleterWeather.class)
public class CommandWeather extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (command.getName().equalsIgnoreCase("weather")) {
            handleWeatherCommand(commandSender, label, arguments);
            return;
        }

        if (commandSender instanceof ConsoleUser && arguments.length == 0) {
            sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
            return;
        }

        World world;

        if (arguments.length >= 1) {
            if (!checkBasePermission(commandSender, "Weather.World", label)) return;
            world = Bukkit.getWorld(arguments[0]);
        } else world = commandSender.getPlayer().getWorld();

        if (world == null) {
            sendCommandMessage(commandSender, "Weather.WorldNotFound", arguments[0], label, null);
            return;
        }

        handleWeatherCommand(commandSender, label, command.getName(), world.getName());
    }

    private void handleWeatherCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Weather.Use", label)) return;

        if (arguments.length == 0) {
            sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
            return;
        }

        if (commandSender instanceof ConsoleUser && arguments.length == 1) {
            sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
            return;
        }

        World world;

        if (arguments.length >= 2) {
            if (!checkBasePermission(commandSender, "Weather.World", label)) return;
            world = Bukkit.getWorld(arguments[1]);
        } else world = commandSender.getPlayer().getWorld();

        if (world == null) {
            sendCommandMessage(commandSender, "Weather.WorldNotFound", arguments[1], label, null);
            return;
        }

        var random = ThreadLocalRandom.current();

        // Random duration between 300 and 900 seconds (5 to 15 minutes)
        var weatherDuration = random.nextInt(300, 900) + 1;
        // Convert to ticks
        weatherDuration *= 20;

        switch (arguments[0].toLowerCase()) {
            case "sun":
            case "clear":
                world.setThundering(false);
                world.setStorm(false);
                world.setClearWeatherDuration(weatherDuration);
                break;
            case "storm":
            case "thunder":
                world.setClearWeatherDuration(0);
                world.setStorm(true);
                world.setThundering(true);
                world.setWeatherDuration(weatherDuration);
                break;
            case "rain":
                world.setClearWeatherDuration(0);
                world.setThundering(false);
                world.setStorm(true);
                world.setWeatherDuration(weatherDuration);
                break;
            default: {
                sendGeneralMessage(commandSender, "InvalidArguments", null, label, null);
                return;
            }
        }

        sendCommandMessage(commandSender, "Weather.Success", world.getName(), label,
                message -> message.replace("<WEATHER>", arguments[0])
                        .replace("<WORLD>", world.getName()));
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Weather.Use");
    }
}
