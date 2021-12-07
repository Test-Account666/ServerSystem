package me.Entity303.ServerSystem.SignEdit;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class SignEdit_Reflection_Latest implements SignEdit {
    private String version;
    private Method getHandleMethodPlayer;
    private Method getHandleMethodWorld;
    private Method getPositionMethod;
    private Method sendPacketMethod;
    private Method getTileEntityMethod;
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
            this.getPositionMethod = Arrays.stream(Class.forName("net.minecraft.world.level.block.entity.TileEntity").getMethods()).filter(method -> method.getParameters().length == 0).filter(method -> method.getReturnType().getName().equalsIgnoreCase(BlockPosition.class.getName())).findFirst().orElse(null);
            if (this.getPositionMethod == null) throw new NoSuchMethodException("Could not find 'getPosition' method!");
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (this.playerConnectionField == null) {
            try {
                this.playerConnectionField = Arrays.stream(entityPlayer.getClass().getFields()).filter(field -> field.getType().getName().equalsIgnoreCase(PlayerConnection.class.getName())).findFirst().orElse(null);
                if (this.playerConnectionField == null)
                    throw new NoSuchFieldException("Couldn't find 'playerConnection' field!");
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

        if (this.getTileEntityMethod == null) try {
            this.getTileEntityMethod = this.getHandleMethodWorld.
                    invoke(sign.getWorld()).
                    getClass().
                    getMethod("getTileEntity", Class.forName("net.minecraft.core.BlockPosition"));
        } catch (NoSuchMethodException | NoSuchMethodError | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            if (e instanceof NoSuchMethodException || e instanceof NoSuchMethodError) try {
                this.getTileEntityMethod = this.getHandleMethodWorld.
                        invoke(sign.getWorld()).
                        getClass().
                        getMethod("getBlockEntity", Class.forName("net.minecraft.core.BlockPosition"), boolean.class);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            else
                e.printStackTrace();
        }

        Object tes = null;
        try {
            tes =
                    this.getTileEntityMethod.getParameterCount() == 2 ?
                            this.getTileEntityMethod.invoke(this.getHandleMethodWorld.
                                            invoke(sign.getWorld()),
                                    Class.forName("net.minecraft.core.BlockPosition").
                                            getConstructor(double.class, double.class, double.class).
                                            newInstance(sign.getLocation().getX(),
                                                    sign.getLocation().getY(),
                                                    sign.getLocation().getZ()), false)

                            :

                            this.getTileEntityMethod.invoke(this.getHandleMethodWorld.
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

        if (this.sendPacketMethod == null)
            this.sendPacketMethod = Arrays.stream(PlayerConnection.class.getMethods()).
                    filter(method -> method.getParameters().length == 1).
                    filter(method -> method.getParameters()[0].getType().getName().equalsIgnoreCase(Packet.class.getName())).
                    findFirst().orElse(null);

        try {
            this.sendPacketMethod.invoke(connection, packet2);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
