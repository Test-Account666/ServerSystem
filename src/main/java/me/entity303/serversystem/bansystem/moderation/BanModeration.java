package me.entity303.serversystem.bansystem.moderation;

import java.util.UUID;

public final class BanModeration extends Moderation {
    public BanModeration(UUID uuid, String senderUuid, Long expireTime, String expireDate, String reason) {
        super(uuid, senderUuid, expireTime, expireDate, reason);
    }
}
