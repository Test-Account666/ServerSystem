package me.testaccount666.serversystem.commands.executables.inventorysee.online;

import me.testaccount666.serversystem.commands.executables.inventorysee.offline.TabCompleterOfflineInventorySee;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;

import java.util.List;
import java.util.Optional;

public class TabCompleterInventorySee implements ServerSystemTabCompleter {
    private final TabCompleterOfflineInventorySee _offlineInventorySee;

    public TabCompleterInventorySee() {
        _offlineInventorySee = new TabCompleterOfflineInventorySee();
    }

    @Override
    public Optional<List<String>> tabComplete(User commandSender, Command command, String label, String... arguments) {
        if (command.getName().toLowerCase().startsWith("offline")) return _offlineInventorySee.tabComplete(commandSender, command, label, arguments);

        if (!PermissionManager.hasCommandPermission(commandSender, "InventorySee.Use", false)) return Optional.of(List.of());

        if (arguments.length <= 1) return Optional.empty();

        return Optional.of(List.of());
    }
}
