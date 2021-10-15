package me.Entity303.ServerSystem.Commands.executable;


import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ChatColor;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_tp extends MessageUtils implements CommandExecutor {

    public COMMAND_tp(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (!this.isAllowed(cs, "tp.self", true) && !this.isAllowed(cs, "tp.others", true)) {
                this.plugin.log(ChatColor.translateAlternateColorCodes('&', this.plugin.getMessages().getCfg().getString("Messages.Misc.NoPermissionInfo")).replace("<SENDER>", cs.getName()));
                cs.sendMessage(this.getPrefix() + this.getNoPermission("tp.self || tp.others"));
                return true;
            }
            cs.sendMessage(this.getPrefix() + this.getSyntax("Tp", label, cmd.getName(), cs, null));
            return true;
        } else if (args.length == 1) {
            if ((!(cs instanceof Player))) {
                cs.sendMessage(this.getPrefix() + this.getSyntax("Tp", label, cmd.getName(), cs, null));
                return true;
            }
            if (!this.isAllowed(cs, "tp.self")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("tp.self")));
                return true;
            }
            Player target = this.getPlayer(cs, args[0]);
            if (target == null) {
                cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
                return true;
            }
            if (target != cs) if (!this.plugin.wantsTP(target)) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Tp.NoTeleportations", label, cmd.getName(), cs, target));
                return true;
            }
            ((Player) cs).teleport(target);
            cs.sendMessage(this.getPrefix() + this.getMessage("Tp.Self", label, cmd.getName(), cs, target));
            return true;
        } else if (!this.isAllowed(cs, "tp.others")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("tp.others")));
            return true;
        }
        Player target1 = this.getPlayer(cs, args[0]);
        if (target1 == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }
        if (target1 != cs) if (!this.plugin.wantsTP(target1)) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Tp.NoTeleportations", label, cmd.getName(), cs, target1));
            return true;
        }
        Player target2 = this.getPlayer(cs, args[1]);
        if (target2 == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[1]));
            return true;
        }
        if (target2 != cs) if (!this.plugin.wantsTP(target2)) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Tp.NoTeleportations", label, cmd.getName(), cs, target2));
            return true;
        }
        target1.teleport(target2);
        cs.sendMessage(this.getPrefix() + this.getMessage("Tp.Others", label, cmd.getName(), cs, target1).replace("<TARGET2>", target2.getName()).replace("<TARGET2DISPLAY>", target2.getDisplayName()));
        return true;
    }
}
