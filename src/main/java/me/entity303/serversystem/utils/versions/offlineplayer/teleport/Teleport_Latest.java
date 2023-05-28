package me.entity303.serversystem.utils.versions.offlineplayer.teleport;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
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

public class Teleport_Latest extends MessageUtils implements Teleport {
    private Method setLocationMethod = null;
    private Field worldField = null;
    private Method getHandleMethod = null;
    private Method teleportToMethod = null;

    public Teleport_Latest(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public void teleport(Player player, Location location) {
        try {
            this.setLocationMethod = net.minecraft.world.entity.Entity.class.getDeclaredMethod("a", double.class, double.class, double.class, float.class, float.class);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }


        if (this.getHandleMethod == null) try {
            this.getHandleMethod = Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".CraftWorld").getDeclaredMethod("getHandle");
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        EntityPlayer entity = null;
        try {
            entity = (EntityPlayer) this.plugin.getVersionStuff().getGetHandleMethod().invoke(player);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        Object worldServer = null;
        try {
            worldServer = this.getHandleMethod.invoke(location.getWorld());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        if (this.teleportToMethod == null) {
            this.teleportToMethod = Arrays.stream(Entity.class.getDeclaredMethods()).filter(method -> method.getParameterTypes().length == 2).filter(method -> method.getParameterTypes()[0] == World.class && method.getParameterTypes()[1] == BlockPosition.class).findFirst().orElse(null);

            if (this.teleportToMethod == null) {
                try {
                    throw new NoSuchMethodException("Couldn't find method 'teleportTo' in class " + Entity.class.getName());
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                return;
            }

            this.teleportToMethod.setAccessible(true);
        }

        try {
            this.teleportToMethod.invoke(entity, worldServer, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        try {
            this.setLocationMethod.invoke(entity, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
