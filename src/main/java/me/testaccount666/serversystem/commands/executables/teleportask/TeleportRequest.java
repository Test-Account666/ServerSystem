package me.testaccount666.serversystem.commands.executables.teleportask;

import me.testaccount666.serversystem.userdata.User;

public final class TeleportRequest {
    private final User _sender;
    private final User _receiver;
    private final long _timeout;
    private final boolean _teleportHere;
    private boolean _cancelled = false;
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

    public User getSender() {
        return _sender;
    }

    public User getReceiver() {
        return _receiver;
    }

    public boolean isTeleportHere() {
        return _teleportHere;
    }

    public boolean isCancelled() {
        return _cancelled;
    }

    public void setCancelled(boolean cancelled) {
        _cancelled = cancelled;
    }

    public int getTimerId() {
        return _timerId;
    }

    public void setTimerId(int timerId) {
        _timerId = timerId;
    }
}
