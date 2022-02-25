package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SmithingTableCommand extends MessageUtils implements CommandExecutor {

    public SmithingTableCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "smithingtable")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("smithingtable")));
            return true;
        }
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }
        this.plugin.getVersionStuff().getVirtualSmithing().openSmithing((Player) cs);
        return true;
    }
}
