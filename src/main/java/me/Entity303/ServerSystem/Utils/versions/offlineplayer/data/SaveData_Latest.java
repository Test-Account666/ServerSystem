package me.Entity303.ServerSystem.Utils.versions.offlineplayer.data;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.storage.WorldNBTStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;

public class SaveData_Latest extends MessageUtils implements SaveData {

    public SaveData_Latest(ss plugin) {
        super(plugin);
    }

    @Override
    public void saveData(Player player) {
        EntityPlayer entityPlayer;
        try {
            entityPlayer = (EntityPlayer) this.plugin.getVersionStuff().getGetHandleMethod().invoke(player);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        try {
            WorldNBTStorage worldNBTStorage = entityPlayer.getMinecraftServer().getPlayerList().r;
            NBTTagCompound playerData = entityPlayer.save(new NBTTagCompound());
            if (!player.isOnline()) {
                NBTTagCompound oldData = worldNBTStorage.load(entityPlayer);
                if (oldData != null && oldData.hasKeyOfType("RootVehicle", 10))
                    playerData.set("RootVehicle", oldData.getCompound("RootVehicle"));
            }

            File file = new File(worldNBTStorage.getPlayerDir(), entityPlayer.getUniqueIDString() + ".dat.tmp");
            File file1 = new File(worldNBTStorage.getPlayerDir(), entityPlayer.getUniqueIDString() + ".dat");
            NBTCompressedStreamTools.a(playerData, new FileOutputStream(file));
            if (file1.exists() && !file1.delete() || !file.renameTo(file1))
                Bukkit.getLogger().severe("Failed to save player data for " + entityPlayer.getDisplayName().getString());
        } catch (Exception var5) {
            Bukkit.getLogger().severe("Failed to save player data for " + entityPlayer.getDisplayName().getString());
        }
    }
}
