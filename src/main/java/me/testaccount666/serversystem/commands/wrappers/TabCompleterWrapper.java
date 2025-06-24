package me.testaccount666.serversystem.commands.wrappers;

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TabCompleterWrapper extends AbstractCommandWrapper implements TabCompleter {
    private final ServerSystemTabCompleter tabCompleter;

    public TabCompleterWrapper(ServerSystemTabCompleter tabCompleter) {
        this.tabCompleter = tabCompleter;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] arguments) {
        var commandUser = resolveCommandUser(commandSender);

        if (commandUser.isEmpty()) return List.of();

        return tabCompleter.tabComplete(commandUser.get(), command, label, arguments);
    }
}
