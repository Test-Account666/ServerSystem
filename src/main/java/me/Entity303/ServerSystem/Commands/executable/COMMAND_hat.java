package me.Entity303.ServerSystem.Commands.executable;


import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class COMMAND_hat extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_hat(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cs instanceof Player) {
            if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.hat.required"))
                if (!this.isAllowed(cs, "hat.permission")) {
                    cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("hat.permission")));
                    return true;
                }
            ((Player) cs).getInventory().getItemInHand().getType();
            if (((Player) cs).getInventory().getItemInHand().getType() != Material.AIR) {
                if (((Player) cs).getInventory().getHelmet() != null)
                    ((Player) cs).getInventory().addItem(((Player) cs).getInventory().getHelmet());
                ((Player) cs).getInventory().setHelmet(((Player) cs).getInventory().getItemInHand());
                ((Player) cs).getInventory().removeItem(((Player) cs).getInventory().getItemInHand());
                cs.sendMessage(this.getPrefix() + this.getMessage("Hat.Success.NewHat", label, cmd.getName(), cs, null).replace("<TYPE>", ((Player) cs).getInventory().getHelmet().getType().toString()));
            } else if (((Player) cs).getInventory().getHelmet() != null) {
                ((Player) cs).getInventory().addItem(((Player) cs).getInventory().getHelmet());
                cs.sendMessage(this.getPrefix() + this.getMessage("Hat.Success.HatRemoved", label, cmd.getName(), cs, null).replace("<TYPE>", ((Player) cs).getInventory().getHelmet().getType().toString()));
                ((Player) cs).getInventory().setHelmet(new ItemStack(Material.AIR));
            } else cs.sendMessage(this.getPrefix() + this.getMessage("Hat.NoItem", label, cmd.getName(), cs, null));
        } else cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
        return true;
    }
}
