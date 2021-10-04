package me.Entity303.ServerSystem.ActionBar;

import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class ActionBar {

    public abstract Method getGetHandleMethod();

    public abstract Field getPlayerConnectionField();

    public abstract Method getSendPacketMethod();

    public abstract void sendActionBar(Player player, String message);
}
