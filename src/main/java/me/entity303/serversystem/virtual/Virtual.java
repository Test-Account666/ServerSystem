package me.entity303.serversystem.virtual;

import me.entity303.serversystem.main.ServerSystem;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class Virtual {
    protected static Method getInventoryMethod = null;
    protected static Method sendPacketMethod = null;
    protected static Method initMenuMethod = null;
    protected static Method getBukkitViewMethod = null;
    protected static Field containerField = null;
    protected static Field playerConnectionField = null;

    protected final ServerSystem plugin;

    public Virtual() {
        this.plugin = ServerSystem.getPlugin(ServerSystem.class);
    }
}
