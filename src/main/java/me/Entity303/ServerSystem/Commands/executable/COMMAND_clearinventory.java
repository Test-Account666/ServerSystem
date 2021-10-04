package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class COMMAND_clearinventory extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_clearinventory(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (!(cs instanceof Player)) return true;
            if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.clearinventory.self.required"))
                if (!this.plugin.getPermissions().hasPerm(cs, "clearinventory.self.permission")) {
                    cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("clearinventory.self.permission")));
                    return true;
                }
            int counter = 0;
            for (int i = 0; i < (27 + 9); i++)
                if (((Player) cs).getInventory().getItem(i) != null && ((Player) cs).getInventory().getItem(i).getType() != Material.AIR)
                    counter = counter + ((Player) cs).getInventory().getItem(i).getAmount();
            ItemStack h = ((Player) cs).getInventory().getHelmet();
            ItemStack c = ((Player) cs).getInventory().getChestplate();
            ItemStack l = ((Player) cs).getInventory().getLeggings();
            ItemStack b = ((Player) cs).getInventory().getBoots();
            if (h != null && h.getType() != Material.AIR) counter = counter + h.getAmount();
            if (c != null && c.getType() != Material.AIR) counter = counter + c.getAmount();
            if (l != null && l.getType() != Material.AIR) counter = counter + l.getAmount();
            if (b != null && b.getType() != Material.AIR) counter = counter + b.getAmount();
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "ClearInventory.Self").replace("<AMOUNT>", String.valueOf(counter)));
            ((Player) cs).getInventory().setHelmet(null);
            ((Player) cs).getInventory().setChestplate(null);
            ((Player) cs).getInventory().setLeggings(null);
            ((Player) cs).getInventory().setBoots(null);
            ((Player) cs).getInventory().clear();
            return true;
        }
        if (!this.plugin.getPermissions().hasPerm(cs, "clearinventory.others")) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("clearinventory.others")));
            return true;
        }
        Player target = this.getPlayer(cs, args[0]);
        if (target == null) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(args[0]));
            return true;
        }
        int counter = 0;
        for (int i = 0; i < (27 + 9); i++)
            if (target.getInventory().getItem(i) != null && target.getInventory().getItem(i).getType() != Material.AIR)
                counter = counter + target.getInventory().getItem(i).getAmount();
        ItemStack h = target.getInventory().getHelmet();
        ItemStack c = target.getInventory().getChestplate();
        ItemStack l = target.getInventory().getLeggings();
        ItemStack b = target.getInventory().getBoots();
        if (h != null && h.getType() != Material.AIR) counter = counter + h.getAmount();
        if (c != null && c.getType() != Material.AIR) counter = counter + c.getAmount();
        if (l != null && l.getType() != Material.AIR) counter = counter + l.getAmount();
        if (b != null && b.getType() != Material.AIR) counter = counter + b.getAmount();
        target.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, target, "ClearInventory.Others.Target").replace("<AMOUNT>", String.valueOf(counter)));
        target.getInventory().setHelmet(null);
        target.getInventory().setChestplate(null);
        target.getInventory().setLeggings(null);
        target.getInventory().setBoots(null);
        target.getInventory().clear();
        cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, target, "ClearInventory.Others.Sender").replace("<AMOUNT>", String.valueOf(counter)));
        return true;
    }
}
