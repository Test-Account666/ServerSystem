package me.testaccount666.serversystem.userdata.teleport

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.userdata.User
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.scheduler.BukkitRunnable

class TeleportRunnable(val user: User, val location: Location, val originLocation: Location, delay: Long) {
    private val endTime = System.currentTimeMillis() + delay
    private var _running = true

    fun cancel() = let { _running = false }
    var onFailure: ((user: User) -> Unit)? = null
    var onSuccess: ((user: User) -> Unit)? = null

    init {
        user.teleportRunnable?.cancel()
        user.teleportRunnable = this

        createTask().runTaskTimer(ServerSystem.instance, 5L, 5L)
    }

    private fun createTask(): BukkitRunnable {
        return object : BukkitRunnable() {
            override fun run() {
                runCatching {
                    if (!_running) {
                        stopTask()
                        return
                    }

                    if (calculateDistance() > 0.1) {
                        stopTask()
                        return
                    }

                    if (endTime > System.currentTimeMillis()) return

                    user.getPlayer()?.let {
                        playAnimation(originLocation)
                        it.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN)
                        playAnimation(location)

                        onSuccess?.invoke(user)
                    }

                    _running = false
                    stopTask()
                }.onFailure {
                    it.printStackTrace()
                    stopTask()
                }
            }

            fun stopTask() {
                if (_running) onFailure?.invoke(user)

                _running = false
                user.teleportRunnable = null
                Bukkit.getScheduler().cancelTask(taskId)
            }
        }
    }

    private fun calculateDistance(): Double {
        val location = user.getPlayer()?.location ?: return Double.MAX_VALUE
        if (location.world != originLocation.world) return Double.MAX_VALUE

        return location.distance(originLocation)
    }

    companion object {
        /**
         * Plays a teleportation animation effect at the given location
         *
         * @param location The location to play the animation at
         */
        fun playAnimation(location: Location) {
            location.world.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f)
            location.world.spawnParticle(Particle.PORTAL, location, 100, 0.5, 0.5, 0.5, 0.05)
        }

        fun User.teleportNow(location: Location) {
            val player = getPlayer() ?: error("Player is null!")

            playAnimation(player.location)
            player.teleport(location)
            playAnimation(location)
        }

        fun User.teleportLater(location: Location, delay: Long = 3000): TeleportRunnable {
            val originalLocation = getPlayer()?.location ?: error("Player is null!")

            return TeleportRunnable(this, location, originalLocation, delay)
        }
    }
}

