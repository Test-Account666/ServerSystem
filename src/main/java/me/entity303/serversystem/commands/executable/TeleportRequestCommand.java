package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import me.entity303.serversystem.utils.TpaData;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportRequestCommand extends CommandUtils implements ICommandExecutorOverload {

    public TeleportRequestCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this._plugin.GetPermissions().GetConfiguration().GetBoolean("Permissions.tpa.required"))
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "tpa.permission")) {
                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("tpa.permission")));
                return true;
            }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command.getName(), commandSender, null, "Tpa"));
            return true;
        }

        var targetPlayer = this.GetPlayer(commandSender, arguments[0]);
        if (targetPlayer == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }

        if (this._plugin.GetTpaDataMap().containsKey(targetPlayer)) {
            var tpaData = this._plugin.GetTpaDataMap().get(targetPlayer);
            if (tpaData.GetEnd() < System.currentTimeMillis()) {
                commandSender.sendMessage(
                        this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, targetPlayer, "Tpa.PendingTpa"));
                return true;
            } else
                this._plugin.GetTpaDataMap().remove(targetPlayer);
        }

        if (!this._plugin.GetWantsTeleport().DoesPlayerWantTeleport(targetPlayer)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, targetPlayer, "Tpa.TeleportationDisabled"));
            return true;
        }

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, targetPlayer, "Tpa.Sender"));
        targetPlayer.sendMessage(
                this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, targetPlayer, "Tpa.Target"));

        var accept = new TextComponent(this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, targetPlayer, "Tpa.Accept", true).replace("&", "§"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, targetPlayer, "Tpa.Accept", true).replace("&", "§")).create()));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
        targetPlayer.spigot().sendMessage(accept);

        var deny = new TextComponent(this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, targetPlayer, "Tpa.Deny", true).replace("&", "§"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, targetPlayer, "Tpa.Deny", true).replace("&", "§")).create()));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));
        targetPlayer.spigot().sendMessage(deny);

        this._plugin.GetTpaDataMap().put(targetPlayer, new TpaData(false, ((Player) commandSender), System.currentTimeMillis() + 120000L));
        return true;
    }
}
