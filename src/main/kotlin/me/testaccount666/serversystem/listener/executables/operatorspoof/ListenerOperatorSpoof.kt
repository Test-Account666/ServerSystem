package me.testaccount666.serversystem.listener.executables.operatorspoof

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import me.testaccount666.paperktx.extensions.TimeExtensions.toTicks
import me.testaccount666.paperktx.scheduler.skedule.okkero.schedule
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.annotations.RequiredCommands
import me.testaccount666.serversystem.commands.executables.gamemode.CommandGameMode
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.userdata.*
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket
import net.minecraft.world.level.GameType
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.*
import org.bukkit.event.server.PluginDisableEvent
import java.util.Locale.getDefault
import java.util.logging.Level
import kotlin.time.Duration.Companion.seconds

@RequiredCommands([CommandGameMode::class])
class ListenerOperatorSpoof : Listener {
    private var _caughtException = false
    private var _injectionPossible = true
    private lateinit var _executorGameMode: CommandGameMode

    init {
        Bukkit.getOnlinePlayers().forEach(this::inject)
    }

    fun canRegister(requiredCommands: Set<ServerSystemCommandExecutor>): Boolean {
        _executorGameMode = requiredCommands.firstOrNull { it is CommandGameMode } as? CommandGameMode ?: return false
        return true
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        updateFakeOperatorStatus(event.getPlayer())
        inject(event.getPlayer())
    }

    @EventHandler
    fun onPlayerWorldChange(event: PlayerChangedWorldEvent) = updateFakeOperatorStatus(event.getPlayer())

    @EventHandler
    fun onGameModeChange(event: PlayerGameModeChangeEvent) = updateFakeOperatorStatus(event.getPlayer())

    private fun updateFakeOperatorStatus(player: Player) {
        var failCount = 0L
        instance.schedule {
            repeating(5L)
            while (failCount <= 4) {
                waitFor(failCount.seconds.toTicks())

                val craftPlayer = player as CraftPlayer
                runCatching { craftPlayer.handle.connection.send(ClientboundEntityEventPacket(craftPlayer.handle, 28.toByte())) }.onSuccess { break }

                failCount++
                yield()
            }

            if (failCount <= 4) return@schedule
            log.warning("Failed to update operator status 4 times in a row, giving up! (${player.name}; ${player.uniqueId})")
        }
    }

    private fun inject(player: Player) {
        if (!_injectionPossible) return

        try {
            Class.forName("net.minecraft.network.protocol.game.ServerboundChangeGameModePacket")
        } catch (_: Throwable) {
            // If we don't have this class, we don't need the injection anyways, probably an older Minecraft version.
            _injectionPossible = false
            return
        }

        val craftPlayer = player as CraftPlayer
        val playerConnection = craftPlayer.handle.connection.connection
        val pipeline = playerConnection.channel.pipeline()

        if (pipeline.get("gamemode_packet_listener") != null) pipeline.remove("gamemode_packet_listener")

        if (pipeline.get("packet_handler") == null) {
            log.warning("Couldn't find packet_handler in channel pipeline! This should not happen!")
            _injectionPossible = false
            return
        }

        pipeline.addBefore("packet_handler", "gamemode_packet_listener", GameModePacketListener(player))
    }

    private fun uninject(player: Player) {
        val craftPlayer = player as CraftPlayer
        val playerConnection = craftPlayer.handle.connection.connection
        val pipeline = playerConnection.channel.pipeline()

        if (pipeline.get("gamemode_packet_listener") == null) return

        pipeline.remove("gamemode_packet_listener")
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) = uninject(event.getPlayer())

    @EventHandler
    fun onPluginUnload(event: PluginDisableEvent) {
        if (event.plugin !== instance) return

        Bukkit.getOnlinePlayers().forEach(this::uninject)
    }

    private inner class GameModePacketListener(private val _player: Player) : ChannelDuplexHandler() {
        private val _cachedUser: CachedUser

        init {
            val cachedUser = getService<UserManager>().getUserOrNull(_player) ?: run {
                throw RuntimeException("Couldn't cache User '${_player.name}'! This should not happen!")
            }
            _cachedUser = cachedUser
        }

        override fun channelRead(ctx: ChannelHandlerContext, msg: Any?) {
            try {
                if (msg == null || _cachedUser.isOfflineUser) {
                    super.channelRead(ctx, msg)
                    return
                }

                if (!msg.javaClass.canonicalName.contains("ServerboundChangeGameModePacket", true)) {
                    super.channelRead(ctx, msg)
                    return
                }

                if (_player.isOp) {
                    super.channelRead(ctx, msg)
                    return
                }

                val mode = msg.javaClass.declaredMethods.find {
                    it.name.contains("mode") || it.returnType.name.contains("game")
                }?.invoke(msg) as? GameType ?: return
                val gameMode = GameMode.valueOf(mode.getName().uppercase(getDefault()))
                val user = _cachedUser.offlineUser as User

                // Go back to main thread
                instance.schedule { _executorGameMode.handleGameModeCommand(user, null, "gamemode", gameMode) }
            } catch (throwable: Throwable) {
                // We don't want to cause issues in case ServerSystem causes an exception
                if (!_caughtException) {
                    log.log(
                        Level.WARNING, "Caught exception in channel pipeline," +
                                " report this to the developer of ServerSystem!\nThis will only display once", throwable
                    )
                    _caughtException = true
                }

                super.channelRead(ctx, msg)
            }
        }
    }
}
