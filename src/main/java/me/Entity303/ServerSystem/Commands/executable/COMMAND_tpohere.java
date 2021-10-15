package me.Entity303.ServerSystem.Commands.executable;


import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class COMMAND_tpohere extends MessageUtils implements CommandExecutor {

    public COMMAND_tpohere(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "tpohere")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("tpohere")));
            return true;
        }
        if (args.length < 1) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("TpoHere", label, cmd.getName(), cs, null));
            return true;
        }
        Player target = this.getPlayer(cs, args[0]);
        if (target == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }
        target.teleport((Entity) cs);
        cs.sendMessage(this.getPrefix() + this.getMessage("TpoHere", label, cmd.getName(), cs, target));
        return true;
    }
}
