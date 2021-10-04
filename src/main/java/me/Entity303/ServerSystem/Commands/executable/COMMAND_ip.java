package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_ip extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_ip(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "ip")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("ip")));
            return true;
        }
        if (args.length <= 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("Ip", label, cmd.getName(), cs, null));
            return true;
        }
        Player target = this.getPlayer(cs, args[0]);
        if (target == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }
        String ip = target.getAddress().getAddress().toString().split("/")[1];
        cs.sendMessage(this.getPrefix() + this.getMessage("Ip", label, cmd.getName(), cs, target).replace("<IP>", ip));
        return true;
    }
}
