package me.Entity303.ServerSystem.Utils.versions.offlineplayer.entityplayer;

import com.mojang.authlib.GameProfile;
import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class EntityPlayer_Old extends MessageUtils implements EntityPlayer {

    private Constructor entityPlayerConstructor;
    private Constructor playerInteractManagerConstructor;
    private Object worldServer;
    private Object world;
    private Object minecraftServer;

    public EntityPlayer_Old(ss plugin) {
        super(plugin);
    }

    @Override
    public Object getEntityPlayer(OfflinePlayer offlinePlayer) {
        GameProfile gameProfile = new GameProfile(offlinePlayer.getUniqueId(), offlinePlayer.getName());
        Object entityPlayer = null;

        if (this.entityPlayerConstructor == null) try {
            this.entityPlayerConstructor = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".EntityPlayer").getConstructors()[0];
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (this.playerInteractManagerConstructor == null) try {
            this.playerInteractManagerConstructor = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".PlayerInteractManager").getConstructors()[0];
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (this.minecraftServer == null) try {
            this.minecraftServer = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".MinecraftServer").getDeclaredMethod("getServer").invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (this.worldServer == null) try {
            this.worldServer = ((Iterable) Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".MinecraftServer").getDeclaredMethod("getWorlds").invoke(this.minecraftServer)).iterator().next();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchMethodError | ClassNotFoundException e) {
            if (e instanceof NoSuchMethodException || e instanceof NoSuchMethodError) try {
                this.worldServer = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".MinecraftServer").getDeclaredMethod("getWorldServer", int.class).invoke(this.minecraftServer, 0);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            else
                e.printStackTrace();
        }

        if (this.world == null) try {
            this.world = Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".CraftWorld").getDeclaredMethod("getHandle").invoke(Bukkit.getWorlds().get(0));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            entityPlayer = this.entityPlayerConstructor.newInstance(this.minecraftServer, this.worldServer, gameProfile, this.playerInteractManagerConstructor.newInstance(this.world));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return entityPlayer;
    }
}
