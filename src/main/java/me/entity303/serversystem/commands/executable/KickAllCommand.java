package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

public class KickAllCommand extends MessageUtils implements CommandExecutor {

    public KickAllCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "kickall")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("kickall")));
            return true;
        }

        String reason = this.getMessage("KickAll.DefaultReason", label, cmd.getName(), cs, null);

        if (args.length > 0) reason = Arrays.stream(args).map(arg -> arg + " ").collect(Collectors.joining());

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (all == cs) continue;
            all.kickPlayer(this.getMessage("KickAll.Kick", label, cmd.getName(), cs, null).replace("<REASON>", reason));
        }

        cs.sendMessage(this.getPrefix() + this.getMessage("KickAll.Success", label, cmd.getName(), cs, null));
        return true;
    }
}
