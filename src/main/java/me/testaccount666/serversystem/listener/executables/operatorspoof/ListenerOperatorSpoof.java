package me.testaccount666.serversystem.listener.executables.operatorspoof;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.annotations.RequiredCommands;
import me.testaccount666.serversystem.commands.executables.gamemode.CommandGameMode;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;
import me.testaccount666.serversystem.userdata.CachedUser;
import me.testaccount666.serversystem.userdata.User;
import net.minecraft.network.protocol.game.ServerboundChangeGameModePacket;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredCommands(requiredCommands = CommandGameMode.class)
public class ListenerOperatorSpoof implements Listener {
    private CommandGameMode _commandGameMode;

    public ListenerOperatorSpoof() {
        Bukkit.getOnlinePlayers().forEach(this::inject);
    }

    public boolean canRegister(Set<ServerSystemCommandExecutor> requiredCommands) {
        var canRegister = new AtomicBoolean(false);

        requiredCommands.forEach(command -> {
            if (!(command instanceof CommandGameMode commandGameMode)) return;

            _commandGameMode = commandGameMode;
            canRegister.set(true);
        });

        return canRegister.get();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updateFakeOperatorStatus(event.getPlayer());
        inject(event.getPlayer());
    }

    @EventHandler
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        updateFakeOperatorStatus(event.getPlayer());
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        updateFakeOperatorStatus(event.getPlayer());
    }

    private void updateFakeOperatorStatus(Player player) {
        var craftPlayer = (CraftPlayer) player;
        var craftServer = (CraftServer) Bukkit.getServer();
        // 28 is level 4 operator level
        craftServer.getHandle().sendPlayerPermissionLevel(craftPlayer.getHandle(), 28, false);
    }

    private void inject(Player player) {
        var craftPlayer = (CraftPlayer) player;
        var playerConnection = craftPlayer.getHandle().connection.connection;
        var pipeline = playerConnection.channel.pipeline();

        if (pipeline.get("gamemode_packet_listener") != null) pipeline.remove("gamemode_packet_listener");

        pipeline.addBefore("packet_handler", "gamemode_packet_listener", new GameModePacketListener(player));
    }

    private void uninject(Player player) {
        var craftPlayer = (CraftPlayer) player;
        var playerConnection = craftPlayer.getHandle().connection.connection;
        var pipeline = playerConnection.channel.pipeline();

        if (pipeline.get("gamemode_packet_listener") == null) return;

        pipeline.remove("gamemode_packet_listener");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        uninject(event.getPlayer());
    }

    @EventHandler
    public void onPluginUnload(PluginDisableEvent event) {
        if (event.getPlugin() != ServerSystem.Instance) return;

        Bukkit.getOnlinePlayers().forEach(this::uninject);
    }

    private class GameModePacketListener extends ChannelDuplexHandler {
        private final Player _player;
        private final CachedUser _cachedUser;

        public GameModePacketListener(Player player) {
            _player = player;

            var cachedUserOptional = ServerSystem.Instance.getUserManager().getUser(player);
            if (cachedUserOptional.isEmpty()) throw new RuntimeException("Couldn't cache User '${player.getName()}'! This should not happen!");
            _cachedUser = cachedUserOptional.get();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (_cachedUser.isOfflineUser()) {
                super.channelRead(ctx, msg);
                return;
            }

            if (!(msg instanceof ServerboundChangeGameModePacket(var mode))) {
                super.channelRead(ctx, msg);
                return;
            }

            if (_player.isOp()) {
                super.channelRead(ctx, msg);
                return;
            }

            var gameMode = GameMode.valueOf(mode.getName().toUpperCase());
            var user = (User) _cachedUser.getOfflineUser();

            // Go back to main thread
            Bukkit.getScheduler().runTask(ServerSystem.Instance, () -> _commandGameMode.handleGameModeCommand(user, gameMode, new String[0]));
        }
    }
}
