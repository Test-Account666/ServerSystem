package me.entity303.serversystem.utils.versions.offlineplayer.teleport;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

public class Teleport_v1_17_R1 extends CommandUtils implements Teleport {
    private Method setLocationMethod = null;
    private Field worldField = null;
    private Method getHandleMethod = null;

    public Teleport_v1_17_R1(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public void teleport(Player player, Location location) {
        if (this.worldField == null)
            try {
                this.worldField = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".Entity").getDeclaredField("world");
                this.worldField.setAccessible(true);
            } catch (NoSuchFieldException | ClassNotFoundException e) {
                if (e instanceof ClassNotFoundException)
                    try {
                        for (var field : Class.forName("net.minecraft.world.entity.Entity").getDeclaredFields())
                            if (field.getType().getName().toLowerCase(Locale.ROOT).contains("world")) {
                                this.worldField = field;
                                break;
                            }

                        this.worldField.setAccessible(true);
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                else
                    e.printStackTrace();
                return;
            }

        if (this.setLocationMethod == null)
            try {
                this.setLocationMethod = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".Entity")
                                              .getDeclaredMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                if (e instanceof ClassNotFoundException)
                    try {
                        this.setLocationMethod = Class.forName("net.minecraft.world.entity.Entity")
                                                      .getDeclaredMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
                    } catch (NoSuchMethodException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                else
                    e.printStackTrace();
            }

        if (this.getHandleMethod == null)
            try {
                this.getHandleMethod =
                        Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".CraftWorld").getDeclaredMethod("getHandle");
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }


        Object entity;
        try {
            entity = this.plugin.getVersionStuff().getGetHandleMethod().invoke(player);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        Object worldServer;
        try {
            worldServer = this.getHandleMethod.invoke(location.getWorld());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }
        try {
            this.worldField.set(entity, worldServer);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
