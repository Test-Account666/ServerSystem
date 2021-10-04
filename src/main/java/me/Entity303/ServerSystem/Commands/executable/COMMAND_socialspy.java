package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_socialspy extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_socialspy(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }
        if (!this.isAllowed(cs, "socialspy")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("socialspy")));
            return true;
        }
        if (this.plugin.getSocialSpy().contains(cs)) {
            this.plugin.getSocialSpy().remove(cs);
            cs.sendMessage(this.getPrefix() + this.getMessage("SocialSpyToggle.Deactivated", label, cmd.getName(), cs, null));
            return true;
        }
        this.plugin.getSocialSpy().add((Player) cs);
        cs.sendMessage(this.getPrefix() + this.getMessage("SocialSpyToggle.Activated", label, cmd.getName(), cs, null));
        return true;
    }
}
