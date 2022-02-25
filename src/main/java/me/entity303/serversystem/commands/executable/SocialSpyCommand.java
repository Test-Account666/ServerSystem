package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SocialSpyCommand extends MessageUtils implements CommandExecutor {

    public SocialSpyCommand(ServerSystem plugin) {
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
