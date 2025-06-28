package me.testaccount666.serversystem.commands.wrappers;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.userdata.UserManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public abstract class AbstractCommandWrapper {
    protected static Optional<User> resolveCommandUser(CommandSender commandSender) {
        if (!(commandSender instanceof Player player)) return Optional.of(UserManager.getConsoleUser());

        var cachedUserOptional = ServerSystem.Instance.getUserManager().getUser(player);

        if (cachedUserOptional.isEmpty()) return Optional.empty();

        var cachedUser = cachedUserOptional.get();

        if (!cachedUser.isOnlineUser()) return Optional.empty();

        return Optional.of((User) cachedUser.getOfflineUser());
    }
}
