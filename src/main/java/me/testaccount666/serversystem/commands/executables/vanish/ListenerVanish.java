package me.testaccount666.serversystem.commands.executables.vanish;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.annotations.RequiredCommands;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.userdata.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockReceiveGameEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;

@RequiredCommands(requiredCommands = CommandVanish.class)
public class ListenerVanish implements Listener {
    private CommandVanish _commandVanish;

    public boolean canRegister(Set<ServerSystemCommandExecutor> requiredCommands) {
        var canRegister = new AtomicBoolean(false);
        requiredCommands.forEach(command -> {
            if (!(command instanceof CommandVanish commandVanish)) return;

            _commandVanish = commandVanish;
            canRegister.set(true);
        });

        return canRegister.get();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var user = getVanishedUser(event.getPlayer());
        if (user == null) {
            handleOtherPlayerJoin(event.getPlayer());
            return;
        }

        handleVanishedPlayerJoin(event, user);
    }

    @EventHandler
    public void onWorldChanged(PlayerChangedWorldEvent event) {
        var user = getVanishedUser(event.getPlayer());
        if (user == null) return;
        _commandVanish.vanishPacket.sendVanishPacket(user);
    }

    @EventHandler
    public void onServerPing(PaperServerListPingEvent event) {
        var listedPlayers = new HashSet<>(event.getListedPlayers());
        for (var listedPlayer : listedPlayers) {
            var user = getVanishedUser(Bukkit.getPlayer(listedPlayer.id()));
            if (user == null) continue;

            event.getListedPlayers().remove(listedPlayer);
            event.setNumPlayers(event.getNumPlayers() - 1);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        var user = getVanishedUser(event.getPlayer());
        if (user == null) return;

        event.quitMessage(null);
    }

    @EventHandler
    public void onTargetPlayer(EntityTargetLivingEntityEvent event) {
        if (!(event.getTarget() instanceof Player player)) return;
        if (!isPlayerVanished(player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        handleVanishRestriction(event.getPlayer(), event, user -> user.getVanishData().canDrop(), "Vanish.Denied.Drop");
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        handleVanishRestriction(event.getPlayer(), event, user -> user.getVanishData().canMessage(), "Vanish.Denied.Message");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        handleVanishRestriction(event.getPlayer(), event, user -> user.getVanishData().canInteract(), null);
    }

    @EventHandler
    public void onItemPickup(PlayerAttemptPickupItemEvent event) {
        handleVanishRestriction(event.getPlayer(), event, user -> user.getVanishData().canPickup(), null);
    }

    @EventHandler
    public void onHangingEntityBreak(HangingBreakByEntityEvent event) {
        if (event.getRemover() instanceof Player player && isPlayerVanished(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleCollide(VehicleEntityCollisionEvent event) {
        if (event.getEntity() instanceof Player player && isPlayerVanished(player)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onContainerOpen(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null || !(event.getClickedBlock().getState() instanceof InventoryHolder)) return;

        var user = getVanishedUser(event.getPlayer());
        if (user == null) return;

        temporarilySetSpectatorMode(event);
    }

    @EventHandler
    public void onGameEvent(BlockReceiveGameEvent event) {
        if (event.getEntity() instanceof Player player && isPlayerVanished(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        var user = getVanishedUser(event.getPlayer());
        if (user == null) return;
        event.message(null);
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Bukkit.getScheduler().runTaskLater(ServerSystem.getInstance(), () -> {
            var user = getVanishedUser(event.getPlayer());
            if (user == null) return;
            _commandVanish.vanishPacket.sendVanishPacket(user);
        }, 1L);
    }

    private void handleOtherPlayerJoin(Player joiningPlayer) {
        if (PermissionManager.hasCommandPermission(joiningPlayer, "Vanish.Show", false)) return;

        ServerSystem.getInstance().getRegistry().getService(UserManager.class).getCachedUsers().stream()
                .filter(cachedUser -> !cachedUser.isOfflineUser())
                .map(cachedUser -> (User) cachedUser.getOfflineUser())
                .filter(User::isVanish)
                .forEach(user -> _commandVanish.vanishPacket.sendVanishPacket(user));
    }

    private void handleVanishedPlayerJoin(PlayerJoinEvent event, User user) {
        event.joinMessage(null);
        event.getPlayer().setSleepingIgnored(true);
        event.getPlayer().setMetadata("vanished", new FixedMetadataValue(ServerSystem.getInstance(), true));
        _commandVanish.vanishPacket.sendVanishPacket(user);
    }

    private void handleVanishRestriction(Player player, Cancellable cancellable, VanishDataCheck permissionCheck, String messagePath) {
        var user = getVanishedUser(player);
        if (user == null) return;

        if (permissionCheck.hasPermission(user)) return;

        cancellable.setCancelled(true);

        if (messagePath == null) return;

        command(messagePath, user).build();
    }

    private void temporarilySetSpectatorMode(PlayerInteractEvent event) {
        var previousGameMode = event.getPlayer().getGameMode();
        event.getPlayer().setGameMode(GameMode.SPECTATOR);
        event.setCancelled(false);

        Bukkit.getScheduler().runTaskLater(ServerSystem.getInstance(), () -> event.getPlayer().setGameMode(previousGameMode), 5L);
    }

    private User getVanishedUser(Player player) {
        if (player == null) return null;
        return ServerSystem.getInstance().getRegistry().getService(UserManager.class).getUser(player)
                .filter(cachedUser -> !cachedUser.isOfflineUser())
                .map(cachedUser -> (User) cachedUser.getOfflineUser())
                .filter(User::isVanish)
                .orElse(null);
    }

    private boolean isPlayerVanished(Player player) {
        return getVanishedUser(player) != null;
    }

    @FunctionalInterface
    private interface VanishDataCheck {
        boolean hasPermission(User user);
    }
}