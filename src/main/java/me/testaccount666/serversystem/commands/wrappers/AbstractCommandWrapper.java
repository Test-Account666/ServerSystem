package me.testaccount666.serversystem.commands.wrappers;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.userdata.UserManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public abstract class AbstractCommandWrapper {
    protected static Optional<User> resolveCommandUser(CommandSender commandSender) {
        if (commandSender instanceof Player player) return ServerSystem.Instance.getUserManager().getUser(player.getUniqueId());
        return Optional.of(UserManager.getConsoleUser());
    }
}
