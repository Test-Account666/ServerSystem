package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ServerSystemCommand(name = "Drop")
public class DropCommand implements ICommandExecutorOverload {
    private final ServerSystem _plugin;

    public DropCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (this._plugin.GetVanish().GetAllowDrop().contains(commandSender)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "Drop.DeActivated"));
            this._plugin.GetVanish().GetAllowDrop().remove(commandSender);
        } else {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "Drop.Activated"));
            this._plugin.GetVanish().GetAllowDrop().add(((Player) commandSender));
        }
        return true;
    }
}
