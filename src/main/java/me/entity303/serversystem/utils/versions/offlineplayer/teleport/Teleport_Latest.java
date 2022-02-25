package me.entity303.serversystem.utils.versions.offlineplayer.teleport;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Teleport_Latest extends MessageUtils implements Teleport {
    private Method setLocationMethod = null;
    private Field worldField = null;
    private Method getHandleMethod = null;

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

        entity.teleportTo((WorldServer) worldServer, new BlockPosition(location.getX(), location.getY(), location.getZ()));

        try {
            this.setLocationMethod.invoke(entity, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
