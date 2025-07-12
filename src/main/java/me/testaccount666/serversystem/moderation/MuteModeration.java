package me.testaccount666.serversystem.moderation;

import java.util.UUID;

public class MuteModeration extends AbstractModeration {
    private final boolean _isShadowMute;

    public MuteModeration(boolean isShadowMute, long issueTime, long expireTime, String reason, UUID senderUuid, UUID targetUuid) {
        super(issueTime, expireTime, reason, senderUuid, targetUuid);
        _isShadowMute = isShadowMute;
    }

    public boolean isShadowMute() {
        return _isShadowMute;
    }
}
