package me.testaccount666.serversystem.commands.executables.moderation.mute;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.annotations.RequiredCommands;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.events.UserPrivateMessageEvent;
import me.testaccount666.serversystem.moderation.MuteModeration;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;

import static me.testaccount666.serversystem.commands.executables.moderation.ModerationUtils.findSenderName;
import static me.testaccount666.serversystem.utils.DurationParser.parseDate;
import static me.testaccount666.serversystem.utils.MessageBuilder.command;

@RequiredCommands(requiredCommands = CommandMute.class)
public class ListenerMute implements Listener {

    public boolean canRegister(Set<ServerSystemCommandExecutor> requiredCommands) {
        return requiredCommands.stream().anyMatch(CommandMute.class::isInstance);
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        var userOptional = ServerSystem.Instance.getUserManager().getUser(event.getPlayer());
        if (userOptional.isEmpty()) return;
        var cachedUser = userOptional.get();
        if (cachedUser.isOfflineUser()) return;
        var user = (User) cachedUser.getOfflineUser();
        var muteManager = user.getMuteManager();

        var muteModerationOptional = muteManager.getActiveModeration();
        if (muteModerationOptional.isEmpty()) return;
        var muteModeration = (MuteModeration) muteModerationOptional.get();

        handleMute(event, user, muteModeration);
        handleShadowMute(event, muteModeration);
    }

    @EventHandler
    public void onPrivateMessage(UserPrivateMessageEvent event) {
        var user = event.getSender();
        if (user instanceof ConsoleUser) return;
        var muteManager = user.getMuteManager();

        var muteModerationOptional = muteManager.getActiveModeration();
        if (muteModerationOptional.isEmpty()) return;
        var muteModeration = (MuteModeration) muteModerationOptional.get();

        handleMute(event, user, muteModeration);
        handleShadowMute(event, muteModeration);
    }

    private void handleMute(Cancellable event, User user, MuteModeration muteModeration) {
        if (muteModeration.isShadowMute()) return;
        event.setCancelled(true);

        var senderName = muteModeration.senderUuid().toString();
        var senderNameOptional = findSenderName(muteModeration);
        if (senderNameOptional.isPresent()) senderName = senderNameOptional.get();

        command("Mute.Muted", user).sender(senderName).target(user.getName().get())
                .postModifier(message -> message.replace("<REASON>", muteModeration.reason())
                        .replace("<DATE>", parseDate(muteModeration.expireTime(), user))).build();
    }

    private void handleShadowMute(AsyncChatEvent event, MuteModeration muteModeration) {
        if (!muteModeration.isShadowMute()) return;

        event.viewers().removeIf(audience -> {
            if (!(audience instanceof Player player)) return false;
            return player != event.getPlayer();
        });
    }

    private void handleShadowMute(UserPrivateMessageEvent event, MuteModeration muteModeration) {
        if (!muteModeration.isShadowMute()) return;

        event.getRecipients().removeIf(user -> {
            if (user instanceof ConsoleUser) return false;
            return user != event.getSender();
        });
    }
}
