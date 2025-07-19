package me.testaccount666.serversystem.commands;

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;

import java.util.List;
import java.util.Optional;

public class DefaultTabCompleter implements ServerSystemTabCompleter {

    @Override
    public Optional<List<String>> tabComplete(User commandSender, Command command, String label, String... arguments) {
        return Optional.empty();
    }
}
