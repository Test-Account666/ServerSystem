package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;

import static java.lang.Runtime.getRuntime;

public class LagCommand extends CommandUtils implements CommandExecutorOverload {
    private static final DecimalFormat twoDPlaces = new DecimalFormat("#,###.##");

    public LagCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(sender, "lag")) {
            var permission = this.plugin.getPermissions().getPermission("lag");
            sender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        var tps = this.getPlugin().getTimer().getAverageTPS();
        ChatColor color;
        if (tps >= 18.0)
            color = ChatColor.GREEN;
        else if (tps >= 15.0)
            color = ChatColor.YELLOW;
        else
            color = ChatColor.RED;

        var maxMemory = getRuntime().maxMemory();
        var totalMemory = getRuntime().totalMemory();
        var freeMemory = getRuntime().freeMemory();

        sender.sendMessage(ChatColor.GRAY + "TPS: " + color + LagCommand.formatDouble(tps));
        sender.sendMessage(ChatColor.GRAY + "Max RAM: " + ChatColor.GOLD + maxMemory / 1048576L + " MB");
        sender.sendMessage(ChatColor.GRAY + "Total RAM: " + ChatColor.GOLD + totalMemory / 1048576L + " MB");
        sender.sendMessage(ChatColor.GRAY + "Free RAM: " + ChatColor.GOLD + freeMemory / 1048576L + " MB");
        sender.sendMessage(ChatColor.GRAY + "Used RAM: " + ChatColor.GOLD + (totalMemory - freeMemory) / 1048576L + " MB");
        sender.sendMessage(ChatColor.GRAY + "Processors: " + ChatColor.GOLD + getRuntime().availableProcessors());
        return true;
    }

    private ServerSystem getPlugin() {
        return this.plugin;
    }

    private static String formatDouble(double value) {
        return LagCommand.twoDPlaces.format(value);
    }
}
