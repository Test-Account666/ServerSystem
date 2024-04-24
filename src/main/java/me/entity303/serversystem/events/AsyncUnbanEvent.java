package me.entity303.serversystem.events;

import me.entity303.serversystem.commands.util.CommandManager;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@SuppressWarnings({ "NewMethodNamingConvention", "FieldNamingConvention" })
public class AsyncUnbanEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final CommandSender _sender;
    private final OfflinePlayer _target;
    private final String _reason;

    public AsyncUnbanEvent(CommandSender sender, OfflinePlayer target) {
        this(sender, target, ServerSystem.getPlugin(ServerSystem.class)
                                         .GetMessages()
                                         .GetMessageWithStringTarget(CommandManager.UNBAN, CommandManager.UNBAN, sender, target.getName(),
                                                                     "Ban.DefaultReason"));
    }

    public AsyncUnbanEvent(CommandSender sender, OfflinePlayer target, String reason) {
        super(true);
        this._sender = sender;
        this._target = target;
        this._reason = reason;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CommandSender getSender() {
        return this._sender;
    }

    public OfflinePlayer getBannedPlayer() {
        return this._target;
    }

    public String getReason() {
        return this._reason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
