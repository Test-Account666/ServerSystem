package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
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

public class BaltopCommand extends CommandUtils implements CommandExecutorOverload {

    public BaltopCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            if (this.plugin.getPermissions().getConfiguration().getBoolean("Permissions.baltop.required"))
                if (!this.plugin.getPermissions().hasPermission(commandSender, "baltop.permission")) {
                    var permission = this.plugin.getPermissions().getPermission("baltop.permission");
                    commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                    return;
                }

            List<String> topPlayers = new LinkedList<>();
            this.assignTopPlayers(this.plugin.getEconomyManager().getTopTen(), topPlayers);

            if (topPlayers.isEmpty()) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + ChatColor.RED + "Error!");
                return;
            }

            commandSender.sendMessage(this.plugin.getMessages()
                                                 .getMessage(commandLabel, command, commandSender, null, "BalTop")
                                                 .replace("<FIRST>", topPlayers.get(0))
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

    private void assignTopPlayers(Map<OfflinePlayer, Double> topTen, List<String> topPlayers) {
        var count = 0;
        for (var entry : topTen.entrySet()) {
            if (count >= 10)
                break;
            topPlayers.add(entry.getKey().getName() + " -> " + this.plugin.getEconomyManager().format(entry.getValue()));
            count++;
        }

        if (topPlayers.size() < 10) {
            var lastPlayer = topPlayers.get(topPlayers.size() - 1);

            while (topPlayers.size() < 10)
                topPlayers.add(lastPlayer);
        }
    }
}


