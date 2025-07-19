package me.testaccount666.serversystem.commands.common.tabcompleters;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class OfflinePlayerTabCompletion {
    public static Optional<List<String>> getOfflinePlayerNames(String[] arguments) {
        var possibleCompletions = Arrays.stream(Bukkit.getOfflinePlayers())
                .filter(offlinePlayer -> !offlinePlayer.isOnline())
                .map(OfflinePlayer::getName).toList();
        var completions = possibleCompletions.stream().filter(completion -> completion.toLowerCase().startsWith(arguments[0].toLowerCase())).toList();

        return Optional.of(completions);
    }
}
