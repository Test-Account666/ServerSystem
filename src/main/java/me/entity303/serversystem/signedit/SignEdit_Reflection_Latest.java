package me.entity303.serversystem.signedit;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class SignEdit_Reflection_Latest implements ISignEdit {
    private Method _getHandleMethodPlayer;
    private Method _getHandleMethodWorld;
    private Method _getPositionMethod;
    private Method _sendPacketMethod;
    private Method _getTileEntityMethod;
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

        if (this._getPositionMethod == null)
            try {
                this._getPositionMethod = Arrays.stream(Class.forName("net.minecraft.world.level.block.entity.TileEntity").getMethods())
                                                .filter(method -> method.getParameters().length == 0)
                                                .filter(method -> method.getReturnType().getName().equalsIgnoreCase(BlockPosition.class.getName()))
                                                .findFirst()
                                                .orElse(null);
                if (this._getPositionMethod == null)
                    throw new NoSuchMethodException("Could not find 'getPosition' method!");
            } catch (NoSuchMethodException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }

        if (this._playerConnectionField == null) {
            try {
                this._playerConnectionField = Arrays.stream(entityPlayer.getClass().getFields())
                                                    .filter(field -> field.getType().getName().equalsIgnoreCase(PlayerConnection.class.getName()))
                                                    .findFirst()
                                                    .orElse(null);
                if (this._playerConnectionField == null)
                    throw new NoSuchFieldException("Couldn't find 'playerConnection' field!");
            } catch (NoSuchFieldException exception) {
                exception.printStackTrace();
            }
            this._playerConnectionField.setAccessible(true);
        }

        if (this._getHandleMethodWorld == null)
            try {
                this._getHandleMethodWorld = paramSign.getWorld().getClass().getMethod("getHandle");
            } catch (NoSuchMethodException exception) {
                exception.printStackTrace();
            }


        var lines = new String[4];
        for (var index = 0; index < paramSign.getLines().length; index++) {
            paramSign.getLine(index);
            lines[index] = paramSign.getLine(index).replace("§", "&");
        }

        if (this._getTileEntityMethod == null)
            try {
                this._getTileEntityMethod = this._getHandleMethodWorld.invoke(paramSign.getWorld())
                                                                      .getClass()
                                                                      .getMethod("getTileEntity", Class.forName("net.minecraft.core.BlockPosition"));
            } catch (NoSuchMethodException | NoSuchMethodError | IllegalAccessException | InvocationTargetException | ClassNotFoundException exception) {
                if (exception instanceof NoSuchMethodException || exception instanceof NoSuchMethodError)
                    try {
                        this._getTileEntityMethod = this._getHandleMethodWorld.invoke(paramSign.getWorld())
                                                                              .getClass()
                                                                              .getMethod("getBlockEntity",
                                                                                         Class.forName("net.minecraft.core.BlockPosition"), boolean.class);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException exception1) {
                        exception1.printStackTrace();
                    }
                else
                    exception.printStackTrace();
            }

        Object tes = null;
        try {
            tes = this._getTileEntityMethod.getParameterCount() == 2?
                  this._getTileEntityMethod.invoke(this._getHandleMethodWorld.invoke(paramSign.getWorld()), Class.forName("net.minecraft.core.BlockPosition")
                                                                                                                 .getConstructor(double.class, double.class,
                                                                                                                            double.class)
                                                                                                                 .newInstance(paramSign.getLocation().getX(),
                                                                                                                              paramSign.getLocation().getY(),
                                                                                                                              paramSign.getLocation().getZ()), false)

                                                                    :

                  this._getTileEntityMethod.invoke(this._getHandleMethodWorld.invoke(paramSign.getWorld()), Class.forName("net.minecraft.core.BlockPosition")
                                                                                                                 .getConstructor(double.class, double.class,
                                                                                                                            double.class)
                                                                                                                 .newInstance(paramSign.getLocation().getX(),
                                                                                                                              paramSign.getLocation().getY(),
                                                                                                                              paramSign.getLocation().getZ()));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | InstantiationException exception) {
            exception.printStackTrace();
        }

        if (this._setEditableField == null)
            try {
                this._setEditableField = Class.forName("net.minecraft.world.level.block.entity.TileEntitySign").getField("f");
            } catch (NoSuchFieldException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }

        if (this._gField == null)
            try {
                this._gField = Class.forName("net.minecraft.world.level.block.entity.TileEntitySign").getDeclaredField("g");
                this._gField.setAccessible(true);
            } catch (NoSuchFieldException | ClassNotFoundException exception) {
                exception.printStackTrace();
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

        if (this._sendPacketMethod == null)
            this._sendPacketMethod = Arrays.stream(PlayerConnection.class.getMethods())
                                           .filter(method -> method.getParameters().length == 1)
                                           .filter(method -> method.getParameters()[0].getType().getName().equalsIgnoreCase(Packet.class.getName()))
                                           .findFirst()
                                           .orElse(null);

        try {
            this._sendPacketMethod.invoke(connection, packet2);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
        }
    }
}
