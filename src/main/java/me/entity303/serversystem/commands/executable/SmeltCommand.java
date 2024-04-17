package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;


public class SmeltCommand extends CommandUtils implements CommandExecutorOverload {

    public SmeltCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (!this.plugin.getPermissions().hasPermission(commandSender, "smelt")) {
            var permission = this.plugin.getPermissions().getPermission("smelt");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        ((Player) commandSender).getInventory().getItemInMainHand();

        try {
            if (((Player) commandSender).getInventory().getItemInMainHand().getType() == Material.AIR) {
                
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Smelt.NoItem"));
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
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage(commandLabel, command, commandSender, null, "Smelt.Success")
                                                                                         .replace("<ITEM>", end.getType() + ":" + end.getDurability()));
            return true;
        }


        var result = this.plugin.getFurnace().getResult(((Player) commandSender).getInventory().getItemInMainHand());

        if (result != null) {
            end = result;
            ((Player) commandSender).getInventory().getItemInMainHand().setType(end.getType());
            ((Player) commandSender).getInventory().getItemInMainHand().setDurability(end.getDurability());
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage(commandLabel, command, commandSender, null, "Smelt.Success")
                                                                                         .replace("<ITEM>", end.getType() + ":" + end.getDurability()));
            return true;
        }

        
        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Smelt.NotSmeltable"));
        return true;
    }

}
