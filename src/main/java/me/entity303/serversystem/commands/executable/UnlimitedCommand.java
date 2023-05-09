package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UnlimitedCommand extends MessageUtils implements CommandExecutor {

    public UnlimitedCommand(ServerSystem plugin) {
        super(plugin);
    }

    public static boolean isUnlimited(ItemStack itemStack) {
        return ServerSystem.getPlugin(ServerSystem.class).getVersionStuff().getNbtViewer().isTagSet("unlimited", itemStack);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command command, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }

        if (!this.isAllowed(cs, "unlimited")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission("unlimited"));
            return true;
        }

        if (((Player) cs).getInventory().getItemInHand() == null) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Unlimited.NoItem", label, command.getName(), cs, null));
            return true;
        }

        if (((Player) cs).getInventory().getItemInHand().getType() == Material.AIR) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Unlimited.NoItem", label, command.getName(), cs, null));
            return true;
        }

        ItemStack itemStack = ((Player) cs).getInventory().getItemInHand();

        if (UnlimitedCommand.isUnlimited(itemStack)) {
            this.plugin.getVersionStuff().getNbtViewer().removeTag("unlimited", itemStack);
            cs.sendMessage(this.getPrefix() + this.getMessage("Unlimited.LimitedNow", label, command.getName(), cs, null));
            return true;
        }

        this.plugin.getVersionStuff().getNbtViewer().setTag("unlimited", itemStack);

        cs.sendMessage(this.getPrefix() + this.getMessage("Unlimited.UnlimitedNow", label, command.getName(), cs, null));
        return true;
    }
}
