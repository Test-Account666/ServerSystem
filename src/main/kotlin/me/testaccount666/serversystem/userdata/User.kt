package me.testaccount666.serversystem.userdata

import me.testaccount666.serversystem.commands.executables.teleportask.TeleportRequest
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File

/**
 * Represents an online user.
 * This class extends OfflineUser and provides additional functionality
 * for interacting with online players.
 */
open class User(userFile: File) : OfflineUser(userFile) {
    protected val messageListeners = HashSet<CachedUser>()
    protected open var onlinePlayer: Player? = null

    var teleportRequest: TeleportRequest? = null

    var replyUser: User? = null

    var isAfk = false
        set(value) {
            // Don't update afkSince if the user is already afk
            if (field == value) return

            field = value
            afkSince = if (value) System.currentTimeMillis() else 0
        }

    protected var afkSince = 0L

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
     * @return The name of this user, or null if the name is not available
     */
    override fun getNameOrNull() = getPlayer()?.name ?: name

    open val commandSender: CommandSender?
        get() = getPlayer()

    /**
     * Uses User#getCommandSender() to send a message.
     * Used as a shortcut.
     *
     * @param message The message to be sent
     */
    fun sendMessage(message: String) {
        commandSender?.sendMessage(message)

        for (listener in messageListeners.toSet()) {
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
        commandSender?.sendMessage(component)

        for (listener in messageListeners.toSet()) {
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