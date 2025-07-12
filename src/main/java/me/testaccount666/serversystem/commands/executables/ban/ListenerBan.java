package me.testaccount666.serversystem.commands.executables.ban;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.annotations.RequiredCommands;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.moderation.AbstractModeration;
import me.testaccount666.serversystem.userdata.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.Optional;
import java.util.Set;

import static me.testaccount666.serversystem.utils.DurationParser.parseUnbanDate;
import static me.testaccount666.serversystem.utils.MessageBuilder.command;

@RequiredCommands(requiredCommands = CommandBan.class)
public class ListenerBan implements Listener {

    public boolean canRegister(Set<ServerSystemCommandExecutor> requiredCommands) {

        Bukkit.getLogger().info("(ListenerBan) Checking required commands for ban listener...");

        var canRegister = requiredCommands.stream().anyMatch(CommandBan.class::isInstance);

        Bukkit.getLogger().info("(ListenerBan) Required commands for ban listener: " + (canRegister? "found" : "not found"));

        return canRegister;
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        Bukkit.getLogger().info("(ListenerBan) Handling login for " + event.getName() + " (" + event.getUniqueId() + ")");

        var userOptional = ServerSystem.Instance.getUserManager().getUser(event.getUniqueId());
        if (userOptional.isEmpty()) {
            Bukkit.getLogger().severe("(ListenerBan) User not found! This should not happen!");
            return;
        }
        var cachedUser = userOptional.get();
        var user = cachedUser.getOfflineUser();
        var banManager = user.getBanManager();

        var banModerationOptional = banManager.getActiveModeration();
        if (banModerationOptional.isEmpty()) {
            Bukkit.getLogger().info("(ListenerBan) No active ban found for " + user.getName().get());
            return;
        }
        var banModeration = banModerationOptional.get();

        var senderName = banModeration.senderUuid().toString();
        var senderNameOptional = findSenderName(banModeration);
        if (senderNameOptional.isPresent()) senderName = senderNameOptional.get();

        var parsedDuration = banModeration.expireTime();

        var unbanDate = parseUnbanDate(parsedDuration);
        var kickOptional = command("Ban.Kick", UserManager.getConsoleUser()).sender(senderName)
                .target(user.getName().get()).prefix(false).send(false)
                .modifier(message -> message.replace("<DATE>", unbanDate)
                        .replace("<REASON>", banModeration.reason())).build();

        if (kickOptional.isEmpty()) {
            Bukkit.getLogger().severe("(CommandBan) Kick message is empty! This should not happen!");
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, "Error occurred!");
            return;
        }

        var kickMessage = kickOptional.get();
        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, kickMessage);
        Bukkit.getLogger().info("(ListenerBan) Disallowed " + event.getName() + " (" + event.getUniqueId() + ") for " + banModeration.reason());
    }

    private Optional<String> findSenderName(AbstractModeration banModeration) {
        var senderOptional = ServerSystem.Instance.getUserManager().getUser(banModeration.senderUuid());
        if (senderOptional.isEmpty()) return Optional.empty();
        var sender = senderOptional.get();
        var senderNameOptional = sender.getOfflineUser().getName();
        if (senderNameOptional.isEmpty()) return Optional.empty();

        var senderName = senderNameOptional.get();
        return Optional.of(senderName);
    }
}
