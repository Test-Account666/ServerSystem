package me.entity303.serversystem.actionbar;

import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class IActionBar {

    public abstract Method getGetHandleMethod();

    public abstract Field getPlayerConnectionField();

    public abstract Method getSendPacketMethod();

    public abstract void sendActionBar(Player player, String message);
}
