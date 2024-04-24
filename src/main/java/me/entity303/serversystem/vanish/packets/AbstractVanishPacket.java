package me.entity303.serversystem.vanish.packets;

import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

public abstract class AbstractVanishPacket {
    protected Constructor<?> _constructor = null;

    public abstract void SetVanish(Player player, boolean vanish);
}
