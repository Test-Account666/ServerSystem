package me.testaccount666.serversystem.commands.executables.moderation;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.moderation.AbstractModeration;

import java.util.Optional;

public class ModerationUtils {
    public static Optional<String> findSenderName(AbstractModeration banModeration) {
        var senderOptional = ServerSystem.Instance.getUserManager().getUser(banModeration.senderUuid());
        if (senderOptional.isEmpty()) return Optional.empty();
        var sender = senderOptional.get();
        var senderNameOptional = sender.getOfflineUser().getName();
        if (senderNameOptional.isEmpty()) return Optional.empty();

        var senderName = senderNameOptional.get();
        return Optional.of(senderName);
    }
}
