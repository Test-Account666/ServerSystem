package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;


public class COMMAND_disposal implements CommandExecutor {
    private final ss plugin;

    public COMMAND_disposal(ss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }
        if (this.plugin.getMessages().getCfg().getBoolean("Permissions.disposal.required"))
            if (!this.plugin.getPermissions().hasPerm(cs, "disposal.permission")) {
                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("disposal.permission")));
                return true;
            }
        Inventory disposal = Bukkit.getServer().createInventory(null, 54, this.plugin.getMessages().getMiscMessage(label, cmd.getName(), cs, null, "DisposalName"));
        ((Player) cs).openInventory(disposal);
        return true;
    }
}
