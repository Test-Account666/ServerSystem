package me.Entity303.ServerSystem.Commands.executable;


import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class COMMAND_skull_newer extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_skull_newer(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }
        if (args.length == 0) {
            if (!this.isAllowed(cs, "skull.self")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("skull.self")));
                return true;
            }
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(((Player) cs).getUniqueId()));
            skull.setItemMeta(skullMeta);
            /*if (!((Player) cs).getInventory().contains(skull)) */
            ((Player) cs).getInventory().addItem(skull);
            /*else
                for (ItemStack itemStack : ((Player) cs).getInventory().getContents())
                    if (itemStack == skull) itemStack.setAmount(itemStack.getAmount() + 1);*/
            cs.sendMessage(this.getPrefix() + this.getMessage("Skull.Self", label, cmd.getName(), cs, null));
        } else if (this.isAllowed(cs, "skull.others", true)) {
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(args[0]));
            skull.setItemMeta(skullMeta);
            /*if (!((Player) cs).getInventory().contains(skull)) */
            ((Player) cs).getInventory().addItem(skull);
            /*else
                for (ItemStack itemStack : ((Player) cs).getInventory().getContents())
                    if (itemStack == skull) itemStack.setAmount(itemStack.getAmount() + 1);*/
            cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Skull.Others", label, cmd.getName(), cs, args[0]));
        } else {
            if (!this.isAllowed(cs, "skull.self")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("skull.others")));
                return true;
            }
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(((Player) cs).getUniqueId()));
            skull.setItemMeta(skullMeta);
            /*if (!((Player) cs).getInventory().contains(skull)) */
            ((Player) cs).getInventory().addItem(skull);
            /*else
                for (ItemStack itemStack : ((Player) cs).getInventory().getContents())
                    if (itemStack == skull) itemStack.setAmount(itemStack.getAmount() + 1);*/
            cs.sendMessage(this.getPrefix() + this.getMessage("skull.self", label, cmd.getName(), cs, null));
        }
        return true;
    }
}
