package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportAllCommand extends MessageUtils implements CommandExecutor {

    public TeleportAllCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (!(cs instanceof Player)) {
                cs.sendMessage(this.getPrefix() + this.getSyntax("TpAll", label, cmd.getName(), cs, null));
                return true;
            }
            if (!this.isAllowed(cs, "tpall.self")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("tpall.self")));
                return true;
            }
            Bukkit.getOnlinePlayers().forEach(all -> all.teleport(((Player) cs)));
            cs.sendMessage(this.getPrefix() + this.getMessage("TpAll.Self", label, cmd.getName(), cs, null));
        } else if (this.isAllowed(cs, "tpall.others")) {
            Player target = this.getPlayer(cs, args[0]);
            if (target != null) {
                Bukkit.getOnlinePlayers().forEach(all -> all.teleport(target));
                target.sendMessage(this.getPrefix() + this.getMessage("TpAll.Self", label, cmd.getName(), cs, target));
                cs.sendMessage(this.getPrefix() + this.getMessage("TpAll.Others", label, cmd.getName(), cs, target));
            } else cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
        } else if (cs instanceof Player) if (this.isAllowed(cs, "tpall.self")) {
            Bukkit.getOnlinePlayers().forEach(all -> all.teleport(((Player) cs)));
            cs.sendMessage(this.getPrefix() + this.getMessage("TpAll.Self", label, cmd.getName(), cs, null));
        } else cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("tpall.others")));
        else
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("tpall.others")));
        return true;
    }
}
