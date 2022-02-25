package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class SunCommand extends MessageUtils implements CommandExecutor {

    public SunCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (!(cs instanceof Player)) {
                cs.sendMessage(this.getPrefix() + this.getSyntax("Sun", label, cmd.getName(), cs, null));
                return true;
            }
            if (!this.isAllowed(cs, "sun")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("sun")));
                return true;
            }
            ((Player) cs).getWorld().setStorm(false);
            cs.sendMessage(this.getPrefix() + this.getMessage("Sun.Success", label, cmd.getName(), cs, null).replace("<WORLD>", ((Player) cs).getWorld().getName()));
        } else if (Bukkit.getWorld(args[0]) != null) {
            Bukkit.getWorld(args[0]).setStorm(false);
            cs.sendMessage(this.getPrefix() + this.getMessage("Sun.Success", label, cmd.getName(), cs, null).replace("<WORLD>", Bukkit.getWorld(args[0]).getName()));
        } else
            cs.sendMessage(this.getPrefix() + this.getMessage("Sun.NoWorld", label, cmd.getName(), cs, null).replace("<WORLD>", args[0]));
        return true;
    }
}
