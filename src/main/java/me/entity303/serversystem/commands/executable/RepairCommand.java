package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class RepairCommand extends CommandUtils implements CommandExecutorOverload {

    public RepairCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (!this.plugin.getPermissions().hasPermission(player, "repair")) {
            var permission = this.plugin.getPermissions().getPermission("repair");
            player.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            player.getInventory().getItemInMainHand();
            if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                player.sendMessage(
                        this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, player, null, "Repair.NoItem"));
                return true;
            }

            this.RepairItems(player.getInventory().getItemInMainHand());

            player.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, player, null, "Repair.Hand"));
            return true;
        }

        if ("all".equalsIgnoreCase(arguments[0])) {
            for (var i = 0; i < player.getInventory().getSize(); i++) {
                var items = player.getInventory().getItem(i);
                this.RepairItems(items);
            }

            player.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, player, null, "Repair.All"));
            return true;
        }

        if ("armor".equalsIgnoreCase(arguments[0])) {
            this.RepairItems(player.getInventory().getHelmet(), player.getInventory().getChestplate(), player.getInventory().getLeggings(),
                             player.getInventory().getBoots());

            player.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, player, null, "Repair.Armor"));
            return true;
        }

        var found = false;
        for (var i = 0; i < player.getInventory().getSize(); i++) {
            var items = player.getInventory().getItem(i);
            if (Material.getMaterial(arguments[0].toUpperCase()) == null)
                break;
            this.RepairItems(items);
            found = true;
        }

        if (!found) {
            player.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                  .getMessage(commandLabel, command, player, null, "Repair.NoType")
                                                                                  .replace("<TYPE>", arguments[0].toUpperCase()));
            return true;
        }

        player.sendMessage(this.plugin.getMessages().getPrefix() +
                           this.plugin.getMessages().getMessage(commandLabel, command, player, null, "Repair.Type").replace("<TYPE>", arguments[0]));
        return true;
    }

    private void RepairItems(ItemStack... itemStacks) {
        for (var itemStack : itemStacks) {
            if (itemStack == null)
                continue;
            if (itemStack.getType() == Material.AIR)
                continue;
            if (itemStack.getItemMeta() == null)
                continue;
            if (!(itemStack.getItemMeta() instanceof Damageable damageable) || /*!items.getType().isItem() ||*/ itemStack.getType().isBlock())
                continue;

            damageable.setDamage(0);
            itemStack.setItemMeta(damageable);
        }
    }
}
