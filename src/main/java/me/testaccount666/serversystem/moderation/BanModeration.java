package me.testaccount666.serversystem.moderation;

import lombok.experimental.SuperBuilder;

import java.util.UUID;

@SuperBuilder
public class BanModeration extends AbstractModeration {
    public BanModeration(long issueTime, long expireTime, String reason, UUID senderUuid, UUID targetUuid) {
        super(issueTime, expireTime, reason, senderUuid, targetUuid);
    }
}
