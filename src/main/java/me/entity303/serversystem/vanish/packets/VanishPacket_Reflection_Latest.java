package me.entity303.serversystem.vanish.packets;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class VanishPacket_Reflection_Latest extends AbstractVanishPacket {
    private final ServerSystem _plugin;
    private VanishPacket_ProtocolLib _vanishPacketProtocolLib = null;
    private Field _playerConnectionField;
    private Field _collidesField;

    public VanishPacket_Reflection_Latest(ServerSystem plugin) {
        this._plugin = plugin;

        try {
            this._collidesField = Class.forName("net.minecraft.world.entity.EntityLiving").getDeclaredField("collides");
            this._collidesField.setAccessible(true);
        } catch (Exception ignored) {
        }

        if (!plugin.GetVersionManager().IsVanishFullyFunctional())
            return;

        var isProtocolLibInstalled = Arrays.stream(Bukkit.getPluginManager().getPlugins())
                                           .filter(Plugin::isEnabled)
                                           .anyMatch(installedPlugin -> installedPlugin.getName().toLowerCase().contains("protocollib"));

        if (!isProtocolLibInstalled)
            return;

        this._vanishPacketProtocolLib = new VanishPacket_ProtocolLib();
    }

    @Override
    public void SetVanish(Player player, boolean vanish) {
        if (this._plugin.GetVersionStuff().GetGetHandleMethod() == null)
            try {
                this._plugin.GetVersionStuff().FetchGetHandleMethod();
            } catch (ClassNotFoundException | NoSuchMethodException exception) {
                throw new RuntimeException(exception);
            }

        Object entityPlayer;

        try {
            entityPlayer = this._plugin.GetVersionStuff().GetGetHandleMethod().invoke(player);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
            return;
        }


        if (this._collidesField == null)
            this._plugin.Warn("CollidesField null!");
        else
            try {
                this._collidesField.set(entityPlayer, !vanish);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        player.setCollidable(false);

        if (this._playerConnectionField == null) {
            this._playerConnectionField = Arrays.stream(entityPlayer.getClass().getDeclaredFields())
                                                .filter(field -> field.getType().getName().toLowerCase().contains("playerconnection"))
                                                .findFirst()
                                                .orElse(null);

            if (this.IsConnectionFieldNull(entityPlayer, this._playerConnectionField, this._plugin))
                return;
        }

        if (this._vanishPacketProtocolLib == null)
            return;

        for (var all : Bukkit.getOnlinePlayers()) {
            if (all == player)
                continue;

            if (!this._plugin.GetPermissions().HasPermission(all, "vanish.see", true))
                continue;

            this._vanishPacketProtocolLib.SendPlayerInfoChangeGameModePacket(all, player, vanish);
        }
    }

    public boolean IsConnectionFieldNull(Object entityPlayer, Field playerConnectionField, ServerSystem plugin) {
        if (playerConnectionField == null) {
            plugin.Error("Couldn't find PlayerConnection field! (Modded environment?)");
            Arrays.stream(entityPlayer.getClass().getDeclaredFields()).forEach(field -> plugin.Info(field.getType() + " -> " + field.getName()));
            plugin.Warn("Please forward this to the developer of ServerSystem!");
            return true;
        }

        this._playerConnectionField = playerConnectionField;

        this._playerConnectionField.setAccessible(true);
        return false;
    }
}
