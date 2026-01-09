package me.testaccount666.serversystem.placeholderapi.executables;

import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.placeholderapi.Placeholder;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.OfflineUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class OnlinePlayersPlaceholder implements Placeholder {
    @Override
    public String execute(@Nullable OfflineUser user, String identifier, String... arguments) {
        var count = Bukkit.getOnlinePlayers().stream().filter(player -> !player.hasMetadata("vanished")).count();
        if ((!(user instanceof User onlineUser)) || user instanceof ConsoleUser) return String.valueOf(count);

        var player = onlineUser.getPlayer();
        if (PermissionManager.hasCommandPermission(player, "Vanish.Show", false)) return String.valueOf(Bukkit.getOnlinePlayers().size());

        return String.valueOf(count);
    }

    @Override
    public Set<String> getIdentifiers() {
        return Set.of("onlineplayers");
    }
}
