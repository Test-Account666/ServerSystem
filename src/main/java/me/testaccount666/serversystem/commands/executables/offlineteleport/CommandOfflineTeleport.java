package me.testaccount666.serversystem.commands.executables.offlineteleport;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.CachedUser;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Optional;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "offlineteleport", variants = "offlineteleporthere", tabCompleter = TabCompleterOfflineTeleport.class)
public class CommandOfflineTeleport extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return;
        }

        if (command.getName().equalsIgnoreCase("offlineteleport")) {
            handleOfflineTeleport(commandSender, command, label, arguments);
            return;
        }

        if (command.getName().equalsIgnoreCase("offlineteleporthere")) handleOfflineTeleportHere(commandSender, command, label, arguments);
    }

    private void handleOfflineTeleport(User commandSender, Command command, String label, String... arguments) {
        var cachedUserOptional = getTargetUser(commandSender, label, arguments[0]);
        if (cachedUserOptional.isEmpty()) return;

        var cachedUser = cachedUserOptional.get();
        var targetName = cachedUser.getOfflineUser().getName().orElse(arguments[0]);

        commandSender.getPlayer().teleport(cachedUser.getOfflineUser().getLogoutPosition());

        command("OfflineTeleport.Success", commandSender).target(targetName).build();
    }

    private void handleOfflineTeleportHere(User commandSender, Command command, String label, String... arguments) {
        var cachedUserOptional = getTargetUser(commandSender, label, arguments[0]);
        if (cachedUserOptional.isEmpty()) return;

        var cachedUser = cachedUserOptional.get();
        var targetName = cachedUser.getOfflineUser().getName().orElse(arguments[0]);

        cachedUser.getOfflineUser().setLogoutPosition(commandSender.getPlayer().getLocation());
        cachedUser.getOfflineUser().save();

        command("OfflineTeleportHere.Success", commandSender).target(targetName).build();
    }

    private Optional<CachedUser> getTargetUser(User commandSender, String label, String name) {
        var cachedUserOptional = ServerSystem.Instance.getUserManager().getUser(name);

        if (cachedUserOptional.isEmpty()) {
            general("ErrorOccurred", commandSender).label(label).target(name).build();
            return Optional.empty();
        }

        var cachedUser = cachedUserOptional.get();
        var targetName = cachedUser.getOfflineUser().getName().orElse(name);

        if (cachedUser.isOnlineUser()) {
            general("Offline.NotOffline", commandSender).target(targetName).build();
            return Optional.empty();
        }

        return Optional.of(cachedUser);
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        if (command.getName().equalsIgnoreCase("offlineteleport"))
            return PermissionManager.hasCommandPermission(player, "OfflineTeleport.Use", false);

        return PermissionManager.hasCommandPermission(player, "OfflineTeleportHere.Use", false);
    }
}
