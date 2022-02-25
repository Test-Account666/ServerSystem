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

public class VanishPacket_Reflection_Latest extends VanishPacket {
    private final ServerSystem plugin;
    private String version;
    private Method getHandleMethod;
    //private Method getPlayerListNameMethod;
    private Method getProfileMethod;
    //private Method getGameModeMethod;
    private Method spigotMethod;
    private Method sendPacketMethod;
    private Field playerInteractManagerField;
    private Field playerConnectionField;
    private Field pingField;
    private Field collidesField;

    public VanishPacket_Reflection_Latest(ServerSystem plugin) {
        this.plugin = plugin;

        try {
            this.collidesField = Class.forName("net.minecraft.world.entity.EntityLiving").getDeclaredField("collides");
            this.collidesField.setAccessible(true);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void setVanish(Player player, boolean vanish) {
        if (this.getHandleMethod == null) {
            try {
                this.getHandleMethod = Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".entity.CraftPlayer").getDeclaredMethod("getHandle");
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            this.getHandleMethod.setAccessible(true);
        }

        Object entityPlayer;

        try {
            entityPlayer = this.getHandleMethod.invoke(player);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        if (this.collidesField != null) try {
            this.collidesField.set(entityPlayer, !vanish);
        } catch (Exception e) {
            e.printStackTrace();
        }
        else this.plugin.warn("CollidesField null!");

        player.setCollidable(false);

        if (this.playerConnectionField == null) {
            try {
                this.playerConnectionField = entityPlayer.getClass().getField("b");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            this.playerConnectionField.setAccessible(true);
        }

        Object playerListName = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + player.getPlayerListName() + "\"}");

        if (this.playerInteractManagerField == null) try {
            this.playerInteractManagerField = Class.forName("net.minecraft.server.level.EntityPlayer").getField("d");
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        if (this.getProfileMethod == null) try {
            this.getProfileMethod = Class.forName("net.minecraft.world.entity.player.EntityHuman").getMethod("getProfile");
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            try {
                for (Method method : Class.forName("net.minecraft.world.entity.player.EntityHuman").getDeclaredMethods())
                    if (method.getReturnType().getName().contains("GameProfile"))
                        if (method.getParameters().length <= 0) this.getProfileMethod = method;
            } catch (ClassNotFoundException ex) {
                ex.addSuppressed(e);
                ex.printStackTrace();
            }
        }

        Object profile = null;

        try {
            profile = this.getProfileMethod.invoke(entityPlayer);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        List<Object> players = new ArrayList<>();

        try {
            players.add(this.getHandleMethod.invoke(player));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        net.minecraft.server.level.EntityPlayer[] entityPlayers = new EntityPlayer[players.size()];

        for (int i = 0; i < players.size(); i++) entityPlayers[i] = (EntityPlayer) players.get(i);

        Object playerInfo = null;
        try {
            Constructor cons = null;

            for (Constructor<?> con : Class.forName("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo").getConstructors())
                if (con.getParameterCount() == 2)
                    if (con.getParameterTypes()[1] == net.minecraft.server.level.EntityPlayer[].class) cons = con;

            if (cons == null) return;

            playerInfo = cons.newInstance(Class.forName("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo$EnumPlayerInfoAction").getEnumConstants()[0], entityPlayers);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (!vanish) {
            for (Player all : Bukkit.getOnlinePlayers())
                if (all != player) {
                    Object connection = null;
                    try {
                        connection = this.playerConnectionField.get(this.getHandleMethod.invoke(all));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                        return;
                    }

                    if (this.sendPacketMethod == null)
                        this.sendPacketMethod = Arrays.stream(PlayerConnection.class.getMethods()).
                                filter(method -> method.getParameters().length == 1).
                                filter(method -> method.getParameters()[0].getType().getName().equalsIgnoreCase(Packet.class.getName())).
                                findFirst().orElse(null);

                    try {
                        this.sendPacketMethod.invoke(connection, playerInfo);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            return;
        }

        Object playerInfoData = null;

        if (this.constructor == null) {

            Class<?> clazz = null;
            try {
                clazz = this.getClass().getClassLoader().loadClass("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo$PlayerInfoData");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {
                this.constructor = clazz
                        .getDeclaredConstructor(
                                Class.forName("com.mojang.authlib.GameProfile"),
                                int.class,
                                EnumGamemode.class,
                                IChatBaseComponent.class);
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            playerInfoData = this.constructor
                    .newInstance(
                            this.getProfileMethod.invoke(entityPlayer),
                            this.getPing(player),
                            Class.forName("net.minecraft.world.level.EnumGamemode").getEnumConstants()[3],
                            playerListName);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException instantiationException) {
            instantiationException.printStackTrace();
        }

        Field b = null;
        try {
            b = playerInfo.getClass().getDeclaredField("b");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        b.setAccessible(true);

        try {
            List<Object> bList = (List<Object>) b.get(playerInfo);
            bList.clear();
            bList.add(playerInfoData);
        } catch (IllegalAccessException illegalAccessException) {
            illegalAccessException.printStackTrace();
        }

        for (Player all : Bukkit.getOnlinePlayers())
            if (all != player) {
                if (!this.plugin.getPermissions().hasPerm(all, "vanish.see", true)) continue;
                Object connection = null;
                try {
                    connection = this.playerConnectionField.get(this.getHandleMethod.invoke(all));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                if (this.sendPacketMethod == null)
                    this.sendPacketMethod = Arrays.stream(PlayerConnection.class.getMethods()).
                            filter(method -> method.getParameters().length == 1).
                            filter(method -> method.getParameters()[0].getType().getName().equalsIgnoreCase(Packet.class.getName())).
                            findFirst().orElse(null);

                this.sendPacketMethod.setAccessible(true);

                try {
                    this.sendPacketMethod.invoke(connection, playerInfo);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
    }

    private int getPing(Player player) {
        try {
            if (this.getHandleMethod == null) {
                this.getHandleMethod = player.getClass().getDeclaredMethod("getHandle");
                this.getHandleMethod.setAccessible(true);
            }
            Object entityPlayer = this.getHandleMethod.invoke(player);
            if (this.pingField == null) {
                this.pingField = entityPlayer.getClass().getDeclaredField("ping");
                this.pingField.setAccessible(true);
            }
            int ping = this.pingField.getInt(entityPlayer);

            return Math.max(ping, 0);
        } catch (Exception e) {
            return 666;
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
