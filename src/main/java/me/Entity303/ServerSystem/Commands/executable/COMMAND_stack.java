package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_stack extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_stack(ss plugin) {
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
