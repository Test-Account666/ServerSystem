package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CreateKitCommand extends CommandUtils implements CommandExecutorOverload {

    public CreateKitCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "createkit")) {
            var permission = this.plugin.getPermissions().getPermission("createkit");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (arguments.length == 0) {
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "CreateKit"));
            return true;
        }

        if (this.plugin.getKitsManager().doesKitExist(arguments[0])) {
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "CreateKit.AlreadyExist").replace("<KIT>", arguments[0].toUpperCase()));
            return true;
        }

        Map<Integer, ItemStack> kit = new HashMap<>();
        for (var i = 0; i < 41; i++) {
            if (i <= 35) {
                kit.put(i, player.getInventory().getItem(i));
                continue;
            }

            if (i == 36) {
                kit.put(i, player.getInventory().getHelmet());
                continue;
            }

            if (i == 37) {
                kit.put(i, player.getInventory().getChestplate());
                continue;
            }
            if (i == 38) {
                kit.put(i, player.getInventory().getLeggings());
                continue;
            }
            if (i == 39) {
                kit.put(i, player.getInventory().getBoots());
                continue;
            }
            try {
                kit.put(i, player.getInventory().getItemInOffHand());
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

        this.plugin.getKitsManager().addKit(arguments[0].toLowerCase(), kit, delay);
        
        commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                  this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "CreateKit.Success").replace("<KIT>", arguments[0].toUpperCase()));
        return true;
    }
}
