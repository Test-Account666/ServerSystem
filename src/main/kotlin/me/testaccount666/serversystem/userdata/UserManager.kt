package me.testaccount666.serversystem.userdata

import me.testaccount666.serversystem.ServerSystem
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.io.File
import java.nio.file.Path
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages ServerSystem's user data.
 * This class is responsible for creating, caching, and retrieving user objects,
 * as well as cleaning up stale user data to prevent memory leaks.
 */
class UserManager {
    private val _userMap = ConcurrentHashMap<String, CachedUser>()
    private val _userUuidMap = ConcurrentHashMap<UUID, CachedUser>()

    init {
        Bukkit.getScheduler()
            .scheduleAsyncRepeatingTask(
                ServerSystem.instance,
                { cleanStaleUsers() },
                (15 * 20 * 60).toLong(),
                (15 * 20 * 60).toLong()
            ) // 15 Minutes

        Bukkit.getOnlinePlayers().forEach(this::getUserOrNull)
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

        val foundUser = _userUuidMap[uuid]

        if (foundUser != null) {
            val cachedUser: CachedUser = foundUser
            if (cachedUser.isOfflineUser && forceOnlineUser) return null

            cachedUser.updateLastAccessTime()

            return cachedUser
        }

        val player = Bukkit.getPlayer(uuid)
        if (player != null) return createOnlineUser(uuid)
        if (forceOnlineUser) return null

        return createOfflineUserOrNull(uuid)
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
        val foundUser = _userMap[name]

        if (foundUser != null) {
            val cachedUser: CachedUser = foundUser

            if (cachedUser.isOfflineUser && forceOnlineUser) return null

            cachedUser.updateLastAccessTime()

            return cachedUser
        }

        val player = Bukkit.getPlayer(name)
        if (player != null) return createOnlineUser(player.uniqueId)
        if (forceOnlineUser) return null

        val offlineUser = Bukkit.getOfflinePlayer(name)
        if (offlineUser.name == null) return null

        return createOfflineUserOrNull(offlineUser.uniqueId)
    }

    /**
     * Creates and caches an offline user for the given UUID.
     *
     * @param uuid The UUID of the user to create
     * @return The cached user or null if the user doesn't exist
     */
    private fun createOfflineUserOrNull(uuid: UUID): CachedUser? {
        val userFile: File = getUserFile(uuid)
        val user = OfflineUser(userFile)

        val name = user.getNameOrNull() ?: return null

        val cachedUser = CachedUser(user)
        _userUuidMap[uuid] = cachedUser
        _userMap[name] = cachedUser

        return cachedUser
    }

    /**
     * Creates and caches an online user for the given UUID.
     *
     * @param uuid The UUID of the user to create
     * @return The cached user
     */
    private fun createOnlineUser(uuid: UUID): CachedUser {
        val userFile: File = getUserFile(uuid)
        val user = User(userFile)

        val cachedUser = CachedUser(user)
        _userUuidMap[uuid] = cachedUser
        _userMap[user.getNameOrNull()!!] = cachedUser

        return cachedUser
    }

    val cachedUsers
        get() = _userMap.values.toSet()

    /**
     * Removes stale users from the cache to prevent memory leaks.
     * This method is called periodically by a scheduled task.
     */
    fun cleanStaleUsers() {
        val staleUsers = _userMap.values.filter { it.isStale }

        _userMap.entries.removeIf { staleUsers.contains(it.value) }
        _userUuidMap.entries.removeIf { staleUsers.contains(it.value) }
    }

    companion object {
        @JvmField
        val USER_DATA_PATH: Path = Path.of("plugins", "ServerSystem", "UserData")

        /**
         * Gets the console user instance.
         *
         * @return The console user instance
         */
        val consoleUser = ConsoleUser()
        private val _NPC_USER = NpcUser()
        private fun getUserFile(uuid: UUID): File = USER_DATA_PATH.resolve("${uuid}.yml.gz").toFile()
    }
}