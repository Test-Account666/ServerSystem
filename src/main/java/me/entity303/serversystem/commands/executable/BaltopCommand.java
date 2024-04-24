package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BaltopCommand extends CommandUtils implements ICommandExecutorOverload {

    public BaltopCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        Bukkit.getScheduler().runTaskAsynchronously(this._plugin, () -> {
            var pluginMessages = this._plugin.GetMessages();
            var prefix = pluginMessages.GetPrefix();
            if (this._plugin.GetPermissions().GetConfiguration().GetBoolean("Permissions.baltop.required"))
                if (!this._plugin.GetPermissions().HasPermission(commandSender, "baltop.permission")) {
                    var permission = this._plugin.GetPermissions().GetPermission("baltop.permission");
                    var noPermissionMessage = pluginMessages.GetNoPermission(permission);
                    commandSender.sendMessage(prefix + noPermissionMessage);
                    return;
                }

            List<String> topPlayers = new LinkedList<>();
            var topTen = this._plugin.GetEconomyManager().GetTopTen();
            this.AssignTopPlayers(topTen, topPlayers);

            if (topPlayers.isEmpty()) {
                commandSender.sendMessage(prefix + ChatColor.RED + "Error!");
                return;
            }

            var message = pluginMessages.GetMessage(commandLabel, command, commandSender, null, "BalTop");
            commandSender.sendMessage(message.replace("<FIRST>", topPlayers.get(0))
                                             .replace("<SECOND>", topPlayers.get(1))
                                             .replace("<THIRD>", topPlayers.get(2))
                                             .replace("<FOURTH>", topPlayers.get(3))
                                             .replace("<FIFTH>", topPlayers.get(4))
                                             .replace("<SIXTH>", topPlayers.get(5))
                                             .replace("<SEVENTH>", topPlayers.get(6))
                                             .replace("<EIGHTH>", topPlayers.get(7))
                                             .replace("<NINTH>", topPlayers.get(8))
                                             .replace("<TENTH>", topPlayers.get(9)));
        });
        return true;
    }

    private void AssignTopPlayers(Map<? extends OfflinePlayer, Double> topTen, List<String> topPlayers) {
        var count = 0;
        for (var entry : topTen.entrySet()) {
            if (count >= 10)
                break;
            var topName = entry.getKey().getName();
            var balance = entry.getValue();
            var formattedBalance = this._plugin.GetEconomyManager().Format(balance);
            topPlayers.add(topName + " -> " + formattedBalance);
            count++;
        }

        var topPlayersSize = topPlayers.size();
        if (topPlayersSize < 10) {
            var lastPlayer = topPlayers.get(topPlayersSize - 1);

            while (topPlayers.size() < 10)
                topPlayers.add(lastPlayer);
        }
    }
}


