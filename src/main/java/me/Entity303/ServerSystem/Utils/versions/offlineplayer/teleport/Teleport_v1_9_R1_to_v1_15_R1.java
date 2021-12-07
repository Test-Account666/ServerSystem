package me.Entity303.ServerSystem.Utils.versions.offlineplayer.teleport;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Teleport_v1_9_R1_to_v1_15_R1 extends MessageUtils implements Teleport {

    private Field locXField = null;
    private Field locYField = null;
    private Field locZField = null;
    private Field yawField = null;
    private Field pitchField = null;
    private Field worldField = null;
    private Method getHandleMethod = null;

    public Teleport_v1_9_R1_to_v1_15_R1(ss plugin) {
        super(plugin);
    }

    @Override
    public void teleport(Player player, Location location) {
        if (this.locXField == null) try {
            this.locXField = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".Entity").getDeclaredField("locX");
            this.locXField.setAccessible(true);
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (this.locYField == null) try {
            this.locYField = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".Entity").getDeclaredField("locY");
            this.locYField.setAccessible(true);
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (this.locZField == null) try {
            this.locZField = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".Entity").getDeclaredField("locZ");
            this.locZField.setAccessible(true);
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (this.yawField == null) try {
            this.yawField = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".Entity").getDeclaredField("yaw");
            this.yawField.setAccessible(true);
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (this.pitchField == null) try {
            this.pitchField = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".Entity").getDeclaredField("pitch");
            this.pitchField.setAccessible(true);
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (this.worldField == null) try {
            this.worldField = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".Entity").getDeclaredField("world");
            this.worldField.setAccessible(true);
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (this.getHandleMethod == null) try {
            this.getHandleMethod = Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".CraftWorld").getDeclaredMethod("getHandle");
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }


        Object entity = null;
        try {
            entity = this.plugin.getVersionStuff().getGetHandleMethod().invoke(player);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        try {
            this.locXField.set(entity, location.getX());
            this.locYField.set(entity, location.getY());
            this.locZField.set(entity, location.getZ());

            this.yawField.set(entity, location.getYaw());
            this.pitchField.set(entity, location.getPitch());
        } catch (IllegalAccessException e) {
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
        try {
            this.worldField.set(entity, worldServer);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
