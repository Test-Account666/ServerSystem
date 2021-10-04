package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_enderchest extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_enderchest(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }

        if (args.length == 0) {
            if (!this.isAllowed(cs, "enderchest.self")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("enderchest.self")));
                return true;
            }
            ((Player) cs).openInventory(((Player) cs).getEnderChest());
            return true;
        }

        if (!this.isAllowed(cs, "enderchest.others")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("enderchest.others")));
            return true;
        }

        Player targetPlayer = this.getPlayer(cs, args[0]);
        if (targetPlayer == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }
        if (this.isAllowed(targetPlayer, "enderchest.exempt", true))
            this.plugin.getEnderchest().put(((Player) cs), targetPlayer);
        ((Player) cs).openInventory(targetPlayer.getEnderChest());
        return true;
    }
}
