package me.testaccount666.serversystem.moderation;

import java.util.UUID;

public abstract class AbstractModeration {
    private final long _issueTime;
    private final long _expireTime;
    private final String _reason;
    private final UUID _senderUuid;
    private final UUID _targetUuid;

    public AbstractModeration(long issueTime, long expireTime, String reason, UUID senderUuid, UUID targetUuid) {
        _issueTime = issueTime;
        _expireTime = expireTime;
        _reason = reason;
        _senderUuid = senderUuid;
        _targetUuid = targetUuid;
    }

    public boolean isExpired() {
        if (_expireTime == -1) return false;

        return System.currentTimeMillis() >= _expireTime;
    }

    public long issueTime() {
        return _issueTime;
    }

    public long expireTime() {
        return _expireTime;
    }

    public String reason() {
        return _reason;
    }

    public UUID senderUuid() {
        return _senderUuid;
    }

    public UUID targetUuid() {
        return _targetUuid;
    }
}
