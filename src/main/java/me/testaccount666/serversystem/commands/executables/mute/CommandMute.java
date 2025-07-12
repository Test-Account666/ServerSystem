package me.testaccount666.serversystem.commands.executables.mute;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.moderation.MuteModeration;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static me.testaccount666.serversystem.utils.DurationParser.parseDuration;
import static me.testaccount666.serversystem.utils.DurationParser.parseUnbanDate;
import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "mute", variants = {"unmute", "shadowmute"})
public class CommandMute extends AbstractServerSystemCommand {

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        var permissionPath = switch (command.getName().toLowerCase()) {
            case "mute" -> "Mute.Use";
            case "unmute" -> "Unmute.Use";
            case "shadowmute" -> "ShadowMute.Use";
            default -> null;
        };
        return PermissionManager.hasCommandPermission(player, permissionPath, false);
    }

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        switch (command.getName().toLowerCase()) {
            case "mute" -> handleMuteCommand(commandSender, label, arguments);
            case "unmute" -> handleUnmuteCommand(commandSender, label, arguments);
            case "shadowmute" -> handleShadowMuteCommand(commandSender, label, arguments);
        }
    }

    private void handleMuteCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Mute.Use")) return;
        if (handleConsoleWithNoTarget(commandSender, 1, arguments)) return;

        if (arguments.length < 2) {
            general("InvalidArguments", commandSender).label(label).build();
            return;
        }

        var targetUserOptional = getTargetUser(commandSender, false, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }
        var targetUser = targetUserOptional.get();
        var muteManager = targetUser.getMuteManager();

        if (muteManager.hasActiveModeration()) {
            command("Mute.PlayerAlreadyMuted", commandSender).target(targetUser.getName().get()).build();
            return;
        }

        var parsedDuration = parseDuration(arguments[1]);
        if (parsedDuration == -2) {
            general("Moderation.InvalidDuration", commandSender).target(targetUser.getName().get()).build();
            return;
        }
        if (parsedDuration == 0) {
            general("Moderation.NotZero", commandSender).target(targetUser.getName().get()).build();
            return;
        }

        var defaultReason = general("Moderation.DefaultReason", commandSender).prefix(false).send(false).build();
        if (defaultReason.isEmpty()) {
            Bukkit.getLogger().severe("(CommandMute) Default reason is empty! This should not happen!");
            general("ErrorOccurred", commandSender).label(label).build();
            return;
        }

        var reason = defaultReason.get();

        if (arguments.length > 2)
            reason = IntStream.range(2, arguments.length).mapToObj(index -> "${arguments[index]} ").collect(Collectors.joining());
        reason = reason.trim();

        var currentTime = System.currentTimeMillis();
        var expireTime = parsedDuration == -1? -1 : currentTime + parsedDuration;

        var muteModeration = MuteModeration.builder()
                .senderUuid(commandSender.getUuid()).targetUuid(targetUser.getUuid())
                .reason(reason).expireTime(expireTime).issueTime(currentTime)
                .isShadowMute(false).build();

        muteManager.addModeration(muteModeration);

        var unmuteDate = parseUnbanDate(expireTime);
        command("Mute.Success", commandSender).target(targetUser.getName().get())
                .modifier(message -> message.replace("<DATE>", unmuteDate)).build();
    }

    private void handleUnmuteCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Unmute.Use")) return;
        if (handleConsoleWithNoTarget(commandSender, arguments)) return;

        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).label(label).build();
            return;
        }

        var targetUserOptional = getTargetUser(commandSender, false, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }
        var targetUser = targetUserOptional.get();
        var muteManager = targetUser.getMuteManager();

        if (!muteManager.hasActiveModeration()) {
            command("Unmute.PlayerNotMuted", commandSender).target(targetUser.getName().get()).build();
            return;
        }

        var count = 0;

        for (var moderation : muteManager.getModerations()) {
            if (moderation.isExpired()) continue;
            muteManager.removeModeration(moderation);
            count++;
        }

        var finalCount = count;
        command("Unmute.Success", commandSender).target(targetUser.getName().get())
                .modifier(message -> message.replace("<COUNT>", String.valueOf(finalCount))).build();
    }

    private void handleShadowMuteCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "ShadowMute.Use")) return;
        if (handleConsoleWithNoTarget(commandSender, 1, arguments)) return;

        if (arguments.length < 2) {
            general("InvalidArguments", commandSender).label(label).build();
            return;
        }

        var targetUserOptional = getTargetUser(commandSender, false, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }
        var targetUser = targetUserOptional.get();
        var muteManager = targetUser.getMuteManager();

        if (muteManager.hasActiveModeration()) {
            command("Mute.PlayerAlreadyMuted", commandSender).target(targetUser.getName().get()).build();
            return;
        }

        var parsedDuration = parseDuration(arguments[1]);
        if (parsedDuration == -2) {
            general("Moderation.InvalidDuration", commandSender).target(targetUser.getName().get()).build();
            return;
        }
        if (parsedDuration == 0) {
            general("Moderation.NotZero", commandSender).target(targetUser.getName().get()).build();
            return;
        }

        var defaultReason = general("Moderation.DefaultReason", commandSender).prefix(false).send(false).build();
        if (defaultReason.isEmpty()) {
            Bukkit.getLogger().severe("(CommandShadowMute) Default reason is empty! This should not happen!");
            general("ErrorOccurred", commandSender).label(label).build();
            return;
        }

        var reason = defaultReason.get();

        if (arguments.length > 2)
            reason = IntStream.range(2, arguments.length).mapToObj(index -> "${arguments[index]} ").collect(Collectors.joining());
        reason = reason.trim();

        var currentTime = System.currentTimeMillis();
        var expireTime = parsedDuration == -1? -1 : currentTime + parsedDuration;

        var muteModeration = MuteModeration.builder()
                .senderUuid(commandSender.getUuid()).targetUuid(targetUser.getUuid())
                .reason(reason).expireTime(expireTime).issueTime(currentTime)
                .isShadowMute(true).build();

        muteManager.addModeration(muteModeration);

        var unmuteDate = parseUnbanDate(expireTime);
        command("ShadowMute.Success", commandSender).target(targetUser.getName().get())
                .modifier(message -> message.replace("<DATE>", unmuteDate)).build();
    }
}
