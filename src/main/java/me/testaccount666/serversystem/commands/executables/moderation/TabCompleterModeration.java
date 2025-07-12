package me.testaccount666.serversystem.commands.executables.moderation;

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TabCompleterModeration implements ServerSystemTabCompleter {

    private static final List<String> _TIME_UNITS = List.of("s", "m", "h", "d", "w", "mo", "y");
    private static final List<String> _NUMBERS = IntStream.range(0, 10).mapToObj(String::valueOf).toList();

    private static final String _PERMANENT = "permanent";

    @Override
    public Optional<List<String>> tabComplete(User commandSender, Command command, String label, String... arguments) {
        var commandName = command.getName().toLowerCase();
        if (!hasPermission(commandSender, commandName)) return Optional.of(Collections.emptyList());

        return switch (arguments.length) {
            case 1 -> handlePlayerNameCompletion(arguments[0]);
            case 2 -> handleTimeCompletion(arguments[1], commandName);
            default -> Optional.empty();
        };
    }

    private boolean hasPermission(User commandSender, String commandName) {
        var permissionNode = switch (commandName) {
            case "mute" -> "Moderation.Mute.Use";
            case "unmute" -> "Moderation.Mute.Remove";
            case "shadowmute" -> "Moderation.Mute.Shadow";
            case "ban" -> "Moderation.Ban.Use";
            case "unban" -> "Moderation.Ban.Remove";
            default -> null;
        };
        return PermissionManager.hasCommandPermission(commandSender.getCommandSender(), permissionNode, false);
    }

    private Optional<List<String>> handlePlayerNameCompletion(String argument) {
        var possibleCompletions = Arrays.stream(Bukkit.getOfflinePlayers())
                .map(OfflinePlayer::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (argument.isEmpty()) return Optional.of(possibleCompletions);

        var filteredCompletions = possibleCompletions.stream()
                .filter(name -> name.toLowerCase().startsWith(argument.toLowerCase()))
                .collect(Collectors.toList());

        return Optional.of(filteredCompletions);
    }

    private Optional<List<String>> handleTimeCompletion(String argument, String commandName) {
        if (isRemoveCommand(commandName)) return Optional.empty();
        if (argument.isEmpty()) return Optional.of(getDefaultTimeSuggestions());

        var suggestions = new ArrayList<String>();

        if (_PERMANENT.startsWith(argument.toLowerCase())) suggestions.add(_PERMANENT);

        if (argument.matches(".*\\d$")) suggestions.addAll(getTimeSuggestions(argument));
        else if (argument.matches("\\d+")) suggestions.addAll(getTimeSuggestions(argument));
        else suggestions.addAll(getMatchingTimeUnits(argument));

        return Optional.of(suggestions);
    }

    private List<String> getDefaultTimeSuggestions() {
        var suggestions = new ArrayList<>(_NUMBERS);
        suggestions.add(_PERMANENT);
        return suggestions;
    }

    private List<String> getTimeSuggestions(String argument) {
        var suggestions = new ArrayList<String>();
        _NUMBERS.forEach(num -> suggestions.add(argument + num));
        _TIME_UNITS.forEach(unit -> suggestions.add(argument + unit));
        return suggestions;
    }

    private List<String> getMatchingTimeUnits(String argument) {
        return _TIME_UNITS.stream()
                .filter(unit -> unit.startsWith(argument.toLowerCase()))
                .collect(Collectors.toList());
    }

    private boolean isRemoveCommand(String commandName) {
        return commandName.toLowerCase().startsWith("un");
    }
}
