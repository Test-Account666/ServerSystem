package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
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

public class EntitySpawnListener extends CommandUtils implements Listener {
    private Field collidesField;
    private Method getHandleMethod;

    public EntitySpawnListener(ServerSystem plugin) {
        super(plugin);
        try {
            var entityLiving = Class.forName("org.bukkit.craftbukkit." + plugin.getVersionManager().getNMSVersion() + ".entity.CraftLivingEntity");
            this.collidesField = Class.forName("net.minecraft.world.entity.EntityLiving").getDeclaredField("collides");
            this.collidesField.setAccessible(true);
        } catch (Exception ignored) {
        }
        try {
            this.getHandleMethod =
                    Class.forName("org.bukkit.craftbukkit." + plugin.getVersionManager().getNMSVersion() + ".entity.CraftEntity").getDeclaredMethod("getHandle");
            this.getHandleMethod.setAccessible(true);
        } catch (NoSuchMethodException | ClassNotFoundException ignored) {
        }

        if (this.getHandleMethod != null && this.collidesField != null)
            for (var world : Bukkit.getWorlds())
                for (Entity entity : world.getEntities()) {
                    if (!(entity instanceof LivingEntity))
                        continue;
                    Object handle = null;
                    try {
                        handle = this.getHandleMethod.invoke(entity);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    try {
                        this.collidesField.set(handle, false);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) {
        if (this.collidesField == null || this.getHandleMethod == null) {
            HandlerList.unregisterAll(this);
            return;
        }
        if (!(e.getEntity() instanceof LivingEntity))
            return;
        try {
            var handle = this.getHandleMethod.invoke(e.getEntity());
            this.collidesField.set(handle, true);
        } catch (Exception ignored) {

        }
    }
}
