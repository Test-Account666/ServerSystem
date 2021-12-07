package me.Entity303.ServerSystem.Virtual.ContainerAccess;

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
    private static Method getWorldMethod = null;
    private final Player player;
    String version = null;
    private EntityPlayer human;

    public ContainerAccess_Latest(Player player) throws NoSuchMethodException {
        if (ContainerAccess_Latest.getWorldMethod == null) {
            ContainerAccess_Latest.getWorldMethod = Arrays.stream(EntityPlayer.class.getDeclaredMethods())
                    .filter(method -> method.getParameters().length <= 0)
                    .filter(method -> {
                        String[] splitted = method.getReturnType().getName().toLowerCase(Locale.ROOT).split("\\.");

                        return splitted[splitted.length - 1].contains("world");
                    })
                    .findFirst().orElse(null);

            if (ContainerAccess_Latest.getWorldMethod == null)
                throw new NoSuchMethodException("Couldn't find method 'getWorld' in class " + EntityPlayer.class.getName());
        }

        this.player = player;
        try {
            this.human = (EntityPlayer) Class.forName("org.bukkit.craftbukkit." + this.getVersion() + ".entity.CraftPlayer").getDeclaredMethod("getHandle").invoke(player);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected String getVersion() {
        if (this.version == null) try {
            this.version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
        return this.version;
    }

    @Override
    public World getWorld() {
        try {
            return (World) ContainerAccess_Latest.getWorldMethod.invoke(this.human);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public BlockPosition getPosition() {
        return new BlockPosition(this.player.getLocation().getX(), this.player.getLocation().getY(), this.player.getLocation().getZ());
    }

    @Override
    public <T> Optional<T> a(BiFunction<World, BlockPosition, T> biFunction) {
        return Optional.of(biFunction.apply(this.getWorld(), new BlockPosition(this.player.getLocation().getX(), this.player.getLocation().getY(), this.player.getLocation().getZ())));
    }
}
