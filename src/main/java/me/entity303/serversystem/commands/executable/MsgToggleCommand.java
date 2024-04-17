package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MsgToggleCommand extends CommandUtils implements CommandExecutorOverload {

    public MsgToggleCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (!this.plugin.getPermissions().hasPermission(commandSender, "msgtoggle")) {
            var permission = this.plugin.getPermissions().getPermission("msgtoggle");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (this.plugin.getMsgOff().contains(commandSender)) {
            this.plugin.getMsgOff().remove(commandSender);

            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "MsgToggle.Activated"));
            return true;
        }

        this.plugin.getMsgOff().add((Player) commandSender);

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                  this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "MsgToggle.Deactivated"));
        return true;
    }
}
