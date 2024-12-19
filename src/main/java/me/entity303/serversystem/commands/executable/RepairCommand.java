package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

@ServerSystemCommand(name = "Repair")
public class RepairCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public RepairCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (!this._plugin.GetPermissions().HasPermission(player, "repair")) {
            var permission = this._plugin.GetPermissions().GetPermission("repair");
            player.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            player.getInventory().getItemInMainHand();
            if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                player.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, player, null, "Repair.NoItem"));
                return true;
            }

            this.RepairItems(player.getInventory().getItemInMainHand());

            player.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, player, null, "Repair.Hand"));
            return true;
        }

        if ("all".equalsIgnoreCase(arguments[0])) {
            for (var index = 0; index < player.getInventory().getSize(); index++) {
                var items = player.getInventory().getItem(index);
                this.RepairItems(items);
            }

            player.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, player, null, "Repair.All"));
            return true;
        }

        if ("armor".equalsIgnoreCase(arguments[0])) {
            this.RepairItems(player.getInventory().getHelmet(), player.getInventory().getChestplate(), player.getInventory().getLeggings(),
                             player.getInventory().getBoots());

            player.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, player, null, "Repair.Armor"));
            return true;
        }

        var found = false;
        for (var index = 0; index < player.getInventory().getSize(); index++) {
            var items = player.getInventory().getItem(index);
            if (Material.getMaterial(arguments[0].toUpperCase()) == null) break;
            this.RepairItems(items);
            found = true;
        }

        if (!found) {
            player.sendMessage(this._plugin.GetMessages().GetPrefix() +
                               this._plugin.GetMessages().GetMessage(commandLabel, command, player, null, "Repair.NoType").replace("<TYPE>", arguments[0].toUpperCase()));
            return true;
        }

        player.sendMessage(this._plugin.GetMessages().GetPrefix() +
                           this._plugin.GetMessages().GetMessage(commandLabel, command, player, null, "Repair.Type").replace("<TYPE>", arguments[0]));
        return true;
    }

    private void RepairItems(ItemStack... itemStacks) {
        for (var itemStack : itemStacks) {
            if (itemStack == null) continue;
            if (itemStack.getType() == Material.AIR) continue;
            if (itemStack.getItemMeta() == null) continue;
            if (!(itemStack.getItemMeta() instanceof Damageable damageable) || /*!items.getType().isItem() ||*/ itemStack.getType().isBlock()) continue;

            damageable.setDamage(0);
            itemStack.setItemMeta(damageable);
        }
    }
}
