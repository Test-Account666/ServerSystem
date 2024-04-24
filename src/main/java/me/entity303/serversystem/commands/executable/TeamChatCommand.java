package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.stream.Collectors;


public class TeamChatCommand extends CommandUtils implements ICommandExecutorOverload {

    public TeamChatCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "teamchat.send")) {
            var permission = this._plugin.GetPermissions().GetPermission("teamchat.send");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }
        if (arguments.length < 1) {
            
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "TeamChat"));
            return true;
        }
        var msg = Arrays.stream(arguments).map(word -> word + " ").collect(Collectors.joining());
        
        Bukkit.broadcast(this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "TeamChat").replace("<MESSAGE>", msg),
                         this._plugin.GetPermissions().GetPermission("teamchat.recieve"));
        return true;
    }
}
