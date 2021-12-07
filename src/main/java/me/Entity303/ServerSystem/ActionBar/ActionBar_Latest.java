package me.Entity303.ServerSystem.ActionBar;

import me.Entity303.ServerSystem.Utils.ChatColor;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class ActionBar_Latest extends ActionBar {
    private final String nmsVersion;
    private Method getHandleMethod = null;
    private Method sendPacketMethod = null;
    private Field playerConnectionField = null;

    public ActionBar_Latest(String nmsVersion) {
        this.nmsVersion = nmsVersion;
    }

    @Override
    public Method getGetHandleMethod() {
        return this.getHandleMethod;
    }

    @Override
    public Field getPlayerConnectionField() {
        return this.playerConnectionField;
    }

    @Override
    public Method getSendPacketMethod() {
        return this.sendPacketMethod;
    }

    @Override
    public void sendActionBar(Player player, String message) {
        PacketPlayOutChat playOutChat;

        String s = ChatColor.translateAlternateColorCodes('&', message);
        IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + s +
                "\"}");

        playOutChat = new PacketPlayOutChat(icbc, ChatMessageType.a((byte) 2), UUID.randomUUID());

        if (this.getHandleMethod == null) try {
            this.getHandleMethod = player.getClass().getDeclaredMethod("getHandle");
            this.getHandleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        Object entityPlayer = null;
        try {
            entityPlayer = this.getHandleMethod.invoke(player);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        if (entityPlayer == null) return;

        if (this.playerConnectionField == null) try {
            this.playerConnectionField = entityPlayer.getClass().getDeclaredField("b");
            this.playerConnectionField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        Object playerConnection = null;
        try {
            playerConnection = this.playerConnectionField.get(entityPlayer);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (this.sendPacketMethod == null) try {
            this.sendPacketMethod = playerConnection.getClass().getDeclaredMethod("sendPacket", Class.forName("net.minecraft.network.protocol.Packet"));
            this.sendPacketMethod.setAccessible(true);
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            this.sendPacketMethod.invoke(playerConnection, playOutChat);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
