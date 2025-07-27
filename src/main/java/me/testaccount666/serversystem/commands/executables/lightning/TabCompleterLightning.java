package me.testaccount666.serversystem.commands.executables.lightning;

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;

import java.util.List;
import java.util.Optional;

public class TabCompleterLightning implements ServerSystemTabCompleter {

    @Override
    public Optional<List<String>> tabComplete(User commandSender, Command command, String label, String... arguments) {
        if (arguments.length == 1) {
            if (!"visual".startsWith(arguments[0].toLowerCase())) return Optional.of(List.of());

            return Optional.of(List.of("visual"));
        }

        return Optional.empty();
    }
}
