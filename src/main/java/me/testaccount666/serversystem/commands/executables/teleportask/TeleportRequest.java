package me.testaccount666.serversystem.commands.executables.teleportask;

import lombok.Getter;
import lombok.Setter;
import me.testaccount666.serversystem.userdata.User;

public final class TeleportRequest {
    @Getter
    private final User _sender;
    @Getter
    private final User _receiver;
    private final long _timeout;
    @Getter
    private final boolean _teleportHere;
    @Setter
    @Getter
    private boolean _cancelled = false;
    @Setter
    @Getter
    private int _timerId;

    public TeleportRequest(User sender, User receiver, long timeout, boolean teleportHere) {
        _sender = sender;
        _receiver = receiver;
        _timeout = timeout;
        _teleportHere = teleportHere;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= _timeout;
    }

}
