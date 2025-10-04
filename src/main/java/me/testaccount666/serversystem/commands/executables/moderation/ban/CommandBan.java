package me.testaccount666.serversystem.commands.executables.moderation.ban;

import io.papermc.paper.ban.BanListType;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.moderation.AbstractModerationCommand;
import me.testaccount666.serversystem.commands.executables.moderation.TabCompleterModeration;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.moderation.AbstractModeration;
import me.testaccount666.serversystem.moderation.AbstractModerationManager;
import me.testaccount666.serversystem.moderation.BanModeration;
import me.testaccount666.serversystem.userdata.OfflineUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.time.Instant;

import static me.testaccount666.serversystem.utils.DurationParser.parseDate;
import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "ban", variants = "unban", tabCompleter = TabCompleterModeration.class)
public class CommandBan extends AbstractModerationCommand {
    @Override
    protected void handlePostModeration(Command command, User commandSender, OfflineUser targetUser, AbstractModeration moderation) {
        if (!(targetUser instanceof User user)) return;

        var player = user.getPlayer();
        var unbanDate = parseDate(moderation.expireTime(), targetUser);
        var kickOptional = command("Moderation.Ban.Kick", user)
                .sender(commandSender.getName().get()).send(false)
                .target(user.getName().get()).prefix(false)
                .postModifier(message -> message.replace("<DATE>", unbanDate)
                        .replace("<REASON>", moderation.reason())).build();

        if (kickOptional.isEmpty()) {
            ServerSystem.getLog().severe("(CommandBan) Kick message is empty! This should not happen!");
            general("ErrorOccurred", commandSender).build();
            return;
        }

        var expireTime = moderation.expireTime();
        if (expireTime == 0) expireTime = Instant.now().toEpochMilli() + 1;
        var unbanTime = expireTime > 0? Instant.ofEpochMilli(expireTime) : null;

        player.ban(kickOptional.get(), unbanTime, moderation.senderUuid().toString(), true);
    }

    @Override
    protected void handlePostRemoveModeration(Command command, User commandSender, OfflineUser targetUser) {
        Bukkit.getServer().getBanList(BanListType.PROFILE).pardon(targetUser.getPlayer().getPlayerProfile());
    }

    @Override
    protected AbstractModeration createModeration(Command command, User commandSender, OfflineUser targetUser, long expireTime, String reason) {
        return BanModeration.builder()
                .senderUuid(commandSender.getUuid()).targetUuid(targetUser.getUuid())
                .reason(reason).expireTime(expireTime).build();
    }

    @Override
    protected boolean checkBasePermission(User commandSender, Command command) {
        var permissionPath = switch (command.getName().toLowerCase()) {
            case "ban" -> "Moderation.Ban.Use";
            case "unban" -> "Moderation.Ban.Remove";
            default -> null;
        };
        return checkBasePermission(commandSender, permissionPath);
    }

    @Override
    protected AbstractModerationManager getModerationManager(OfflineUser targetUser) {
        return targetUser.getBanManager();
    }

    @Override
    protected String type(Command command) {
        return "Ban";
    }

    @Override
    public String getSyntaxPath(Command command) {
        var commandName = command.getName().toLowerCase();
        return switch (commandName) {
            case "ban" -> "Ban";
            case "unban" -> "Unban";
            default -> throw new IllegalArgumentException("(CommandBan) Unknown command name: ${commandName}");
        };
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        var permissionPath = switch (command.getName().toLowerCase()) {
            case "ban" -> "Moderation.Ban.Use";
            case "unban" -> "Moderation.Ban.Remove";
            default -> null;
        };
        return PermissionManager.hasCommandPermission(player, permissionPath, false);
    }
}
