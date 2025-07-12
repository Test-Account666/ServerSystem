package me.testaccount666.serversystem.moderation;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MuteModeration extends AbstractModeration {
    private final boolean _isShadowMute;

    public MuteModeration(boolean isShadowMute, long issueTime, long expireTime, String reason, UUID senderUuid, UUID targetUuid) {
        super(issueTime, expireTime, reason, senderUuid, targetUuid);
        _isShadowMute = isShadowMute;
    }

}
