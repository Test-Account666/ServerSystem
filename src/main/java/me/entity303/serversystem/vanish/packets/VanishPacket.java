package me.entity303.serversystem.vanish.packets;

import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

public abstract class VanishPacket {
    protected Constructor<?> constructor = null;

    public abstract void setVanish(Player player, boolean vanish);
}
