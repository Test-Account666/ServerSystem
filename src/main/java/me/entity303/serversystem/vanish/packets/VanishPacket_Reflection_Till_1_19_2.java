package me.entity303.serversystem.vanish.packets;

import me.entity303.serversystem.main.ServerSystem;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.level.EnumGamemode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VanishPacket_Reflection_Till_1_19_2 extends AbstractVanishPacket {
    private final ServerSystem _plugin;
    private Method _getHandleMethod;
    private Method _getProfileMethod;
    private Method _spigotMethod;
    private Method _sendPacketMethod;
    private Field _playerInteractManagerField;
    private Field _playerConnectionField;
    private Field _pingField;
    private Field _collidesField;
    private boolean _v19 = false;

    public VanishPacket_Reflection_Till_1_19_2(ServerSystem plugin) {
        this._plugin = plugin;

        try {
            this._collidesField = Class.forName("net.minecraft.world.entity.EntityLiving").getDeclaredField("collides");
            this._collidesField.setAccessible(true);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void SetVanish(Player player, boolean vanish) {
        if (this._getHandleMethod == null) {
            try {
                this._getHandleMethod = player.getClass().getDeclaredMethod("getHandle");
            } catch (NoSuchMethodException exception) {
                exception.printStackTrace();
            }
            this._getHandleMethod.setAccessible(true);
        }

        Object entityPlayer;

        try {
            entityPlayer = this._getHandleMethod.invoke(player);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
            return;
        }

        if (this._collidesField != null) {
            try {
                this._collidesField.set(entityPlayer, !vanish);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else {
            this._plugin.Warn("CollidesField null!");
        }

        player.setCollidable(false);

        if (this._playerConnectionField == null) {
            try {
                this._playerConnectionField = entityPlayer.getClass().getField("b");
            } catch (NoSuchFieldException exception) {
                exception.printStackTrace();
            }
            this._playerConnectionField.setAccessible(true);
        }

        Object playerListName = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + player.getPlayerListName() + "\"}");

        if (this._playerInteractManagerField == null) {
            try {
                this._playerInteractManagerField = Class.forName("net.minecraft.server.level.EntityPlayer").getField("d");
            } catch (ClassNotFoundException | NoSuchFieldException exception) {
                exception.printStackTrace();
            }
        }

        if (this._getProfileMethod == null) {
            try {
                this._getProfileMethod = Class.forName("net.minecraft.world.entity.player.EntityHuman").getMethod("getProfile");
            } catch (NoSuchMethodException | ClassNotFoundException exception) {
                try {
                    for (var method : Class.forName("net.minecraft.world.entity.player.EntityHuman").getDeclaredMethods())
                        if (method.getReturnType().getName().contains("GameProfile")) if (method.getParameters().length == 0) this._getProfileMethod = method;
                } catch (ClassNotFoundException exception1) {
                    exception1.addSuppressed(exception);
                    exception1.printStackTrace();
                }
            }
        }

        Object profile = null;

        try {
            this._getProfileMethod.invoke(entityPlayer);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
        }

        List<Object> players = new ArrayList<>();

        try {
            players.add(this._getHandleMethod.invoke(player));
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
        }

        var entityPlayers = new EntityPlayer[players.size()];

        for (var index = 0; index < players.size(); index++)
            entityPlayers[index] = (EntityPlayer) players.get(index);

        Object playerInfo = null;
        try {
            Constructor cons = null;

            for (var con : Class.forName("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo").getConstructors())
                if (con.getParameterCount() == 2) if (con.getParameterTypes()[1] == net.minecraft.server.level.EntityPlayer[].class) cons = con;

            if (cons == null) return;

            playerInfo = cons.newInstance(Class.forName("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo$EnumPlayerInfoAction").getEnumConstants()[0],
                                          entityPlayers);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        if (!vanish) {
            for (var all : Bukkit.getOnlinePlayers())
                if (all != player) {
                    Object connection;
                    try {
                        connection = this._playerConnectionField.get(this._getHandleMethod.invoke(all));
                    } catch (IllegalAccessException | InvocationTargetException exception) {
                        exception.printStackTrace();
                        return;
                    }

                    if (this._sendPacketMethod == null) {
                        this._sendPacketMethod = Arrays.stream(PlayerConnection.class.getMethods())
                                                       .filter(method -> method.getParameters().length == 1)
                                                       .filter(method -> method.getParameters()[0].getType().getName().equalsIgnoreCase(Packet.class.getName()))
                                                       .findFirst()
                                                       .orElse(null);
                    }

                    try {
                        this._sendPacketMethod.invoke(connection, playerInfo);
                    } catch (IllegalAccessException | InvocationTargetException exception) {
                        exception.printStackTrace();
                    }
                }
            return;
        }

        Object playerInfoData = null;

        if (this._constructor == null) {

            Class<?> clazz = null;
            try {
                clazz = this.getClass().getClassLoader().loadClass("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo$PlayerInfoData");
            } catch (ClassNotFoundException exception) {
                exception.printStackTrace();
            }

            try {
                this._constructor =
                        clazz.getDeclaredConstructor(Class.forName("com.mojang.authlib.GameProfile"), int.class, EnumGamemode.class, IChatBaseComponent.class);
            } catch (NoSuchMethodException | ClassNotFoundException exception) {
                if (!(exception instanceof NoSuchMethodException)) {
                    exception.printStackTrace();
                    return;
                }
                this._constructor = clazz.getDeclaredConstructors(/*
                                Class.forName("com.mojang.authlib.GameProfile"),
                                int.class,
                                EnumGamemode.class,
                                IChatBaseComponent.class*/)[0];
                this._v19 = true;
            }
        }

        if (!this._v19) {
            try {
                playerInfoData = this._constructor.newInstance(this._getProfileMethod.invoke(entityPlayer), this.GetPing(player),
                                                               Class.forName("net.minecraft.world.level.EnumGamemode").getEnumConstants()[3], playerListName);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException instantiationException) {
                instantiationException.printStackTrace();
            }
        } else {
            try {
                playerInfoData = this._constructor.newInstance(this._getProfileMethod.invoke(entityPlayer), this.GetPing(player),
                                                               Class.forName("net.minecraft.world.level.EnumGamemode").getEnumConstants()[3], playerListName, null);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException instantiationException) {
                instantiationException.printStackTrace();
            }
        }

        Field bField = null;
        try {
            bField = playerInfo.getClass().getDeclaredField("b");
        } catch (NoSuchFieldException exception) {
            exception.printStackTrace();
        }

        bField.setAccessible(true);

        try {
            var bList = (List<Object>) bField.get(playerInfo);
            bList.clear();
            bList.add(playerInfoData);
        } catch (IllegalAccessException illegalAccessException) {
            illegalAccessException.printStackTrace();
        }

        for (var all : Bukkit.getOnlinePlayers())
            if (all != player) {
                if (!this._plugin.GetPermissions().HasPermission(all, "vanish.see", true)) continue;
                Object connection = null;
                try {
                    connection = this._playerConnectionField.get(this._getHandleMethod.invoke(all));
                } catch (IllegalAccessException | InvocationTargetException exception) {
                    exception.printStackTrace();
                }

                if (this._sendPacketMethod == null) {
                    this._sendPacketMethod = Arrays.stream(PlayerConnection.class.getMethods())
                                                   .filter(method -> method.getParameters().length == 1)
                                                   .filter(method -> method.getParameters()[0].getType().getName().equalsIgnoreCase(Packet.class.getName()))
                                                   .findFirst()
                                                   .orElse(null);
                }

                this._sendPacketMethod.setAccessible(true);

                try {
                    this._sendPacketMethod.invoke(connection, playerInfo);
                } catch (IllegalAccessException | InvocationTargetException exception) {
                    exception.printStackTrace();
                }
            }
    }

    private int GetPing(Player player) {
        try {
            if (this._getHandleMethod == null) {
                this._getHandleMethod = player.getClass().getDeclaredMethod("getHandle");
                this._getHandleMethod.setAccessible(true);
            }
            var entityPlayer = this._getHandleMethod.invoke(player);
            if (this._pingField == null) {
                this._pingField = entityPlayer.getClass().getDeclaredField("ping");
                this._pingField.setAccessible(true);
            }
            var ping = this._pingField.getInt(entityPlayer);

            return Math.max(ping, 0);
        } catch (Exception exception) {
            return 666;
        }
    }
}
