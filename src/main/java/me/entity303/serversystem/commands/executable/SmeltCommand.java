package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import me.entity303.serversystem.utils.Message;
import me.entity303.serversystem.utils.PermissionsChecker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;


public class SmeltCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public SmeltCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        boolean result1 = false;
        Message messages = this._plugin.GetMessages();
        PermissionsChecker permissions = this._plugin.GetPermissions();
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(messages.GetPrefix() + messages.GetOnlyPlayer());
        } else if (!permissions.HasPermission(commandSender, "smelt")) {
            var permission = permissions.GetPermission("smelt");
            commandSender.sendMessage(messages.GetPrefix() + messages.GetNoPermission(permission));
        } else {
            result1 = true;
        }

        if (result1)
            return true;

        ((Player) commandSender).getInventory().getItemInMainHand();

        try {
            if (((Player) commandSender).getInventory().getItemInMainHand().getType() == Material.AIR) {
                
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Smelt.NoItem"));
                return true;
            }
        } catch (Exception ignored) {
        }

        var recipeFound = false;
        ItemStack end = null;

        var recipeIterator = Bukkit.recipeIterator();

        while (recipeIterator.hasNext()) {
            var recipe = recipeIterator.next();
            if (recipe instanceof FurnaceRecipe furnaceRecipe)
                if (furnaceRecipe.getInput().getType() == ((Player) commandSender).getInventory().getItemInMainHand().getType()) {
                    end = furnaceRecipe.getResult();
                    recipeFound = true;
                    break;
                }
        }

        if (recipeFound) {
            ((Player) commandSender).getInventory().getItemInMainHand().setType(end.getType());
            ((Player) commandSender).getInventory().getItemInMainHand().setDurability(end.getDurability());
            
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                         .GetMessage(commandLabel, command, commandSender, null, "Smelt.Success")
                                                                                         .replace("<ITEM>", end.getType() + ":" + end.getDurability()));
            return true;
        }


        var result = this._plugin.GetFurnace().GetResult(((Player) commandSender).getInventory().getItemInMainHand());

        if (result != null) {
            end = result;
            ((Player) commandSender).getInventory().getItemInMainHand().setType(end.getType());
            ((Player) commandSender).getInventory().getItemInMainHand().setDurability(end.getDurability());
            
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                         .GetMessage(commandLabel, command, commandSender, null, "Smelt.Success")
                                                                                         .replace("<ITEM>", end.getType() + ":" + end.getDurability()));
            return true;
        }

        
        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Smelt.NotSmeltable"));
        return true;
    }

}
