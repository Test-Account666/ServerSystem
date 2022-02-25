package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IpCommand extends MessageUtils implements CommandExecutor {

    public IpCommand(ServerSystem plugin) {
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
