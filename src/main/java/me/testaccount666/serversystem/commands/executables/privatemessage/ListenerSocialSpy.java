package me.testaccount666.serversystem.commands.executables.privatemessage;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.annotations.RequiredCommands;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.events.UserPrivateMessageEvent;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Set;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;

@RequiredCommands(requiredCommands = CommandPrivateMessage.class)
public class ListenerSocialSpy implements Listener {

    public boolean canRegister(Set<ServerSystemCommandExecutor> requiredCommands) {
        return requiredCommands.stream().anyMatch(CommandPrivateMessage.class::isInstance);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPrivateMessage(UserPrivateMessageEvent event) {
        ServerSystem.Instance.getUserManager().getCachedUsers().forEach(cachedUser -> {
            if (!cachedUser.isOnlineUser()) return;
            var user = (User) cachedUser.getOfflineUser();
            if (!user.isSocialSpyEnabled()) return;

            var target = event.getRecipients().stream().findFirst();
            if (target.isEmpty()) return;

            var senderName = event.getSender().getName().get();
            var targetName = target.get().getName().get();

            command("SocialSpy.Format", user).sender(senderName).target(targetName)
                    .postModifier(message -> message.replace("<MESSAGE>", event.getMessage())).build();
        });
    }
}
