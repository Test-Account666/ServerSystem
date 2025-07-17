package me.testaccount666.serversystem.commands.executables.language;

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TabCompleterLanguages implements ServerSystemTabCompleter {
    private final File _messagesDirectory = Path.of("plugins", "ServerSystem", "messages").toFile();

    @Override
    public Optional<List<String>> tabComplete(User commandSender, Command command, String label, String... arguments) {
        if (arguments.length != 1) return Optional.of(List.of());

        var possibleCompletions = Arrays.stream(_messagesDirectory.listFiles()).map(File::getName);
        var completions = possibleCompletions.filter(language -> language.toLowerCase().startsWith(arguments[0].toLowerCase())).toList();
        return Optional.of(completions);
    }
}
