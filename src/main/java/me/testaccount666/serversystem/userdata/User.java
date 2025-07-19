package me.testaccount666.serversystem.userdata;

import lombok.Getter;
import lombok.Setter;
import me.testaccount666.serversystem.commands.executables.teleportask.TeleportRequest;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents an online user.
 * This class extends OfflineUser and provides additional functionality
 * for interacting with online players.
 */
public class User extends OfflineUser {
    protected final Set<CachedUser> messageListeners = new HashSet<>();
    protected Player player;
    @Setter
    @Getter
    protected TeleportRequest teleportRequest;
    @Setter
    @Getter
    protected User replyUser;
    @Getter
    protected boolean isAfk = false;
    @Getter
    protected long afkSince;

    protected User(File userFile) {
        super(userFile);
    }

    protected User(OfflineUser offlineUser) {
        this(offlineUser.userFile);
    }

    @Override
    protected void loadBasicData() {
        super.loadBasicData();

        // Update online-specific fields
        name = getPlayer().getName();
        lastSeen = System.currentTimeMillis();
        lastKnownIp = getPlayer().getAddress().getAddress().getHostAddress();

        save();
    }

    /**
     * Gets the Player object associated with this user.
     *
     * @return The Player object for this user
     */
    @Override
    public Player getPlayer() {
        if (player == null) player = (Player) super.getPlayer();

        return player;
    }

    /**
     * Gets the name of this user.
     *
     * @return An Optional containing the name of this user
     */
    @Override
    public Optional<String> getName() {
        return Optional.of(getPlayer().getName());
    }

    /**
     * Gets the CommandSender object associated with this user.
     * This can be used to interact with the user without
     * using User#getPlayer()
     *
     * @return The CommandSender object for this user
     */
    public CommandSender getCommandSender() {
        return getPlayer();
    }

    /**
     * Uses User#getCommandSender() to send a message.
     * Used as a shortcut.
     *
     * @param message The message to be sent
     */
    public void sendMessage(String message) {
        getCommandSender().sendMessage(message);

        for (var listener : Collections.unmodifiableSet(messageListeners)) {
            if (listener.isOfflineUser()) {
                messageListeners.remove(listener);
                continue;
            }

            var user = (User) listener.getOfflineUser();
            user.sendMessage(message);
        }
    }
    
    /**
     * Uses User#getCommandSender() to send a component message.
     * Used as a shortcut for sending formatted messages using the Component API.
     *
     * @param component The component message to be sent
     */
    public void sendMessage(Component component) {
        getCommandSender().sendMessage(component);

        for (var listener : Collections.unmodifiableSet(messageListeners)) {
            if (listener.isOfflineUser()) {
                messageListeners.remove(listener);
                continue;
            }

            var user = (User) listener.getOfflineUser();
            user.sendMessage(component);
        }
    }

    public void addMessageListener(CachedUser cachedUser) {
        messageListeners.add(cachedUser);
    }

    public void removeMessageListener(CachedUser cachedUser) {
        messageListeners.remove(cachedUser);
    }

    public void setAfk(boolean afk) {
        // Don't update afkSince if the user is already afk
        if (isAfk == afk) return;

        isAfk = afk;
        afkSince = afk? System.currentTimeMillis() : 0;
    }

}
