package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportToggleCommand extends MessageUtils implements CommandExecutor {

    public TeleportToggleCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (!this.plugin.getPermissions().hasPerm(cs, "tptoggle.self")) {
                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("tptoggle.self")));
                return true;
            }
            if (!(cs instanceof Player)) {
                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(label, cmd.getName(), cs, null, "TpToggle"));
                return true;
            }
            if (this.plugin.getWantsTeleport().wantsTeleport(((Player) cs))) {
                this.plugin.getWantsTeleport().setWantsTeleport(((Player) cs), false);
                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "TpToggle.Self.DeActivated"));
            } else {
                this.plugin.getWantsTeleport().setWantsTeleport(((Player) cs), true);
                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "TpToggle.Self.Activated"));
            }
            return true;
        }
        Player targetPlayer = this.getPlayer(cs, args[0]);
        if (targetPlayer == null) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(args[0]));
            return true;
        }

        if (this.plugin.getWantsTeleport().wantsTeleport(targetPlayer)) {
            this.plugin.getWantsTeleport().setWantsTeleport(targetPlayer, false);
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, targetPlayer, "TpToggle.Others.DeActivated.Sender"));
            targetPlayer.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, targetPlayer, "TpToggle.Others.DeActivated.Target"));
        } else {
            this.plugin.getWantsTeleport().setWantsTeleport(targetPlayer, true);
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, targetPlayer, "TpToggle.Others.Activated.Sender"));
            targetPlayer.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, targetPlayer, "TpToggle.Others.Activated.Target"));
        }
        return true;
    }
}
