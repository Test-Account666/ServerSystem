package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;

public class RepairCommand extends MessageUtils implements CommandExecutor {

    public RepairCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }
        Player p = (Player) cs;
        if (!this.isAllowed(cs, "repair")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("repair")));
            return true;
        }
        if (args.length <= 0) {
            p.getInventory().getItemInHand();
            if (p.getInventory().getItemInHand().getType() == Material.AIR) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Repair.NoItem", label, cmd.getName(), cs, null));
                return true;
            }
            p.getInventory().getItemInHand().setDurability((short) 0);
            cs.sendMessage(this.getPrefix() + this.getMessage("Repair.Hand", label, cmd.getName(), cs, null));
            return true;
        }
        if ("all".equalsIgnoreCase(args[0])) {
            for (int i = 0; i < p.getInventory().getSize(); i++) {
                ItemStack items = p.getInventory().getItem(i);
                if (items == null) continue;
                if (items.getType() == Material.AIR) continue;
                if (items.getItemMeta() == null) continue;
                if (!(items.getItemMeta() instanceof Repairable) && !(items instanceof Repairable) && !(items.getType().getMaxDurability() > items.getDurability()))
                    continue;
                items.setDurability((short) 0);
            }
            cs.sendMessage(this.getPrefix() + this.getMessage("Repair.All", label, cmd.getName(), cs, null));
            return true;
        }

        if ("armor".equalsIgnoreCase(args[0])) {
            for (int i = 0; i < 4; i++) {
                ItemStack items;
                switch (i) {
                    case 0: {
                        items = ((Player) cs).getInventory().getHelmet();
                    }
                    case 1: {
                        items = ((Player) cs).getInventory().getChestplate();
                    }
                    case 2: {
                        items = ((Player) cs).getInventory().getLeggings();
                    }
                    case 3: {
                        items = ((Player) cs).getInventory().getBoots();
                    }
                    default: {
                        items = ((Player) cs).getInventory().getBoots();
                    }
                }
                if (items == null) continue;
                if (items.getType() == Material.AIR) continue;
                if (items.getItemMeta() == null) continue;
                if (!(items.getItemMeta() instanceof Repairable) && !(items instanceof Repairable) && !(items.getType().getMaxDurability() > items.getDurability()))
                    continue;
                items.setDurability((short) 0);
            }

            cs.sendMessage(this.getPrefix() + this.getMessage("Repair.Armor", label, cmd.getName(), cs, null));
            return true;
        }

        boolean found = false;
        for (/*ItemStack items : p.getInventory().getContents()*/ int i = 0; i < p.getInventory().getSize(); i++) {
            ItemStack items = p.getInventory().getItem(i);
            if (Material.getMaterial(args[0].toUpperCase()) == null) break;
            if (items == null) continue;
            if (items.getType() == Material.AIR) continue;
            if (items.getType() != Material.getMaterial(args[0].toUpperCase())) continue;
            if (items.getItemMeta() == null) continue;
            if (items.getItemMeta() instanceof Repairable || items instanceof Repairable || (items.getType().getMaxDurability() > items.getDurability())) {
                items.setDurability((short) 0);
                found = true;
            }
        }
        if (!found) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Repair.NoType", label, cmd.getName(), cs, null).replace("<TYPE>", args[0].toUpperCase()));
            return true;
        }
        p.sendMessage(this.getPrefix() + "Alle Items der Art " + ChatColor.DARK_GRAY + args[0] + ChatColor.GRAY + " wurden repariert!");
        return true;
    }
}
