package me.entity303.serversystem.utils.versions.offlineplayer.data;

import me.entity303.serversystem.main.ServerSystem;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.storage.WorldNBTStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Locale;

public class SaveData_Latest implements ISaveData {

    private static PlayerList PLAYER_LIST = null;
    private static Method SAVE_DATA_METHOD = null;
    private static Method LOAD_METHOD = null;
    private static Method HAS_KEY_OF_TYPE_METHOD = null;
    private static Method GET_COMPOUND_METHOD = null;
    private static Method SET_METHOD = null;
    private static Field WORLD_NBT_FIELD = null;
    protected final ServerSystem _plugin;

    public SaveData_Latest(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public void SaveData(Player player) {
        EntityPlayer entityPlayer;
        try {
            entityPlayer = (EntityPlayer) this._plugin.GetVersionStuff().GetGetHandleMethod().invoke(player);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
            return;
        }

        try {
            if (SaveData_Latest.PLAYER_LIST == null)
                try {
                    var method = MinecraftServer.class.getDeclaredMethod("getPlayerList");
                    method.setAccessible(true);
                    SaveData_Latest.PLAYER_LIST = (PlayerList) method.invoke(MinecraftServer.getServer());
                } catch (NoSuchMethodException | NoSuchMethodError exception) {
                    var field = Arrays.stream(MinecraftServer.class.getDeclaredFields())
                                      .filter(field1 -> field1.getType().getName().equalsIgnoreCase(PlayerList.class.getName()))
                                      .findFirst()
                                      .orElse(null);
                    if (field == null) {
                        exception.printStackTrace();
                        return;
                    }
                    field.setAccessible(true);
                    SaveData_Latest.PLAYER_LIST = (PlayerList) field.get(MinecraftServer.getServer());
                }

            if (SaveData_Latest.WORLD_NBT_FIELD == null) {
                SaveData_Latest.WORLD_NBT_FIELD = Arrays.stream(PlayerList.class.getDeclaredFields())
                                                        .filter(field -> field.getType().getName().contains(WorldNBTStorage.class.getName()))
                                                        .findFirst()
                                                        .orElse(null);

                if (SaveData_Latest.WORLD_NBT_FIELD == null)
                    try {
                        throw new NoSuchFieldException("Couldn't find field 'worldNbt' in class " + PlayerList.class.getName());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                return;

            }

            var worldNBTStorage = (WorldNBTStorage) SaveData_Latest.WORLD_NBT_FIELD.get(SaveData_Latest.PLAYER_LIST);

            if (SaveData_Latest.SAVE_DATA_METHOD == null) {
                SaveData_Latest.SAVE_DATA_METHOD = Arrays.stream(Entity.class.getDeclaredMethods())
                                                         .filter(method -> method.getReturnType().getName().equalsIgnoreCase(NBTTagCompound.class.getName()))
                                                         .findFirst()
                                                         .orElse(null);
                if (SaveData_Latest.SAVE_DATA_METHOD == null) {
                    try {
                        throw new NoSuchMethodException("Couldn't find method 'saveData' in class " + Entity.class.getName());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    return;
                }

                SaveData_Latest.SAVE_DATA_METHOD.setAccessible(true);
            }

            var playerData = (NBTTagCompound) SaveData_Latest.SAVE_DATA_METHOD.invoke(entityPlayer, new NBTTagCompound());
            if (!player.isOnline()) {

                if (SaveData_Latest.LOAD_METHOD == null) {
                    SaveData_Latest.LOAD_METHOD = Arrays.stream(WorldNBTStorage.class.getDeclaredMethods())
                                                        .filter(method -> method.getReturnType().getName().equalsIgnoreCase(NBTTagCompound.class.getName()))
                                                        .filter(method -> method.getParameters().length == 1)
                                                        .filter(method -> method.getParameters()[0].getType()
                                                                                                  .getName()
                                                                                                  .equalsIgnoreCase(EntityHuman.class.getName()))
                                                        .findFirst()
                                                        .orElse(null);
                    if (SaveData_Latest.LOAD_METHOD == null) {
                        try {
                            throw new NoSuchMethodException("Couldn't find method 'load' in class " + WorldNBTStorage.class.getName());
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        return;
                    }

                    SaveData_Latest.LOAD_METHOD.setAccessible(true);
                }

                var oldData = (NBTTagCompound) SaveData_Latest.LOAD_METHOD.invoke(worldNBTStorage, entityPlayer);

                if (SaveData_Latest.HAS_KEY_OF_TYPE_METHOD == null) {
                    SaveData_Latest.HAS_KEY_OF_TYPE_METHOD = Arrays.stream(NBTTagCompound.class.getDeclaredMethods())
                                                                   .filter(method -> method.getReturnType().getName().toLowerCase(Locale.ROOT).contains("boolean"))
                                                                   .filter(method -> method.getParameters().length == 2)
                                                                   .filter(method -> method.getParameters()[0].getType()
                                                                                                          .getName()
                                                                                                          .toLowerCase(Locale.ROOT)
                                                                                                          .contains("string"))
                                                                   .filter(method -> method.getParameters()[1].getType()
                                                                                                          .getName()
                                                                                                          .toLowerCase(Locale.ROOT)
                                                                                                          .contains("int"))
                                                                   .findFirst()
                                                                   .orElse(null);

                    if (SaveData_Latest.HAS_KEY_OF_TYPE_METHOD == null) {
                        try {
                            throw new NoSuchMethodException("Couldn't find method 'hasKeyOfType' in class " + NBTTagCompound.class.getName());
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        return;
                    }

                    SaveData_Latest.HAS_KEY_OF_TYPE_METHOD.setAccessible(true);
                }

                if (SaveData_Latest.GET_COMPOUND_METHOD == null) {
                    SaveData_Latest.GET_COMPOUND_METHOD = Arrays.stream(NBTTagCompound.class.getDeclaredMethods())
                                                                .filter(method -> method.getReturnType().getName().equalsIgnoreCase(NBTTagCompound.class.getName()))
                                                                .filter(method -> method.getParameters().length == 1)
                                                                .filter(method -> method.getParameters()[0].getType()
                                                                                                         .getName()
                                                                                                         .toLowerCase(Locale.ROOT)
                                                                                                         .contains("string"))
                                                                .findFirst()
                                                                .orElse(null);

                    if (SaveData_Latest.GET_COMPOUND_METHOD == null) {
                        try {
                            throw new NoSuchMethodException("Couldn't find method 'getCompoun' in class " + NBTTagCompound.class.getName());
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        return;
                    }

                    SaveData_Latest.GET_COMPOUND_METHOD.setAccessible(true);
                }


                if (SaveData_Latest.SET_METHOD == null) {
                    SaveData_Latest.SET_METHOD = Arrays.stream(NBTTagCompound.class.getDeclaredMethods())
                                                       .filter(method -> method.getReturnType().getName().equalsIgnoreCase(NBTBase.class.getName()))
                                                       .filter(method -> method.getParameters().length == 2)
                                                       .filter(method -> method.getParameters()[0].getType().getName().toLowerCase(Locale.ROOT).contains("string"))
                                                       .filter(method -> method.getParameters()[1].getType().getName().equalsIgnoreCase(NBTBase.class.getName()))
                                                       .findFirst()
                                                       .orElse(null);

                    if (SaveData_Latest.SET_METHOD == null) {
                        try {
                            throw new NoSuchMethodException("Couldn't find method 'set' in class " + NBTTagCompound.class.getName());
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        return;
                    }

                    SaveData_Latest.SET_METHOD.setAccessible(true);
                }


                if (oldData != null && (boolean) SaveData_Latest.HAS_KEY_OF_TYPE_METHOD.invoke(oldData, "RootVehicle", 10))
                    SaveData_Latest.SET_METHOD.invoke(playerData, "RootVehicle", SaveData_Latest.GET_COMPOUND_METHOD.invoke(oldData, "RootVehicle"));
            }

            var file = new File(worldNBTStorage.getPlayerDir(), player.getUniqueId() + ".dat.tmp");
            var file1 = new File(worldNBTStorage.getPlayerDir(), player.getUniqueId() + ".dat");
            NBTCompressedStreamTools.a(playerData, Files.newOutputStream(file.toPath()));
            if (file1.exists() && !file1.delete() || !file.renameTo(file1))
                Bukkit.getLogger().severe("Failed to save player data for " + player.getDisplayName());
        } catch (Exception var5) {
            var5.printStackTrace();
            Bukkit.getLogger().severe("Failed to save player data for " + player.getDisplayName());
        }
    }
}
