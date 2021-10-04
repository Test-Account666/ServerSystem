package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class COMMAND_sudo extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_sudo(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "sudo.use")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("sudo.use")));
            return true;
        }
        if (args.length <= 1) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("Sudo", label, cmd.getName(), cs, null));
            return true;
        }
        Player target = this.getPlayer(cs, args[0]);
        if (target == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }
        if (this.isAllowed(target, "sudo.exempt")) {
            if (cs instanceof Player) {
                cs.sendMessage(this.getPrefix() + this.getMessage("Sudo", label, cmd.getName(), cs, target));
                return true;
            }
            StringBuilder msg = new StringBuilder();
            for (int i = 1; args.length > i; i++) msg.append(args[i]).append(" ");
            target.chat(msg.toString());
            return true;
        }
        StringBuilder msg = new StringBuilder();
        for (int i = 1; args.length > i; i++) msg.append(args[i]).append(" ");
        target.chat(msg.toString());
        return true;
    }
}
