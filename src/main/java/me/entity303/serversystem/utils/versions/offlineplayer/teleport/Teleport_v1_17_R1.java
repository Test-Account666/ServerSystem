package me.entity303.serversystem.utils.versions.offlineplayer.teleport;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

public class Teleport_v1_17_R1 implements ITeleport {
    protected final ServerSystem _plugin;
    private Method _setLocationMethod = null;
    private Field _worldField = null;
    private Method _getHandleMethod = null;

    public Teleport_v1_17_R1(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public void Teleport(Player player, Location location) {
        if (this._worldField == null)
            try {
                this._worldField = Class.forName("net.minecraft.server." + this._plugin.GetVersionManager().GetNMSVersion() + "Entity").getDeclaredField("world");
                this._worldField.setAccessible(true);
            } catch (NoSuchFieldException | ClassNotFoundException exception) {
                if (exception instanceof ClassNotFoundException)
                    try {
                        for (var field : Class.forName("net.minecraft.world.entity.Entity").getDeclaredFields())
                            if (field.getType().getName().toLowerCase(Locale.ROOT).contains("world")) {
                                this._worldField = field;
                                break;
                            }

                        this._worldField.setAccessible(true);
                    } catch (ClassNotFoundException exception1) {
                        exception1.printStackTrace();
                    }
                else
                    exception.printStackTrace();
                return;
            }

        if (this._setLocationMethod == null)
            try {
                this._setLocationMethod = Class.forName("net.minecraft.server." + this._plugin.GetVersionManager().GetNMSVersion() + "Entity")
                                               .getDeclaredMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
            } catch (NoSuchMethodException | ClassNotFoundException exception) {
                if (exception instanceof ClassNotFoundException)
                    try {
                        this._setLocationMethod = Class.forName("net.minecraft.world.entity.Entity")
                                                       .getDeclaredMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
                    } catch (NoSuchMethodException | ClassNotFoundException exception1) {
                        exception1.printStackTrace();
                    }
                else
                    exception.printStackTrace();
            }

        if (this._getHandleMethod == null)
            try {
                this._getHandleMethod =
                        Class.forName("org.bukkit.craftbukkit." + this._plugin.GetVersionManager().GetNMSVersion() + "CraftWorld").getDeclaredMethod("getHandle");
            } catch (NoSuchMethodException | ClassNotFoundException exception) {
                exception.printStackTrace();
                return;
            }


        Object entity;
        try {
            entity = this._plugin.GetVersionStuff().GetGetHandleMethod().invoke(player);
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
        try {
            this._worldField.set(entity, worldServer);
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }
    }
}
