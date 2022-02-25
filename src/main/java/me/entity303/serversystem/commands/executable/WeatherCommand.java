package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class WeatherCommand extends MessageUtils implements CommandExecutor {

    public WeatherCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "weather")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("weather")));
            return true;
        }
        if (args.length == 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("Weather", label, cmd.getName(), cs, null));
            return true;
        }
        if (args.length == 1) {
            if (!(cs instanceof Player)) {
                cs.sendMessage(this.getPrefix() + this.getSyntax("Weather", label, cmd.getName(), cs, null));
                return true;
            }
            if (!this.isAllowed(cs, "weather")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("weather")));
                return true;
            }
            if ("sun".equalsIgnoreCase(args[0]) || "sonne".equalsIgnoreCase(args[0]) || "clear".equalsIgnoreCase(args[0]) || "klar".equalsIgnoreCase(args[0])) {
                ((Player) cs).getWorld().setStorm(false);
                cs.sendMessage(this.getPrefix() + this.getMessage("Weather.RainStopped", label, cmd.getName(), cs, null).replace("<WORLD>", ((Player) cs).getWorld().getName()));
            } else if ("storm".equalsIgnoreCase(args[0]) || "sturm".equalsIgnoreCase(args[0]) || "regen".equalsIgnoreCase(args[0]) || "rain".equalsIgnoreCase(args[0])) {
                ((Player) cs).getWorld().setStorm(true);
                cs.sendMessage(this.getPrefix() + this.getMessage("Weather.RainStarted", label, cmd.getName(), cs, null).replace("<WORLD>", ((Player) cs).getWorld().getName()));
            } else cs.sendMessage(this.getPrefix() + this.getSyntax("Weather", label, cmd.getName(), cs, null));
            return true;
        }
        if (Bukkit.getWorld(args[1]) == null) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Weather.NoWorld", label, cmd.getName(), cs, null).replace("<WORLD>", args[1]));
            return true;
        }
        if ("sun".equalsIgnoreCase(args[0]) || "sonne".equalsIgnoreCase(args[0]) || "clear".equalsIgnoreCase(args[0]) || "klar".equalsIgnoreCase(args[0])) {
            Bukkit.getWorld(args[1]).setStorm(false);
            cs.sendMessage(this.getPrefix() + this.getMessage("Weather.RainStopped", label, cmd.getName(), cs, null).replace("<WORLD>", Bukkit.getWorld(args[1]).getName()));
        } else if ("storm".equalsIgnoreCase(args[0]) || "sturm".equalsIgnoreCase(args[0]) || "regen".equalsIgnoreCase(args[0]) || "rain".equalsIgnoreCase(args[0])) {
            Bukkit.getWorld(args[1]).setStorm(true);
            cs.sendMessage(this.getPrefix() + this.getMessage("Weather.RainStarted", label, cmd.getName(), cs, null).replace("<WORLD>", Bukkit.getWorld(args[1]).getName()));
        } else cs.sendMessage(this.getPrefix() + this.getSyntax("Weather", label, cmd.getName(), cs, null));
        return true;
    }
}
