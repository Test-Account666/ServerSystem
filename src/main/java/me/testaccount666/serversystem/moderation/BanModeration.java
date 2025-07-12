package me.testaccount666.serversystem.moderation;

import java.util.UUID;

public class BanModeration extends AbstractModeration {
    public BanModeration(long issueTime, long expireTime, String reason, UUID senderUuid, UUID targetUuid) {
        super(issueTime, expireTime, reason, senderUuid, targetUuid);
    }
}
