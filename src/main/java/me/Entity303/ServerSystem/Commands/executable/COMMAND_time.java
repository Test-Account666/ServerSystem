package me.Entity303.ServerSystem.Commands.executable;


import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class COMMAND_time extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_time(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 0) cs.sendMessage(this.getPrefix() + this.getSyntax("Time", label, cmd.getName(), cs, null));
        else if (args.length == 1) {
            if (!(cs instanceof Player)) {
                cs.sendMessage(this.getPrefix() + this.getSyntax("Time", label, cmd.getName(), cs, null));
                return true;
            }
            if (!this.isAllowed(cs, "time")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("time")));
                return true;
            }
            if ("Tag".equalsIgnoreCase(args[0]) || "Day".equalsIgnoreCase(args[0])) {
                ((Player) cs).getWorld().setTime(0);
                cs.sendMessage(this.getPrefix() + this.getMessage("Time.Success", label, cmd.getName(), cs, null).replace("<WORLD>", ((Player) cs).getWorld().getName()).replace("<TIME>", this.getTime("Day")));
            } else if ("Nacht".equalsIgnoreCase(args[0]) || "Night".equalsIgnoreCase(args[0])) {
                ((Player) cs).getWorld().setTime(16000);
                cs.sendMessage(this.getPrefix() + this.getMessage("Time.Success", label, cmd.getName(), cs, null).replace("<WORLD>", ((Player) cs).getWorld().getName()).replace("<TIME>", this.getTime("Night")));
            } else if ("Mittag".equalsIgnoreCase(args[0]) || "noon".equalsIgnoreCase(args[0])) {
                ((Player) cs).getWorld().setTime(6000);
                cs.sendMessage(this.getPrefix() + this.getMessage("Time.Success", label, cmd.getName(), cs, null).replace("<WORLD>", ((Player) cs).getWorld().getName()).replace("<TIME>", this.getTime("Noon")));
            } else try {
                ((Player) cs).getWorld().setTime(Long.parseLong(args[0]));
            } catch (Exception e) {
                cs.sendMessage(this.getPrefix() + this.getSyntax("Time", label, cmd.getName(), cs, null));
            }
        } else {
            if (Bukkit.getWorld(args[1]) == null) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Time.NoWorld", label, cmd.getName(), cs, null).replace("<WORLD>", args[1]));
                return true;
            }
            if ("Tag".equalsIgnoreCase(args[0]) || "Day".equalsIgnoreCase(args[0])) {
                Bukkit.getWorld(args[1]).setTime(0);
                cs.sendMessage(this.getPrefix() + this.getMessage("Time.Success", label, cmd.getName(), cs, null).replace("<WORLD>", ((Player) cs).getWorld().getName()).replace("<TIME>", this.getTime("Day")));
            } else if ("Nacht".equalsIgnoreCase(args[0]) || "Night".equalsIgnoreCase(args[0])) {
                Bukkit.getWorld(args[1]).setTime(16000);
                cs.sendMessage(this.getPrefix() + this.getMessage("Time.Success", label, cmd.getName(), cs, null).replace("<WORLD>", ((Player) cs).getWorld().getName()).replace("<TIME>", this.getTime("Night")));
            } else if ("Mittag".equalsIgnoreCase(args[0]) || "noon".equalsIgnoreCase(args[0])) {
                Bukkit.getWorld(args[1]).setTime(6000);
                cs.sendMessage(this.getPrefix() + this.getMessage("Time.Success", label, cmd.getName(), cs, null).replace("<WORLD>", ((Player) cs).getWorld().getName()).replace("<TIME>", this.getTime("Noon")));
            } else try {
                Bukkit.getWorld(args[1]).setTime(Long.parseLong(args[0]));
            } catch (Exception e) {
                cs.sendMessage(this.getPrefix() + this.getSyntax("Time", label, cmd.getName(), cs, null));
            }
        }
        return true;
    }

    private String getTime(String time) {
        return this.plugin.getMessages().getCfg().getString("Messages.Misc.Times." + time);
    }
}
