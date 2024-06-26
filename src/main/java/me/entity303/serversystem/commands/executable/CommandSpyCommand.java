package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandSpyCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public CommandSpyCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "commandspy")) {
            var permission = this._plugin.GetPermissions().GetPermission("commandspy");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (!this._plugin.GetCommanddSpy().contains(commandSender)) {
            this._plugin.GetCommanddSpy().add((Player) commandSender);

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "CommandSpy.Activated"));
        } else {
            this._plugin.GetCommanddSpy().remove(commandSender);

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "CommandSpy.Deactivated"));
        }
        return true;
    }
}
