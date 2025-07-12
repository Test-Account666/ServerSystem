package me.testaccount666.serversystem.events;

import me.testaccount666.serversystem.userdata.User;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class UserPrivateMessageEvent extends Event implements Cancellable {
    private static final HandlerList _HANDLERS = new HandlerList();
    private final User _sender;
    private final Set<User> _recipients = new HashSet<>();
    private boolean _cancelled = false;

    public UserPrivateMessageEvent(User sender, User... recipients) {
        _sender = sender;
        _recipients.addAll(Set.of(recipients));
        _recipients.add(sender);
    }

    public static HandlerList getHandlerList() {
        return _HANDLERS;
    }

    public Set<User> getRecipients() {
        return _recipients;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return _HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return _cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        _cancelled = cancelled;
    }

    public User getSender() {
        return _sender;
    }
}
