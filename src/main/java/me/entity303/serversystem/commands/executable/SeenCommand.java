package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class SeenCommand extends MessageUtils implements TabExecutor {
    //TODO: Seen Command, hopp hopp, Hutch meckert

    public SeenCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!this.isAllowed(sender, "seen")) {
            sender.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("seen")));
            return true;
        }

        if (args.length <= 0) {
            sender.sendMessage(this.getPrefix() + this.getSyntax("Seen", label, command.getName(), sender, null));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target.getLastPlayed() <= 0) {
            sender.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Seen.PlayerNeverPlayed", label, command.getName(), sender, target.getName()));
            return true;
        }

        long lastPlayed = target.getLastPlayed();

        if (target.isOnline())
            lastPlayed = System.currentTimeMillis();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(this.getMessageWithStringTarget("Seen.TimeFormat", label, command.getName(), sender, target.getName()));

        LocalDateTime date = Instant.ofEpochMilli(lastPlayed).atZone(ZoneId.systemDefault()).toLocalDateTime();
        String format = dtf.format(date);

        sender.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("Seen.LastSeen", label, command.getName(), sender, target.getName()).replace("<TIME>", format));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!this.isAllowed(sender, "seen", true))
            return Collections.singletonList("");

        List<String> players = Arrays.stream(Bukkit.getOfflinePlayers()).filter(offlinePlayer -> !offlinePlayer.isOnline()).map(OfflinePlayer::getName).collect(Collectors.toList());

        List<String> possiblePlayers = new ArrayList<>();

        for (String player : players)
            if (player.toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT)))
                possiblePlayers.add(player);

        return !possiblePlayers.isEmpty() ? possiblePlayers : players;
    }
}
