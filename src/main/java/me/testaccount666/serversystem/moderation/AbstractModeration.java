package me.testaccount666.serversystem.moderation;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Accessors(fluent = true)
@Getter
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
}
