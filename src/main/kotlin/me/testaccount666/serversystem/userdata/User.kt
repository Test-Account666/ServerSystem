package me.testaccount666.serversystem.userdata

import me.testaccount666.serversystem.commands.executables.teleportask.TeleportRequest
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File
import java.util.*

/**
 * Represents an online user.
 * This class extends OfflineUser and provides additional functionality
 * for interacting with online players.
 */
open class User(userFile: File) : OfflineUser(userFile) {
    val messageListeners: MutableSet<CachedUser> = HashSet()
    open var onlinePlayer: Player? = null

    var teleportRequest: TeleportRequest? = null

    var replyUser: User? = null

    var isAfk: Boolean = false
        set(value) {
            // Don't update afkSince if the user is already afk
            if (field == value) return

            field = value
            afkSince = if (value) System.currentTimeMillis() else 0
        }

    protected var afkSince: Long = 0

    internal constructor(offlineUser: OfflineUser) : this(offlineUser.userFile)

    override fun loadBasicData() {
        super.loadBasicData()

        // Update online-specific fields
        name = getPlayer()?.name
        lastSeen = System.currentTimeMillis()
        lastKnownIp = getPlayer()?.address?.address?.hostAddress

        save()
    }

    /**
     * Gets the Player object associated with this user.
     *
     * @return The Player object for this user
     */
    fun getPlayer(): Player? {
        if (onlinePlayer == null) onlinePlayer = super.player as Player?

        return onlinePlayer
    }

    /**
     * Gets the name of this user.
     *
     * @return An Optional containing the name of this user
     */
    override fun getName(): Optional<String> = Optional.ofNullable(getPlayer()?.name)

    open val commandSender: CommandSender?
        /**
         * Gets the CommandSender object associated with this user.
         * This can be used to interact with the user without
         * using User#getPlayer()
         *
         * @return The CommandSender object for this user
         */
        get() = getPlayer()

    /**
     * Uses User#getCommandSender() to send a message.
     * Used as a shortcut.
     *
     * @param message The message to be sent
     */
    fun sendMessage(message: String) {
        this.commandSender?.sendMessage(message)

        for (listener in Collections.unmodifiableSet<CachedUser>(messageListeners)) {
            if (listener.isOfflineUser) {
                messageListeners.remove(listener)
                continue
            }

            val user = listener.offlineUser as User
            user.sendMessage(message)
        }
    }

    /**
     * Uses User#getCommandSender() to send a component message.
     * Used as a shortcut for sending formatted messages using the Component API.
     *
     * @param component The component message to be sent
     */
    fun sendMessage(component: Component) {
        this.commandSender?.sendMessage(component)

        for (listener in Collections.unmodifiableSet<CachedUser>(messageListeners)) {
            if (listener.isOfflineUser) {
                messageListeners.remove(listener)
                continue
            }

            val user = listener.offlineUser as User
            user.sendMessage(component)
        }
    }

    fun addMessageListener(cachedUser: CachedUser) = messageListeners.add(cachedUser)

    fun removeMessageListener(cachedUser: CachedUser) = messageListeners.remove(cachedUser)
}