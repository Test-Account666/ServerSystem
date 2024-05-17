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

public class SaveData_Old implements ISaveData {

    protected final ServerSystem _plugin;
    private Object _worldNBTStorage = null;
    private Method _getPlayerDirectoryMethod = null;
    private Method _loadMethod = null;
    private Method _hasKeyOfTypeMethod = null;
    private Method _getCompoundMethod = null;
    private Method _setMethod = null;
    private Method _aMethod = null;
    private Method _saveMethod = null;
    private Constructor _nbtTagCompoundConstructor = null;

    public SaveData_Old(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public void SaveData(Player player) {
        Object entityPlayer;
        try {
            entityPlayer = this._plugin.GetVersionStuff().GetGetHandleMethod().invoke(player);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
            return;
        }

        try {
            if (this._worldNBTStorage == null) {
                var minecraftServer = Class.forName("net.minecraft.server." + this._plugin.GetVersionManager().GetNMSVersion() + "MinecraftServer")
                                           .getDeclaredMethod("getServer")
                                           .invoke(null);
                var playerList = Class.forName("net.minecraft.server." + this._plugin.GetVersionManager().GetNMSVersion() + "MinecraftServer")
                                      .getDeclaredMethod("getPlayerList")
                                      .invoke(minecraftServer);
                this._worldNBTStorage = Class.forName("net.minecraft.server." + this._plugin.GetVersionManager().GetNMSVersion() + "PlayerList")
                                             .getDeclaredField("playerFileData")
                                             .get(playerList);
            }

            if (this._saveMethod == null)
                try {
                    this._saveMethod = Class.forName("net.minecraft.server." + this._plugin.GetVersionManager().GetNMSVersion() + "Entity")
                                            .getDeclaredMethod("save", Class.forName(
                                                   "net.minecraft.server." + this._plugin.GetVersionManager().GetNMSVersion() + "NBTTagCompound"));
                } catch (NoSuchMethodError | NoSuchMethodException exception) {
                    this._saveMethod = Class.forName("net.minecraft.server." + this._plugin.GetVersionManager().GetNMSVersion() + "Entity")
                                            .getDeclaredMethod("e", Class.forName(
                                                   "net.minecraft.server." + this._plugin.GetVersionManager().GetNMSVersion() + "NBTTagCompound"));
                }

            if (this._nbtTagCompoundConstructor == null)
                this._nbtTagCompoundConstructor =
                        Class.forName("net.minecraft.server." + this._plugin.GetVersionManager().GetNMSVersion() + "NBTTagCompound").getDeclaredConstructor();

            var playerData = this._nbtTagCompoundConstructor.newInstance();
            this._saveMethod.invoke(entityPlayer, playerData);

            if (!player.isOnline()) {
                if (this._loadMethod == null)
                    this._loadMethod = Class.forName("net.minecraft.server." + this._plugin.GetVersionManager().GetNMSVersion() + "WorldNBTStorage")
                                            .getDeclaredMethod("load", Class.forName(
                                                   "net.minecraft.server." + this._plugin.GetVersionManager().GetNMSVersion() + "EntityHuman"));

                var oldData = this._loadMethod.invoke(this._worldNBTStorage, entityPlayer);

                if (this._hasKeyOfTypeMethod == null)
                    this._hasKeyOfTypeMethod = Class.forName("net.minecraft.server." + this._plugin.GetVersionManager().GetNMSVersion() + "NBTTagCompound")
                                                    .getDeclaredMethod("hasKeyOfType", String.class, int.class);

                if (this._getCompoundMethod == null)
                    this._getCompoundMethod = Class.forName("net.minecraft.server." + this._plugin.GetVersionManager().GetNMSVersion() + "NBTTagCompound")
                                                   .getDeclaredMethod("getCompound", String.class);

                if (this._setMethod == null)
                    this._setMethod = Class.forName("net.minecraft.server." + this._plugin.GetVersionManager().GetNMSVersion() + "NBTTagCompound")
                                           .getDeclaredMethod("set", String.class, Class.forName(
                                                  "net.minecraft.server." + this._plugin.GetVersionManager().GetNMSVersion() + "NBTBase"));

                if (oldData != null && ((boolean) this._hasKeyOfTypeMethod.invoke(oldData, "RootVehicle", 10)))
                    this._setMethod.invoke(playerData, "RootVehicle", this._getCompoundMethod.invoke(oldData, "RootVehicle"));
            }

            if (this._getPlayerDirectoryMethod == null)
                this._getPlayerDirectoryMethod = Class.forName("net.minecraft.server." + this._plugin.GetVersionManager().GetNMSVersion() + "WorldNBTStorage")
                                                      .getDeclaredMethod("getPlayerDir");

            var playerDir = (File) this._getPlayerDirectoryMethod.invoke(this._worldNBTStorage);

            var file = new File(playerDir, player.getUniqueId() + ".dat.tmp");
            var file1 = new File(playerDir, player.getUniqueId() + ".dat");

            if (this._aMethod == null)
                this._aMethod = Class.forName("net.minecraft.server." + this._plugin.GetVersionManager().GetNMSVersion() + "NBTCompressedStreamTools")
                                     .getDeclaredMethod("a", Class.forName(
                                            "net.minecraft.server." + this._plugin.GetVersionManager().GetNMSVersion() + "NBTTagCompound"), OutputStream.class);

            this._aMethod.invoke(null, playerData, Files.newOutputStream(file.toPath()));

            if (file1.exists() && !file1.delete() || !file.renameTo(file1))
                Bukkit.getLogger().severe("Failed to save player data for " + player.getDisplayName());
        } catch (Exception var5) {
            var5.printStackTrace();
            Bukkit.getLogger().severe("Failed to save player data for " + player.getDisplayName());
        }
    }
}
