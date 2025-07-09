package me.testaccount666.serversystem.commands.executables.offlineteleport;

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;

import java.util.List;
import java.util.Optional;

import static me.testaccount666.serversystem.commands.common.tabcompleters.OfflinePlayerTabCompletion.getOfflinePlayerNames;

public class TabCompleterOfflineTeleport implements ServerSystemTabCompleter {

    @Override
    public Optional<List<String>> tabComplete(User commandSender, Command command, String label, String... arguments) {
        var permissionName = switch (command.getName()) {
            case "offlineteleport" -> "OfflineTeleport.Use";
            case "offlineteleporthere" -> "OfflineTeleportHere.Use";
            default -> {
                Bukkit.getLogger().warning("Unknown OfflineTeleport command: ${command.getName()}");
                yield null;
            }
        };

        if (permissionName == null) return Optional.of(List.of());
        if (!PermissionManager.hasPermission(commandSender.getCommandSender(), permissionName, false)) return Optional.of(List.of());
        if (arguments.length != 1) return Optional.of(List.of());

        return getOfflinePlayerNames(arguments);
    }
}
