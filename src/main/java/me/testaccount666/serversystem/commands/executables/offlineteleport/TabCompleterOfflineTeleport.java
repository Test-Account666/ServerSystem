package me.testaccount666.serversystem.commands.executables.offlineteleport;

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TabCompleterOfflineTeleport implements ServerSystemTabCompleter {

    @Override
    public Optional<List<String>> tabComplete(User commandSender, Command command, String label, String... arguments) {
        var permissionName = switch (command.getName()) {
            case "offlineteleport" -> "OfflineTeleport.Use";
            case "offlineteleporthere" -> "OfflineTeleportHere.Use";
            default -> {
                Bukkit.getLogger().warning("Unknown OfflineTeleport command: ${command.getName()}");
                yield null;
            }
        };

        if (permissionName == null) return Optional.of(List.of());
        if (!PermissionManager.hasPermission(commandSender.getCommandSender(), permissionName, false)) return Optional.of(List.of());
        if (arguments.length != 1) return Optional.of(List.of());

        var possibleCompletions = Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).filter(Objects::nonNull).toList();
        var completions = possibleCompletions.stream().filter(completion -> completion.toLowerCase().startsWith(arguments[0].toLowerCase())).toList();

        return Optional.of(completions.isEmpty()? possibleCompletions : completions);
    }
}
