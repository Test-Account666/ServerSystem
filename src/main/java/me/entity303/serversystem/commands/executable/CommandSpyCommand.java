package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandSpyCommand extends MessageUtils implements CommandExecutor {

    public CommandSpyCommand(ServerSystem plugin) {
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
