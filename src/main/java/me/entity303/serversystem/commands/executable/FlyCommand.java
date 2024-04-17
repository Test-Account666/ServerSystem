package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class FlyCommand extends CommandUtils implements CommandExecutorOverload {

    public FlyCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(
                        this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Fly"));
                return true;
            }
            if (!this.plugin.getPermissions().hasPermission(commandSender, "fly.self")) {
                var permission = this.plugin.getPermissions().getPermission("fly.self");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }
            if (!player.getAllowFlight()) {
                player.setAllowFlight(true);

                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Fly.Activated.Self"));
            } else {
                player.setAllowFlight(false);

                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Fly.DeActivated.Self"));
            }
            return true;
        }

        if (!this.plugin.getPermissions().hasPermission(commandSender, "fly.others")) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Fly"));
            return true;
        }

        var target = this.getPlayer(commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }

        if (!target.getAllowFlight()) {
            target.setAllowFlight(true);

            target.sendMessage(this.plugin.getMessages().getPrefix() +
                               this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Fly.Activated.Others.Target"));

            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Fly.Activated.Others.Sender"));
        } else {
            target.setAllowFlight(false);

            target.sendMessage(this.plugin.getMessages().getPrefix() +
                               this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Fly.DeActivated.Others.Target"));

            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Fly.DeActivated.Others.Sender"));
        }
        return true;
    }
}
