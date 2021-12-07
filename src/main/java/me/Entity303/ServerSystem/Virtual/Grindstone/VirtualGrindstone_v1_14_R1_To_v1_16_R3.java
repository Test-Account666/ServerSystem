package me.Entity303.ServerSystem.Virtual.Grindstone;

import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class VirtualGrindstone_v1_14_R1_To_v1_16_R3 extends VirtualGrindstone {
    private Method getHandleMethod = null;
    private Method nextContainerCounterMethod = null;
    private Method sendPacketMethod = null;
    private Method addSlotListenerMethod = null;
    private Method aMethod = null;
    private Field playerConnectionField = null;
    private Field activeContainerField = null;
    private Field windowIdField = null;
    private Field inventoryField = null;
    private Field checkReachableField = null;
    private Constructor containerGrindstoneConstructor = null;
    private Constructor packetPlayOutOpenWindowConstructor = null;
    private Constructor blockPositionConstructor = null;

    @Override
    public void openGrind(Player player) {
        if (this.getHandleMethod == null) try {
            this.getHandleMethod = Class.forName("org.bukkit.craftbukkit." + this.getVersion() + ".entity.CraftPlayer").getDeclaredMethod("getHandle");
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        Object entityPlayer;
        try {
            entityPlayer = this.getHandleMethod.invoke(player);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        if (this.nextContainerCounterMethod == null) try {
            this.nextContainerCounterMethod = entityPlayer.getClass().getDeclaredMethod("nextContainerCounter");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }

        int windowID;
        try {
            windowID = (int) this.nextContainerCounterMethod.invoke(entityPlayer);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        if (this.containerGrindstoneConstructor == null) try {
            this.containerGrindstoneConstructor = Class.forName("net.minecraft.server." + this.getVersion() + ".ContainerGrindstone").getDeclaredConstructor(int.class, Class.forName("net.minecraft.server." + this.getVersion() + ".PlayerInventory"), Class.forName("net.minecraft.server." + this.getVersion() + ".ContainerAccess"));
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (this.inventoryField == null) try {
            this.inventoryField = Class.forName("net.minecraft.server." + this.getVersion() + ".EntityHuman").getDeclaredField("inventory");
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        Object playerInventory;

        try {
            playerInventory = this.inventoryField.get(entityPlayer);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        if (this.blockPositionConstructor == null) try {
            this.blockPositionConstructor = Class.forName("net.minecraft.server." + this.getVersion() + ".BlockPosition").getDeclaredConstructor(double.class, double.class, double.class);
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        Object blockPostion;
        try {
            blockPostion = this.blockPositionConstructor.newInstance(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        Object containerGrindstone;

        try {
            containerGrindstone = this.containerGrindstoneConstructor.newInstance(windowID, playerInventory, this.getWrapper(player));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        if (this.checkReachableField == null) try {
            this.checkReachableField = Class.forName("net.minecraft.server." + this.getVersion() + ".Container").getDeclaredField("checkReachable");
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try {
            this.checkReachableField.set(containerGrindstone, false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        if (this.playerConnectionField == null) try {
            this.playerConnectionField = entityPlayer.getClass().getDeclaredField("playerConnection");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return;
        }

        Object playerConnection;

        try {
            playerConnection = this.playerConnectionField.get(entityPlayer);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        if (this.sendPacketMethod == null) try {
            this.sendPacketMethod = playerConnection.getClass().getDeclaredMethod("sendPacket", Class.forName("net.minecraft.server." + this.getVersion() + ".Packet"));
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (this.packetPlayOutOpenWindowConstructor == null) try {
            this.packetPlayOutOpenWindowConstructor = Class.forName("net.minecraft.server." + this.getVersion() + ".PacketPlayOutOpenWindow").getDeclaredConstructor(int.class, Class.forName("net.minecraft.server." + this.getVersion() + ".Containers"), Class.forName("net.minecraft.server." + this.getVersion() + ".IChatBaseComponent"));
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (this.aMethod == null) try {
            this.aMethod = Class.forName("net.minecraft.server." + this.getVersion() + ".IChatBaseComponent$ChatSerializer").getDeclaredMethod("a", String.class);
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        Object component;

        try {
            component = this.aMethod.invoke(null, "{\"text\":\"Grindstone\"}");
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        Object packetPlayOutOpenWindow;

        try {
            packetPlayOutOpenWindow = this.packetPlayOutOpenWindowConstructor.newInstance(windowID, Class.forName("net.minecraft.server." + this.getVersion() + ".Containers").getDeclaredField("GRINDSTONE").get(null), component);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
            return;
        }

        if (this.activeContainerField == null) try {
            this.activeContainerField = Class.forName("net.minecraft.server." + this.getVersion() + ".EntityHuman").getDeclaredField("activeContainer");
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (this.windowIdField == null) try {
            this.windowIdField = Class.forName("net.minecraft.server." + this.getVersion() + ".Container").getDeclaredField("windowId");
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (this.addSlotListenerMethod == null) try {
            this.addSlotListenerMethod = Class.forName("net.minecraft.server." + this.getVersion() + ".Container").getDeclaredMethod("addSlotListener", Class.forName("net.minecraft.server." + this.getVersion() + ".ICrafting"));
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try {
            this.sendPacketMethod.invoke(playerConnection, packetPlayOutOpenWindow);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }
        try {
            this.activeContainerField.set(entityPlayer, containerGrindstone);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
        try {
            this.windowIdField.set(this.activeContainerField.get(entityPlayer), windowID);
        } catch (IllegalAccessException ignored) {
        }
        try {
            this.addSlotListenerMethod.invoke(this.activeContainerField.get(entityPlayer), entityPlayer);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
