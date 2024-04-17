package me.entity303.serversystem.utils.versions.offlineplayer.data;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
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

public class SaveData_Latest extends CommandUtils implements SaveData {

    private static PlayerList playerList = null;
    private static Method saveDataMethod = null;
    private static Method loadMethod = null;
    private static Method hasKeyOfTypeMethod = null;
    private static Method getCompoundMethod = null;
    private static Method setMethod = null;
    private static Field worldNbtField = null;

    public SaveData_Latest(ServerSystem plugin) {
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
            if (SaveData_Latest.playerList == null)
                try {
                    var method = MinecraftServer.class.getDeclaredMethod("getPlayerList");
                    method.setAccessible(true);
                    SaveData_Latest.playerList = (PlayerList) method.invoke(MinecraftServer.getServer());
                } catch (NoSuchMethodException | NoSuchMethodError e) {
                    var field = Arrays.stream(MinecraftServer.class.getDeclaredFields())
                                      .filter(field1 -> field1.getType().getName().equalsIgnoreCase(PlayerList.class.getName()))
                                      .findFirst()
                                      .orElse(null);
                    if (field == null) {
                        e.printStackTrace();
                        return;
                    }
                    field.setAccessible(true);
                    SaveData_Latest.playerList = (PlayerList) field.get(MinecraftServer.getServer());
                }

            if (SaveData_Latest.worldNbtField == null) {
                SaveData_Latest.worldNbtField = Arrays.stream(PlayerList.class.getDeclaredFields())
                                                      .filter(field -> field.getType().getName().contains(WorldNBTStorage.class.getName()))
                                                      .findFirst()
                                                      .orElse(null);

                if (SaveData_Latest.worldNbtField == null)
                    try {
                        throw new NoSuchFieldException("Couldn't find field 'worldNbt' in class " + PlayerList.class.getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                return;

            }

            var worldNBTStorage = (WorldNBTStorage) SaveData_Latest.worldNbtField.get(SaveData_Latest.playerList);

            if (SaveData_Latest.saveDataMethod == null) {
                SaveData_Latest.saveDataMethod = Arrays.stream(Entity.class.getDeclaredMethods())
                                                       .filter(method -> method.getReturnType().getName().equalsIgnoreCase(NBTTagCompound.class.getName()))
                                                       .findFirst()
                                                       .orElse(null);
                if (SaveData_Latest.saveDataMethod == null) {
                    try {
                        throw new NoSuchMethodException("Couldn't find method 'saveData' in class " + Entity.class.getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }

                SaveData_Latest.saveDataMethod.setAccessible(true);
            }

            var playerData = (NBTTagCompound) SaveData_Latest.saveDataMethod.invoke(entityPlayer, new NBTTagCompound());
            if (!player.isOnline()) {

                if (SaveData_Latest.loadMethod == null) {
                    SaveData_Latest.loadMethod = Arrays.stream(WorldNBTStorage.class.getDeclaredMethods())
                                                       .filter(method -> method.getReturnType().getName().equalsIgnoreCase(NBTTagCompound.class.getName()))
                                                       .filter(method -> method.getParameters().length == 1)
                                                       .filter(method -> method.getParameters()[0].getType()
                                                                                                  .getName()
                                                                                                  .equalsIgnoreCase(EntityHuman.class.getName()))
                                                       .findFirst()
                                                       .orElse(null);
                    if (SaveData_Latest.loadMethod == null) {
                        try {
                            throw new NoSuchMethodException("Couldn't find method 'load' in class " + WorldNBTStorage.class.getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }

                    SaveData_Latest.loadMethod.setAccessible(true);
                }

                var oldData = (NBTTagCompound) SaveData_Latest.loadMethod.invoke(worldNBTStorage, entityPlayer);

                if (SaveData_Latest.hasKeyOfTypeMethod == null) {
                    SaveData_Latest.hasKeyOfTypeMethod = Arrays.stream(NBTTagCompound.class.getDeclaredMethods())
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

                    if (SaveData_Latest.hasKeyOfTypeMethod == null) {
                        try {
                            throw new NoSuchMethodException("Couldn't find method 'hasKeyOfType' in class " + NBTTagCompound.class.getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }

                    SaveData_Latest.hasKeyOfTypeMethod.setAccessible(true);
                }

                if (SaveData_Latest.getCompoundMethod == null) {
                    SaveData_Latest.getCompoundMethod = Arrays.stream(NBTTagCompound.class.getDeclaredMethods())
                                                              .filter(method -> method.getReturnType().getName().equalsIgnoreCase(NBTTagCompound.class.getName()))
                                                              .filter(method -> method.getParameters().length == 1)
                                                              .filter(method -> method.getParameters()[0].getType()
                                                                                                         .getName()
                                                                                                         .toLowerCase(Locale.ROOT)
                                                                                                         .contains("string"))
                                                              .findFirst()
                                                              .orElse(null);

                    if (SaveData_Latest.getCompoundMethod == null) {
                        try {
                            throw new NoSuchMethodException("Couldn't find method 'getCompoun' in class " + NBTTagCompound.class.getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }

                    SaveData_Latest.getCompoundMethod.setAccessible(true);
                }


                if (SaveData_Latest.setMethod == null) {
                    SaveData_Latest.setMethod = Arrays.stream(NBTTagCompound.class.getDeclaredMethods())
                                                      .filter(method -> method.getReturnType().getName().equalsIgnoreCase(NBTBase.class.getName()))
                                                      .filter(method -> method.getParameters().length == 2)
                                                      .filter(method -> method.getParameters()[0].getType().getName().toLowerCase(Locale.ROOT).contains("string"))
                                                      .filter(method -> method.getParameters()[1].getType().getName().equalsIgnoreCase(NBTBase.class.getName()))
                                                      .findFirst()
                                                      .orElse(null);

                    if (SaveData_Latest.setMethod == null) {
                        try {
                            throw new NoSuchMethodException("Couldn't find method 'set' in class " + NBTTagCompound.class.getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }

                    SaveData_Latest.setMethod.setAccessible(true);
                }


                if (oldData != null && (boolean) SaveData_Latest.hasKeyOfTypeMethod.invoke(oldData, "RootVehicle", 10))
                    SaveData_Latest.setMethod.invoke(playerData, "RootVehicle", SaveData_Latest.getCompoundMethod.invoke(oldData, "RootVehicle"));
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
