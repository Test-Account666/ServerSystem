package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import me.entity303.serversystem.utils.TpaData;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportRequestHereCommand extends CommandUtils implements CommandExecutorOverload {

    public TeleportRequestHereCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this.plugin.getPermissions().getConfiguration().getBoolean("Permissions.tpahere.required"))
            if (!this.plugin.getPermissions().hasPermission(commandSender, "tpahere.permission")) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("tpahere.permission")));
                return true;
            }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command.getName(), commandSender, null, "Tpahere"));
            return true;
        }

        var targetPlayer = this.getPlayer(commandSender, arguments[0]);
        if (targetPlayer == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }

        if (this.plugin.getTpaDataMap().containsKey(targetPlayer)) {
            var tpaData = this.plugin.getTpaDataMap().get(targetPlayer);
            if (tpaData.getEnd() < System.currentTimeMillis()) {
                commandSender.sendMessage(
                        this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, targetPlayer, "Tpa.PendingTpa"));
                return true;
            } else
                this.plugin.getTpaDataMap().remove(targetPlayer);
        }

        if (!this.plugin.getWantsTeleport().wantsTeleport(targetPlayer)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "TpaHere.TeleportationDisabled"));
            return true;
        }

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, targetPlayer, "TpaHere.Sender"));
        targetPlayer.sendMessage(
                this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, targetPlayer, "TpaHere.Target"));

        var accept = new TextComponent(this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, targetPlayer, "TpaHere.Accept", true).replace("&", "§"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, targetPlayer, "TpaHere.Accept", true).replace("&", "§")).create()));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
        targetPlayer.spigot().sendMessage(accept);

        var deny = new TextComponent(this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, targetPlayer, "TpaHere.Deny", true).replace("&", "§"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, targetPlayer, "TpaHere.Deny", true).replace("&", "§")).create()));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));
        targetPlayer.spigot().sendMessage(deny);

        this.plugin.getTpaDataMap().put(targetPlayer, new TpaData(true, ((Player) commandSender), System.currentTimeMillis() + 120000L));
        return true;
    }
}
