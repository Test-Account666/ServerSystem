package me.Entity303.ServerSystem.Commands.executable;


import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_day extends MessageUtils implements CommandExecutor {

    public COMMAND_day(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 0) if (cs instanceof Player) if (this.isPermAllowed(cs, this.Perm("day"))) {
            ((Player) cs).getWorld().setTime(0);
            cs.sendMessage(this.getPrefix() + this.getMessage("Time.Success", label, "time", cs, null).replace("<WORLD>", ((Player) cs).getWorld().getName()).replace("<TIME>", this.getTime("Day")));
        } else cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("day")));
        else
            cs.sendMessage(this.getPrefix() + this.getSyntax("Time", "time", "time", cs, null));
        else if (this.isPermAllowed(cs, this.Perm("day"))) {
            World w = Bukkit.getWorld(args[0]);
            if (w != null) {
                w.setTime(0);
                cs.sendMessage(this.getPrefix() + this.getMessage("Time.Success", label, "time", cs, null).replace("<WORLD>", w.getName()).replace("<TIME>", this.getTime("Day")));
            } else
                cs.sendMessage(this.getPrefix() + this.getMessage("Time.NoWorld", label, "time", cs, null).replace("<WORLD>", args[0]));
        } else if (cs instanceof Player) if (this.isPermAllowed(cs, this.Perm("day"))) {
            ((Player) cs).getWorld().setTime(0);
            cs.sendMessage(this.getPrefix() + this.getMessage("Time.Success", label, "time", cs, null).replace("<WORLD>", ((Player) cs).getWorld().getName()).replace("<TIME>", this.getTime("Day")));
        } else cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("day")));
        else
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("day")));
        return true;
    }

    private String getTime(String time) {
        return this.plugin.getMessages().getCfg().getString("Messages.Misc.Times." + time);
    }
}
