package me.entity303.serversystem.utils.versions.offlineplayer.teleport;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class Teleport_Latest implements ITeleport {
    protected final ServerSystem _plugin;
    private final Field _worldField = null;
    private Method _setLocationMethod = null;
    private Method _getHandleMethod = null;
    private Method _teleportToMethod = null;

    public Teleport_Latest(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public void Teleport(Player player, Location location) {
        try {
            this._setLocationMethod =
                    net.minecraft.world.entity.Entity.class.getDeclaredMethod("a", double.class, double.class, double.class, float.class, float.class);
        } catch (NoSuchMethodException exception) {
            exception.printStackTrace();
        }


        if (this._getHandleMethod == null)
            try {
                this._getHandleMethod =
                        Class.forName("org.bukkit.craftbukkit." + this._plugin.GetVersionManager().GetNMSVersion() + ".CraftWorld").getDeclaredMethod("getHandle");
            } catch (NoSuchMethodException | ClassNotFoundException exception) {
                exception.printStackTrace();
                return;
            }

        EntityPlayer entity;
        try {
            entity = (EntityPlayer) this._plugin.GetVersionStuff().GetGetHandleMethod().invoke(player);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
            return;
        }

        Object worldServer;
        try {
            worldServer = this._getHandleMethod.invoke(location.getWorld());
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
            return;
        }

        if (this._teleportToMethod == null) {
            this._teleportToMethod = Arrays.stream(Entity.class.getDeclaredMethods())
                                           .filter(method -> method.getParameterTypes().length == 2)
                                           .filter(method -> method.getParameterTypes()[0] == World.class && method.getParameterTypes()[1] == BlockPosition.class)
                                           .findFirst()
                                           .orElse(null);

            if (this._teleportToMethod == null) {
                try {
                    throw new NoSuchMethodException("Couldn't find method 'teleportTo' in class " + Entity.class.getName());
                } catch (NoSuchMethodException exception) {
                    exception.printStackTrace();
                }
                return;
            }

            this._teleportToMethod.setAccessible(true);
        }

        try {
            this._teleportToMethod.invoke(entity, worldServer, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }

        try {
            this._setLocationMethod.invoke(entity, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
        }
    }
}
