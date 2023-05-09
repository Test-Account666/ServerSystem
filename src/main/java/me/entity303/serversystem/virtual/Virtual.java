package me.entity303.serversystem.virtual;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class Virtual {
    protected static Method getInventoryMethod = null;
    protected static Method sendPacketMethod = null;
    protected static Method initMenuMethod = null;
    protected static Method getBukkitViewMethod = null;
    protected static Field containerField = null;
}
