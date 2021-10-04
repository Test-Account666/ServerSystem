package me.Entity303.ServerSystem.Commands.executable;


import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;


public class COMMAND_clearenderchest extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_clearenderchest(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (!(cs instanceof Player)) {
                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(label, cmd.getName(), cs, null, "ClearEnderChest"));
                return true;
            }
            if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.clearenderchest.self.required"))
                if (!this.plugin.getPermissions().hasPerm(cs, "clearenderchest.self.permission")) {
                    cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("clearenderchest.self.permission")));
                    return true;
                }

            int counter = 0;
            for (int i = 0; i < ((Player) cs).getEnderChest().getSize(); i++) {
                Inventory inventory = ((Player) cs).getEnderChest();
                if (inventory.getItem(i) != null) counter = counter + inventory.getItem(i).getAmount();
            }
            ((Player) cs).getEnderChest().clear();
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "ClearEnderChest.Self").replace("<AMOUNT>", String.valueOf(counter)));
            return true;
        }

        if (!this.plugin.getPermissions().hasPerm(cs, "clearenderchest.others")) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("clearenderchest.others")));
            return true;
        }
        Player target = this.getPlayer(cs, args[0]);
        if (target == null) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(args[0]));
            return true;
        }
        int counter = 0;
        for (int i = 0; i < target.getEnderChest().getSize(); i++) {
            Inventory inventory = target.getEnderChest();
            if (inventory.getItem(i) != null) counter = counter + inventory.getItem(i).getAmount();
        }
        target.getEnderChest().clear();
        target.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, target, "ClearEnderChest.Others.Target").replace("<AMOUNT>", String.valueOf(counter)));
        cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, target, "ClearEnderChest.Others.Sender").replace("<AMOUNT>", String.valueOf(counter)));
        return true;
    }
}
