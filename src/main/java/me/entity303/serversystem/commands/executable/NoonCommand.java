package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class NoonCommand extends CommandUtils implements CommandExecutorOverload {
    private static final String TIME = "noon";
    private final TimeCommand timeCommand;

    public NoonCommand(ServerSystem plugin, TimeCommand timeCommand) {
        super(plugin);

        this.timeCommand = timeCommand;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        this.timeCommand.ExecuteTime(TIME, commandSender, command, commandLabel, arguments);
        return true;
    }
}
