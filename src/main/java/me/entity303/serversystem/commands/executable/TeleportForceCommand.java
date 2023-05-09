package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.MessageUtils;
import me.entity303.serversystem.utils.Teleport;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportForceCommand extends MessageUtils implements CommandExecutor {

    public TeleportForceCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (this.isAllowed(cs, "tpo.self", true) || this.isAllowed(cs, "tpo.others", true)) {
                cs.sendMessage(this.getPrefix() + this.getSyntax("Tpo", label, cmd.getName(), cs, null));
                return true;
            }
            this.plugin.log(ChatColor.translateAlternateColorCodes('&', this.plugin.getMessages().getCfg().getString("Messages.Misc.NoPermissionInfo")).replace("<SENDER>", cs.getName()));
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("tpo.self") + " || " + this.Perm("tpo.others")));
            return true;
        }
        if (args.length == 1) {
            if ((!(cs instanceof Player))) {
                cs.sendMessage(this.getPrefix() + this.getSyntax("Tpo", label, cmd.getName(), cs, null));
                return true;
            }
            if (!this.isAllowed(cs, "tpo.self")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("tpo.self")));
                return true;
            }
            Player target = this.getPlayer(cs, args[0]);
            if (target == null) {
                cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
                return true;
            }

            Teleport.teleport((Player) cs, target);

            cs.sendMessage(this.getPrefix() + this.getMessage("Tpo.Self", label, cmd.getName(), cs, target));
            return true;
        }
        if (!this.isAllowed(cs, "tpo.others")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("tpo.others")));
            return true;
        }
        Player target1 = this.getPlayer(cs, args[0]);
        if (target1 == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }
        Player target2 = this.getPlayer(cs, args[1]);
        if (target2 == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[1]));
            return true;
        }

        Teleport.teleport(target1, target2);

        cs.sendMessage(this.getPrefix() + this.getMessage("Tpo.Others", label, cmd.getName(), cs, target1).replace("<TARGET2>", target2.getName()));
        return true;
    }
}
