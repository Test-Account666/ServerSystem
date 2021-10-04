package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;


public class COMMAND_unsign extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_unsign(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "unsign")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("unsign")));
            return true;
        }
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }
        ((Player) cs).getInventory().getItemInHand();
        if (((Player) cs).getInventory().getItemInHand().getType() == Material.AIR) {
            cs.sendMessage(this.getPrefix() + this.getMessage("UnSign.NoItem", label, cmd.getName(), cs, null));
            return true;
        }
        ItemMeta meta = ((Player) cs).getInventory().getItemInHand().getItemMeta();
        if (!meta.hasLore()) {
            cs.sendMessage(this.getPrefix() + this.getMessage("UnSign.NotSigned", label, cmd.getName(), cs, null));
            return true;
        }
        meta.setLore(null);
        ((Player) cs).getInventory().getItemInHand().setItemMeta(meta);
        ((Player) cs).updateInventory();
        cs.sendMessage(this.getPrefix() + this.getMessage("UnSign.Success", label, cmd.getName(), cs, null));
        return true;
    }
}
