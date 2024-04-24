package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DisenchantCommand implements ICommandExecutorOverload {
    private final ServerSystem _plugin;

    public DisenchantCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this.GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (!this._plugin.GetPermissions().HasPermission(player, "disenchant")) {
            player.sendMessage(this.GetPrefix() + this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("disenchant")));
            return true;
        }

        if (arguments.length == 0) {
            player.getInventory().getItemInMainHand();
            this.RemoveEnchantments(player.getInventory().getItemInMainHand());
            player.sendMessage(this.GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "DisEnchant.Hand"));
            return true;
        }

        var inv = player.getInventory().getContents();
        if ("all".equalsIgnoreCase(arguments[0])) {
            for (var items : inv)
                this.RemoveEnchantments(items);

            player.sendMessage(this.GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "DisEnchant.All"));
            return true;
        }

        List<ItemStack> itemsList = new ArrayList<>();

        for (var items : inv) {
            if (items == null)
                continue;
            if (items.getType() == Material.getMaterial(arguments[0].toUpperCase()))
                itemsList.add(items);
        }

        if (itemsList.isEmpty()) {
            var command1 = command.getName();
            commandSender.sendMessage(this.GetPrefix() + this._plugin.GetMessages()
                                                                    .GetMessage(commandLabel, command1, commandSender, null, "DisEnchant.NotInInv")
                                                                    .replace("<TYPE>", arguments[0]));
            return true;
        }

        itemsList.forEach(this::RemoveEnchantments);
        player.sendMessage(this.GetPrefix() + this._plugin.GetMessages()
                                                    .GetMessage(commandLabel, command.getName(), commandSender, null, "DisEnchant.Type")
                                                    .replace("<TYPE>", arguments[0]));
        return true;
    }

    private String GetPrefix() {
        return this._plugin.GetMessages().GetPrefix();
    }

    private void RemoveEnchantments(ItemStack item) {
        if (item != null) {
            item.getEnchantments();
            for (var enchantment : item.getEnchantments().keySet())
                item.removeEnchantment(enchantment);
        }
    }
}
