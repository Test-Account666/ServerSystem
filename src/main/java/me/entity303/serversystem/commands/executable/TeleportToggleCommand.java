package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ServerSystemCommand(name = "TeleportToggle")
public class TeleportToggleCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public TeleportToggleCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "tptoggle.self")) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("tptoggle.self")));
                return true;
            }
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command.getName(), commandSender, null, "TpToggle"));
                return true;
            }
            if (this._plugin.GetWantsTeleport().DoesPlayerWantTeleport(((Player) commandSender))) {
                this._plugin.GetWantsTeleport().SetWantsTeleport(((Player) commandSender), false);
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "TpToggle.Self.DeActivated"));
            } else {
                this._plugin.GetWantsTeleport().SetWantsTeleport(((Player) commandSender), true);
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "TpToggle.Self.Activated"));
            }
            return true;
        }
        var targetPlayer = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]);
        if (targetPlayer == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }

        if (this._plugin.GetWantsTeleport().DoesPlayerWantTeleport(targetPlayer)) {
            this._plugin.GetWantsTeleport().SetWantsTeleport(targetPlayer, false);
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command.getName(), commandSender, targetPlayer,
                                                                                                       "TpToggle.Others.DeActivated.Sender"));
            targetPlayer.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                          .GetMessage(commandLabel, command.getName(), commandSender, targetPlayer,
                                                                                                      "TpToggle.Others.DeActivated.Target"));
        } else {
            this._plugin.GetWantsTeleport().SetWantsTeleport(targetPlayer, true);
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command.getName(), commandSender, targetPlayer,
                                                                                                       "TpToggle.Others.Activated.Sender"));
            targetPlayer.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                          .GetMessage(commandLabel, command.getName(), commandSender, targetPlayer,
                                                                                                      "TpToggle.Others.Activated.Target"));
        }
        return true;
    }
}
