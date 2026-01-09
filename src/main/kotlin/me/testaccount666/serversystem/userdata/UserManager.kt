package me.testaccount666.serversystem.userdata

import me.testaccount666.serversystem.ServerSystem
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.io.File
import java.nio.file.Path
import java.util.*
import java.util.Set
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors

/**
 * Manages ServerSystem's user data.
 * This class is responsible for creating, caching, and retrieving user objects,
 * as well as cleaning up stale user data to prevent memory leaks.
 */
class UserManager {
    private val _userMap: MutableMap<String, CachedUser> = ConcurrentHashMap()
    private val _userUuidMap: MutableMap<UUID, CachedUser> = ConcurrentHashMap()

    init {
        Bukkit.getScheduler()
            .scheduleAsyncRepeatingTask(
                ServerSystem.instance,
                { this.cleanStaleUsers() },
                (15 * 20 * 60).toLong(),
                (15 * 20 * 60).toLong()
            ) // 15 Minutes

        Bukkit.getOnlinePlayers().forEach { player: Player -> this.getUser(player) }
    }

    /**
     * Gets a cached user by player instance.
     *
     * @param player The player to get the user for
     * @return An Optional containing the cached user or empty if the user doesn't exist
     */
    fun getUser(player: Player): Optional<CachedUser> {
        if (player.hasMetadata("NPC")) return Optional.of(CachedUser(_NPC_USER))

        return getUser(player.uniqueId, true)
    }

    /**
     * Gets a cached user by UUID.
     * If the user is already cached, returns the cached instance.
     * If the user is online but not cached, creates and caches a new online user.
     * If the user is offline, creates and caches a new offline user.
     *
     * @param uuid The UUID of the user to get
     * @return An Optional containing the cached user or empty if the user doesn't exist
     */
    fun getUser(uuid: UUID): Optional<CachedUser> = getUser(uuid, false)

    /**
     * Gets a cached user by UUID.
     * If the user is already cached, returns the cached instance.
     * If the user is online but not cached, creates and caches a new online user.
     * If the user is offline, creates and caches a new offline user.
     *
     * @param uuid            The UUID of the user to get
     * @param forceOnlineUser If the method should only return online users
     * @return An Optional containing the cached user or empty if the user doesn't exist
     */
    fun getUser(uuid: UUID, forceOnlineUser: Boolean): Optional<CachedUser> {
        if (!forceOnlineUser && uuid == ConsoleUser.CONSOLE_UUID) return Optional.of(CachedUser(consoleUser))

        val foundUser = _userUuidMap[uuid];

        if (foundUser != null) {
            val cachedUser: CachedUser = foundUser
            if (cachedUser.isOfflineUser && forceOnlineUser) return Optional.empty()

            cachedUser.updateLastAccessTime()

            return Optional.of(cachedUser)
        }

        val player = Bukkit.getPlayer(uuid)
        if (player != null) return Optional.of(createOnlineUser(uuid))
        if (forceOnlineUser) return Optional.empty()

        return createOfflineUser(uuid)
    }

    /**
     * Gets a cached user by name.
     * If the user is already cached, returns the cached instance.
     * If the user is online but not cached, creates and caches a new online user.
     * If the user is offline, creates and caches a new offline user.
     *
     * @param name The name of the user to get
     * @return An Optional containing the cached user or empty if the user doesn't exist
     */
    fun getUser(name: String): Optional<CachedUser> = getUser(name, false)

    /**
     * Gets a cached user by name.
     * If the user is already cached, returns the cached instance.
     * If the user is online but not cached, creates and caches a new online user.
     * If the user is offline, creates and caches a new offline user.
     *
     * @param name            The name of the user to get
     * @param forceOnlineUser If the method should only return online users
     * @return An Optional containing the cached user or empty if the user doesn't exist
     */
    fun getUser(name: String, forceOnlineUser: Boolean): Optional<CachedUser> {
        val foundUser = _userMap[name];

        if (foundUser != null) {
            val cachedUser: CachedUser = foundUser

            if (cachedUser.isOfflineUser && forceOnlineUser) return Optional.empty()

            cachedUser.updateLastAccessTime()

            return Optional.of(cachedUser)
        }

        val player = Bukkit.getPlayer(name)
        if (player != null) return Optional.of(createOnlineUser(player.uniqueId))
        if (forceOnlineUser) return Optional.empty()

        val offlineUser = Bukkit.getOfflinePlayer(name)
        if (offlineUser.name == null) return Optional.empty()

        return createOfflineUser(offlineUser.uniqueId)
    }

    /**
     * Creates and caches an offline user for the given UUID.
     *
     * @param uuid The UUID of the user to create
     * @return An Optional containing the cached user or empty if the user doesn't exist
     */
    private fun createOfflineUser(uuid: UUID): Optional<CachedUser> {
        val userFile: File = getUserFile(uuid)
        val user = OfflineUser(userFile)

        if (user.getName().isEmpty) return Optional.empty()

        val cachedUser = CachedUser(user)
        _userUuidMap[uuid] = cachedUser
        _userMap[user.getName().get()] = cachedUser

        return Optional.of(cachedUser)
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
        _userMap[user.getName().get()] = cachedUser

        return cachedUser
    }

    val cachedUsers: MutableSet<CachedUser>
        /**
         * Gets a copy of all currently cached users.
         *
         * @return A set containing all cached users
         */
        get() = Set.copyOf<CachedUser>(_userMap.values)

    /**
     * Removes stale users from the cache to prevent memory leaks.
     * This method is called periodically by a scheduled task.
     */
    fun cleanStaleUsers() {
        val staleUsers = _userMap.values.stream().filter { obj: CachedUser -> obj.isStale }.collect(Collectors.toSet())

        _userMap.entries.removeIf { entry: MutableMap.MutableEntry<String, CachedUser> -> staleUsers.contains(entry.value) }
        _userUuidMap.entries.removeIf { entry: MutableMap.MutableEntry<UUID, CachedUser> -> staleUsers.contains(entry.value) }
    }

    companion object {
        @JvmField
        val USER_DATA_PATH: Path = Path.of("plugins", "ServerSystem", "UserData")

        /**
         * Gets the console user instance.
         *
         * @return The console user instance
         */
        val consoleUser: ConsoleUser = ConsoleUser()
        private val _NPC_USER = NpcUser()
        private fun getUserFile(uuid: UUID): File = USER_DATA_PATH.resolve("${uuid}.yml.gz").toFile()
    }
}