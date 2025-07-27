package me.testaccount666.serversystem.events;

import lombok.Getter;
import lombok.Setter;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class UserPrivateMessageEvent extends Event implements Cancellable {
    private static final HandlerList _HANDLERS = new HandlerList();
    @Getter
    private final User _sender;
    @Getter
    private final Set<User> _recipients = new HashSet<>();
    @Getter
    private final String _message;

    @Getter
    @Setter
    private boolean _cancelled = false;

    public UserPrivateMessageEvent(User sender, String message, User... recipients) {
        _sender = sender;
        _message = message;
        _recipients.addAll(Set.of(recipients));
        _recipients.add(sender);
    }

    public static HandlerList getHandlerList() {
        return _HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return _HANDLERS;
    }

}
