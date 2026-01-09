package me.testaccount666.serversystem.commands.wrappers;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.userdata.UserManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public abstract class AbstractCommandWrapper {
    protected static Optional<User> resolveCommandUser(CommandSender commandSender) {
        if (!(commandSender instanceof Player player)) return Optional.of(UserManager.Companion.getConsoleUser());

        var cachedUserOptional = ServerSystem.getInstance().getRegistry().getService(UserManager.class).getUser(player);

        if (cachedUserOptional.isEmpty()) return Optional.empty();

        var cachedUser = cachedUserOptional.get();

        if (cachedUser.isOfflineUser()) return Optional.empty();

        return Optional.ofNullable((User) cachedUser.getOfflineUser());
    }
}
