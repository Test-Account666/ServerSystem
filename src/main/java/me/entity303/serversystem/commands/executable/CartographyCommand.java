package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CartographyCommand extends MessageUtils implements CommandExecutor {

    public CartographyCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "cartography")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("cartography")));
            return true;
        }
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }
        this.plugin.getVersionStuff().getVirtualCartography().openCartography((Player) cs);
        return true;
    }
}
