package me.testaccount666.serversystem.commands.executables.teleportask

import me.testaccount666.serversystem.annotations.RequiredCommands
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

@RequiredCommands([CommandTeleportAsk::class])
class ListenerTeleportRequest : Listener {
    private lateinit var _commandTeleportAsk: CommandTeleportAsk

    fun canRegister(requiredCommands: Set<ServerSystemCommandExecutor>): Boolean {
        _commandTeleportAsk = requiredCommands.firstOrNull { it is CommandTeleportAsk } as? CommandTeleportAsk ?: return false
        return true
    }

    @EventHandler
    fun onTeleporterMove(event: PlayerMoveEvent) {
        val distance: Double = getDistance(event)
        if (distance < .1) return

        _commandTeleportAsk.activeTeleportRequests.toList()
            .forEach { teleportRequest ->
                val teleporter = if (teleportRequest.isTeleportHere) teleportRequest.receiver else teleportRequest.sender
                if (teleporter.getPlayer() == null) return@forEach
                val teleporterPlayer = teleporter.getPlayer()!!

                if (event.getPlayer().uniqueId != teleporterPlayer.uniqueId) return@forEach

                Bukkit.getScheduler().cancelTask(teleportRequest.timerId)
                teleportRequest.isCancelled = true
                _commandTeleportAsk.activeTeleportRequests.remove(teleportRequest)
                command("TeleportAsk.Moved", teleporter).build()
            }
    }

    companion object {
        private fun getDistance(event: PlayerMoveEvent): Double {
            val fromX = event.from.x
            val toX = event.to.x

            val fromY = event.from.y
            val toY = event.to.y

            val fromZ = event.from.z
            val toZ = event.to.z

            val fromWorld = event.from.world
            val toWorld = event.to.world

            val from = Location(fromWorld, fromX, fromY, fromZ)
            val to = Location(toWorld, toX, toY, toZ)

            return if (from.world.name.equals(to.world.name, true)) from.distance(to) else Double.MAX_VALUE
        }
    }
}
