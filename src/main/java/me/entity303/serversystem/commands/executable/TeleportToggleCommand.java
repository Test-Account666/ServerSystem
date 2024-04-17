package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportToggleCommand extends CommandUtils implements CommandExecutorOverload {

    public TeleportToggleCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (arguments.length == 0) {
            if (!this.plugin.getPermissions().hasPermission(commandSender, "tptoggle.self")) {
                commandSender.sendMessage(
                        this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("tptoggle.self")));
                return true;
            }
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command.getName(),
                                                                                                                      commandSender, null, "TpToggle"));
                return true;
            }
            if (this.plugin.getWantsTeleport().wantsTeleport(((Player) commandSender))) {
                this.plugin.getWantsTeleport().setWantsTeleport(((Player) commandSender), false);
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "TpToggle.Self.DeActivated"));
            } else {
                this.plugin.getWantsTeleport().setWantsTeleport(((Player) commandSender), true);
                commandSender.sendMessage(
                        this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "TpToggle.Self.Activated"));
            }
            return true;
        }
        var targetPlayer = this.getPlayer(commandSender, arguments[0]);
        if (targetPlayer == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }

        if (this.plugin.getWantsTeleport().wantsTeleport(targetPlayer)) {
            this.plugin.getWantsTeleport().setWantsTeleport(targetPlayer, false);
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, targetPlayer, "TpToggle.Others.DeActivated.Sender"));
            targetPlayer.sendMessage(this.plugin.getMessages().getPrefix() +
                                     this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, targetPlayer, "TpToggle.Others.DeActivated.Target"));
        } else {
            this.plugin.getWantsTeleport().setWantsTeleport(targetPlayer, true);
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, targetPlayer, "TpToggle.Others.Activated.Sender"));
            targetPlayer.sendMessage(this.plugin.getMessages().getPrefix() +
                                     this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, targetPlayer, "TpToggle.Others.Activated.Target"));
        }
        return true;
    }
}
