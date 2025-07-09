package me.testaccount666.serversystem.commands.executables.home.user;

import me.testaccount666.serversystem.commands.executables.home.admin.TabCompleterAdminHome;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.userdata.home.Home;
import org.bukkit.command.Command;

import java.util.List;
import java.util.Optional;

public class TabCompleterHome implements ServerSystemTabCompleter {
    private final TabCompleterAdminHome _tabCompleterAdminHome;

    public TabCompleterHome() {
        _tabCompleterAdminHome = new TabCompleterAdminHome();
    }

    @Override
    public Optional<List<String>> tabComplete(User commandSender, Command command, String label, String... arguments) {
        if (command.getName().toLowerCase().startsWith("admin")) return _tabCompleterAdminHome.tabComplete(commandSender, command, label, arguments);

        if (command.getName().equalsIgnoreCase("sethome")) return handleSetHomeCommand();

        if (command.getName().equalsIgnoreCase("deletehome")) return handleDeleteHomeCommand(commandSender, label, arguments);

        return handleHomeCommand(commandSender, label, arguments);
    }

    private Optional<List<String>> handleHomeCommand(User commandSender, String label, String... arguments) {
        if (!PermissionManager.hasPermission(commandSender.getCommandSender(), "Commands.Home.Use", false)) return Optional.empty();

        return handleHomeCompletion(commandSender, arguments);
    }

    private Optional<List<String>> handleSetHomeCommand() {
        return Optional.of(List.of());
    }

    private Optional<List<String>> handleDeleteHomeCommand(User commandSender, String label, String... arguments) {
        if (!PermissionManager.hasPermission(commandSender.getCommandSender(), "Commands.DeleteHome.Use", false)) return Optional.empty();

        return handleHomeCompletion(commandSender, arguments);
    }

    private Optional<List<String>> handleHomeCompletion(User commandSender, String... arguments) {
        if (arguments.length != 1) return Optional.of(List.of());

        var homeManager = commandSender.getHomeManager();

        var potentialCompletions = homeManager.getHomes().stream().map(Home::getDisplayName).toList();

        var completions = potentialCompletions.stream().filter(completion -> completion.toLowerCase().startsWith(arguments[0].toLowerCase())).toList();

        return Optional.of(completions);
    }
}
