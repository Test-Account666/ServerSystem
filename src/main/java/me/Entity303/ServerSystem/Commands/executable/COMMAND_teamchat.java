package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.stream.Collectors;


public class COMMAND_teamchat extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_teamchat(ss plugin) {
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
