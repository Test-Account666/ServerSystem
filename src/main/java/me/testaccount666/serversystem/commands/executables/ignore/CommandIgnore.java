package me.testaccount666.serversystem.commands.executables.ignore;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Optional;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "ignore", variants = "unignore")
public class CommandIgnore extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (command.getName().equalsIgnoreCase("ignore")) {
            executeIgnore(commandSender, label, arguments);
            return;
        }

        executeUnignore(commandSender, label, arguments);
    }

    private void executeUnignore(User commandSender, String label, String... arguments) {
        var targetUserOptional = validateAndGetUser(commandSender, label, "Unignore", arguments);
        if (targetUserOptional.isEmpty()) return;

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();

        var targetUuid = targetPlayer.getUniqueId();
        if (!commandSender.isIgnoredPlayer(targetUuid)) {
            command("Unignore.NotIgnored", commandSender).target(targetPlayer.getName()).build();
            return;
        }

        commandSender.removeIgnoredPlayer(targetUuid);
        commandSender.save();
        command("Unignore.Success", commandSender).target(targetPlayer.getName()).build();
    }


    private void executeIgnore(User commandSender, String label, String... arguments) {
        var targetUserOptional = validateAndGetUser(commandSender, label, "Ignore", arguments);
        if (targetUserOptional.isEmpty()) return;

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();

        var targetUuid = targetPlayer.getUniqueId();
        if (commandSender.isIgnoredPlayer(targetUuid)) {
            command("Ignore.AlreadyIgnored", commandSender).target(targetPlayer.getName()).build();
            return;
        }

        commandSender.addIgnoredPlayer(targetUuid);
        commandSender.save();
        command("Ignore.Success", commandSender).target(targetPlayer.getName()).build();
    }

    private Optional<User> validateAndGetUser(User commandSender, String label, String command, String... arguments) {
        if (!checkBasePermission(commandSender, "${command}.Use")) return Optional.empty();
        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return Optional.empty();
        }
        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).syntax(getSyntaxPath(null)).label(label).build();
            return Optional.empty();
        }

        var targetUserOptional = getTargetUser(commandSender, false, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return Optional.empty();
        }

        var targetUser = targetUserOptional.get();
        var isSelf = targetUser == commandSender;

        if (isSelf) {
            command("${command}.Self", commandSender).build();
            return Optional.empty();
        }

        return Optional.of(targetUser);
    }

    @Override
    public String getSyntaxPath(Command command) {
        return "Ignore";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        var permissionPath = command.getName().equalsIgnoreCase("ignore")? "Ignore.Use" : "Unignore.Use";
        return PermissionManager.hasCommandPermission(player, permissionPath, false);
    }
}
