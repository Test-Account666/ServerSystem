package me.testaccount666.serversystem.commands.wrappers;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TabCompleterWrapper extends AbstractCommandWrapper implements TabCompleter {
    private final ServerSystemTabCompleter _tabCompleter;

    public TabCompleterWrapper(ServerSystemTabCompleter tabCompleter) {
        _tabCompleter = tabCompleter;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] arguments) {
        var commandUser = resolveCommandUser(commandSender);

        // This should technically never happen...
        if (commandUser.isEmpty()) {
            ServerSystem.getLog().severe("Error tab completing command '${command.getName()}'. CommandSender '${commandSender.getName()}' is not a valid user?!");
            return List.of();
        }

        return _tabCompleter.tabComplete(commandUser.get(), command, label, arguments).orElse(null);
    }
}
