package me.entity303.serversystem.virtual;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.virtual.containeraccess.ContainerAccessWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class AbstractVirtual {
    protected static Method GET_INVENTORY_METHOD = null;
    protected static Method SEND_PACKET_METHOD = null;
    protected static Method INIT_MENU_METHOD = null;
    protected static Method GET_BUKKIT_VIEW_METHOD = null;
    protected static Field CONTAINER_FIELD = null;
    protected static Field PLAYER_CONNECTION_FIELD = null;
    protected final ServerSystem _plugin;
    protected String _version = null;

    public AbstractVirtual() {
        this._plugin = ServerSystem.getPlugin(ServerSystem.class);
    }

    protected ContainerAccessWrapper GetWrapper(Player player) {
        try {
            var clazz = Class.forName("me.entity303.serversystem.virtual.containeraccess.ContainerAccess_" + this.GetVersion());
            return (ContainerAccessWrapper) clazz.getConstructor(Player.class).newInstance(player);
        } catch (ClassNotFoundException ignored) {
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException exception) {
            exception.printStackTrace();
        }
        try {
            var clazz = Class.forName("me.entity303.serversystem.virtual.containeraccess.ContainerAccess_Latest");
            return (ContainerAccessWrapper) clazz.getConstructor(Player.class).newInstance(player);
        } catch (ClassNotFoundException ignored) {
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException exception) {
            exception.printStackTrace();
        }
        return null;
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
}
