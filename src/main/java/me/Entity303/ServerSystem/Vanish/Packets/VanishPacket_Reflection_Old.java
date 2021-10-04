package me.Entity303.ServerSystem.Vanish.Packets;

import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class VanishPacket_Reflection_Old extends VanishPacket {
    private final ss plugin;
    private String version;
    private Method getHandleMethod;
    private Method getPlayerListNameMethod;
    private Method getProfileMethod;
    private Method getGameModeMethod;
    private Field playerInteractManagerField;
    private Field playerConnectionField;
    private Field pingField;
    private Field collidesField;
    private Field collidesWithEntitiesField;

    public VanishPacket_Reflection_Old(ss plugin) {
        this.plugin = plugin;
        try {
            this.collidesField = Class.forName("net.minecraft.server." + plugin.getVersionManager().getNMSVersion() + ".EntityLiving").getDeclaredField("collides");
            this.collidesField.setAccessible(true);
        } catch (Exception ignored) {
        }

        try {
            this.collidesWithEntitiesField = Class.forName("net.minecraft.server." + plugin.getVersionManager().getNMSVersion() + ".EntityPlayer").getDeclaredField("collidesWithEntities");
            this.collidesWithEntitiesField.setAccessible(true);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void setVanish(Player player, boolean vanish) {
        if (this.getHandleMethod == null) {
            try {
                this.getHandleMethod = player.getClass().getDeclaredMethod("getHandle");
            } catch (NoSuchMethodException e) {
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
        } catch (Exception ignored) {
        }
        else if (this.collidesWithEntitiesField != null) try {
            this.collidesWithEntitiesField.set(entityPlayer, !vanish);
        } catch (Exception ignored) {
        }

        if (this.playerConnectionField == null) {
            try {
                this.playerConnectionField = entityPlayer.getClass().getField("playerConnection");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            this.playerConnectionField.setAccessible(true);
        }

        if (this.getPlayerListNameMethod == null) try {
            this.getPlayerListNameMethod = Class.forName("net.minecraft.server." + this.getVersion() + ".EntityPlayer").getMethod("getPlayerListName");
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (this.playerInteractManagerField == null) try {
            this.playerInteractManagerField = Class.forName("net.minecraft.server." + this.getVersion() + ".EntityPlayer").getField("playerInteractManager");
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        if (this.getGameModeMethod == null) try {
            this.getGameModeMethod = Class.forName("net.minecraft.server." + this.getVersion() + ".PlayerInteractManager").getMethod("getGameMode");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        if (this.getProfileMethod == null) try {
            this.getProfileMethod = Class.forName("net.minecraft.server." + this.getVersion() + ".EntityPlayer").getMethod("getProfile");
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
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


        Object playerInfo = null;
        try {
            Constructor cons = null;

            for (Constructor<?> con : Class.forName("net.minecraft.server." + this.getVersion() + ".PacketPlayOutPlayerInfo").getConstructors())
                if (con.getParameterCount() == 2) if (con.getParameterTypes()[1] == Iterable.class) cons = con;

            if (cons == null) return;

            playerInfo = cons.newInstance(Class.forName("net.minecraft.server." + this.getVersion() + ".PacketPlayOutPlayerInfo$EnumPlayerInfoAction").getEnumConstants()[1], players);
        } catch (ClassNotFoundException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
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
                    }

                    try {
                        connection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + this.getVersion() + ".Packet")).invoke(connection, playerInfo);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            return;
        }

        Object playerInfoData = null;

        if (this.constructor == null) try {
            try {
                this.constructor = Class.forName("net.minecraft.server." + this.getVersion() + ".PacketPlayOutPlayerInfo$PlayerInfoData")
                        .getDeclaredConstructor(Class.forName("net.minecraft.server." + this.getVersion() + ".PacketPlayOutPlayerInfo"),
                                profile
                                        .getClass(),
                                int
                                        .class,
                                Class.forName("net.minecraft.server." + this.getVersion() + ".WorldSettings$EnumGamemode"),
                                Class.forName("net.minecraft.server." + this.getVersion() + ".IChatBaseComponent"));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }

        try {
            playerInfoData = this.constructor
                    .newInstance(
                            playerInfo,
                            profile,
                            this.getPing(player),
                            Class.forName("net.minecraft.server." + this.getVersion() + ".WorldSettings$EnumGamemode").getEnumConstants()[4],
                            this.getPlayerListNameMethod.
                                    invoke(entityPlayer));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException instantiationException) {
            instantiationException.printStackTrace();
        }

        Field b = null;
        try {
            b = Class.forName("net.minecraft.server." + this.getVersion() + ".PacketPlayOutPlayerInfo").getDeclaredField("b");
        } catch (NoSuchFieldException | ClassNotFoundException e) {
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

                try {
                    connection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + this.getVersion() + ".Packet")).invoke(connection, playerInfo);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
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
