package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ServerSystemCommand(name = "God")
public class GodCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public GodCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "God"));
                return true;
            }

            if (!this._plugin.GetPermissions().HasPermission(commandSender, "god.self")) {
                var permission = this._plugin.GetPermissions().GetPermission("god.self");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }

            if (this._plugin.GetGodList().contains(commandSender)) {
                this._plugin.GetGodList().remove(commandSender);

                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "God.Self.Deactivated"));
            } else {
                this._plugin.GetGodList().add(((Player) commandSender));

                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "God.Self.Activated"));
            }
            return true;
        }

        if (!this._plugin.GetPermissions().HasPermission(commandSender, "god.others")) {
            var permission = this._plugin.GetPermissions().GetPermission("god.others");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        var targetPlayer = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]);
        if (targetPlayer == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }

        if (this._plugin.GetGodList().contains(targetPlayer)) {
            this._plugin.GetGodList().remove(targetPlayer);
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, targetPlayer, "God.Others.Deactivated.Sender"));

            targetPlayer.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                     this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, targetPlayer, "God.Others.Deactivated.Target"));
        } else {
            this._plugin.GetGodList().add(targetPlayer);
            var command1 = command.getName();
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command1, commandSender, targetPlayer, "God.Others.Activated.Sender"));

            targetPlayer.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                     this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, targetPlayer, "God.Others.Activated.Target"));
        }
        return true;
    }
}
