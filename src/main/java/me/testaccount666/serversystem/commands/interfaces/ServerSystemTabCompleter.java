package me.testaccount666.serversystem.commands.interfaces;

import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;

import java.util.List;
import java.util.Optional;

/**
 * Custom Interface for tab completers.
 * Classes implementing this interface can be registered as tab completers
 * with the CommandManager to provide tab completion for commands.
 */
public interface ServerSystemTabCompleter {
    /**
     * Provides tab completion options for a command.
     *
     * @param commandSender The user who is tab-completing the command
     * @param command       The command being tab-completed
     * @param label         The alias of the command that was used
     * @param arguments     The arguments passed to the command
     * @return An Optional containing a list of possible completions, or an empty Optional to use default completions
     */
    Optional<List<String>> tabComplete(User commandSender, Command command, String label, String... arguments);
}
