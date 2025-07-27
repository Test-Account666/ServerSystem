package me.testaccount666.serversystem.commands.executables.moderation.mute;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.moderation.AbstractModerationCommand;
import me.testaccount666.serversystem.commands.executables.moderation.TabCompleterModeration;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.moderation.AbstractModeration;
import me.testaccount666.serversystem.moderation.AbstractModerationManager;
import me.testaccount666.serversystem.moderation.MuteModeration;
import me.testaccount666.serversystem.userdata.OfflineUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

@ServerSystemCommand(name = "mute", variants = {"unmute", "shadowmute"}, tabCompleter = TabCompleterModeration.class)
public class CommandMute extends AbstractModerationCommand {

    @Override
    protected AbstractModeration createModeration(Command command, User commandSender, OfflineUser targetUser, long expireTime, String reason) {
        var shadowMute = command.getName().equalsIgnoreCase("shadowmute");

        return MuteModeration.builder()
                .isShadowMute(shadowMute).expireTime(expireTime)
                .reason(reason).senderUuid(commandSender.getUuid())
                .targetUuid(targetUser.getUuid()).build();
    }

    @Override
    protected boolean checkBasePermission(User commandSender, Command command) {
        var permissionPath = switch (command.getName().toLowerCase()) {
            case "mute" -> "Moderation.Mute.Use";
            case "shadowmute" -> "Moderation.Mute.Shadow";
            case "unmute" -> "Moderation.Mute.Remove";
            default -> null;
        };
        return checkBasePermission(commandSender, permissionPath);
    }

    @Override
    protected AbstractModerationManager getModerationManager(OfflineUser targetUser) {
        return targetUser.getMuteManager();
    }

    @Override
    protected String type(Command command) {
        if (command.getName().equalsIgnoreCase("shadowmute")) return "ShadowMute";
        return "Mute";
    }

    @Override
    public String getSyntaxPath(Command command) {
        var commandName = command.getName().toLowerCase();
        return switch (commandName) {
            case "mute", "shadowmute" -> "Mute";
            case "unmute" -> "Unmute";
            default -> throw new IllegalArgumentException("(CommandMute) Unknown command name: ${commandName}");
        };
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        var permissionPath = switch (command.getName().toLowerCase()) {
            case "mute" -> "Moderation.Mute.Use";
            case "shadowmute" -> "Moderation.Mute.Shadow";
            case "unmute" -> "Moderation.Mute.Remove";
            default -> null;
        };
        return PermissionManager.hasCommandPermission(player, permissionPath, false);
    }

    @Override
    protected void handlePostModeration(Command command, User commandSender, OfflineUser targetUser, AbstractModeration moderation) {
        // Nothing to do
    }
}
