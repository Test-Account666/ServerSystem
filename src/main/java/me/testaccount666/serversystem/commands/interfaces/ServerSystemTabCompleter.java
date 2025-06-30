package me.testaccount666.serversystem.commands.interfaces;

import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;

import java.util.List;
import java.util.Optional;

public interface ServerSystemTabCompleter {
    Optional<List<String>> tabComplete(User commandSender, Command command, String label, String... arguments);
}
