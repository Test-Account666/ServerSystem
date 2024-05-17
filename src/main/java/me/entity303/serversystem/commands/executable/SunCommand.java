package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


public class SunCommand implements ICommandExecutorOverload {
    private final static String WEATHER = "sun";
    protected final ServerSystem _plugin;
    private final WeatherCommand _weatherCommand;

    public SunCommand(ServerSystem plugin, WeatherCommand weatherCommand) {
        this._plugin = plugin;

        this._weatherCommand = weatherCommand;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        this._weatherCommand.ExecuteWeather(WEATHER, commandSender, command, commandLabel, arguments);
        return true;
    }
}
