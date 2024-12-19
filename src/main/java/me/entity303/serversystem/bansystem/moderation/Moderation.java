package me.entity303.serversystem.bansystem.moderation;

import java.util.UUID;

public class Moderation {
    private final UUID _uuid;
    private final String _senderUuid;
    private final Long _expireTime;
    private final String _expireDate;
    private final String _reason;

    public Moderation(UUID uuid, String senderUuid, Long expireTime, String expireDate, String reason) {
        this._uuid = uuid;
        this._senderUuid = senderUuid;
        this._expireTime = expireTime;
        this._expireDate = expireDate;
        this._reason = reason;
    }

    public UUID GetUuid() {
        return this._uuid;
    }

    public String GetSenderUuid() {
        return this._senderUuid;
    }

    public Long GetExpireTime() {
        return this._expireTime;
    }

    public String GetExpireDate() {
        return this._expireDate;
    }

    public String GetReason() {
        return this._reason;
    }
}
