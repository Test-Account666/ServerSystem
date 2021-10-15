package me.Entity303.ServerSystem.Utils.versions.commands;

import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BukkitCommandWrap_Reflection_Old extends BukkitCommandWrap {
    Field bField;
    private String nmsVersion;
    private Class minecraftServerClass;
    private Method aMethod;
    private Method getServerMethod;
    private Method getCommandDispatcherMethod;
    private Method registerMethod;
    private Constructor bukkitcommandWrapperConstructor;
    private boolean informed = false;

    public BukkitCommandWrap_Reflection_Old() {
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
            this.minecraftServerClass = Class.forName("net.minecraft.server." + this.nmsVersion + ".MinecraftServer");
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

        if (this.getCommandDispatcherMethod == null) try {
            this.getCommandDispatcherMethod = this.minecraftServerClass.getDeclaredMethod("getCommandDispatcher");
            this.getCommandDispatcherMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }

        Object commandDispatcher;
        try {
            commandDispatcher = this.getCommandDispatcherMethod.invoke(minecraftServer);
        } catch (IllegalAccessException | InvocationTargetException e) {
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

        try {
            com.mojang.brigadier.tree.CommandNode.class.getDeclaredMethod("removeCommand", String.class);
        } catch (NoSuchMethodException ignored) {
            if (!this.informed)
                ss.getPlugin(ss.class).log("'removeCommand' method not found, are you using Mohist?");
            this.informed = true;
            return;
        }

        if (this.minecraftServerClass == null) try {
            this.minecraftServerClass = Class.forName("net.minecraft.server." + this.nmsVersion + ".MinecraftServer");
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

        if (this.getCommandDispatcherMethod == null) try {
            this.getCommandDispatcherMethod = this.minecraftServerClass.getDeclaredMethod("getCommandDispatcher");
            this.getCommandDispatcherMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }

        Object commandDispatcher = null;
        try {
            commandDispatcher = this.getCommandDispatcherMethod.invoke(server);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        if (this.bField == null) try {
            this.bField = Class.forName("net.minecraft.server." + this.nmsVersion + ".CommandDispatcher").getDeclaredField("b");
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















/*{
    private String version = null;
    private Subplugin subplugin = null;

    @Override
    public void wrap(Command command, String alias) {
        Object craftServer = Bukkit.getServer();
        Object server = null;
        try {
            server = Class.forName("net.minecraft.server." + this.getVersion() + ".MinecraftServer").getDeclaredMethod("getServer").invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            Bukkit.getLogger().severe("Could not find MinecraftServer!");
            e.printStackTrace();
            return;
        }

        try {
            Class.forName("org.bukkit.craftbukkit." + this.getVersion() + ".command.BukkitCommandWrapper").
                    getConstructor(
                            Class.
                                    forName("org.bukkit.craftbukkit." + this.getVersion() + ".CraftServer"),
                            Command.
                                    class).
                    newInstance(
                            craftServer,
                            command).
                    getClass().
                    getMethod(
                            "register",
                            server.
                                    getClass().
                                    getSuperclass().
                                    getMethod(
                                            "getCommandDispatcher").
                                    invoke(
                                            server).
                                    getClass().
                                    getMethod(
                                            "a").
                                    invoke(
                                            server.
                                                    getClass().
                                                    getSuperclass().
                                                    getMethod(
                                                            "getCommandDispatcher").
                                                    invoke(
                                                            server)).
                                    getClass(),
                            String.
                                    class)
                    .invoke(
                            Class.
                                    forName("org.bukkit.craftbukkit." + this.getVersion() + ".command.BukkitCommandWrapper").
                                    getConstructor(
                                            Class.
                                                    forName("org.bukkit.craftbukkit." + this.getVersion() + ".CraftServer"),
                                            Command.
                                                    class).
                                    newInstance(
                                            craftServer,
                                            command),
                            server.
                                    getClass().
                                    getSuperclass().
                                    getMethod(
                                            "getCommandDispatcher").
                                    invoke(
                                            server).
                                    getClass().
                                    getMethod(
                                            "a").
                                    invoke(
                                            server.
                                                    getClass().
                                                    getSuperclass().
                                                    getMethod(
                                                            "getCommandDispatcher").
                                                    invoke(
                                                            server)),
                            alias);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unwrap(String alias, ss plugin) {
        if (this.subplugin == null) {
            this.subplugin = new Subplugin((JavaPluginLoader) plugin.getPluginLoader()).addCommand(alias).register();
            return;
        }
        this.subplugin.addCommand(alias);
    }

    @Override
    public Subplugin getSubPlugin() {
        return this.subplugin;
    }

    private String getVersion() {
        if (this.version == null) try {
            this.version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
        return this.version;
    }

}*/
