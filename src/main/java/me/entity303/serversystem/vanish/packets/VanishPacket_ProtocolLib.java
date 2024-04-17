package me.entity303.serversystem.vanish.packets;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static com.comphenix.protocol.PacketType.Play.Server.PLAYER_INFO;

public class VanishPacket_ProtocolLib {
    private Field pingField;

    public void sendPlayerInfoChangeGameModePacket(Player target, Player vanishPlayer, boolean vanish) {
        var playerInfoPacket = new PacketContainer(PLAYER_INFO);

        var action = playerInfoPacket.getPlayerInfoAction();

        action.writeSafely(0, EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE);

        var gameProfile = WrappedGameProfile.fromPlayer(vanishPlayer);
        var ping = this.getPing(vanishPlayer);
        var gameMode = vanish? EnumWrappers.NativeGameMode.SPECTATOR : EnumWrappers.NativeGameMode.fromBukkit(vanishPlayer.getGameMode());
        var displayName = WrappedChatComponent.fromText(vanishPlayer.getPlayerListName());

        var playerInfoDataList = new ArrayList<PlayerInfoData>();

        playerInfoDataList.add(new PlayerInfoData(gameProfile, ping, gameMode, displayName));

        playerInfoPacket.getPlayerInfoDataLists().writeSafely(0, playerInfoDataList);

        ProtocolLibrary.getProtocolManager().sendServerPacket(target, playerInfoPacket);
    }

    private int getPing(Player player) {
        try {

            var plugin = ServerSystem.getPlugin(ServerSystem.class);

            var getHandleMethod = plugin.getVersionStuff().getGetHandleMethod();

            if (getHandleMethod == null) {
                getHandleMethod = player.getClass().getDeclaredMethod("getHandle");
                getHandleMethod.setAccessible(true);

                plugin.getVersionStuff().setGetHandleMethod(getHandleMethod);
            }

            var entityPlayer = getHandleMethod.invoke(player);

            if (this.pingField == null) {
                this.pingField = entityPlayer.getClass().getDeclaredField("ping");
                this.pingField.setAccessible(true);
            }
            var ping = this.pingField.getInt(entityPlayer);

            return Math.max(ping, 0);
        } catch (Exception e) {
            return 666;
        }
    }
}
