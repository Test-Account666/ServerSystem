package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MsgToggleCommand extends MessageUtils implements CommandExecutor {

    public MsgToggleCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }
        if (!this.isAllowed(cs, "msgtoggle")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("msgtoggle")));
            return true;
        }
        if (this.plugin.getMsgOff().contains(cs)) {
            this.plugin.getMsgOff().remove(cs);
            cs.sendMessage(this.getPrefix() + this.getMessage("MsgToggle.Activated", label, cmd.getName(), cs, null));
            return true;
        }
        this.plugin.getMsgOff().add((Player) cs);
        cs.sendMessage(this.getPrefix() + this.getMessage("MsgToggle.Deactivated", label, cmd.getName(), cs, null));
        return true;
    }
}
