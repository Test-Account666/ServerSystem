package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DisenchantCommand implements CommandExecutorOverload {
    private final ServerSystem plugin;

    public DisenchantCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player p)) {
            commandSender.sendMessage(this.getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (!this.plugin.getPermissions().hasPermission(p, "disenchant")) {
            p.sendMessage(this.getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("disenchant")));
            return true;
        }

        if (arguments.length == 0) {
            p.getInventory().getItemInMainHand();
            this.removeEnchantments(p.getInventory().getItemInMainHand());
            p.sendMessage(this.getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "DisEnchant.Hand"));
            return true;
        }

        var inv = p.getInventory().getContents();
        if ("all".equalsIgnoreCase(arguments[0])) {
            for (var items : inv)
                this.removeEnchantments(items);

            p.sendMessage(this.getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "DisEnchant.All"));
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
            commandSender.sendMessage(this.getPrefix() + this.plugin.getMessages()
                                                                    .getMessage(commandLabel, command1, commandSender, null, "DisEnchant.NotInInv")
                                                                    .replace("<TYPE>", arguments[0]));
            return true;
        }

        itemsList.forEach(this::removeEnchantments);
        p.sendMessage(this.getPrefix() + this.plugin.getMessages()
                                                    .getMessage(commandLabel, command.getName(), commandSender, null, "DisEnchant.Type")
                                                    .replace("<TYPE>", arguments[0]));
        return true;
    }

    private String getPrefix() {
        return this.plugin.getMessages().getPrefix();
    }

    private void removeEnchantments(ItemStack item) {
        if (item != null) {
            item.getEnchantments();
            for (var e : item.getEnchantments().keySet())
                item.removeEnchantment(e);
        }
    }
}
