package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AnvilCommand extends MessageUtils implements CommandExecutor {

    public AnvilCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }

        if (!this.isAllowed(cs, "anvil")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("anvil")));
            return true;
        }

        this.plugin.getVersionStuff().getVirtualAnvil().openAnvil((Player) cs);
        return true;
    }
}
