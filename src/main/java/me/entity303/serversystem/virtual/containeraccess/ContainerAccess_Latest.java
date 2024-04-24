package me.entity303.serversystem.virtual.containeraccess;

import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.level.World;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;

public class ContainerAccess_Latest extends ContainerAccessWrapper implements ContainerAccess {
    private static Method GET_WORLD_METHOD = null;
    private final Player _player;
    String _version = null;
    private EntityPlayer _human;

    public ContainerAccess_Latest(Player player) throws NoSuchMethodException {
        if (ContainerAccess_Latest.GET_WORLD_METHOD == null) {
            ContainerAccess_Latest.GET_WORLD_METHOD =
                    Arrays.stream(EntityPlayer.class.getDeclaredMethods()).filter(method -> method.getParameters().length == 0).filter(method -> {
                        var splitted = method.getReturnType().getName().toLowerCase(Locale.ROOT).split("\\.");

                        return splitted[splitted.length - 1].contains("world");
                    }).findFirst().orElse(null);

            if (ContainerAccess_Latest.GET_WORLD_METHOD == null)
                throw new NoSuchMethodException("Couldn't find method 'getWorld' in class " + EntityPlayer.class.getName());
        }

        this._player = player;
        try {
            this._human = (EntityPlayer) Class.forName("org.bukkit.craftbukkit." + this.GetVersion() + ".entity.CraftPlayer")
                                              .getDeclaredMethod("getHandle")
                                              .invoke(player);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    protected String GetVersion() {
        if (this._version == null)
            try {
                this._version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            } catch (ArrayIndexOutOfBoundsException exception) {
                exception.printStackTrace();
                return null;
            }
        return this._version;
    }

    @Override
    public World getWorld() {
        try {
            return (World) ContainerAccess_Latest.GET_WORLD_METHOD.invoke(this._human);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public BlockPosition getPosition() {
        return new BlockPosition(this._player.getLocation().getBlockX(), this._player.getLocation().getBlockY(), this._player.getLocation().getBlockZ());
    }

    @Override
    public <T> Optional<T> a(BiFunction<World, BlockPosition, T> biFunction) {
        return Optional.of(biFunction.apply(this.getWorld(), new BlockPosition(this._player.getLocation().getBlockX(), this._player.getLocation().getBlockY(),
                                                                               this._player.getLocation().getBlockZ())));
    }
}
