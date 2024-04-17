package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportRequestDenyCommand implements CommandExecutorOverload {
    private final ServerSystem plugin;

    public TeleportRequestDenyCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this.plugin.getPermissions().getConfiguration().getBoolean("Permissions.tpdeny.required"))
            if (!this.plugin.getPermissions().hasPermission(commandSender, "tpdeny.permission")) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("tpdeny.permission")));
                return true;
            }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (!this.plugin.getTpaDataMap().containsKey(commandSender)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "TpDeny.NoTpa"));
            return true;
        }

        var tpaData = this.plugin.getTpaDataMap().get(commandSender);

        if (tpaData.getEnd() <= System.currentTimeMillis()) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "TpDeny.NoTpa"));
            return true;
        }

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                  this.plugin.getMessages().getMessageWithStringTarget(commandLabel, command.getName(), commandSender, tpaData.getSender().getName(), "TpDeny.Sender"));
        if (tpaData.getSender().isOnline())
            tpaData.getSender()
                   .getPlayer()
                   .sendMessage(this.plugin.getMessages().getPrefix() +
                                this.plugin.getMessages().getMessageWithStringTarget(commandLabel, command.getName(), commandSender, tpaData.getSender().getName(), "TpDeny.Target"));
        this.plugin.getTpaDataMap().remove(commandSender);
        return true;
    }
}
