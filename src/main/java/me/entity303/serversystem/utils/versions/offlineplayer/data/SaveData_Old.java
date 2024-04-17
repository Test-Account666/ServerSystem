package me.entity303.serversystem.utils.versions.offlineplayer.data;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;

public class SaveData_Old extends CommandUtils implements SaveData {

    private Object worldNBTStorage = null;
    private Method getPlayerDirMethod = null;
    private Method loadMethod = null;
    private Method hasKeyOfTypeMethod = null;
    private Method getCompoundMethod = null;
    private Method setMethod = null;
    private Method aMethod = null;
    private Method saveMethod = null;
    private Constructor NBTTagCompoundConstructor = null;

    public SaveData_Old(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public void saveData(Player player) {
        Object entityPlayer;
        try {
            entityPlayer = this.plugin.getVersionStuff().getGetHandleMethod().invoke(player);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        try {
            if (this.worldNBTStorage == null) {
                var minecraftServer = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".MinecraftServer")
                                           .getDeclaredMethod("getServer")
                                           .invoke(null);
                var playerList = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".MinecraftServer")
                                      .getDeclaredMethod("getPlayerList")
                                      .invoke(minecraftServer);
                this.worldNBTStorage = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".PlayerList")
                                            .getDeclaredField("playerFileData")
                                            .get(playerList);
            }

            if (this.saveMethod == null)
                try {
                    this.saveMethod = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".Entity")
                                           .getDeclaredMethod("save", Class.forName(
                                                   "net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".NBTTagCompound"));
                } catch (NoSuchMethodError | NoSuchMethodException e) {
                    this.saveMethod = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".Entity")
                                           .getDeclaredMethod("e", Class.forName(
                                                   "net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".NBTTagCompound"));
                }

            if (this.NBTTagCompoundConstructor == null)
                this.NBTTagCompoundConstructor =
                        Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".NBTTagCompound").getDeclaredConstructor();

            var playerData = this.NBTTagCompoundConstructor.newInstance();
            this.saveMethod.invoke(entityPlayer, playerData);

            if (!player.isOnline()) {
                if (this.loadMethod == null)
                    this.loadMethod = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".WorldNBTStorage")
                                           .getDeclaredMethod("load", Class.forName(
                                                   "net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".EntityHuman"));

                var oldData = this.loadMethod.invoke(this.worldNBTStorage, entityPlayer);

                if (this.hasKeyOfTypeMethod == null)
                    this.hasKeyOfTypeMethod = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".NBTTagCompound")
                                                   .getDeclaredMethod("hasKeyOfType", String.class, int.class);

                if (this.getCompoundMethod == null)
                    this.getCompoundMethod = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".NBTTagCompound")
                                                  .getDeclaredMethod("getCompound", String.class);

                if (this.setMethod == null)
                    this.setMethod = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".NBTTagCompound")
                                          .getDeclaredMethod("set", String.class, Class.forName(
                                                  "net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".NBTBase"));

                if (oldData != null && ((boolean) this.hasKeyOfTypeMethod.invoke(oldData, "RootVehicle", 10)))
                    this.setMethod.invoke(playerData, "RootVehicle", this.getCompoundMethod.invoke(oldData, "RootVehicle"));
            }

            if (this.getPlayerDirMethod == null)
                this.getPlayerDirMethod = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".WorldNBTStorage")
                                               .getDeclaredMethod("getPlayerDir");

            var playerDir = (File) this.getPlayerDirMethod.invoke(this.worldNBTStorage);

            var file = new File(playerDir, player.getUniqueId() + ".dat.tmp");
            var file1 = new File(playerDir, player.getUniqueId() + ".dat");

            if (this.aMethod == null)
                this.aMethod = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".NBTCompressedStreamTools")
                                    .getDeclaredMethod("a", Class.forName(
                                            "net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".NBTTagCompound"), OutputStream.class);

            this.aMethod.invoke(null, playerData, Files.newOutputStream(file.toPath()));

            if (file1.exists() && !file1.delete() || !file.renameTo(file1))
                Bukkit.getLogger().severe("Failed to save player data for " + player.getDisplayName());
        } catch (Exception var5) {
            var5.printStackTrace();
            Bukkit.getLogger().severe("Failed to save player data for " + player.getDisplayName());
        }
    }
}
