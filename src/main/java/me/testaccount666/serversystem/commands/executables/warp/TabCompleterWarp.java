package me.testaccount666.serversystem.commands.executables.warp;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.executables.warp.manager.Warp;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;

import java.util.List;
import java.util.Optional;

public class TabCompleterWarp implements ServerSystemTabCompleter {

    @Override
    public Optional<List<String>> tabComplete(User commandSender, Command command, String label, String... arguments) {
        var commandName = command.getName();
        if (!commandName.equalsIgnoreCase("warp") && !commandName.equalsIgnoreCase("deletewarp")) return Optional.empty();

        var permissionPath = switch (commandName) {
            case "warp" -> "Warp.Use";
            case "deletewarp" -> "Warp.Delete";
            case "setwarp" -> "Warp.Set";
            default -> null;
        };

        if (!PermissionManager.hasCommandPermission(commandSender, permissionPath, false)) return Optional.of(List.of());

        var potentialCompletions = ServerSystem.Instance.getWarpManager().getWarps().stream().map(Warp::getName).toList();
        var completions = potentialCompletions.stream().filter(completion -> completion.toLowerCase().startsWith(arguments[0].toLowerCase())).toList();

        return Optional.of(completions);
    }
}
