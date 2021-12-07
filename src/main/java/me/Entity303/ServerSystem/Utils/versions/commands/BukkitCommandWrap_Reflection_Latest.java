package me.Entity303.ServerSystem.Utils.versions.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BukkitCommandWrap_Reflection_Latest extends BukkitCommandWrap {
    Field bField;
    private String nmsVersion;
    private Class minecraftServerClass;
    private Method aMethod;
    private Method getServerMethod;
    private Field vanillaCommandDispatcherField;
    private Method registerMethod;
    private Constructor bukkitcommandWrapperConstructor;

    public BukkitCommandWrap_Reflection_Latest() {
        try {
            this.nmsVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            this.nmsVersion = null;
        }
    }

    @Override
    public void wrap(Command command, String alias) {
        if (this.nmsVersion == null) return;
        if (this.minecraftServerClass == null) try {
            this.minecraftServerClass = Class.forName("net.minecraft.server.MinecraftServer");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (this.getServerMethod == null) try {
            this.getServerMethod = this.minecraftServerClass.getMethod("getServer");
            this.getServerMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }

        Object minecraftServer;
        try {
            minecraftServer = this.getServerMethod.invoke(this.minecraftServerClass);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        if (this.vanillaCommandDispatcherField == null) try {
            this.vanillaCommandDispatcherField = this.minecraftServerClass.getDeclaredField("vanillaCommandDispatcher");
            this.vanillaCommandDispatcherField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return;
        }

        Object commandDispatcher;
        try {
            commandDispatcher = this.vanillaCommandDispatcherField.get(minecraftServer);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        if (this.aMethod == null) try {
            this.aMethod = commandDispatcher.getClass().getDeclaredMethod("a");
            this.aMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }

        if (this.bukkitcommandWrapperConstructor == null) try {
            this.bukkitcommandWrapperConstructor = Class.forName("org.bukkit.craftbukkit." + this.nmsVersion + ".command.BukkitCommandWrapper").getDeclaredConstructor(Class.forName("org.bukkit.craftbukkit." + this.nmsVersion + ".CraftServer"), Command.class);
            this.bukkitcommandWrapperConstructor.setAccessible(true);
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        Object commandWrapper;

        try {
            commandWrapper = this.bukkitcommandWrapperConstructor.newInstance(Bukkit.getServer(), command);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        Object a;

        try {
            a = this.aMethod.invoke(commandDispatcher);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        if (this.registerMethod == null) try {
            this.registerMethod = Class.forName("org.bukkit.craftbukkit." + this.nmsVersion + ".command.BukkitCommandWrapper").getMethod("register", com.mojang.brigadier.CommandDispatcher.class, String.class);
            this.registerMethod.setAccessible(true);
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try {
            this.registerMethod.invoke(commandWrapper, a, alias);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unwrap(String command) {
        if (this.nmsVersion == null) return;
        if (this.minecraftServerClass == null) try {
            this.minecraftServerClass = Class.forName("net.minecraft.server.MinecraftServer");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        if (this.getServerMethod == null) try {
            this.getServerMethod = this.minecraftServerClass.getMethod("getServer");
            this.getServerMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }

        Object server;

        try {
            server = this.getServerMethod.invoke(this.minecraftServerClass);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        if (this.vanillaCommandDispatcherField == null) try {
            this.vanillaCommandDispatcherField = this.minecraftServerClass.getDeclaredField("vanillaCommandDispatcher");
            this.vanillaCommandDispatcherField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return;
        }

        Object commandDispatcher = null;
        try {
            commandDispatcher = this.vanillaCommandDispatcherField.get(server);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        if (this.bField == null) try {
            this.bField = Class.forName("net.minecraft.commands.CommandDispatcher").getDeclaredField("g");
            this.bField.setAccessible(true);
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        com.mojang.brigadier.CommandDispatcher b;
        try {
            b = (com.mojang.brigadier.CommandDispatcher) this.bField.get(commandDispatcher);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        b.getRoot().removeCommand(command);
    }
}
