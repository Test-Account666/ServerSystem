package me.testaccount666.serversystem.moderation;

import java.util.UUID;

public class MuteModerationBuilder {
    private long _issueTime;
    private long _expireTime;
    private String _reason;
    private UUID _senderUuid;
    private UUID _targetUuid;
    private boolean _isShadowMute = false;

    public MuteModerationBuilder issueTime(long issueTime) {
        _issueTime = issueTime;
        return this;
    }

    public MuteModerationBuilder expireTime(long expireTime) {
        _expireTime = expireTime;
        return this;
    }

    public MuteModerationBuilder reason(String reason) {
        _reason = reason;
        return this;
    }

    public MuteModerationBuilder senderUuid(UUID senderUuid) {
        _senderUuid = senderUuid;
        return this;
    }

    public MuteModerationBuilder targetUuid(UUID targetUuid) {
        _targetUuid = targetUuid;
        return this;
    }

    public MuteModerationBuilder shadowMute(boolean isShadowMute) {
        _isShadowMute = isShadowMute;
        return this;
    }

    public MuteModeration build() {
        return new MuteModeration(_isShadowMute, _issueTime, _expireTime, _reason, _senderUuid, _targetUuid);
    }
}