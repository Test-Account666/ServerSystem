package me.testaccount666.serversystem.commands.executables.home.admin;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.OfflineUser;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.userdata.home.Home;
import org.bukkit.command.Command;

import java.util.List;
import java.util.Optional;

public class TabCompleterAdminHome implements ServerSystemTabCompleter {

    @Override
    public Optional<List<String>> tabComplete(User commandSender, Command command, String label, String... arguments) {
        var commandName = command.getName().substring("admin".length());

        if (commandName.equalsIgnoreCase("sethome")) return handleSetHomeCommand(commandSender, arguments);

        if (commandName.equalsIgnoreCase("deletehome")) return handleDeleteHomeCommand(commandSender, arguments);

        return handleHomeCommand(commandSender, arguments);
    }

    private Optional<List<String>> handleHomeCommand(User commandSender, String... arguments) {
        if (!PermissionManager.hasPermission(commandSender.getCommandSender(), "Commands.AdminHome.Use", false)) return Optional.empty();

        if (arguments.length <= 1) return Optional.empty();

        return handleHomeCompletion(commandSender, arguments);
    }

    private Optional<List<String>> handleSetHomeCommand(User commandSender, String... arguments) {
        if (!PermissionManager.hasPermission(commandSender.getCommandSender(), "Commands.AdminSetHome.Use", false)) return Optional.empty();
        if (arguments.length <= 1) return Optional.empty();

        return Optional.of(List.of());
    }

    private Optional<List<String>> handleDeleteHomeCommand(User commandSender, String... arguments) {
        if (!PermissionManager.hasPermission(commandSender.getCommandSender(), "Commands.AdminDeleteHome.Use", false)) return Optional.empty();
        if (arguments.length <= 1) return Optional.empty();

        return handleHomeCompletion(commandSender, arguments);
    }

    private Optional<List<String>> handleHomeCompletion(OfflineUser commandSender, String... arguments) {
        if (arguments.length != 2) return Optional.of(List.of());

        var targetUserOptional = ServerSystem.Instance.getUserManager().getUser(arguments[0]);
        if (targetUserOptional.isEmpty()) return Optional.of(List.of());

        var targetCachedUser = targetUserOptional.get();
        var targetUser = targetCachedUser.getOfflineUser();

        var homeManager = targetUser.getHomeManager();

        var potentialCompletions = homeManager.getHomes().stream().map(Home::getDisplayName).toList();
        var foundCompletions = potentialCompletions.stream().filter(completion -> completion.toLowerCase().startsWith(arguments[1].toLowerCase())).toList();

        return foundCompletions.isEmpty()? Optional.of(potentialCompletions) : Optional.of(foundCompletions);
    }
}
