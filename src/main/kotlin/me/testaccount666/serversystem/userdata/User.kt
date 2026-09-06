package me.testaccount666.serversystem.userdata

import me.testaccount666.serversystem.userdata.teleport.TeleportRequest
import me.testaccount666.serversystem.userdata.teleport.TeleportRunnable
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
    protected val messageListeners = mutableSetOf<CachedUser>()
    protected open var onlinePlayer: Player? = null

    var teleportRequest: TeleportRequest? = null
    var teleportRunnable: TeleportRunnable? = null
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
    open fun getPlayer() = (onlinePlayer ?: super.player as? Player).also { onlinePlayer = it }

    /**
     * Gets the name of this user.
     *
     * @return The name of this user, or null if the name is not available
     */
    override fun getNameOrNull() = getPlayer()?.name ?: name

    open val commandSender: CommandSender
        get() = getPlayer()!!

    /**
     * Uses User#getCommandSender() to send a message.
     * Used as a shortcut.
     *
     * @param message The message to be sent
     */
    fun sendMessage(message: String) = sendMessage(message as Any)

    /**
     * Uses User#getCommandSender() to send a component message.
     * Used as a shortcut for sending formatted messages using the Component API.
     *
     * @param component The component message to be sent
     */
    fun sendMessage(component: Component) = sendMessage(component as Any)

    private fun sendMessage(obj: Any) {
        if (obj is String) commandSender.sendMessage(obj)
        if (obj is Component) commandSender.sendMessage(obj)

        messageListeners.toSet().forEach {
            if (it.isOfflineUser) {
                messageListeners.remove(it)
                return@forEach
            }

            it.onlineUser.sendMessage(obj)
        }
    }

    fun addMessageListener(cachedUser: CachedUser) = messageListeners.add(cachedUser)

    fun removeMessageListener(cachedUser: CachedUser) = messageListeners.remove(cachedUser)
}