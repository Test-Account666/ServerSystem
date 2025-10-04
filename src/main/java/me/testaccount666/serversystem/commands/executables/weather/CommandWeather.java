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

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "weather", variants = {"sun", "storm", "rain"}, tabCompleter = TabCompleterWeather.class)
public class CommandWeather extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (command.getName().equalsIgnoreCase("weather")) {
            handleWeatherCommand(commandSender, command, label, arguments);
            return;
        }

        if (commandSender instanceof ConsoleUser && arguments.length == 0) {
            general("InvalidArguments", commandSender).syntax(getSyntaxPath(command)).label(label).build();
            return;
        }

        World world;

        if (arguments.length >= 1) {
            if (!checkBasePermission(commandSender, "Weather.World")) return;
            world = Bukkit.getWorld(arguments[0]);
        } else world = commandSender.getPlayer().getWorld();

        if (world == null) {
            command("Weather.WorldNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        handleWeatherCommand(commandSender, command, label, command.getName(), world.getName());
    }

    private void handleWeatherCommand(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Weather.Use")) return;

        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).syntax(getSyntaxPath(command)).label(label).build();
            return;
        }

        if (commandSender instanceof ConsoleUser && arguments.length == 1) {
            general("InvalidArguments", commandSender).syntax(getSyntaxPath(command)).label(label).build();
            return;
        }

        World world;

        if (arguments.length >= 2) {
            if (!checkBasePermission(commandSender, "Weather.World")) return;
            world = Bukkit.getWorld(arguments[1]);
        } else world = commandSender.getPlayer().getWorld();

        if (world == null) {
            command("Weather.WorldNotFound", commandSender).target(arguments[1]).build();
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
                general("InvalidArguments", commandSender).syntax(getSyntaxPath(command)).label(label).build();
                return;
            }
        }

        command("Weather.Success", commandSender).target(world.getName())
                .postModifier(message -> message.replace("<WEATHER>", arguments[0])
                        .replace("<WORLD>", world.getName()));
    }

    @Override
    public String getSyntaxPath(Command command) {
        var commandName = command.getName().toLowerCase();
        return switch (commandName) {
            case "weather" -> "Weather";
            case "sun" -> "Sun";
            case "storm" -> "Storm";
            case "rain" -> "Rain";
            default -> throw new IllegalStateException("(CommandWeather) Unexpected value: ${commandName}");
        };
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Weather.Use", false);
    }
}
