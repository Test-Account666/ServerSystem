package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RenameCommand extends CommandUtils implements ICommandExecutorOverload {

    public RenameCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "rename")) {
            var permission = this._plugin.GetPermissions().GetPermission("rename");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (arguments.length == 0) {
            
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Rename"));
            return true;
        }

        ((Player) commandSender).getInventory().getItemInMainHand();
        if (((Player) commandSender).getInventory().getItemInMainHand().getType() == Material.AIR) {
            
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Rename.NoItem"));
            return true;
        }
        var handStack = ((Player) commandSender).getInventory().getItemInMainHand();
        var nameBuilder = new StringBuilder();
        for (var arg : arguments)
            nameBuilder.append(arg).append(" ");

        var name = ChatColor.TranslateAlternateColorCodes('&', nameBuilder.toString().trim());

        var meta = handStack.getItemMeta();
        meta.setDisplayName(name);

        handStack.setItemMeta(meta);

        
        commandSender.sendMessage(
                this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Rename.Success").replace("<NAME>", name));
        return true;
    }
}
