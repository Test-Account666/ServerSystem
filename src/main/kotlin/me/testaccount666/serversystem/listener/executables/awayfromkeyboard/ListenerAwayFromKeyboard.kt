package me.testaccount666.serversystem.listener.executables.awayfromkeyboard

import io.papermc.paper.event.player.AsyncChatEvent
import me.testaccount666.paperktx.extensions.TimeExtensions.toTicks
import me.testaccount666.paperktx.extensions.location
import me.testaccount666.paperktx.scheduler.skedule.okkero.schedule
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.extensions.*
import me.testaccount666.serversystem.managers.config.ConfigurationManager
import me.testaccount666.serversystem.userdata.User
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
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ListenerAwayFromKeyboard : Listener {
    private val _enabled = getService<ConfigurationManager>().generalConfig.getValue("AwayFromKeyboard.Enabled", false)
    private val _lastActionMap = HashMap<UUID, Long>()
    private val _chunkLocationMap = HashMap<UUID, Location>()
    private val _lastMouseMovement = HashMap<UUID, Long>()

    init {

        instance.schedule {
            loop(1.minutes.toTicks(), 1.minutes.toTicks()) {
                Bukkit.getOnlinePlayers().forEach { player ->
                    val lastAction = _lastActionMap.getOrDefault(player.uniqueId, System.currentTimeMillis())
                    val currentTime = System.currentTimeMillis()

                    val timeOut = lastAction + 5.minutes.inWholeMilliseconds

                    if (currentTime < timeOut) return@forEach

                    val user = player.asUser() ?: return@forEach
                    if (user.isAfk) return@forEach

                    user.isAfk = true
                    user.generalMsg("AwayFromKeyboard.NowAfk")
                }
            }
        }
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

    private fun getUser(player: Player): User? = player.asUser()

    private fun getChunkLocation(player: Player): Location {
        val chunk = player.location.chunk

        return location(chunk.x, player.location.blockY, chunk.z, chunk.world)
    }

    fun isMouseInactive(player: Player): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastMouseMovement = _lastMouseMovement.getOrDefault(player.uniqueId, currentTime) + 30.seconds.inWholeMilliseconds

        return currentTime > lastMouseMovement
    }

    private fun resetAfkStatus(player: Player) {
        _lastActionMap[player.uniqueId] = System.currentTimeMillis()

        val user = player.asUser() ?: return
        if (!user.isAfk) return

        user.isAfk = false
        user.generalMsg("AwayFromKeyboard.NoLongerAfk")
    }
}
