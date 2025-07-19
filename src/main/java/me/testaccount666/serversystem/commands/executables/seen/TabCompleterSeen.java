package me.testaccount666.serversystem.commands.executables.seen;

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TabCompleterSeen implements ServerSystemTabCompleter {

    @Override
    public Optional<List<String>> tabComplete(User commandSender, Command command, String label, String... arguments) {
        if (!PermissionManager.hasCommandPermission(commandSender, "Seen.Use", false)) return Optional.empty();

        if (arguments.length == 1) return Optional.of(Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList());
        return Optional.empty();
    }
}
