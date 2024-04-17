package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SocialSpyCommand extends CommandUtils implements CommandExecutorOverload {

    public SocialSpyCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }
        if (!this.plugin.getPermissions().hasPermission(commandSender, "socialspy")) {
            var permission = this.plugin.getPermissions().getPermission("socialspy");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }
        if (this.plugin.getSocialSpy().contains(commandSender)) {
            this.plugin.getSocialSpy().remove(commandSender);
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "SocialSpyToggle.Deactivated"));
            return true;
        }
        this.plugin.getSocialSpy().add((Player) commandSender);
        
        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "SocialSpyToggle.Activated"));
        return true;
    }
}
