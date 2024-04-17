package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.stream.Collectors;

public class KickAllCommand extends CommandUtils implements CommandExecutorOverload {

    public KickAllCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "kickall")) {
            var permission = this.plugin.getPermissions().getPermission("kickall");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        var command1 = command.getName();
        var reason = this.plugin.getMessages().getMessage(commandLabel, command1, commandSender, null, "KickAll.DefaultReason");

        if (arguments.length > 0)
            reason = Arrays.stream(arguments).map(arg -> arg + " ").collect(Collectors.joining());

        for (var all : Bukkit.getOnlinePlayers()) {
            if (all == commandSender)
                continue;
            
            all.kickPlayer(this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "KickAll.Kick").replace("<REASON>", reason));
        }

        
        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "KickAll.Success"));
        return true;
    }
}
