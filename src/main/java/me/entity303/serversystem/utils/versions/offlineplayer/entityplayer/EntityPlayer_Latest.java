package me.entity303.serversystem.utils.versions.offlineplayer.entityplayer;

import com.mojang.authlib.GameProfile;
import me.entity303.serversystem.main.ServerSystem;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.World;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;

public class EntityPlayer_Latest implements IEntityPlayer {
    private static Method GET_WORLD_SERVER_METHOD = null;
    protected final ServerSystem _plugin;
    private Constructor<net.minecraft.server.level.EntityPlayer> _entityPlayerConstructor = null;

    public EntityPlayer_Latest(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public Object GetEntityPlayer(OfflinePlayer offlinePlayer) {
        if (EntityPlayer_Latest.GET_WORLD_SERVER_METHOD == null)
            EntityPlayer_Latest.GET_WORLD_SERVER_METHOD = Arrays.stream(MinecraftServer.class.getDeclaredMethods())
                                                                .filter(method -> method.getParameters().length == 1)
                                                                .filter(method -> method.getParameters()[0].getType()
                                                                                                        .getName()
                                                                                                        .equalsIgnoreCase(ResourceKey.class.getName()))
                                                                .filter(method -> method.getReturnType().getName().toLowerCase(Locale.ROOT).contains("world"))
                                                                .findFirst()
                                                                .orElse(null);

        var gameProfile = new GameProfile(offlinePlayer.getUniqueId(), offlinePlayer.getName());

        var worldResourceKeyField = Arrays.stream(World.class.getDeclaredFields()).filter(field -> {
            if (!field.getType().getName().equalsIgnoreCase("net.minecraft.resources.ResourceKey"))
                return false;

            try {
                var name = field.get(null).toString();

                return name.toLowerCase().contains("overworld");
            } catch (IllegalAccessException exception) {
                return false;
            }

        }).findFirst().orElse(null);

        ResourceKey<World> worldResourceKey;
        try {
            worldResourceKey = (ResourceKey<World>) worldResourceKeyField.get(null);
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
            return null;
        }

        try {

            if (this._entityPlayerConstructor != null) {

                var lastParameter = this._entityPlayerConstructor.getParameterCount() - 1;

                if (this._entityPlayerConstructor.getParameterTypes()[lastParameter].getName().contains("ClientInformation"))
                    try {
                        var clientInformationClass = Class.forName("net.minecraft.server.level.ClientInformation");

                        var createDefaultMethod = Arrays.stream(clientInformationClass.getDeclaredMethods())
                                                        .filter(method -> method.getReturnType().getName().contains("ClientInformation"))
                                                        .findFirst()
                                                        .orElse(null);

                        return this._entityPlayerConstructor.newInstance(MinecraftServer.getServer(),
                                                                         EntityPlayer_Latest.GET_WORLD_SERVER_METHOD.invoke(MinecraftServer.getServer(),
                                                                                                                            worldResourceKey), gameProfile,
                                                                         createDefaultMethod.invoke(null));
                    } catch (ClassNotFoundException exception) {
                        exception.printStackTrace();
                        return null;
                    }

                return this._entityPlayerConstructor.newInstance(MinecraftServer.getServer(),
                                                                 EntityPlayer_Latest.GET_WORLD_SERVER_METHOD.invoke(MinecraftServer.getServer(), worldResourceKey),
                                                                 gameProfile, null);
            }

            try {
                return new net.minecraft.server.level.EntityPlayer(MinecraftServer.getServer(),
                                                                   (WorldServer) EntityPlayer_Latest.GET_WORLD_SERVER_METHOD.invoke(MinecraftServer.getServer(),
                                                                                                                                    worldResourceKey), gameProfile);
            } catch (NoSuchMethodError ignored) {
                this._entityPlayerConstructor =
                        (Constructor<net.minecraft.server.level.EntityPlayer>) net.minecraft.server.level.EntityPlayer.class.getConstructors()[0];

                var lastParameter = this._entityPlayerConstructor.getParameterCount() - 1;

                if (this._entityPlayerConstructor.getParameterTypes()[lastParameter].getName().contains("ClientInformation"))
                    try {
                        var clientInformationClass = Class.forName("net.minecraft.server.level.ClientInformation");

                        var createDefaultMethod = Arrays.stream(clientInformationClass.getDeclaredMethods())
                                                        .filter(method -> method.getReturnType().getName().contains("ClientInformation"))
                                                        .findFirst()
                                                        .orElse(null);

                        return this._entityPlayerConstructor.newInstance(MinecraftServer.getServer(),
                                                                         EntityPlayer_Latest.GET_WORLD_SERVER_METHOD.invoke(MinecraftServer.getServer(),
                                                                                                                            worldResourceKey), gameProfile,
                                                                         createDefaultMethod.invoke(null));
                    } catch (ClassNotFoundException exception) {
                        exception.printStackTrace();
                        return null;
                    }

                return this._entityPlayerConstructor.newInstance(MinecraftServer.getServer(),
                                                                 EntityPlayer_Latest.GET_WORLD_SERVER_METHOD.invoke(MinecraftServer.getServer(), worldResourceKey),
                                                                 gameProfile, null);
            }
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException exception) {
            exception.printStackTrace();
        }

        return null;
    }
}
