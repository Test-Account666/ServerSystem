package me.testaccount666.serversystem.commands.executables.inventorysee.offline;

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;

import java.util.List;
import java.util.Optional;

import static me.testaccount666.serversystem.commands.common.tabcompleters.OfflinePlayerTabCompletion.getOfflinePlayerNames;

public class TabCompleterOfflineInventorySee implements ServerSystemTabCompleter {

    @Override
    public Optional<List<String>> tabComplete(User commandSender, Command command, String label, String... arguments) {
        if (!PermissionManager.hasCommandPermission(commandSender, "OfflineInventorySee.Use", false))
            return Optional.of(List.of());

        if (arguments.length <= 1) return getOfflinePlayerNames(arguments);

        return Optional.of(List.of());
    }
}
