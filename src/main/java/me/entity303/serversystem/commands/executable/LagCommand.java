package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;

import static java.lang.Runtime.getRuntime;

public class LagCommand implements ICommandExecutorOverload {
    private static final DecimalFormat TWO_DECIMALS_FORMAT = new DecimalFormat("#,###.##");
    protected final ServerSystem _plugin;

    public LagCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "lag")) {
            var permission = this._plugin.GetPermissions().GetPermission("lag");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        var tps = this._plugin.GetTimer().GetAverageTPS();
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

        commandSender.sendMessage(ChatColor.GRAY + "TPS: " + color + LagCommand.FormatDouble(tps));
        commandSender.sendMessage(ChatColor.GRAY + "Max RAM: " + ChatColor.GOLD + maxMemory / 1048576L + " MB");
        commandSender.sendMessage(ChatColor.GRAY + "Total RAM: " + ChatColor.GOLD + totalMemory / 1048576L + " MB");
        commandSender.sendMessage(ChatColor.GRAY + "Free RAM: " + ChatColor.GOLD + freeMemory / 1048576L + " MB");
        commandSender.sendMessage(ChatColor.GRAY + "Used RAM: " + ChatColor.GOLD + (totalMemory - freeMemory) / 1048576L + " MB");
        commandSender.sendMessage(ChatColor.GRAY + "Processors: " + ChatColor.GOLD + getRuntime().availableProcessors());
        return true;
    }

    private ServerSystem GetPlugin() {
        return this._plugin;
    }

    private static String FormatDouble(double value) {
        return LagCommand.TWO_DECIMALS_FORMAT.format(value);
    }
}
