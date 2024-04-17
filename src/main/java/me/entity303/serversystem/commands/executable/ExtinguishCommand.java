package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExtinguishCommand extends CommandUtils implements CommandExecutorOverload {

    public ExtinguishCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(
                        this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Extinguish"));
                return true;
            }

            if (!this.plugin.getPermissions().hasPermission(player, "extinguish.self")) {
                var permission = this.plugin.getPermissions().getPermission("extinguish.self");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }

            player.setFireTicks(0);

            player.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Extinguish.Self"));
            return true;
        }

        if (!this.plugin.getPermissions().hasPermission(commandSender, "extinguish.others")) {
            var permission = this.plugin.getPermissions().getPermission("extinguish.others");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        var target = this.getPlayer(commandSender, arguments[0]);
        if (target == null)
            return true;

        target.setFireTicks(0);

        target.sendMessage(this.plugin.getMessages().getPrefix() +
                           this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Extinguish.Others.Target"));

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                  this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Extinguish.Others.Sender"));
        return true;
    }

    public String getPrefix() {
        return this.plugin.getMessages().getPrefix();
    }
}
