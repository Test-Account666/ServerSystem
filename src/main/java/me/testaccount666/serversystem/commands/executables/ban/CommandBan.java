package me.testaccount666.serversystem.commands.executables.ban;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.moderation.BanModeration;
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

@ServerSystemCommand(name = "ban", variants = "unban")
public class CommandBan extends AbstractServerSystemCommand {

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        var messagePath = command.getName().equalsIgnoreCase("ban")? "Ban.Use" : "Unban.Use";

        return PermissionManager.hasCommandPermission(player, messagePath, false);
    }

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        switch (command.getName().toLowerCase()) {
            case "ban" -> handleBanCommand(commandSender, label, arguments);
            case "unban" -> handleUnbanCommand(commandSender, label, arguments);
        }
    }

    private void handleBanCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Ban.Use")) return;

        if (arguments.length < 2) {
            general("InvalidArguments", commandSender).label(label).build();
            return;
        }

        var cachedUserOptional = ServerSystem.Instance.getUserManager().getUser(arguments[0]);
        if (cachedUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }
        var cachedUser = cachedUserOptional.get();
        var user = cachedUser.getOfflineUser();

        var banManager = user.getBanManager();

        Bukkit.getLogger().info("(CommandBan) Bans: " + banManager.getModerations().size());

        if (banManager.hasActiveModeration()) {
            command("Ban.PlayerAlreadyBanned", commandSender).target(user.getName().get()).build();
            return;
        }

        var parsedDuration = parseDuration(arguments[1]);
        if (parsedDuration == -2) {
            general("Moderation.InvalidDuration", commandSender).target(user.getName().get()).build();
            return;
        }
        if (parsedDuration == 0) {
            general("Moderation.NotZero", commandSender).target(user.getName().get()).build();
            return;
        }

        var defaultReason = general("Moderation.DefaultReason", commandSender).prefix(false).send(false).build();
        if (defaultReason.isEmpty()) {
            Bukkit.getLogger().severe("(CommandBan) Default reason is empty! This should not happen!");
            general("ErrorOccurred", commandSender).label(label).build();
            return;
        }


        var reason = defaultReason.get();

        if (arguments.length > 2)
            reason = IntStream.range(2, arguments.length).mapToObj(index -> "${arguments[index]} ").collect(Collectors.joining());
        reason = reason.trim();

        var currentTime = System.currentTimeMillis();
        var expireTime = parsedDuration == -1? -1 : currentTime + parsedDuration;

        var banModeration = BanModeration.builder()
                .senderUuid(commandSender.getUuid()).targetUuid(user.getUuid())
                .reason(reason).expireTime(expireTime).issueTime(currentTime).build();

        banManager.addModeration(banModeration);

        command("Ban.Success", commandSender).target(user.getName().get()).build();

        var unbanDate = parseUnbanDate(expireTime);
        var kickOptional = command("Ban.Kick", commandSender).target(user.getName().get()).prefix(false)
                .modifier(message -> message.replace("<DATE>", unbanDate)
                        .replace("<REASON>", banModeration.reason())).send(false).build();

        if (kickOptional.isEmpty()) {
            Bukkit.getLogger().severe("(CommandBan) Kick message is empty! This should not happen!");
            general("ErrorOccurred", commandSender).label(label).build();
            return;
        }
        var kickMessage = kickOptional.get();

        if (user instanceof User) {
            var userPlayer = ((User) user).getPlayer();
            userPlayer.kickPlayer(kickMessage);
        }
    }

    private void handleUnbanCommand(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Unban.Use")) return;

        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).label(label).build();
            return;
        }

        var cachedUserOptional = ServerSystem.Instance.getUserManager().getUser(arguments[0]);
        if (cachedUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }
        var cachedUser = cachedUserOptional.get();
        var user = cachedUser.getOfflineUser();
        var banManager = user.getBanManager();

        if (!banManager.hasActiveModeration()) {
            command("Unban.PlayerNotBanned", commandSender).target(user.getName().get()).build();
            return;
        }

        var count = 0;

        for (var moderation : banManager.getModerations()) {
            if (moderation.isExpired()) continue;
            banManager.removeModeration(moderation);
            count++;
        }

        var finalCount = count;
        command("Unban.Success", commandSender).target(user.getName().get())
                .modifier(message -> message.replace("<COUNT>", String.valueOf(finalCount))).build();
    }
}
