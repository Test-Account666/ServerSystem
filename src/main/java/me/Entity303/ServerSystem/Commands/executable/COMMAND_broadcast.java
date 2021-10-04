package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class COMMAND_broadcast implements CommandExecutor {
    private final ss plugin;

    public COMMAND_broadcast(ss plugin) {
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
