package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RenameCommand extends CommandUtils implements CommandExecutorOverload {

    public RenameCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "rename")) {
            var permission = this.plugin.getPermissions().getPermission("rename");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (arguments.length == 0) {
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Rename"));
            return true;
        }

        ((Player) commandSender).getInventory().getItemInMainHand();
        if (((Player) commandSender).getInventory().getItemInMainHand().getType() == Material.AIR) {
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Rename.NoItem"));
            return true;
        }
        var handStack = ((Player) commandSender).getInventory().getItemInMainHand();
        var nameBuilder = new StringBuilder();
        for (var arg : arguments)
            nameBuilder.append(arg).append(" ");

        var name = ChatColor.translateAlternateColorCodes('&', nameBuilder.toString().trim());

        var meta = handStack.getItemMeta();
        meta.setDisplayName(name);

        handStack.setItemMeta(meta);

        
        commandSender.sendMessage(
                this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Rename.Success").replace("<NAME>", name));
        return true;
    }
}
