package me.testaccount666.serversystem.commands.executables.moderation;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.moderation.AbstractModeration;
import me.testaccount666.serversystem.moderation.AbstractModerationManager;
import me.testaccount666.serversystem.userdata.OfflineUser;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.DurationParser;
import org.bukkit.command.Command;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

public abstract class AbstractModerationCommand extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, command)) return;
        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).syntaxPath(getSyntaxPath(command)).label(label).build();
            return;
        }

        if (!isRemoveModeration(command) && arguments.length < 2) {
            general("InvalidArguments", commandSender).syntaxPath(getSyntaxPath(command)).label(label).build();
            return;
        }

        var targetOptional = ServerSystem.Instance.getUserManager().getUser(arguments[0]);
        if (targetOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }
        var cachedUser = targetOptional.get();
        var targetUser = cachedUser.getOfflineUser();
        if (targetUser.getName().isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        if (isRemoveModeration(command)) {
            handleRemoveModeration(command, commandSender, targetUser);
            return;
        }

        var duration = DurationParser.parseDuration(arguments[1]);
        if (duration == -2) {
            command("Moderation.InvalidDuration", commandSender).target(targetUser.getName().get()).build();
            return;
        }
        if (duration == 0) {
            command("Moderation.NotZero", commandSender).target(targetUser.getName().get()).build();
            return;
        }

        var currentTime = System.currentTimeMillis();
        var expireTime = duration == -1? -1 : currentTime + duration;

        var defaultReason = command("Moderation.DefaultReason", commandSender)
                .target(targetUser.getName().get()).prefix(false).send(false).build();
        if (defaultReason.isEmpty()) {
            ServerSystem.getLog().severe("(Command: ${command.getName()}) Default reason is empty! This should not happen!");
            general("ErrorOccurred", commandSender).label(label).build();
            return;
        }
        var reason = defaultReason.get();
        if (arguments.length > 2)
            reason = IntStream.range(2, arguments.length).mapToObj(index -> "${arguments[index]} ").collect(Collectors.joining());

        handleCreateModeration(command, commandSender, targetUser, expireTime, reason);
    }

    private void handleRemoveModeration(Command command, User commandSender, OfflineUser targetUser) {
        var moderationManager = getModerationManager(targetUser);
        var activeModeration = moderationManager.getActiveModeration();
        if (activeModeration.isEmpty()) {
            general("Moderation.${type(command)}.Remove.NoActiveModeration", commandSender).target(targetUser.getName().get()).build();
            return;
        }

        moderationManager.removeModeration(activeModeration.get());
        command("Moderation.${type(command)}.Remove.Success", commandSender).target(targetUser.getName().get()).build();
    }

    private void handleCreateModeration(Command command, User commandSender, OfflineUser targetUser, long expireTime, String reason) {
        var moderationManager = getModerationManager(targetUser);
        if (moderationManager.hasActiveModeration()) {
            general("Moderation.${type(command)}.Add.AlreadyActiveModeration", commandSender).target(targetUser.getName().get()).build();
            return;
        }

        var moderation = createModeration(command, commandSender, targetUser, expireTime, reason);
        moderationManager.addModeration(moderation);
        command("Moderation.${type(command)}.Add.Success", commandSender).target(targetUser.getName().get()).build();
        handlePostModeration(command, commandSender, targetUser, moderation);
    }

    protected abstract void handlePostModeration(Command command, User commandSender, OfflineUser targetUser, AbstractModeration moderation);

    protected abstract AbstractModeration createModeration(Command command, User commandSender, OfflineUser targetUser, long expireTime, String reason);

    protected abstract boolean checkBasePermission(User commandSender, Command command);

    protected boolean isRemoveModeration(Command command) {
        return command.getName().startsWith("un");
    }

    protected abstract AbstractModerationManager getModerationManager(OfflineUser targetUser);

    protected abstract String type(Command command);
}
