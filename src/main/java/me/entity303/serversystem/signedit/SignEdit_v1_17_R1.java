package me.entity303.serversystem.signedit;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SignEdit_v1_17_R1 implements ISignEdit {
    private Method _getHandleMethodPlayer;
    private Method _getHandleMethodWorld;
    private Method _getPositionMethod;
    private Field _playerConnectionField;
    private Field _setEditableField;
    private Field _gField;

    @Override
    public void EditSign(Player paramPlayer, Sign paramSign) {
        if (this._getHandleMethodPlayer == null) {
            try {
                this._getHandleMethodPlayer = paramPlayer.getClass().getDeclaredMethod("getHandle");
            } catch (NoSuchMethodException exception) {
                exception.printStackTrace();
            }
            this._getHandleMethodPlayer.setAccessible(true);
        }

        Object entityPlayer = null;

        try {
            entityPlayer = this._getHandleMethodPlayer.invoke(paramPlayer);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
        }

        if (this._getPositionMethod == null) {
            try {
                this._getPositionMethod = Class.forName("net.minecraft.world.level.block.entity.TileEntity").getMethod("getPosition");
            } catch (NoSuchMethodException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }
        }

        if (this._playerConnectionField == null) {
            try {
                this._playerConnectionField = entityPlayer.getClass().getField("b");
            } catch (NoSuchFieldException exception) {
                exception.printStackTrace();
            }
            this._playerConnectionField.setAccessible(true);
        }

        if (this._getHandleMethodWorld == null) {
            try {
                this._getHandleMethodWorld = paramSign.getWorld().getClass().getMethod("getHandle");
            } catch (NoSuchMethodException exception) {
                exception.printStackTrace();
            }
        }


        var lines = new String[4];
        for (var index = 0; index < paramSign.getLines().length; index++) {
            paramSign.getLine(index);
            lines[index] = paramSign.getLine(index).replace("§", "&");
        }
        Object tes = null;
        try {
            tes = this._getHandleMethodWorld.invoke(paramSign.getWorld())
                                            .getClass()
                                            .getMethod("getTileEntity", Class.forName("net.minecraft.core.BlockPosition"))
                                            .invoke(this._getHandleMethodWorld.invoke(paramSign.getWorld()), Class.forName("net.minecraft.core.BlockPosition")
                                                                                                                  .getConstructor(double.class, double.class,
                                                                                                                                  double.class)
                                                                                                                  .newInstance(paramSign.getLocation().getX(),
                                                                                                                               paramSign.getLocation().getY(),
                                                                                                                               paramSign.getLocation().getZ()));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | InstantiationException exception) {
            exception.printStackTrace();
        }

        if (this._setEditableField == null) {
            try {
                this._setEditableField = Class.forName("net.minecraft.world.level.block.entity.TileEntitySign").getField("f");
            } catch (NoSuchFieldException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }
        }

        if (this._gField == null) {
            try {
                this._gField = Class.forName("net.minecraft.world.level.block.entity.TileEntitySign").getDeclaredField("g");
                this._gField.setAccessible(true);
            } catch (NoSuchFieldException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }
        }

        try {
            this._setEditableField.set(tes, true);
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }

        try {
            this._gField.set(tes, paramPlayer.getUniqueId());
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }

        try {
            tes.getClass().getMethod("a", Class.forName("net.minecraft.server.level.EntityPlayer")).invoke(tes, entityPlayer);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        paramPlayer.sendSignChange(paramSign.getLocation(), lines);

        Object packet2 = null;
        try {
            packet2 = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor")
                           .getConstructor(Class.forName("net.minecraft.core.BlockPosition"))
                           .newInstance(this._getPositionMethod.invoke(tes));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        Object connection = null;
        try {
            connection = this._playerConnectionField.get(entityPlayer);
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }

        try {
            connection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.network.protocol.Packet")).invoke(connection, packet2);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }
}
