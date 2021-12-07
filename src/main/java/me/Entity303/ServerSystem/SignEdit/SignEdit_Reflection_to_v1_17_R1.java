package me.Entity303.ServerSystem.SignEdit;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SignEdit_Reflection_to_v1_17_R1 implements SignEdit {
    private String version;
    private Method getHandleMethodPlayer;
    private Method getHandleMethodWorld;
    private Method getPositionMethod;
    private Field playerConnectionField;
    private Field setEditableField;
    private Field gField;

    @Override
    public void editSign(Player player, Sign sign) {
        if (this.getHandleMethodPlayer == null) {
            try {
                this.getHandleMethodPlayer = player.getClass().getDeclaredMethod("getHandle");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            this.getHandleMethodPlayer.setAccessible(true);
        }

        Object entityPlayer = null;

        try {
            entityPlayer = this.getHandleMethodPlayer.invoke(player);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        if (this.getPositionMethod == null) try {
            this.getPositionMethod = Class.forName("net.minecraft.world.level.block.entity.TileEntity").getMethod("getPosition");
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (this.playerConnectionField == null) {
            try {
                this.playerConnectionField = entityPlayer.getClass().getField("b");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            this.playerConnectionField.setAccessible(true);
        }

        if (this.getHandleMethodWorld == null) try {
            this.getHandleMethodWorld = sign.
                    getWorld().
                    getClass().
                    getMethod("getHandle");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }


        String[] lines = new String[4];
        for (int i = 0; i < sign.getLines().length; i++) {
            sign.getLine(i);
            lines[i] = sign.getLine(i).replace("§", "&");
        }
        Object tes = null;
        try {
            tes = this.getHandleMethodWorld.
                    invoke(sign.getWorld()).
                    getClass().
                    getMethod("getTileEntity", Class.forName("net.minecraft.core.BlockPosition")).
                    invoke(this.getHandleMethodWorld.
                                    invoke(sign.getWorld()),
                            Class.forName("net.minecraft.core.BlockPosition").
                                    getConstructor(double.class, double.class, double.class).
                                    newInstance(sign.getLocation().getX(),
                                            sign.getLocation().getY(),
                                            sign.getLocation().getZ()));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
        }

        if (this.setEditableField == null) try {
            this.setEditableField = Class.forName("net.minecraft.world.level.block.entity.TileEntitySign").getField("f");
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (this.gField == null) try {
            this.gField = Class.forName("net.minecraft.world.level.block.entity.TileEntitySign").getDeclaredField("g");
            this.gField.setAccessible(true);
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            this.setEditableField.set(tes, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            this.gField.set(tes, player.getUniqueId());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            tes.getClass().getMethod("a", Class.forName("net.minecraft.server.level.EntityPlayer")).invoke(tes, entityPlayer);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        player.sendSignChange(sign.getLocation(), lines);

        Object packet2 = null;
        try {
            packet2 = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor").getConstructor(Class.forName("net.minecraft.core.BlockPosition")).newInstance(this.getPositionMethod.invoke(tes));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Object connection = null;
        try {
            connection = this.playerConnectionField.get(entityPlayer);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            connection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.network.protocol.Packet")).invoke(connection, packet2);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String getVersion() {
        if (this.version == null) try {
            this.version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
        return this.version;
    }
}
