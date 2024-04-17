package me.entity303.serversystem.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Teleport {

    public static void teleport(Player player, Entity entity) {
        teleport(player, entity.getLocation());
    }

    //TODO: Feature to come
    public static void teleport(Player player, Location location) {
        player.teleport(location);
    }
}
