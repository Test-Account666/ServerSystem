package me.testaccount666.serversystem.commands.executables.time;

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;

import java.util.List;
import java.util.Optional;

public class TabCompleterTime implements ServerSystemTabCompleter {

    @Override
    public Optional<List<String>> tabComplete(User commandSender, Command command, String label, String... arguments) {
        if (!PermissionManager.hasCommandPermission(commandSender, "Time.Use", false)) return Optional.of(List.of());

        if (command.getName().equalsIgnoreCase("time")) {
            if (arguments.length <= 1) {
                var possibleCompletions = List.of("day", "night", "noon", "midnight");
                var completions = possibleCompletions.stream().filter(completion -> completion.toLowerCase().startsWith(arguments[0].toLowerCase())).toList();

                return Optional.of(completions);
            }

            if (arguments.length == 2) return handleWorldCompletions(commandSender, 1, arguments);

            return Optional.of(List.of());
        }

        if (arguments.length == 1) return handleWorldCompletions(commandSender, 0, arguments);

        return Optional.of(List.of());
    }

    private Optional<List<String>> handleWorldCompletions(User commandSender, int index, String... arguments) {
        if (!PermissionManager.hasCommandPermission(commandSender, "Time.World", false)) return Optional.of(List.of());

        var possibleCompletions = Bukkit.getWorlds().stream().map(World::getName).toList();
        var completions = possibleCompletions.stream().filter(completion -> completion.toLowerCase().startsWith(arguments[index].toLowerCase())).toList();

        return Optional.of(completions);
    }
}
