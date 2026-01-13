package me.testaccount666.serversystem.commands.executables.vanish

import com.destroystokyo.paper.event.server.PaperServerListPingEvent
import com.destroystokyo.paper.event.server.PaperServerListPingEvent.ListedPlayerInfo
import io.papermc.paper.event.player.AsyncChatEvent
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.annotations.RequiredCommands
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockReceiveGameEvent
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.player.*
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent
import org.bukkit.inventory.InventoryHolder
import org.bukkit.metadata.FixedMetadataValue

@RequiredCommands([CommandVanish::class])
class ListenerVanish : Listener {
    private lateinit var _commandVanish: CommandVanish

    fun canRegister(requiredCommands: Set<ServerSystemCommandExecutor>): Boolean {
        _commandVanish = requiredCommands.firstOrNull { it is CommandVanish } as? CommandVanish ?: return false
        return true
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val user = getVanishedUser(event.getPlayer())
        if (user == null) {
            handleOtherPlayerJoin(event.getPlayer())
            return
        }

        handleVanishedPlayerJoin(event, user)
    }

    @EventHandler
    fun onWorldChanged(event: PlayerChangedWorldEvent) {
        val user = getVanishedUser(event.getPlayer()) ?: return
        _commandVanish.vanishPacket.sendVanishPacket(user)
    }

    @EventHandler
    fun onServerPing(event: PaperServerListPingEvent) {
        val listedPlayers = HashSet<ListedPlayerInfo>(event.listedPlayers)
        for (listedPlayer in listedPlayers) {
            val user = getVanishedUser(Bukkit.getPlayer(listedPlayer.id())) ?: continue

            event.listedPlayers.remove(listedPlayer)
            event.numPlayers -= 1
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val user = getVanishedUser(event.getPlayer()) ?: return

        event.quitMessage(null)
    }

    @EventHandler
    fun onTargetPlayer(event: EntityTargetLivingEntityEvent) {
        val player = event.entity as? Player ?: return
        if (!isPlayerVanished(player)) return
        event.isCancelled = true
    }

    @EventHandler
    fun onItemDrop(event: PlayerDropItemEvent) {
        handleVanishRestriction(event.getPlayer(), event, { it.vanishData.canDrop }, "Vanish.Denied.Drop")
    }

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        handleVanishRestriction(
            event.getPlayer(),
            event,
            { it.vanishData.canMessage },
            "Vanish.Denied.Message"
        )
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        handleVanishRestriction(event.getPlayer(), event, { it.vanishData.canInteract }, null)
    }

    @EventHandler
    fun onItemPickup(event: PlayerAttemptPickupItemEvent) {
        handleVanishRestriction(event.getPlayer(), event, { it.vanishData.canPickup }, null)
    }

    @EventHandler
    fun onHangingEntityBreak(event: HangingBreakByEntityEvent) {
        val player = event.remover as? Player ?: return
        if (isPlayerVanished(player)) event.isCancelled = true
    }

    @EventHandler
    fun onVehicleCollide(event: VehicleEntityCollisionEvent) {
        val player = event.entity as? Player ?: return
        if (isPlayerVanished(player)) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onContainerOpen(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        if (event.clickedBlock == null || event.clickedBlock!!.state !is InventoryHolder) return

        val user = getVanishedUser(event.getPlayer()) ?: return

        temporarilySetSpectatorMode(event)
    }

    @EventHandler
    fun onGameEvent(event: BlockReceiveGameEvent) {
        val player = event.entity as? Player ?: return
        if (isPlayerVanished(player)) event.isCancelled = true
    }

    @EventHandler
    fun onAdvancement(event: PlayerAdvancementDoneEvent) {
        val user = getVanishedUser(event.getPlayer()) ?: return
        event.message(null)
    }

    @EventHandler
    fun onGameModeChange(event: PlayerGameModeChangeEvent) {
        Bukkit.getScheduler().runTaskLater(instance, Runnable {
            val user = getVanishedUser(event.getPlayer()) ?: return@Runnable
            _commandVanish.vanishPacket.sendVanishPacket(user)
        }, 1L)
    }

    private fun handleOtherPlayerJoin(joiningPlayer: Player) {
        if (hasCommandPermission(joiningPlayer, "Vanish.Show", false)) return

        instance.registry.getService<UserManager>().cachedUsers
            .mapNotNull { it.offlineUser as? User }
            .filter(User::isVanish)
            .forEach { user -> _commandVanish.vanishPacket.sendVanishPacket(user) }
    }

    private fun handleVanishedPlayerJoin(event: PlayerJoinEvent, user: User) {
        event.joinMessage(null)
        event.getPlayer().isSleepingIgnored = true
        event.getPlayer().setMetadata("vanished", FixedMetadataValue(instance, true))
        _commandVanish.vanishPacket.sendVanishPacket(user)
    }

    private fun handleVanishRestriction(player: Player, cancellable: Cancellable, permissionCheck: VanishDataCheck, messagePath: String?) {
        val user = getVanishedUser(player) ?: return

        if (permissionCheck.hasPermission(user)) return

        cancellable.isCancelled = true

        if (messagePath == null) return

        command(messagePath, user).build()
    }

    private fun temporarilySetSpectatorMode(event: PlayerInteractEvent) {
        val previousGameMode = event.getPlayer().gameMode
        event.getPlayer().gameMode = GameMode.SPECTATOR
        event.isCancelled = false

        Bukkit.getScheduler().runTaskLater(instance, Runnable { event.getPlayer().gameMode = previousGameMode }, 5L)
    }

    private fun getVanishedUser(player: Player?): User? {
        if (player == null) return null
        val user = instance.registry.getService<UserManager>().getUserOrNull(player) ?: return null
        if (!user.isOnlineUser) return null

        val onlineUser = user.offlineUser as User
        if (!onlineUser.isVanish) return null

        return onlineUser
    }

    private fun isPlayerVanished(player: Player?): Boolean {
        return getVanishedUser(player) != null
    }

    private fun interface VanishDataCheck {
        fun hasPermission(user: User): Boolean
    }
}