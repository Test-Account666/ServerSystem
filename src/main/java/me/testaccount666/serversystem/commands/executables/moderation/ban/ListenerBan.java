package me.testaccount666.serversystem.commands.executables.moderation.ban;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.annotations.RequiredCommands;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.userdata.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.Set;

import static me.testaccount666.serversystem.commands.executables.moderation.ModerationUtils.findSenderName;
import static me.testaccount666.serversystem.utils.DurationParser.parseUnbanDate;
import static me.testaccount666.serversystem.utils.MessageBuilder.command;

@RequiredCommands(requiredCommands = CommandBan.class)
public class ListenerBan implements Listener {

    public boolean canRegister(Set<ServerSystemCommandExecutor> requiredCommands) {
        return requiredCommands.stream().anyMatch(CommandBan.class::isInstance);
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        var userOptional = ServerSystem.Instance.getUserManager().getUser(event.getUniqueId());
        if (userOptional.isEmpty()) {
            Bukkit.getLogger().severe("(ListenerBan) User not found! This should not happen!");
            return;
        }
        var cachedUser = userOptional.get();
        var user = cachedUser.getOfflineUser();
        var banManager = user.getBanManager();

        var banModerationOptional = banManager.getActiveModeration();
        if (banModerationOptional.isEmpty()) return;
        var banModeration = banModerationOptional.get();

        var senderName = banModeration.senderUuid().toString();
        var senderNameOptional = findSenderName(banModeration);
        if (senderNameOptional.isPresent()) senderName = senderNameOptional.get();

        var parsedDuration = banModeration.expireTime();

        var unbanDate = parseUnbanDate(parsedDuration);
        var kickOptional = command("Moderation.Ban.Kick", UserManager.getConsoleUser()).sender(senderName)
                .target(user.getName().get()).prefix(false).send(false)
                .postModifier(message -> message.replace("<DATE>", unbanDate)
                        .replace("<REASON>", banModeration.reason())).build();

        if (kickOptional.isEmpty()) {
            Bukkit.getLogger().severe("(CommandBan) Kick message is empty! This should not happen!");
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, "Error occurred!");
            return;
        }

        var kickMessage = kickOptional.get();
        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, kickMessage);
        Bukkit.getLogger().info("(ListenerBan) Disallowed ${event.getName()} (${event.getUniqueId()}) for ${banModeration.reason()}");
    }
}
