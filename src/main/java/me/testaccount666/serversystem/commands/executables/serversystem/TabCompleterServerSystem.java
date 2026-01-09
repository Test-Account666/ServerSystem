package me.testaccount666.serversystem.commands.executables.serversystem;

import me.testaccount666.migration.plugins.MigratorRegistry;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TabCompleterServerSystem implements ServerSystemTabCompleter {
    @Override
    public Optional<List<String>> tabComplete(User commandSender, Command command, String label, String... arguments) {
        if (!PermissionManager.hasCommandPermission(commandSender, "ServerSystem.Use", false)) return Optional.empty();

        if (arguments.length <= 1) {
            var possibleCompletions = List.of("version", "reload", "migrate");
            if (arguments.length == 0) return Optional.of(possibleCompletions);

            var completions = possibleCompletions.stream().filter(completion -> completion.toLowerCase().startsWith(arguments[0].toLowerCase())).toList();
            return Optional.of(completions);
        }

        var subCommand = arguments[0].toLowerCase();
        if (subCommand.equals("migrate")) {
            var newArguments = new String[arguments.length - 1];
            System.arraycopy(arguments, 1, newArguments, 0, newArguments.length);

            return migrate(newArguments);
        }
        return Optional.of(Collections.emptyList());
    }

    public Optional<List<String>> migrate(String... arguments) {
        if (arguments.length <= 1) {
            var possibleCompletions = List.of("to", "from");
            if (arguments.length == 0) return Optional.of(possibleCompletions);

            var completions = possibleCompletions.stream().filter(completion -> completion.toLowerCase().startsWith(arguments[0].toLowerCase())).toList();
            return Optional.of(completions);
        }

        if (arguments.length > 2) return Optional.of(Collections.emptyList());

        var migratorRegistry = ServerSystem.getInstance().getRegistry().getService(MigratorRegistry.class);
        var possibleCompletions = migratorRegistry.getMigrators();

        var completions = possibleCompletions.stream().filter(completion -> completion.toLowerCase().startsWith(arguments[1].toLowerCase())).toList();
        return Optional.of(completions);
    }
}
