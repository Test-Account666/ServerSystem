package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

//TODO: Rewrite this
public class SmeltCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public SmeltCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        var messages = this._plugin.GetMessages();
        var permissions = this._plugin.GetPermissions();
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(messages.GetPrefix() + messages.GetOnlyPlayer());
            return true;
        }

        if (!permissions.HasPermission(commandSender, "smelt")) {
            var permission = permissions.GetPermission("smelt");
            commandSender.sendMessage(messages.GetPrefix() + messages.GetNoPermission(permission));
            return true;
        }

        var itemInHand = player.getInventory().getItemInMainHand();

        try {
            if (itemInHand.getType() == Material.AIR) {

                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Smelt.NoItem"));
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
                if (furnaceRecipe.getInput().getType() == itemInHand.getType()) {
                    end = furnaceRecipe.getResult();
                    recipeFound = true;
                    break;
                }
        }

        if (recipeFound)
            return this.SmeltItem(commandSender, command, commandLabel, player, end);


        var result = this._plugin.GetFurnace().GetResult(itemInHand);

        if (result != null) {
            end = result;
            return this.SmeltItem(commandSender, command, commandLabel, player, end);
        }


        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                  this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Smelt.NotSmeltable"));
        return true;
    }

    // Weakening would result in the wrong Inventory type
    @SuppressWarnings({ "TypeMayBeWeakened" })
    private boolean SmeltItem(CommandSender commandSender, Command command, String commandLabel, Player player, ItemStack end) {
        player.getInventory().getItemInMainHand().setType(end.getType());

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                       .GetMessage(commandLabel, command, commandSender, null,
                                                                                                   "Smelt.Success")
                                                                                       .replace("<ITEM>", end.getType() + ":" + end.getDurability()));
        return true;
    }

}
