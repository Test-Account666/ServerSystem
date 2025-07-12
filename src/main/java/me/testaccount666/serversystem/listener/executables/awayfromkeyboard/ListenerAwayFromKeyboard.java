package me.testaccount666.serversystem.listener.executables.awayfromkeyboard;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ListenerAwayFromKeyboard implements Listener {
    private final Map<UUID, Long> _lastActionMap = new HashMap<>();
    private final Map<UUID, Location> _chunkLocationMap = new HashMap<>();
    private final Map<UUID, Long> _lastMouseMovement = new HashMap<>();
    private final Map<UUID, Long> _lastReelMap = new HashMap<>();

    public ListenerAwayFromKeyboard() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(ServerSystem.Instance, () -> Bukkit.getOnlinePlayers().forEach(player -> {
            var lastAction = _lastActionMap.getOrDefault(player.getUniqueId(), System.currentTimeMillis());
            var currentTime = System.currentTimeMillis();

            var timeOut = lastAction + 1000 * 60 * 5;

            if (currentTime < timeOut) return;

            var userOptional = getUser(player);
            if (userOptional.isEmpty()) return;
            var user = userOptional.get();
            if (user.isAfk()) return;

            user.setAfk(true);
            MessageBuilder.general("AwayFromKeyboard.NowAfk", user).build();
        }), 20 * 60, 20 * 60);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        _lastActionMap.remove(event.getPlayer().getUniqueId());
        _chunkLocationMap.remove(event.getPlayer().getUniqueId());
        _lastMouseMovement.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.FISHING) return;

        _lastActionMap.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        if (isMouseInactive(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }

        _lastActionMap.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());

        resetAfkStatus(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDamageEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        if (isMouseInactive(player)) {
            event.setCancelled(true);
            return;
        }

        resetAfkStatus(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();

        _chunkLocationMap.put(player.getUniqueId(), getChunkLocation(player));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        handleChunkChange(event.getPlayer());
        handleMouseMovement(event.getPlayer(), event.getFrom(), event.getTo());
    }

    private void handleMouseMovement(Player player, Location from, Location to) {
        var fromYaw = from.getYaw();
        var toYaw = to.getYaw();

        var yawDifference = Math.abs(fromYaw - toYaw);

        var fromPitch = from.getPitch();
        var toPitch = to.getPitch();

        var pitchDifference = Math.abs(fromPitch - toPitch);

        if (yawDifference <= 2F && pitchDifference <= 2F) return;

        _lastMouseMovement.put(player.getUniqueId(), System.currentTimeMillis());
    }

    private void handleChunkChange(Player player) {
        _chunkLocationMap.putIfAbsent(player.getUniqueId(), getChunkLocation(player));

        var chunkLocation = _chunkLocationMap.get(player.getUniqueId());
        var currentChunkLocation = getChunkLocation(player);
        var currentY = currentChunkLocation.getY();

        currentChunkLocation.setY(chunkLocation.getY());

        var distance = currentChunkLocation.distance(chunkLocation);

        if (distance < 3 && (Math.abs(currentY - chunkLocation.getY()) < 70)) return;

        _chunkLocationMap.put(player.getUniqueId(), getChunkLocation(player));
        resetAfkStatus(player);
    }

    @EventHandler
    public void onPlayerContainerOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        resetAfkStatus(player);
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        resetAfkStatus(event.getPlayer());
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        resetAfkStatus(event.getPlayer());
    }

    private Optional<User> getUser(Player player) {
        var userOptional = ServerSystem.Instance.getUserManager().getUser(player);
        if (userOptional.isEmpty()) return Optional.empty();
        var cachedUser = userOptional.get();

        if (cachedUser.isOfflineUser()) return Optional.empty();
        var user = (User) cachedUser.getOfflineUser();

        return Optional.of(user);
    }

    private Location getChunkLocation(Player player) {
        var chunk = player.getLocation().getChunk();

        var chunkX = chunk.getX();
        var chunkY = player.getLocation().getBlockY();
        var chunkZ = chunk.getZ();
        var world = chunk.getWorld();

        return new Location(world, chunkX, chunkY, chunkZ);
    }

    public boolean isMouseInactive(Player player) {
        var lastMouseMovement = _lastMouseMovement.getOrDefault(player.getUniqueId(), System.currentTimeMillis());
        var currentTime = System.currentTimeMillis();

        lastMouseMovement += 1000 * 30;

        return currentTime > lastMouseMovement;
    }

    private void resetAfkStatus(Player player) {
        _lastActionMap.put(player.getUniqueId(), System.currentTimeMillis());

        var userOptional = getUser(player);
        if (userOptional.isEmpty()) return;
        var user = userOptional.get();
        if (!user.isAfk()) return;

        user.setAfk(false);
        MessageBuilder.general("AwayFromKeyboard.NoLongerAfk", user).build();
    }
}
