package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_rain extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_rain(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 0) if (cs instanceof Player) if (this.isAllowed(cs, "rain")) {
            ((Player) cs).getWorld().setStorm(true);
            cs.sendMessage(this.getPrefix() + this.getMessage("Weather.RainStarted", label, cmd.getName(), cs, null).replace("<WORLD>", ((Player) cs).getWorld().getName()));
        } else cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("rain")));
        else
            cs.sendMessage(this.getPrefix() + this.getSyntax("Rain", label, cmd.getName(), cs, null));
        else {
            if (!this.isAllowed(cs, "rain")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("rain")));
                return true;
            }
            if (Bukkit.getWorld(args[0]) != null) {
                Bukkit.getWorld(args[0]).setStorm(true);
                cs.sendMessage(this.getPrefix() + this.getMessage("Weather.RainStarted", label, cmd.getName(), cs, null).replace("<WORLD>", Bukkit.getWorld(args[0]).getName()));
            } else
                cs.sendMessage(this.getPrefix() + this.getMessage("Weather.NoWorld", label, cmd.getName(), cs, null).replace("<WORLD>", args[0]));
        }
        return true;
    }
}
