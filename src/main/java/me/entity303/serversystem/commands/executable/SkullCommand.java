package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullCommand extends CommandUtils implements CommandExecutorOverload {

    public SkullCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }
        if (arguments.length == 0) {
            if (!this.plugin.getPermissions().hasPermission(commandSender, "skull.self")) {
                var permission = this.plugin.getPermissions().getPermission("skull.self");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }
            var skull = new ItemStack(Material.PLAYER_HEAD, 1);
            var skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(((Player) commandSender).getUniqueId()));
            skull.setItemMeta(skullMeta);
            ((Player) commandSender).getInventory().addItem(skull);
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Skull.Self"));
        } else if (this.plugin.getPermissions().hasPermission(commandSender, "skull.others", true)) {
            var skull = new ItemStack(Material.PLAYER_HEAD, 1);
            var skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(arguments[0]));
            skull.setItemMeta(skullMeta);
            ((Player) commandSender).getInventory().addItem(skull);
            
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessageWithStringTarget(commandLabel, command, commandSender, arguments[0], "Skull.Others"));
        } else {
            if (!this.plugin.getPermissions().hasPermission(commandSender, "skull.self")) {
                var permission = this.plugin.getPermissions().getPermission("skull.others");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }
            var skull = new ItemStack(Material.PLAYER_HEAD, 1);
            var skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(((Player) commandSender).getUniqueId()));
            skull.setItemMeta(skullMeta);
            ((Player) commandSender).getInventory().addItem(skull);
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "skull.self"));
        }
        return true;
    }
}
