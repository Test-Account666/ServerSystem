package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StackCommand extends MessageUtils implements CommandExecutor {

    public StackCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (!(cs instanceof Player)) {
                cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
                return true;
            }
            if (!this.isAllowed(cs, "stack")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("stack")));
                return true;
            }
            ((Player) cs).getInventory().getItemInHand();
            ((Player) cs).getInventory().getItemInHand().setAmount(((Player) cs).getInventory().getItemInHand().getMaxStackSize());
            cs.sendMessage(this.getPrefix() + this.getMessage("Stack.Success", label, cmd.getName(), cs, null));
        }
        return true;
    }
}
