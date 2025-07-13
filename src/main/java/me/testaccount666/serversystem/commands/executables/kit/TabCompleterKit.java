package me.testaccount666.serversystem.commands.executables.kit;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TabCompleterKit implements ServerSystemTabCompleter {
    private static final List<String> _TIME_UNITS = List.of("s", "m", "h", "d", "w", "mo", "y");
    private static final List<String> _NUMBERS = IntStream.range(0, 10).mapToObj(String::valueOf).toList();

    @Override
    public Optional<List<String>> tabComplete(User commandSender, Command command, String label, String... arguments) {
        var commandName = command.getName().toLowerCase();
        if (!hasPermission(commandSender, commandName)) return Optional.of(List.of());

        return switch (commandName) {
            case "kit" -> handleKitCommand(arguments);
            case "createkit" -> handleCreateKitCommand(arguments);
            case "deletekit" -> handleDeleteKitCommand(arguments);
            default -> Optional.empty();
        };
    }

    private Optional<List<String>> handleKitCommand(String... arguments) {
        return switch (arguments.length) {
            case 1 -> handleKitNameCompletion(arguments[0]);
            case 2 -> handlePlayerNameCompletion(arguments[1]);
            default -> Optional.empty();
        };
    }

    private Optional<List<String>> handleCreateKitCommand(String... arguments) {
        return switch (arguments.length) {
            case 1 -> Optional.of(List.of());
            case 2 -> handleTimeCompletion(arguments[1]);
            default -> Optional.empty();
        };
    }

    private Optional<List<String>> handleDeleteKitCommand(String... arguments) {
        if (arguments.length != 1) return Optional.of(List.of());

        return handleKitNameCompletion(arguments[0]);
    }

    private boolean hasPermission(User commandSender, String commandName) {
        var permissionNode = switch (commandName) {
            case "kit" -> "Kit.Use";
            case "createkit" -> "Kit.Create";
            case "deletekit" -> "Kit.Delete";
            default -> null;
        };
        return PermissionManager.hasCommandPermission(commandSender.getCommandSender(), permissionNode, false);
    }

    private Optional<List<String>> handleKitNameCompletion(String argument) {
        var kitManager = ServerSystem.Instance.getKitManager();
        if (kitManager == null) return Optional.of(List.of());

        var possibleCompletions = kitManager.getAllKitNames();

        if (argument.isEmpty()) return Optional.of(possibleCompletions);

        var filteredCompletions = possibleCompletions.stream()
                .filter(name -> name.toLowerCase().startsWith(argument.toLowerCase()))
                .collect(Collectors.toList());

        return Optional.of(filteredCompletions);
    }

    private Optional<List<String>> handlePlayerNameCompletion(String argument) {
        var possibleCompletions = Bukkit.getOnlinePlayers().stream()
                .map(Player::getName).collect(Collectors.toList());

        if (argument.isEmpty()) return Optional.of(possibleCompletions);

        var filteredCompletions = possibleCompletions.stream()
                .filter(name -> name.toLowerCase().startsWith(argument.toLowerCase()))
                .collect(Collectors.toList());

        return Optional.of(filteredCompletions);
    }

    private Optional<List<String>> handleTimeCompletion(String argument) {
        if (argument.isEmpty()) return Optional.of(getDefaultTimeSuggestions());

        var suggestions = new ArrayList<String>();

        if (argument.matches(".*\\d$")) suggestions.addAll(getTimeSuggestions(argument));
        else if (argument.matches("\\d+")) suggestions.addAll(getTimeSuggestions(argument));
        else suggestions.addAll(getMatchingTimeUnits(argument));

        return Optional.of(suggestions);
    }

    private List<String> getDefaultTimeSuggestions() {
        return new ArrayList<>(_NUMBERS);
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
}
