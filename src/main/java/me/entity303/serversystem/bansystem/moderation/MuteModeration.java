package me.entity303.serversystem.bansystem.moderation;

import java.util.UUID;

public final class MuteModeration extends Moderation {
    private final boolean _shadow;

    public MuteModeration(UUID uuid, String senderUuid, Long expireTime, String expireDate, String reason, boolean shadow) {
        super(uuid, senderUuid, expireTime, expireDate, reason);
        this._shadow = shadow;
    }

    public boolean IsShadow() {
        return this._shadow;
    }
}
