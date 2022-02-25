package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.stream.Collectors;


public class TeamChatCommand extends MessageUtils implements CommandExecutor {

    public TeamChatCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "teamchat.send")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("teamchat.send")));
            return true;
        }
        if (args.length < 1) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("TeamChat", label, cmd.getName(), cs, null));
            return true;
        }
        String msg = Arrays.stream(args).map(word -> word + " ").collect(Collectors.joining());
        Bukkit.broadcast(this.getMessage("TeamChat", label, cmd.getName(), cs, null).replace("<MESSAGE>", msg), this.Perm("teamchat.recieve"));
        return true;
    }
}
