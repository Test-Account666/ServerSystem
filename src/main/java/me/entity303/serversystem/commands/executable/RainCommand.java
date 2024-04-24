package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class RainCommand extends CommandUtils implements ICommandExecutorOverload {
    private final static String WEATHER = "rain";
    private final WeatherCommand _weatherCommand;

    public RainCommand(ServerSystem plugin, WeatherCommand weatherCommand) {
        super(plugin);

        this._weatherCommand = weatherCommand;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        this._weatherCommand.ExecuteWeather(WEATHER, commandSender, command, commandLabel, arguments);
        return true;
    }
}
