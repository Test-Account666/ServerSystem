package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GodCommand extends CommandUtils implements CommandExecutorOverload {

    public GodCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(
                        this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "God"));
                return true;
            }

            if (!this.plugin.getPermissions().hasPermission(commandSender, "god.self")) {
                var permission = this.plugin.getPermissions().getPermission("god.self");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }

            if (this.plugin.getGodList().contains(commandSender)) {
                this.plugin.getGodList().remove(commandSender);

                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "God.Self.Deactivated"));
            } else {
                this.plugin.getGodList().add(((Player) commandSender));

                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "God.Self.Activated"));
            }
            return true;
        }

        if (!this.plugin.getPermissions().hasPermission(commandSender, "god.others")) {
            var permission = this.plugin.getPermissions().getPermission("god.others");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        var targetPlayer = this.getPlayer(commandSender, arguments[0]);
        if (targetPlayer == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }

        if (this.plugin.getGodList().contains(targetPlayer)) {
            this.plugin.getGodList().remove(targetPlayer);
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, targetPlayer, "God.Others.Deactivated.Sender"));

            targetPlayer.sendMessage(this.plugin.getMessages().getPrefix() +
                                     this.plugin.getMessages().getMessage(commandLabel, command, commandSender, targetPlayer, "God.Others.Deactivated.Target"));
        } else {
            this.plugin.getGodList().add(targetPlayer);
            var command1 = command.getName();
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command1, commandSender, targetPlayer, "God.Others.Activated.Sender"));

            targetPlayer.sendMessage(this.plugin.getMessages().getPrefix() +
                                     this.plugin.getMessages().getMessage(commandLabel, command, commandSender, targetPlayer, "God.Others.Activated.Target"));
        }
        return true;
    }
}
