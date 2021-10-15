package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class COMMAND_kick extends MessageUtils implements CommandExecutor {

    public COMMAND_kick(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "kick.use")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("kick.use")));
            return true;
        }
        if (args.length == 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("Kick", label, cmd.getName(), cs, null));
            return true;
        }
        Player target = this.getPlayer(cs, args[0]);
        if (target == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }
        if (this.isAllowed(target, "kick.exempt", true)) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Kick.CannotKick", label, cmd.getName(), cs, target));
            return true;
        }
        String reason = this.getMessage("Kick.DefaultReason", label, cmd.getName(), cs, target);
        if (args.length > 1)
            reason = IntStream.range(1, args.length).mapToObj(i -> args[i] + " ").collect(Collectors.joining());
        target.kickPlayer(this.getMessage("Kick.Kick", label, cmd.getName(), cs, target).replace("<REASON>", reason));
        cs.sendMessage(this.getPrefix() + this.getMessage("Kick.Success", label, cmd.getName(), cs, target));
        return true;
    }
}
