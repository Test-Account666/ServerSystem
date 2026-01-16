package me.testaccount666.serversystem.listener.executables.awayfromkeyboard

import io.papermc.paper.event.player.AsyncChatEvent
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.managers.config.ConfigurationManager
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.*
import java.util.*
import kotlin.math.abs

class ListenerAwayFromKeyboard : Listener {
    private val _enabled: Boolean
    private val _lastActionMap: MutableMap<UUID, Long> = HashMap()
    private val _chunkLocationMap: MutableMap<UUID, Location> = HashMap()
    private val _lastMouseMovement: MutableMap<UUID, Long> = HashMap()

    init {
        val configManager = instance.registry.getService<ConfigurationManager>()
        _enabled = configManager.generalConfig.getBoolean("AwayFromKeyboard.Enabled", false)

        Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, {
            Bukkit.getOnlinePlayers().forEach { player ->
                val lastAction = _lastActionMap.getOrDefault(player.uniqueId, System.currentTimeMillis())
                val currentTime = System.currentTimeMillis()

                val timeOut = lastAction + 1000 * 60 * 5

                if (currentTime < timeOut) return@forEach

                val user = getUser(player) ?: return@forEach
                if (user.isAfk) return@forEach

                user.isAfk = true
                general("AwayFromKeyboard.NowAfk", user).build()
            }
        }, (20 * 60).toLong(), (20 * 60).toLong())
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        _lastActionMap.remove(event.getPlayer().uniqueId)
        _chunkLocationMap.remove(event.getPlayer().uniqueId)
        _lastMouseMovement.remove(event.getPlayer().uniqueId)
    }

    @EventHandler
    fun onPlayerFish(event: PlayerFishEvent) {
        if (!_enabled) return
        if (event.state == PlayerFishEvent.State.FISHING) return

        _lastActionMap[event.getPlayer().uniqueId] = System.currentTimeMillis()
    }

    @EventHandler
    fun onPlayerBreakBlock(event: BlockBreakEvent) {
        if (!_enabled) return
        if (isMouseInactive(event.player)) {
            event.isCancelled = true
            return
        }

        _lastActionMap[event.player.uniqueId] = System.currentTimeMillis()

        resetAfkStatus(event.player)
    }

    @EventHandler
    fun onPlayerDamageEntity(event: EntityDamageByEntityEvent) {
        if (!_enabled) return
        val player = event.damager as? Player ?: return

        if (isMouseInactive(player)) {
            event.isCancelled = true
            return
        }

        resetAfkStatus(player)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (!_enabled) return
        val player = event.getPlayer()

        _chunkLocationMap[player.uniqueId] = getChunkLocation(player)
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (!_enabled) return
        handleChunkChange(event.getPlayer())
        handleMouseMovement(event.getPlayer(), event.from, event.to)
    }

    private fun handleMouseMovement(player: Player, from: Location, to: Location) {
        val fromYaw = from.yaw
        val toYaw = to.yaw

        val yawDifference = abs(fromYaw - toYaw)

        val fromPitch = from.pitch
        val toPitch = to.pitch

        val pitchDifference = abs(fromPitch - toPitch)

        if (yawDifference <= 2f && pitchDifference <= 2f) return

        _lastMouseMovement[player.uniqueId] = System.currentTimeMillis()
    }

    private fun handleChunkChange(player: Player) {
        _chunkLocationMap.putIfAbsent(player.uniqueId, getChunkLocation(player))

        val chunkLocation = _chunkLocationMap[player.uniqueId]!!
        val currentChunkLocation = getChunkLocation(player)
        val currentY = currentChunkLocation.y

        currentChunkLocation.y = chunkLocation.y

        val currentWorld = currentChunkLocation.world
        val chunkWorld = chunkLocation.world
        if (currentWorld !== chunkWorld) {
            _chunkLocationMap[player.uniqueId] = getChunkLocation(player)
            resetAfkStatus(player)
            return
        }

        val distance = currentChunkLocation.distance(chunkLocation)

        if (distance < 3 && (abs(currentY - chunkLocation.y) < 70)) return

        _chunkLocationMap[player.uniqueId] = getChunkLocation(player)
        resetAfkStatus(player)
    }

    @EventHandler
    fun onPlayerContainerOpen(event: InventoryOpenEvent) {
        if (!_enabled) return
        val player = event.player as? Player ?: return

        resetAfkStatus(player)
    }

    @EventHandler
    fun onPlayerChat(event: AsyncChatEvent) {
        if (!_enabled) return
        resetAfkStatus(event.getPlayer())
    }

    @EventHandler
    fun onPlayerCommand(event: PlayerCommandPreprocessEvent) {
        if (!_enabled) return
        resetAfkStatus(event.getPlayer())
    }

    private fun getUser(player: Player): User? {
        val cachedUser = instance.registry.getService<UserManager>().getUserOrNull(player) ?: return null

        if (cachedUser.isOfflineUser) return null
        return cachedUser.offlineUser as User
    }

    private fun getChunkLocation(player: Player): Location {
        val chunk = player.location.chunk

        val chunkX = chunk.x
        val chunkY = player.location.blockY
        val chunkZ = chunk.z
        val world = chunk.world

        return Location(world, chunkX.toDouble(), chunkY.toDouble(), chunkZ.toDouble())
    }

    fun isMouseInactive(player: Player): Boolean {
        var lastMouseMovement = _lastMouseMovement.getOrDefault(player.uniqueId, System.currentTimeMillis())
        val currentTime = System.currentTimeMillis()

        lastMouseMovement += (1000 * 30).toLong()

        return currentTime > lastMouseMovement
    }

    private fun resetAfkStatus(player: Player) {
        _lastActionMap[player.uniqueId] = System.currentTimeMillis()

        val user = getUser(player) ?: return
        if (!user.isAfk) return

        user.isAfk = false
        general("AwayFromKeyboard.NoLongerAfk", user).build()
    }
}
