package me.testaccount666.serversystem.commands.executables.moderation.kick;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "kick")
public class CommandKick extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Moderation.Kick.Use")) return;

        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).syntaxPath(getSyntaxPath(command)).label(label).build();
            return;
        }

        var targetUserOptional = getTargetUser(commandSender, false, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();

        var defaultReason = command("Moderation.DefaultReason", commandSender)
                .target(targetUser.getName().get()).prefix(false).send(false).build();
        if (defaultReason.isEmpty()) {
            ServerSystem.getLog().severe("(CommandKick) Default reason is empty! This should not happen!");
            general("ErrorOccurred", commandSender).label(label).build();
            return;
        }
        var reason = defaultReason.get();

        if (arguments.length > 1) reason = IntStream.range(1, arguments.length)
                .mapToObj(index -> arguments[index] + " ")
                .collect(Collectors.joining()).trim();

        var finalReason = reason;
        var kickOptional = command("Moderation.Kick.Kick", commandSender).target(targetUser.getName().get()).prefix(false)
                .postModifier(message -> message.replace("<REASON>", finalReason)).send(false).build();

        if (kickOptional.isEmpty()) {
            ServerSystem.getLog().severe("(CommandBan) Kick message is empty! This should not happen!");
            general("ErrorOccurred", commandSender).build();
            return;
        }

        targetUser.getPlayer().kickPlayer(kickOptional.get());

        command("Moderation.Kick.Success", commandSender)
                .target(targetUser.getName().get()).build();
    }

    @Override
    public String getSyntaxPath(Command command) {
        return "Kick";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Moderation.Kick.Use", false);
    }
}

