package me.testaccount666.serversystem.moderation;

import java.util.UUID;

public class BanModerationBuilder {
    private long _issueTime;
    private long _expireTime;
    private String _reason;
    private UUID _senderUuid;
    private UUID _targetUuid;

    public BanModerationBuilder issueTime(long issueTime) {
        _issueTime = issueTime;
        return this;
    }

    public BanModerationBuilder expireTime(long expireTime) {
        _expireTime = expireTime;
        return this;
    }

    public BanModerationBuilder reason(String reason) {
        _reason = reason;
        return this;
    }

    public BanModerationBuilder senderUuid(UUID senderUuid) {
        _senderUuid = senderUuid;
        return this;
    }

    public BanModerationBuilder targetUuid(UUID targetUuid) {
        _targetUuid = targetUuid;
        return this;
    }

    public BanModeration build() {
        return new BanModeration(_issueTime, _expireTime, _reason, _senderUuid, _targetUuid);
    }
}