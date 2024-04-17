package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class InvseeCommand extends CommandUtils implements CommandExecutorOverload, Listener {
    private final HashMap<Player, PlayerInventory> cachedCustomInventories = new HashMap<>();
    private final Method getInventoryMethodNew = null;
    private Class playerInventoryNmsClass = null;
    private Constructor craftInventoryPlayerConstructor = null;
    private boolean onceFired = false;
    private Method getInventoryMethod = null;
    private Method getHandleMethod = null;

    public InvseeCommand(ServerSystem plugin) {
        super(plugin);

        this.plugin.getEventManager().registerEvent(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this.onceFired || !this.plugin.isAdvancedInvsee())
            return this.onCommand0(commandSender, command, commandLabel, arguments);

        if (!this.plugin.getPermissions().hasPermission(commandSender, "invsee.use")) {
            var permission = this.plugin.getPermissions().getPermission("invsee.use");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (arguments.length == 0) {

            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Invsee"));
            return true;
        }

        var targetPlayer = this.getPlayer(commandSender, arguments[0]);
        if (targetPlayer == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }

        if (this.playerInventoryNmsClass == null)
            try {
                this.playerInventoryNmsClass = Class.forName("net.minecraft.server." + this.plugin.getVersionManager().getNMSVersion() + ".PlayerInventory");
            } catch (ClassNotFoundException e) {
                if (this.playerInventoryNmsClass == null)
                    try {
                        this.playerInventoryNmsClass = Class.forName("net.minecraft.world.entity.player.PlayerInventory");
                    } catch (ClassNotFoundException ex) {
                        ex.addSuppressed(e);
                        ex.printStackTrace();
                        this.onceFired = true;
                        return this.onCommand0(commandSender, command, commandLabel, arguments);
                    }
            }

        if (this.getInventoryMethod == null)
            try {
                this.getInventoryMethod =
                        Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".inventory.CraftInventoryPlayer")
                             .getDeclaredMethod("getInventory");
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
                this.onceFired = true;
                return this.onCommand0(commandSender, command, commandLabel, arguments);
            }

        if (this.getHandleMethod == null)
            try {
                this.getHandleMethod = Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".entity.CraftPlayer")
                                            .getDeclaredMethod("getHandle");
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
                this.onceFired = true;
                return this.onCommand0(commandSender, command, commandLabel, arguments);
            }

        if (this.craftInventoryPlayerConstructor == null)
            try {
                this.craftInventoryPlayerConstructor =
                        Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".inventory.CraftInventoryPlayer")
                             .getConstructors()[0];
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                this.onceFired = true;
                return this.onCommand0(commandSender, command, commandLabel, arguments);
            }

        PlayerInventory playerInventory;
        if (this.cachedCustomInventories.containsKey(targetPlayer))
            playerInventory = this.cachedCustomInventories.get(targetPlayer);
        else
            playerInventory = this.createCustomInventory(targetPlayer, commandSender);

        if (playerInventory == null)
            return this.onCommand0(commandSender, command, commandLabel, arguments);

        ((Player) commandSender).openInventory(playerInventory);

        var finalPlayerInventory = playerInventory;
        var taskId = new AtomicInteger(0);
        taskId.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            if (((Player) commandSender).getOpenInventory().getTopInventory() == finalPlayerInventory)
                ((Player) commandSender).updateInventory();
            else
                Bukkit.getScheduler().cancelTask(taskId.get());
        }, 10L, 10L));
        return true;
    }

    private boolean onCommand0(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.plugin.getPermissions().hasPermission(cs, "invsee.use")) {
            var permission = this.plugin.getPermissions().getPermission("invsee.use");
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }
        if (args.length == 0) {
            var command = cmd.getName();
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(label, command, cs, null, "Invsee"));
            return true;
        }
        var targetPlayer = this.getPlayer(cs, args[0]);
        if (targetPlayer == null) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(args[0]));
            return true;
        }
        ((Player) cs).openInventory(targetPlayer.getInventory());
        return true;
    }

    public PlayerInventory createCustomInventory(Player targetPlayer, CommandSender cs) {
        Object handle;
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
            for (var method : this.playerInventoryNmsClass.getDeclaredMethods())
                if (method.getReturnType().getName().equalsIgnoreCase(int.class.getName()))
                    if (method.getParameters().length == 0)
                        try {
                            if ((int) method.invoke(this.getInventoryMethod.invoke(targetPlayer.getInventory())) == 41) {
                                getSizeMethod = method;
                                break;
                            }
                        } catch (IllegalAccessException | InvocationTargetException ex) {
                            ex.printStackTrace();
                        }


            Method getItemMethod = null;
            for (var method : this.playerInventoryNmsClass.getDeclaredMethods())
                if (method.getParameters().length == 1)
                    if (method.getParameters()[0].getType().getName().equalsIgnoreCase(int.class.getName()))
                        if (method.getReturnType().getName().toLowerCase(Locale.ROOT).contains("itemstack"))
                            if (method.getName().equalsIgnoreCase("a")) {
                                getItemMethod = method;
                                break;
                            }

            Method setItemMethod = null;
            for (var method : this.playerInventoryNmsClass.getDeclaredMethods())
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


        PlayerInventory playerInventory;
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
            customPlayerInventory = new ByteBuddy().subclass(this.playerInventoryNmsClass)

                                                   .method(ElementMatchers.isDeclaredBy(this.playerInventoryNmsClass)
                                                                          .and(ElementMatchers.named("setItem"))
                                                                          .or(ElementMatchers.is(setItemMethod)))
                                                   .intercept(MethodDelegation.withDefaultConfiguration()
                                                                              .withBinders(Morph.Binder.install(Morpher.class))
                                                                              .to(new InvseeSetItemInterceptor(this.plugin, targetPlayer, (Player) cs)))

                                                   .method((ElementMatchers.isDeclaredBy(this.playerInventoryNmsClass).and(ElementMatchers.named("getItem"))).or(
                                                           ElementMatchers.is(getItemMethod)))
                                                   .intercept(MethodDelegation.withDefaultConfiguration()
                                                                              .withBinders(Morph.Binder.install(Morpher.class))
                                                                              .to(new InvseeGetItemInterceptor(this.plugin, targetPlayer)))

                                                   .method(ElementMatchers.isDeclaredBy(this.playerInventoryNmsClass).and(ElementMatchers.named("getOwner")))
                                                   .intercept(FixedValue.value(cs))

                                                   .method((ElementMatchers.isDeclaredBy(this.playerInventoryNmsClass)).and(ElementMatchers.named("getSize")))
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
                                                   .getConstructors()[0].newInstance(handle);

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
            customPlayerInventory = new ByteBuddy().subclass(this.playerInventoryNmsClass)

                                                   .method(ElementMatchers.isDeclaredBy(this.playerInventoryNmsClass).and(ElementMatchers.named("setItem")))
                                                   .intercept(MethodDelegation.withDefaultConfiguration()
                                                                              .withBinders(Morph.Binder.install(Morpher.class))
                                                                              .to(new InvseeSetItemInterceptor(this.plugin, targetPlayer, (Player) cs)))

                                                   .method((ElementMatchers.isDeclaredBy(this.playerInventoryNmsClass).and(ElementMatchers.named("getItem"))))
                                                   .intercept(MethodDelegation.withDefaultConfiguration()
                                                                              .withBinders(Morph.Binder.install(Morpher.class))
                                                                              .to(new InvseeGetItemInterceptor(this.plugin, targetPlayer)))

                                                   .method(ElementMatchers.isDeclaredBy(this.playerInventoryNmsClass).and(ElementMatchers.named("getOwner")))
                                                   .intercept(FixedValue.value(cs))

                                                   .method((ElementMatchers.isDeclaredBy(this.playerInventoryNmsClass)).and(ElementMatchers.named("getSize")))
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
                                                   .getConstructors()[0].newInstance(handle);

            return customPlayerInventory;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            this.onceFired = true;
            return null;
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Inventory inventory = null;

        if (this.cachedCustomInventories.containsKey(e.getPlayer())) {
            inventory = this.cachedCustomInventories.get(e.getPlayer());

            this.cachedCustomInventories.remove(e.getPlayer());
        }

        if (inventory == null)
            inventory = e.getPlayer().getInventory();

        var tryOfflineCommand = this.plugin.getConfigReader().getBoolean("invseeAndEndechest.tryOfflineCommandOnPlayerQuit");

        if (tryOfflineCommand)
            this.tryOfflineInventorySee(inventory, e.getPlayer());
    }

    private void tryOfflineInventorySee(Inventory inventory, Player target) {
        for (var human : new ArrayList<>(inventory.getViewers())) {
            if (human.getUniqueId().toString().equalsIgnoreCase(target.getUniqueId().toString()))
                continue;

            var cursorStack = human.getItemOnCursor();

            human.setItemOnCursor(null);

            human.closeInventory();

            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                Bukkit.getScheduler().runTaskLater(this.plugin, () -> human.setItemOnCursor(cursorStack), 2L);

                if (!(human instanceof Player player))
                    return;

                if (!this.plugin.getPermissions().hasPermission(player, "offlineinvsee", true))
                    return;

                player.chat("/offlineinvsee " + target.getName());
            }, 2L);

            human.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage("invsee", "invsee", human, target, "InvSee.PlayerWentOffline"));
        }
    }
}