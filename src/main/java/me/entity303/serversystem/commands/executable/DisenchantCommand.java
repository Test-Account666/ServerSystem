package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DisenchantCommand implements CommandExecutor {
    private final ServerSystem plugin;

    public DisenchantCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    private void removeEnchantments(ItemStack item) {
        if (item != null) {
            item.getEnchantments();
            for (Enchantment e : item.getEnchantments().keySet()) item.removeEnchantment(e);
        }
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        Player p = (Player) cs;
        if (!this.isAllowed(p, "disenchant")) {
            p.sendMessage(this.getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("disenchant")));
            return true;
        }

        if (args.length == 0) {
            p.getInventory().getItemInHand();
            this.removeEnchantments(p.getInventory().getItemInHand());
            p.sendMessage(this.getPrefix() + this.getMessage("DisEnchant.Hand", label, cmd.getName(), cs, null));
            return true;
        }

        ItemStack[] inv = p.getInventory().getContents();
        if ("all".equalsIgnoreCase(args[0])) {
            for (ItemStack items : inv) this.removeEnchantments(items);
            p.sendMessage(this.getPrefix() + this.getMessage("DisEnchant.All", label, cmd.getName(), cs, null));
            return true;
        }

        List<ItemStack> itms = new ArrayList<>();

        for (ItemStack items : inv) {
            if (items == null) continue;
            if (items.getType() == Material.getMaterial(args[0].toUpperCase())) itms.add(items);
        }

        if (itms.size() <= 0) {
            cs.sendMessage(this.getPrefix() + this.getMessage("DisEnchant.NotInInv", label, cmd.getName(), cs, null).replace("<TYPE>", args[0]));
            return true;
        }

        itms.forEach(this::removeEnchantments);
        p.sendMessage(this.getPrefix() + this.getMessage("DisEnchant.Type", label, cmd.getName(), cs, null).replace("<TYPE>", args[0]));
        return true;
    }

    private String getPrefix() {
        return this.plugin.getMessages().getPrefix();
    }

    private String getMessage(String action, String label, String command, CommandSender sender, CommandSender target) {
        return this.plugin.getMessages().getMessage(label, command, sender, target, action);
    }

    private boolean isAllowed(CommandSender cs, String permission) {
        return this.plugin.getPermissions().hasPerm(cs, permission);
    }
}
