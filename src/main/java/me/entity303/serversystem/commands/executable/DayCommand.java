package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DayCommand extends CommandUtils implements ICommandExecutorOverload {
    private static final String TIME = "day";
    private final TimeCommand _timeCommand;

    public DayCommand(ServerSystem plugin, TimeCommand timeCommand) {
        super(plugin);

        this._timeCommand = timeCommand;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        this._timeCommand.ExecuteTime(TIME, commandSender, command, commandLabel, arguments);
        return true;
    }
}
