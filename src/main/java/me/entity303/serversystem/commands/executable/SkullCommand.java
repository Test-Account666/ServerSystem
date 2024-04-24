package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullCommand extends CommandUtils implements ICommandExecutorOverload {

    public SkullCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }
        if (arguments.length == 0) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "skull.self")) {
                var permission = this._plugin.GetPermissions().GetPermission("skull.self");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }
            var skull = new ItemStack(Material.PLAYER_HEAD, 1);
            var skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(((Player) commandSender).getUniqueId()));
            skull.setItemMeta(skullMeta);
            ((Player) commandSender).getInventory().addItem(skull);
            
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Skull.Self"));
        } else if (this._plugin.GetPermissions().HasPermission(commandSender, "skull.others", true)) {
            var skull = new ItemStack(Material.PLAYER_HEAD, 1);
            var skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(arguments[0]));
            skull.setItemMeta(skullMeta);
            ((Player) commandSender).getInventory().addItem(skull);
            
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessageWithStringTarget(commandLabel, command, commandSender, arguments[0], "Skull.Others"));
        } else {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "skull.self")) {
                var permission = this._plugin.GetPermissions().GetPermission("skull.others");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }
            var skull = new ItemStack(Material.PLAYER_HEAD, 1);
            var skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(((Player) commandSender).getUniqueId()));
            skull.setItemMeta(skullMeta);
            ((Player) commandSender).getInventory().addItem(skull);
            
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "skull.self"));
        }
        return true;
    }
}
