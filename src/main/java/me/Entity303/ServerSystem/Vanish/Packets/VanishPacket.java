package me.Entity303.ServerSystem.Vanish.Packets;

import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

public abstract class VanishPacket {
    protected Constructor<?> constructor = null;

    public abstract void setVanish(Player player, boolean vanish);
}
