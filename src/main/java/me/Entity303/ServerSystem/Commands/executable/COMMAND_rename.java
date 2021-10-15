package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ChatColor;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class COMMAND_rename extends MessageUtils implements CommandExecutor {

    public COMMAND_rename(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "rename")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("rename")));
            return true;
        }
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }

        if (args.length <= 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("Rename", label, cmd.getName(), cs, null));
            return true;
        }

        ((Player) cs).getInventory().getItemInHand();
        if (((Player) cs).getInventory().getItemInHand().getType() == Material.AIR) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Rename.NoItem", label, cmd.getName(), cs, null));
            return true;
        }
        ItemStack handStack = ((Player) cs).getInventory().getItemInHand();
        StringBuilder nameBuilder = new StringBuilder();
        for (String arg : args) nameBuilder.append(arg).append(" ");

        String name = ChatColor.translateAlternateColorCodes('&', nameBuilder.toString().trim());

        ItemMeta meta = handStack.getItemMeta();
        meta.setDisplayName(name);

        handStack.setItemMeta(meta);

        cs.sendMessage(this.getPrefix() + this.getMessage("Rename.Success", label, cmd.getName(), cs, null).replace("<NAME>", name));
        return true;
    }
}
