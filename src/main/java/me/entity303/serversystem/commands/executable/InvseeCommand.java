package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import me.entity303.serversystem.utils.Morpher;
import me.entity303.serversystem.utils.interceptors.invsee.InvseeGetItemInterceptor;
import me.entity303.serversystem.utils.interceptors.invsee.InvseeSetItemInterceptor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.matcher.ElementMatchers;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class InvseeCommand extends MessageUtils implements CommandExecutor, Listener {
    private final HashMap<Player, PlayerInventory> cachedCustomInventories = new HashMap<>();
    private Class playerInventoryNmsClass = null;
    private Constructor craftInventoryPlayerConstructor = null;
    private boolean onceFired = false;
    private Method getInventoryMethod = null;
    private Method getHandleMethod = null;
    private Method getInventoryMethodNew = null;

    public InvseeCommand(ServerSystem plugin) {
        super(plugin);

        this.plugin.getEventManager().re(this);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (this.onceFired || !this.plugin.isAdvancedInvsee()) return this.onCommand0(cs, cmd, label, args);

        if (!this.isAllowed(cs, "invsee.use")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("invsee.use")));
            return true;
        }

        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }

        if (args.length == 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("Invsee", label, cmd.getName(), cs, null));
            return true;
        }

        Player targetPlayer = this.getPlayer(cs, args[0]);
        if (targetPlayer == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }

        if (this.playerInventoryNmsClass == null) try {
            this.playerInventoryNmsClass = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".PlayerInventory");
        } catch (ClassNotFoundException e) {
            if (this.playerInventoryNmsClass == null) try {
                this.playerInventoryNmsClass = Class.forName("net.minecraft.world.entity.player.PlayerInventory");
            } catch (ClassNotFoundException ex) {
                ex.addSuppressed(e);
                ex.printStackTrace();
                this.onceFired = true;
                return this.onCommand0(cs, cmd, label, args);
            }
        }

        if (this.getInventoryMethod == null) try {
            this.getInventoryMethod = Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".inventory.CraftInventoryPlayer").getDeclaredMethod("getInventory");
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            this.onceFired = true;
            return this.onCommand0(cs, cmd, label, args);
        }

        if (this.getHandleMethod == null) try {
            this.getHandleMethod = Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".entity.CraftPlayer").getDeclaredMethod("getHandle");
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            this.onceFired = true;
            return this.onCommand0(cs, cmd, label, args);
        }

        if (this.craftInventoryPlayerConstructor == null) try {
            this.craftInventoryPlayerConstructor = Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".inventory.CraftInventoryPlayer").getConstructors()[0];
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            this.onceFired = true;
            return this.onCommand0(cs, cmd, label, args);
        }

        PlayerInventory playerInventory = null;
        if (this.cachedCustomInventories.containsKey(targetPlayer))
            playerInventory = this.cachedCustomInventories.get(targetPlayer);
        else
            playerInventory = this.createCustomInventory(targetPlayer, cs);

        if (playerInventory == null) return this.onCommand0(cs, cmd, label, args);

        ((Player) cs).openInventory(playerInventory);

        PlayerInventory finalPlayerInventory = playerInventory;
        AtomicInteger taskId = new AtomicInteger(0);
        taskId.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            if (((Player) cs).getOpenInventory().getTopInventory() == finalPlayerInventory)
                ((Player) cs).updateInventory();
            else Bukkit.getScheduler().cancelTask(taskId.get());
        }, 10L, 10L));
        return true;
    }

    private boolean onCommand0(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "invsee.use")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("invsee.use")));
            return true;
        }
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }
        if (args.length == 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("Invsee", label, cmd.getName(), cs, null));
            return true;
        }
        Player targetPlayer = this.getPlayer(cs, args[0]);
        if (targetPlayer == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }
        ((Player) cs).openInventory(targetPlayer.getInventory());
        return true;
    }

    public PlayerInventory createCustomInventory(Player targetPlayer, CommandSender cs) {
        Object handle = null;
        try {
            handle = this.getHandleMethod.invoke(cs);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }


        Object customPlayerInventory = null;

        try {
            this.playerInventoryNmsClass.getDeclaredMethod("getItem", int.class);
        } catch (NoSuchMethodException | NoSuchMethodError e) {
            Method getSizeMethod = null;
            for (Method method : this.playerInventoryNmsClass.getDeclaredMethods())
                if (method.getReturnType().getName().equalsIgnoreCase(int.class.getName()))
                    if (method.getParameters().length <= 0)
                        try {
                            if ((int) method.invoke(this.getInventoryMethod.invoke(targetPlayer.getInventory())) == 41) {
                                getSizeMethod = method;
                                break;
                            }
                        } catch (IllegalAccessException | InvocationTargetException ex) {
                            ex.printStackTrace();
                        }


            Method getItemMethod = null;
            for (Method method : this.playerInventoryNmsClass.getDeclaredMethods())
                if (method.getParameters().length == 1)
                    if (method.getParameters()[0].getType().getName().equalsIgnoreCase(int.class.getName()))
                        if (method.getReturnType().getName().toLowerCase(Locale.ROOT).contains("itemstack"))
                            if (method.getName().equalsIgnoreCase("a")) {
                                getItemMethod = method;
                                break;
                            }

            Method setItemMethod = null;
            for (Method method : this.playerInventoryNmsClass.getDeclaredMethods())
                if (method.getReturnType().getName().equalsIgnoreCase(void.class.getName()))
                    if (method.getParameters().length == 2)
                        if (method.getParameters()[0].getType().getName().equalsIgnoreCase(int.class.getName()))
                            if (method.getParameters()[1].getType().getName().toLowerCase(Locale.ROOT).contains("itemstack")) {
                                setItemMethod = method;
                                break;
                            }

            customPlayerInventory = this.createNew(handle, setItemMethod, getItemMethod, getSizeMethod, targetPlayer, cs);
        }

        if (customPlayerInventory == null)
            customPlayerInventory = this.createNormal(handle, targetPlayer, cs);


        PlayerInventory playerInventory = null;
        try {
            playerInventory = (PlayerInventory) this.craftInventoryPlayerConstructor.newInstance(customPlayerInventory);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }

        return playerInventory;
    }

    private Object createNew(Object handle, Method setItemMethod, Method getItemMethod, Method getSizeMethod, Player targetPlayer, CommandSender cs) {
        Object customPlayerInventory;
        try {
            customPlayerInventory = new ByteBuddy()
                    .subclass(this.playerInventoryNmsClass)

                    .method(ElementMatchers.isDeclaredBy(this.playerInventoryNmsClass)
                            .and(ElementMatchers.named("setItem"))
                            .or(ElementMatchers.is(setItemMethod)))
                    .intercept(MethodDelegation.withDefaultConfiguration().
                            withBinders(Morph.Binder.install(Morpher.class)).
                            to(new InvseeSetItemInterceptor(this.plugin, targetPlayer, (Player) cs)))

                    .method((ElementMatchers.isDeclaredBy(this.playerInventoryNmsClass)
                            .and(ElementMatchers.named("getItem")))
                            .or(ElementMatchers.is(getItemMethod)))
                    .intercept(MethodDelegation.withDefaultConfiguration().
                            withBinders(Morph.Binder.install(Morpher.class)).
                            to(new InvseeGetItemInterceptor(this.plugin, targetPlayer)))

                    .method(ElementMatchers.isDeclaredBy(this.playerInventoryNmsClass)
                            .and(ElementMatchers.named("getOwner")))
                    .intercept(FixedValue.value(cs))

                    .method((ElementMatchers.isDeclaredBy(this.playerInventoryNmsClass))
                            .and(ElementMatchers.named("getSize")))
                    .intercept(FixedValue.value(45))

                    .method(ElementMatchers.is(getSizeMethod))
                    .intercept(FixedValue.value(45))

                    .method(ElementMatchers.isDeclaredBy(this.playerInventoryNmsClass)

                            .and(ElementMatchers.not(ElementMatchers.named("getSize")))
                            .and(ElementMatchers.not(ElementMatchers.is(getSizeMethod)))
                            .and(ElementMatchers.not(ElementMatchers.named("getOwner")))

                            .and(ElementMatchers.not(ElementMatchers.named("setItem")))
                            .and(ElementMatchers.not(ElementMatchers.is(setItemMethod)))
                            .and(ElementMatchers.not(ElementMatchers.named("getItem")))
                            .and(ElementMatchers.not(ElementMatchers.is(getItemMethod))))
                    .intercept(MethodDelegation.to(this.getInventoryMethod.invoke(targetPlayer.getInventory())))

                    .make()
                    .load(this.getClass().getClassLoader())
                    .getLoaded()
                    .getConstructors()[0]
                    .newInstance(handle);

            return customPlayerInventory;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            this.onceFired = true;
            return null;
        }
    }

    private Object createNormal(Object handle, Player targetPlayer, CommandSender cs) {
        Object customPlayerInventory;
        try {
            customPlayerInventory = new ByteBuddy()
                    .subclass(this.playerInventoryNmsClass)

                    .method(ElementMatchers.isDeclaredBy(this.playerInventoryNmsClass)
                            .and(ElementMatchers.named("setItem")))
                    .intercept(MethodDelegation.withDefaultConfiguration().
                            withBinders(Morph.Binder.install(Morpher.class)).
                            to(new InvseeSetItemInterceptor(this.plugin, targetPlayer, (Player) cs)))

                    .method((ElementMatchers.isDeclaredBy(this.playerInventoryNmsClass)
                            .and(ElementMatchers.named("getItem"))))
                    .intercept(MethodDelegation.withDefaultConfiguration().
                            withBinders(Morph.Binder.install(Morpher.class)).
                            to(new InvseeGetItemInterceptor(this.plugin, targetPlayer)))

                    .method(ElementMatchers.isDeclaredBy(this.playerInventoryNmsClass)
                            .and(ElementMatchers.named("getOwner")))
                    .intercept(FixedValue.value(cs))

                    .method((ElementMatchers.isDeclaredBy(this.playerInventoryNmsClass))
                            .and(ElementMatchers.named("getSize")))
                    .intercept(FixedValue.value(45))

                    .method(ElementMatchers.isDeclaredBy(this.playerInventoryNmsClass)

                            .and(ElementMatchers.not(ElementMatchers.named("getSize")))
                            .and(ElementMatchers.not(ElementMatchers.named("getOwner")))

                            .and(ElementMatchers.not(ElementMatchers.named("setItem")))
                            .and(ElementMatchers.not(ElementMatchers.named("getItem"))))
                    .intercept(MethodDelegation.to(this.getInventoryMethod.invoke(targetPlayer.getInventory())))

                    .make()
                    .load(this.getClass().getClassLoader())
                    .getLoaded()
                    .getConstructors()[0]
                    .newInstance(handle);

            return customPlayerInventory;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            this.onceFired = true;
            return null;
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        this.cachedCustomInventories.remove(e.getPlayer());
    }
}