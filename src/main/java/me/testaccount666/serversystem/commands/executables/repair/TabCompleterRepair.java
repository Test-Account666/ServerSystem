package me.testaccount666.serversystem.commands.executables.repair;

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;

import java.util.List;
import java.util.Optional;

public class TabCompleterRepair implements ServerSystemTabCompleter {

    @Override
    public Optional<List<String>> tabComplete(User commandSender, Command command, String label, String... arguments) {
        if (!PermissionManager.hasCommandPermission(commandSender, "Repair.Use", false)) return Optional.of(List.of());
        if (arguments.length != 1) return Optional.of(List.of());

        var potentialCompletions = List.of("all", "*", "armor", "hand", "offhand", "inventory");
        var completions = potentialCompletions.stream().filter(completion -> completion.toLowerCase().startsWith(arguments[0].toLowerCase())).toList();
        return Optional.of(completions);

    }
}
