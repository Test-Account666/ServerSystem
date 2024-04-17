package me.entity303.serversystem.virtual.grindstone;

import me.entity303.serversystem.virtual.Virtual;
import me.entity303.serversystem.virtual.containeraccess.ContainerAccessWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public abstract class VirtualGrindstone extends Virtual {

    String version = null;

    public abstract void openGrind(Player player);

    protected ContainerAccessWrapper getWrapper(Player player) {
        try {
            var clazz = Class.forName("me.entity303.serversystem.virtual.containeraccess.ContainerAccess_" + this.getVersion());
            return (ContainerAccessWrapper) clazz.getConstructor(Player.class).newInstance(player);
        } catch (ClassNotFoundException ignored) {
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            var clazz = Class.forName("me.entity303.serversystem.virtual.containeraccess.ContainerAccess_Latest");
            return (ContainerAccessWrapper) clazz.getConstructor(Player.class).newInstance(player);
        } catch (ClassNotFoundException ignored) {
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected String getVersion() {
        if (this.version == null)
            try {
                this.version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                return null;
            }
        return this.version;
    }
}
