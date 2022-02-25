package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GodCommand extends MessageUtils implements CommandExecutor {

    public GodCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length <= 0) {
            if (!(cs instanceof Player)) {
                cs.sendMessage(this.getPrefix() + this.getSyntax("God", label, cmd.getName(), cs, null));
                return true;
            }
            if (!this.isAllowed(cs, "god.self")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("god.self")));
                return true;
            }
            if (this.plugin.getGodList().contains(cs)) {
                this.plugin.getGodList().remove(cs);
                cs.sendMessage(this.getPrefix() + this.getMessage("God.Self.Deactivated", label, cmd.getName(), cs, null));
            } else {
                this.plugin.getGodList().add(((Player) cs));
                cs.sendMessage(this.getPrefix() + this.getMessage("God.Self.Activated", label, cmd.getName(), cs, null));
            }
            return true;
        }
        if (!this.isAllowed(cs, "god.others")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("god.others")));
            return true;
        }
        Player targetPlayer = this.getPlayer(cs, args[0]);
        if (targetPlayer == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }
        if (this.plugin.getGodList().contains(targetPlayer)) {
            this.plugin.getGodList().remove(targetPlayer);
            cs.sendMessage(this.getPrefix() + this.getMessage("God.Others.Deactivated.Sender", label, cmd.getName(), cs, targetPlayer));
            targetPlayer.sendMessage(this.getPrefix() + this.getMessage("God.Others.Deactivated.Target", label, cmd.getName(), cs, targetPlayer));
        } else {
            this.plugin.getGodList().add(targetPlayer);
            cs.sendMessage(this.getPrefix() + this.getMessage("God.Others.Activated.Sender", label, cmd.getName(), cs, targetPlayer));
            targetPlayer.sendMessage(this.getPrefix() + this.getMessage("God.Others.Activated.Target", label, cmd.getName(), cs, targetPlayer));
        }
        return true;
    }
}
