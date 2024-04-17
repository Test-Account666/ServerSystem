package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class NightCommand extends CommandUtils implements CommandExecutorOverload {
    private static final String TIME = "night";
    private final TimeCommand timeCommand;

    public NightCommand(ServerSystem plugin, TimeCommand timeCommand) {
        super(plugin);

        this.timeCommand = timeCommand;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        this.timeCommand.ExecuteTime(TIME, commandSender, command, commandLabel, arguments);
        return true;
    }
}
