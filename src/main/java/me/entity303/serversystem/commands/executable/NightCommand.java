package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

// Annotation isn't required, as this command will get registered in TimeCommand.
public class NightCommand implements ICommandExecutorOverload {
    private static final String TIME = "night";
    protected final ServerSystem _plugin;
    private final TimeCommand _timeCommand;

    public NightCommand(ServerSystem plugin, TimeCommand timeCommand) {
        this._plugin = plugin;

        this._timeCommand = timeCommand;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        this._timeCommand.ExecuteTime(TIME, commandSender, command, commandLabel, arguments);
        return true;
    }
}
