package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class FlyCommand extends MessageUtils implements CommandExecutor {

    public FlyCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 0)
            if (cs instanceof Player) if (this.isAllowed(cs, "fly.self")) if (!((Player) cs).getAllowFlight()) {
                ((Player) cs).setAllowFlight(true);
                cs.sendMessage(this.getPrefix() + this.getMessage("Fly.Activated.Self", label, cmd.getName(), cs, null));
            } else {
                ((Player) cs).setAllowFlight(false);
                cs.sendMessage(this.getPrefix() + this.getMessage("Fly.DeActivated.Self", label, cmd.getName(), cs, null));
            }
            else cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("fly.self")));
            else
                cs.sendMessage(this.getPrefix() + this.getSyntax("Fly", label, cmd.getName(), cs, null));
        else if (this.isAllowed(cs, "fly.others")) {
            Player target = this.getPlayer(cs, args[0]);
            if (target != null) if (!target.getAllowFlight()) {
                target.setAllowFlight(true);
                target.sendMessage(this.getPrefix() + this.getMessage("Fly.Activated.Others.Target", label, cmd.getName(), cs, target));
                cs.sendMessage(this.getPrefix() + this.getMessage("Fly.Activated.Others.Sender", label, cmd.getName(), cs, target));
            } else {
                target.setAllowFlight(false);
                target.sendMessage(this.getPrefix() + this.getMessage("Fly.DeActivated.Others.Target", label, cmd.getName(), cs, target));
                cs.sendMessage(this.getPrefix() + this.getMessage("Fly.DeActivated.Others.Sender", label, cmd.getName(), cs, target));
            }
            else
                cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
        } else if (cs instanceof Player) if (this.isAllowed(cs, "fly.self")) if (!((Player) cs).getAllowFlight()) {
            ((Player) cs).setAllowFlight(true);
            cs.sendMessage(this.getPrefix() + this.getMessage("Fly.Activated.Self", label, cmd.getName(), cs, null));
        } else {
            ((Player) cs).setAllowFlight(false);
            cs.sendMessage(this.getPrefix() + this.getMessage("Fly.DeActivated.Self", label, cmd.getName(), cs, null));
        }
        else cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("fly.others")));
        else
            cs.sendMessage(this.getPrefix() + this.getSyntax("Fly", label, cmd.getName(), cs, null));
        return true;
    }
}
