package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StoneCutterCommand extends MessageUtils implements CommandExecutor {

    public StoneCutterCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "stonecutter")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("stonecutter")));
            return true;
        }
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }
        this.plugin.getVersionStuff().getVirtualStoneCutter().openCutter((Player) cs);
        return true;
    }
}
