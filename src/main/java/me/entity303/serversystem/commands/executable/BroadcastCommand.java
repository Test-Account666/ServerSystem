package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BroadcastCommand implements CommandExecutor {
    private final ServerSystem plugin;

    public BroadcastCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.plugin.getPermissions().hasPerm(cs, "broadcast")) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("broadcast")));
            return true;
        }
        if (args.length == 0) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(label, cmd.getName(), cs, null, "BroadCast"));
            return true;
        }

        StringBuilder builder = new StringBuilder();
        for (String arg : args) builder.append(arg).append(" ");
        Bukkit.broadcastMessage(this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "Broadcast").replace("<MESSAGE>", ChatColor.translateAlternateColorCodes('&', builder.toString())));
        return true;
    }
}
