package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.stream.Collectors;


public class TeamChatCommand extends CommandUtils implements CommandExecutorOverload {

    public TeamChatCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "teamchat.send")) {
            var permission = this.plugin.getPermissions().getPermission("teamchat.send");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }
        if (arguments.length < 1) {
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "TeamChat"));
            return true;
        }
        var msg = Arrays.stream(arguments).map(word -> word + " ").collect(Collectors.joining());
        
        Bukkit.broadcast(this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "TeamChat").replace("<MESSAGE>", msg),
                         this.plugin.getPermissions().getPermission("teamchat.recieve"));
        return true;
    }
}
