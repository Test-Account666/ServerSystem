package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CreateKitCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public CreateKitCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "createkit")) {
            var permission = this._plugin.GetPermissions().GetPermission("createkit");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (arguments.length == 0) {
            
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "CreateKit"));
            return true;
        }

        if (this._plugin.GetKitsManager().DoesKitExist(arguments[0])) {
            
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "CreateKit.AlreadyExist").replace("<KIT>", arguments[0].toUpperCase()));
            return true;
        }

        Map<Integer, ItemStack> kit = new HashMap<>();
        for (var index = 0; index < 41; index++) {
            if (index <= 35) {
                kit.put(index, player.getInventory().getItem(index));
                continue;
            }

            if (index == 36) {
                kit.put(index, player.getInventory().getHelmet());
                continue;
            }

            if (index == 37) {
                kit.put(index, player.getInventory().getChestplate());
                continue;
            }
            if (index == 38) {
                kit.put(index, player.getInventory().getLeggings());
                continue;
            }
            if (index == 39) {
                kit.put(index, player.getInventory().getBoots());
                continue;
            }
            try {
                kit.put(index, player.getInventory().getItemInOffHand());
                break;
            } catch (Exception ignored) {
                break;
            }
        }

        long delay = 0;

        if (arguments.length > 1)
            try {
                delay = Long.parseLong(arguments[1]);
                delay = (delay * 60) * 1000;
            } catch (NumberFormatException ignored) {
            }

        this._plugin.GetKitsManager().AddKit(arguments[0].toLowerCase(), kit, delay);
        
        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                  this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "CreateKit.Success").replace("<KIT>", arguments[0].toUpperCase()));
        return true;
    }
}
