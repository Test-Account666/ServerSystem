package me.entity303.serversystem.vanish.packets;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class VanishPacket_Reflection_Latest extends VanishPacket {
    private final ServerSystem plugin;
    private VanishPacket_ProtocolLib vanishPacketProtocolLib = null;
    private Method getHandleMethod;
    private Field playerConnectionField;
    private Field collidesField;

    public VanishPacket_Reflection_Latest(ServerSystem plugin) {
        this.plugin = plugin;

        try {
            this.collidesField = Class.forName("net.minecraft.world.entity.EntityLiving").getDeclaredField("collides");
            this.collidesField.setAccessible(true);
        } catch (Exception ignored) {
        }

        if (!plugin.getVersionManager().isVanishFullyFunctional())
            return;

        var isProtocolLibInstalled = Arrays.stream(Bukkit.getPluginManager().getPlugins())
                                           .filter(Plugin::isEnabled)
                                           .anyMatch(installedPlugin -> installedPlugin.getName().toLowerCase().contains("protocollib"));

        if (!isProtocolLibInstalled)
            return;

        this.vanishPacketProtocolLib = new VanishPacket_ProtocolLib();
    }

    @Override
    public void setVanish(Player player, boolean vanish) {
        if (this.plugin.getVersionStuff().getGetHandleMethod() == null)
            try {
                this.plugin.getVersionStuff().FetchGetHandleMethod();
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

        Object entityPlayer;

        try {
            entityPlayer = this.getHandleMethod.invoke(player);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }


        if (this.collidesField == null)
            this.plugin.warn("CollidesField null!");
        else
            try {
                this.collidesField.set(entityPlayer, !vanish);
            } catch (Exception e) {
                e.printStackTrace();
            }

        player.setCollidable(false);

        if (this.playerConnectionField == null) {
            this.playerConnectionField = Arrays.stream(entityPlayer.getClass().getDeclaredFields())
                                               .filter(field -> field.getType().getName().toLowerCase().contains("playerconnection"))
                                               .findFirst()
                                               .orElse(null);

            if (this.isConnectionFieldNull(entityPlayer, this.playerConnectionField, this.plugin))
                return;
        }

        if (this.vanishPacketProtocolLib == null)
            return;

        for (var all : Bukkit.getOnlinePlayers()) {
            if (all == player)
                continue;

            if (!this.plugin.getPermissions().hasPermission(all, "vanish.see", true))
                continue;

            this.vanishPacketProtocolLib.sendPlayerInfoChangeGameModePacket(all, player, vanish);
        }
    }

    public boolean isConnectionFieldNull(Object entityPlayer, Field playerConnectionField, ServerSystem plugin) {
        if (playerConnectionField == null) {
            plugin.error("Couldn't find PlayerConnection field! (Modded environment?)");
            Arrays.stream(entityPlayer.getClass().getDeclaredFields())
                  .forEach(field -> plugin.log(field.getType() + " -> " + field.getName()));
            plugin.warn("Please forward this to the developer of ServerSystem!");
            return true;
        }

        this.playerConnectionField = playerConnectionField;

        this.playerConnectionField.setAccessible(true);
        return false;
    }
}
