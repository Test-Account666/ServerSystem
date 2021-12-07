package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_getpos extends MessageUtils implements CommandExecutor {

    public COMMAND_getpos(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length <= 0) {
            if (!this.isAllowed(cs, "getpos.self")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("getpos.self")));
                return true;
            }

            if (!(cs instanceof Player)) {
                cs.sendMessage(this.getPrefix() + this.getSyntax("GetPos", label, cmd.getName(), cs, null));
                return true;
            }

            String x;
            x = String.valueOf(((Player) cs).getLocation().getX());
            String xPart2 = x.split("\\.")[1];
            if (xPart2.length() > 2) x = x.split("\\.")[0] + "." + xPart2.substring(0, 2);

            String y;
            y = String.valueOf(((Player) cs).getLocation().getY());
            String yPart2 = y.split("\\.")[1];
            if (yPart2.length() > 2) y = y.split("\\.")[0] + "." + yPart2.substring(0, 2);

            String z;
            z = String.valueOf(((Player) cs).getLocation().getZ());
            String zPart2 = z.split("\\.")[1];
            if (zPart2.length() > 2) z = z.split("\\.")[0] + "." + zPart2.substring(0, 2);

            String world = ((Player) cs).getLocation().getWorld().getName();
            cs.sendMessage(this.getPrefix() + this.getMessage("GetPos.Self", label, cmd.getName(), cs, null).replace("<X>", x).replace("<Y>", y).replace("<Z>", z).replace("<WORLD>", world));
            return true;
        }

        if (!this.isAllowed(cs, "getpos.others")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("getpos.others")));
            return true;
        }

        Player target = this.getPlayer(cs, args[0]);

        if (target == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }

        String x;
        x = String.valueOf(target.getLocation().getX());
        String xPart2 = x.split("\\.")[1];
        if (xPart2.length() > 2) x = x.split("\\.")[0] + "." + xPart2.substring(0, 2);

        String y;
        y = String.valueOf(target.getLocation().getY());
        String yPart2 = y.split("\\.")[1];
        if (yPart2.length() > 2) y = y.split("\\.")[0] + "." + yPart2.substring(0, 2);

        String z;
        z = String.valueOf(target.getLocation().getZ());
        String zPart2 = z.split("\\.")[1];
        if (zPart2.length() > 2) z = z.split("\\.")[0] + "." + zPart2.substring(0, 2);

        String world = target.getLocation().getWorld().getName();

        cs.sendMessage(this.getPrefix() + this.getMessage("GetPos.Others", label, cmd.getName(), cs, target).replace("<X>", x).replace("<Y>", y).replace("<Z>", z).replace("<WORLD>", world));
        return true;
    }
}
