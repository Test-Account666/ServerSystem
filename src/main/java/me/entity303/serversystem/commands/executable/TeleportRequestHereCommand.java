package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import me.entity303.serversystem.utils.TpaData;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportRequestHereCommand extends MessageUtils implements CommandExecutor {

    public TeleportRequestHereCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.tpahere.required"))
            if (!this.plugin.getPermissions().hasPerm(cs, "tpahere.permission")) {
                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("tpahere.permission")));
                return true;
            }

        if (!(cs instanceof Player)) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (args.length <= 0) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(label, cmd.getName(), cs, null, "tpahere"));
            return true;
        }

        Player targetPlayer = this.getPlayer(cs, args[0]);
        if (targetPlayer == null) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(args[0]));
            return true;
        }

        if (this.plugin.getTpaDataMap().containsKey(targetPlayer)) {
            TpaData tpaData = this.plugin.getTpaDataMap().get(targetPlayer);
            if (tpaData.getEnd() < System.currentTimeMillis()) {
                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, targetPlayer, "Tpa.PendingTpa"));
                return true;
            } else this.plugin.getTpaDataMap().remove(targetPlayer);
        }

        if (!this.plugin.wantsTP(targetPlayer)) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "TpaHere.TeleportationDisabled"));
            return true;
        }

        cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, targetPlayer, "TpaHere.Sender"));
        targetPlayer.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, targetPlayer, "TpaHere.Target"));

        TextComponent accept = new TextComponent(this.plugin.getMessages().getMessage(label, cmd.getName(), cs, targetPlayer, "TpaHere.Accept", true).replace("&", "§"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(this.plugin.getMessages().getMessage(label, cmd.getName(), cs, targetPlayer, "TpaHere.Accept", true).replace("&", "§")).create()));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
        targetPlayer.spigot().sendMessage(accept);

        TextComponent deny = new TextComponent(this.plugin.getMessages().getMessage(label, cmd.getName(), cs, targetPlayer, "TpaHere.Deny", true).replace("&", "§"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(this.plugin.getMessages().getMessage(label, cmd.getName(), cs, targetPlayer, "TpaHere.Deny", true).replace("&", "§")).create()));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));
        targetPlayer.spigot().sendMessage(deny);

        this.plugin.getTpaDataMap().put(targetPlayer, new TpaData(true, ((Player) cs), System.currentTimeMillis() + 120000L));
        return true;
    }
}
