package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MsgToggleCommand extends CommandUtils implements ICommandExecutorOverload {

    public MsgToggleCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (AnvilCommand.HasPermission(commandSender, this._plugin.GetMessages(), this._plugin.GetPermissions(), "msgtoggle"))
            return true;

        if (this._plugin.GetMsgOff().contains(commandSender)) {
            this._plugin.GetMsgOff().remove(commandSender);

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "MsgToggle.Activated"));
            return true;
        }

        this._plugin.GetMsgOff().add((Player) commandSender);

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                  this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "MsgToggle.Deactivated"));
        return true;
    }
}
