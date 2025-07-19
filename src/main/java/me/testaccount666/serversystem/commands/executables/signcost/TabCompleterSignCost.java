package me.testaccount666.serversystem.commands.executables.signcost;

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TabCompleterSignCost implements ServerSystemTabCompleter {

    private static final Set<String> _COST_TYPES = Set.of("none", "exp", "economy");

    @Override
    public Optional<List<String>> tabComplete(User commandSender, Command command, String label, String... arguments) {
        if (!PermissionManager.hasCommandPermission(commandSender, "SignCost.Use", false)) return Optional.of(List.of());

        if (arguments.length == 1) {
            var completions = _COST_TYPES.stream()
                    .filter(type -> type.toLowerCase().startsWith(arguments[0].toLowerCase()))
                    .collect(Collectors.toList());
            return Optional.of(completions);
        }

        return Optional.of(List.of());
    }
}