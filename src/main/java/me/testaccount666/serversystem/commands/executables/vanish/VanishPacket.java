package me.testaccount666.serversystem.commands.executables.vanish;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.User;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.world.level.GameType;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import java.util.EnumSet;

public class VanishPacket {

    public void sendVanishPacket(User vanishUser) {
        var enableVanish = vanishUser.isVanish();

        var craftPlayer = (CraftPlayer) vanishUser.getPlayer();

        var updateEntry = new ClientboundPlayerInfoUpdatePacket.Entry(vanishUser.getUuid(), craftPlayer.getProfile(),
                true, craftPlayer.getPing(), enableVanish? GameType.SPECTATOR : craftPlayer.getHandle().gameMode(),
                craftPlayer.getHandle().getDisplayName(), true,
                craftPlayer.getPlayerListOrder(), null);

        var infoPacket = new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE), updateEntry);

        for (var all : Bukkit.getOnlinePlayers()) {
            if (all == vanishUser.getPlayer()) continue;

            if (!enableVanish) {
                var craftAll = (CraftPlayer) all;
                craftAll.getHandle().connection.send(infoPacket);
                all.showPlayer(ServerSystem.Instance, vanishUser.getPlayer());
                continue;
            }

            if (PermissionManager.hasCommandPermission(all, "Vanish.Show", false)) {
                var craftAll = (CraftPlayer) all;
                craftAll.getHandle().connection.send(infoPacket);
                continue;
            }

            all.hidePlayer(ServerSystem.Instance, vanishUser.getPlayer());
        }
    }
}
