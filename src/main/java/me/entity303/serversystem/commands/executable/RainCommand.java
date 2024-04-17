package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class RainCommand extends CommandUtils implements CommandExecutorOverload {
    private final static String WEATHER = "rain";
    private final WeatherCommand weatherCommand;

    public RainCommand(ServerSystem plugin, WeatherCommand weatherCommand) {
        super(plugin);

        this.weatherCommand = weatherCommand;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        this.weatherCommand.ExecuteWeather(WEATHER, commandSender, command, commandLabel, arguments);
        return true;
    }
}
