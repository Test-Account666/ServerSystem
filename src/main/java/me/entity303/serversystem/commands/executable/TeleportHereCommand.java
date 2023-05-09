package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import me.entity303.serversystem.utils.Teleport;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class TeleportHereCommand extends MessageUtils implements CommandExecutor {

    public TeleportHereCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "tphere")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("tphere")));
            return true;
        }
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }
        if (args.length <= 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("Tphere", label, cmd.getName(), cs, null));
            return true;
        }
        Player target = this.getPlayer(cs, args[0]);
        if (target == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }
        if (!this.plugin.wantsTP(target)) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Tphere.NoTeleportations", label, cmd.getName(), cs, target));
            return true;
        }

        Teleport.teleport(target, (Entity) cs);
        cs.sendMessage(this.getPrefix() + this.getMessage("Tphere.Success", label, cmd.getName(), cs, target));
        return true;
    }
}
