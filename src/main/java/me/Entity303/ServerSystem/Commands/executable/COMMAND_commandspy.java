package me.Entity303.ServerSystem.Commands.executable;


import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class COMMAND_commandspy extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_commandspy(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (this.isAllowed(cs, "commandspy")) if (cs instanceof Player) if (!this.plugin.getCmdSpy().contains(cs)) {
            this.plugin.getCmdSpy().add((Player) cs);
            cs.sendMessage(this.getPrefix() + this.getMessage("CommandSpy.Activated", label, cmd.getName(), cs, null));
        } else {
            this.plugin.getCmdSpy().remove(cs);
            cs.sendMessage(this.getPrefix() + this.getMessage("CommandSpy.Deactivated", label, cmd.getName(), cs, null));
        }
        else cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
        else
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("commandspy")));
        return true;
    }
}
