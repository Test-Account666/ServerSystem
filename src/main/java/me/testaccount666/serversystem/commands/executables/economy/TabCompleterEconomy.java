package me.testaccount666.serversystem.commands.executables.economy;

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;

import java.util.List;
import java.util.Optional;

public class TabCompleterEconomy implements ServerSystemTabCompleter {

    @Override
    public Optional<List<String>> tabComplete(User commandSender, Command command, String label, String... arguments) {
        if (!PermissionManager.hasCommandPermission(commandSender, "Economy.Use", false)) return Optional.of(List.of());

        if (arguments.length == 1) {
            var potentialCompletions = List.of("set", "take", "give");
            var completions = potentialCompletions.stream().filter(completion -> completion.toLowerCase().startsWith(arguments[0].toLowerCase())).toList();
            return Optional.of(completions);
        }

        if (arguments.length == 2) return Optional.empty();

        return Optional.of(List.of());
    }
}
