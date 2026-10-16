package me.testaccount666.serversystem.userdata

import me.testaccount666.paperktx.extensions.TimeExtensions.toTicks
import me.testaccount666.paperktx.scheduler.skedule.okkero.schedule
import me.testaccount666.serversystem.ServerSystem
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.nio.file.Path
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.minutes

/**
 * Manages ServerSystem's user data.
 * This class is responsible for creating, caching, and retrieving user objects,
 * as well as cleaning up stale user data to prevent memory leaks.
 */
class UserManager {
    private val _userMap = ConcurrentHashMap<String, CachedUser>()
    private val _userUuidMap = ConcurrentHashMap<UUID, CachedUser>()

    init {
        ServerSystem.instance.schedule {
            loop(15.minutes.toTicks(), 15.minutes.toTicks()) { cleanStaleUsers() }
        }

        Bukkit.getOnlinePlayers().forEach(::getUserOrNull)
    }

    /**
     * Gets a cached user by player instance.
     *
     * @param player The player to get the user for
     * @return An Optional containing the cached user or empty if the user doesn't exist
     */
    fun getUserOrNull(player: Player): CachedUser? {
        if (player.hasMetadata("NPC")) return CachedUser(_NPC_USER)

        return getUserOrNull(player.uniqueId, true)
    }

    /**
     * Gets a cached user by UUID.
     * If the user is already cached, returns the cached instance.
     * If the user is online but not cached, creates and caches a new online user.
     * If the user is offline, creates and caches a new offline user.
     *
     * @param uuid The UUID of the user to get
     * @return The cached user or null if the user doesn't exist
     */
    fun getUserOrNull(uuid: UUID) = getUserOrNull(uuid, false)

    /**
     * Gets a cached user by UUID.
     * If the user is already cached, returns the cached instance.
     * If the user is online but not cached, creates and caches a new online user.
     * If the user is offline, creates and caches a new offline user.
     *
     * @param uuid            The UUID of the user to get
     * @param forceOnlineUser If the method should only return online users
     * @return The cached user or null if the user doesn't exist
     */
    fun getUserOrNull(uuid: UUID, forceOnlineUser: Boolean): CachedUser? {
        if (!forceOnlineUser && uuid == ConsoleUser.CONSOLE_UUID) return CachedUser(consoleUser)

        _userUuidMap[uuid]?.let {
            if (it.isOfflineUser && forceOnlineUser) return null
            it.updateLastAccessTime()
            return it
        }

        if (Bukkit.getPlayer(uuid) != null) return createOnlineUser(uuid)
        return if (!forceOnlineUser) createOfflineUserOrNull(uuid) else null
    }

    /**
     * Gets a cached user by name.
     * If the user is already cached, returns the cached instance.
     * If the user is online but not cached, creates and caches a new online user.
     * If the user is offline, creates and caches a new offline user.
     *
     * @param name The name of the user to get
     * @return The cached user or null if the user doesn't exist
     */
    fun getUserOrNull(name: String) = getUserOrNull(name, false)

    /**
     * Gets a cached user by name.
     * If the user is already cached, returns the cached instance.
     * If the user is online but not cached, creates and caches a new online user.
     * If the user is offline, creates and caches a new offline user.
     *
     * @param name            The name of the user to get
     * @param forceOnlineUser If the method should only return online users
     * @return The cached user or null if the user doesn't exist
     */
    fun getUserOrNull(name: String, forceOnlineUser: Boolean): CachedUser? {
        _userMap[name]?.let {
            if (it.isOfflineUser && forceOnlineUser) return null
            it.updateLastAccessTime()
            return it
        }

        Bukkit.getPlayer(name)?.let { return createOnlineUser(it.uniqueId) }
        if (forceOnlineUser) return null

        val offlinePlayer = Bukkit.getOfflinePlayer(name).takeIf { it.name != null } ?: return null
        return createOfflineUserOrNull(offlinePlayer.uniqueId)
    }

    /**
     * Creates and caches an offline user for the given UUID.
     *
     * @param uuid The UUID of the user to create
     * @return The cached user or null if the user doesn't exist
     */
    private fun createOfflineUserOrNull(uuid: UUID): CachedUser? {
        val user = OfflineUser(getUserFile(uuid))
        val name = user.getNameOrNull() ?: return null

        return CachedUser(user).also {
            _userUuidMap[uuid] = it
            _userMap[name] = it
        }
    }

    /**
     * Creates and caches an online user for the given UUID.
     *
     * @param uuid The UUID of the user to create
     * @return The cached user
     */
    private fun createOnlineUser(uuid: UUID): CachedUser {
        val user = User(getUserFile(uuid))

        return CachedUser(user).also {
            _userUuidMap[uuid] = it
            _userMap[user.getNameOrNull()!!] = it
        }
    }

    val cachedUsers
        get() = _userMap.values.toSet()

    /**
     * Removes stale users from the cache to prevent memory leaks.
     * This method is called periodically by a scheduled task.
     */
    fun cleanStaleUsers() {
        _userMap.values.removeIf { it.isStale }
        _userUuidMap.values.removeIf { it.isStale }
    }

    companion object {
        @JvmField
        val USER_DATA_PATH: Path = ServerSystem.instance.dataFolder.toPath().resolve("UserData")

        /**
         * Gets the console user instance.
         *
         * @return The console user instance
         */
        val consoleUser = ConsoleUser()
        private val _NPC_USER = NpcUser()
        private fun getUserFile(uuid: UUID) = USER_DATA_PATH.resolve("${uuid}.yml.gz").toFile()
    }
}