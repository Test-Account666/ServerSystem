package me.entity303.serversystem.vanish.packets;

import me.entity303.serversystem.main.ServerSystem;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

public class VanishPacket_Reflection_1_21 extends AbstractVanishPacket {
    private final ServerSystem _plugin;
    private Field _listOrderField = null;
    private boolean _useListOrderField = true;

    private Constructor<?> _entryConstructor;

    public VanishPacket_Reflection_1_21(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public void SetVanish(Player player, boolean vanish) {
        try {
            var serverPlayer = (ServerPlayer) this._plugin.GetVersionStuff().GetEntityPlayer(player);

            if (this._useListOrderField && this._listOrderField == null) {
                try {
                    this._listOrderField = ServerPlayer.class.getDeclaredField("listOrder");
                } catch (NoSuchFieldException | NoSuchFieldError ignored) {
                    this._useListOrderField = false;
                }
            }

            serverPlayer.collides = !vanish;
            player.setCollidable(!vanish);

            if (this._constructor == null) {
                var clazz = ClientboundPlayerInfoUpdatePacket.class;

                this._constructor = Arrays.stream(clazz.getDeclaredConstructors()).collect(Collectors.toList()).reversed().stream().findFirst().orElse(null);

                if (this._constructor == null) this._plugin.Error("Couldn't find `ClientboundPlayerInfoUpdatePacket` constructor!");
            }

            var actionSet = EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE);

            var entry = this.CreateEntry(player, vanish, serverPlayer);

            var infoPacket = new ClientboundPlayerInfoUpdatePacket(actionSet, entry);

            for (var all : Bukkit.getOnlinePlayers()) {
                if (all == player) continue;

                if (!this._plugin.GetPermissions().HasPermission(all, "vanish.see", true)) continue;

                var connection = ((ServerPlayer) this._plugin.GetVersionStuff().GetEntityPlayer(all)).connection;

                connection.sendPacket(infoPacket);
            }

        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    private ClientboundPlayerInfoUpdatePacket.@NotNull Entry CreateEntry(Player player, boolean vanish, ServerPlayer serverPlayer) {
        var gameType = vanish? GameType.SPECTATOR : serverPlayer.gameMode.getGameModeForPlayer();

        var chatSession = serverPlayer.getChatSession();

        var parameterArray = new Object[] { player.getUniqueId(), serverPlayer.gameProfile, true, player.getPing(), gameType, serverPlayer.getDisplayName(), true,
                                            this._useListOrderField? serverPlayer.listOrder : -1, chatSession == null? null : chatSession.asData() };

        if (!this._useListOrderField) {
            var newParameterArray = new Object[parameterArray.length - 2];

            // This should cut out the 6th and 7th parameter
            System.arraycopy(parameterArray, 0, newParameterArray, 0, 7);
            System.arraycopy(parameterArray, 8, newParameterArray, 6, parameterArray.length - 8);

            parameterArray = newParameterArray;
        }

        if (this._entryConstructor == null) this._entryConstructor = ClientboundPlayerInfoUpdatePacket.Entry.class.getConstructors()[0];

        try {
            return (ClientboundPlayerInfoUpdatePacket.Entry) this._entryConstructor.newInstance(parameterArray);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }
}
