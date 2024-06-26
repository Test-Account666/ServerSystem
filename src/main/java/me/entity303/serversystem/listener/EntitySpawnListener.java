package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EntitySpawnListener implements Listener {
    protected final ServerSystem _plugin;
    private Field _collidesField;
    private Method _getHandleMethod;

    public EntitySpawnListener(ServerSystem plugin) {
        this._plugin = plugin;
        try {
            var entityLiving = Class.forName("org.bukkit.craftbukkit." + plugin.GetVersionManager().GetNMSVersion() + "entity.CraftLivingEntity");
            this._collidesField = Class.forName("net.minecraft.world.entity.EntityLiving").getDeclaredField("collides");
            this._collidesField.setAccessible(true);
        } catch (Exception ignored) {
        }
        try {
            this._getHandleMethod =
                    Class.forName("org.bukkit.craftbukkit." + plugin.GetVersionManager().GetNMSVersion() + "entity.CraftEntity").getDeclaredMethod("getHandle");
            this._getHandleMethod.setAccessible(true);
        } catch (NoSuchMethodException | ClassNotFoundException ignored) {
        }

        if (this._getHandleMethod != null && this._collidesField != null)
            for (var world : Bukkit.getWorlds())
                for (Entity entity : world.getEntities()) {
                    if (!(entity instanceof LivingEntity))
                        continue;
                    Object handle = null;
                    try {
                        handle = this._getHandleMethod.invoke(entity);
                    } catch (IllegalAccessException | InvocationTargetException exception) {
                        exception.printStackTrace();
                    }
                    try {
                        this._collidesField.set(handle, false);
                    } catch (IllegalAccessException exception) {
                        exception.printStackTrace();
                    }
                }
    }

    @EventHandler
    public void OnEntitySpawn(EntitySpawnEvent event) {
        if (this._collidesField == null || this._getHandleMethod == null) {
            HandlerList.unregisterAll(this);
            return;
        }
        if (!(event.getEntity() instanceof LivingEntity))
            return;
        try {
            var handle = this._getHandleMethod.invoke(event.getEntity());
            this._collidesField.set(handle, true);
        } catch (Exception ignored) {

        }
    }
}
