package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static me.entity303.serversystem.commands.executable.OfflineEnderChestCommand.GetOfflinePlayers;

public class SeenCommand extends CommandUtils implements TabExecutor {
    //TODO: Seen Command, hopp hopp, Hutch meckert

    public SeenCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!this.plugin.getPermissions().hasPermission(sender, "seen")) {
            var permission = this.plugin.getPermissions().getPermission("seen");
            sender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (args.length == 0) {
            var command1 = command.getName();
            sender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(label, command1, sender, null, "Seen"));
            return true;
        }

        var target = Bukkit.getOfflinePlayer(args[0]);
        if (target.getLastPlayed() <= 0) {
            var command1 = command.getName();
            var target1 = target.getName();
            sender.sendMessage(this.plugin.getMessages().getPrefix() +
                               this.plugin.getMessages().getMessageWithStringTarget(label, command1, sender, target1, "Seen.PlayerNeverPlayed"));
            return true;
        }

        var lastPlayed = target.getLastPlayed();

        if (target.isOnline())
            lastPlayed = System.currentTimeMillis();

        var command2 = command.getName();
        var target2 = target.getName();
        var dtf = DateTimeFormatter.ofPattern(this.plugin.getMessages().getMessageWithStringTarget(label, command2, sender, target2, "Seen.TimeFormat"));

        var date = Instant.ofEpochMilli(lastPlayed).atZone(ZoneId.systemDefault()).toLocalDateTime();
        var format = dtf.format(date);

        var command1 = command.getName();
        var target1 = target.getName();
        sender.sendMessage(this.plugin.getMessages().getPrefix() +
                           this.plugin.getMessages().getMessageWithStringTarget(label, command1, sender, target1, "Seen.LastSeen").replace("<TIME>", format));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!this.plugin.getPermissions().hasPermission(sender, "seen", true))
            return Collections.singletonList("");

        return GetOfflinePlayers(args);
    }
}
