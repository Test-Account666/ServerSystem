package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import me.entity303.serversystem.utils.IMorpher;
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

public class InvseeCommand extends CommandUtils implements ICommandExecutorOverload, Listener {
    private final HashMap<Player, PlayerInventory> _cachedCustomInventories = new HashMap<>();
    private final Method _getInventoryMethodNew = null;
    private Class _playerInventoryNmsClass = null;
    private Constructor _craftInventoryPlayerConstructor = null;
    private boolean _onceFired = false;
    private Method _getInventoryMethod = null;
    private Method _getHandleMethod = null;

    public InvseeCommand(ServerSystem plugin) {
        super(plugin);

        this._plugin.GetEventManager().RegisterEvent(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this._onceFired || !this._plugin.IsAdvancedInvsee())
            return this.OnCommandInternal(commandSender, command, commandLabel, arguments);

        if (!this._plugin.GetPermissions().HasPermission(commandSender, "invsee.use")) {
            var permission = this._plugin.GetPermissions().GetPermission("invsee.use");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (arguments.length == 0) {

            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Invsee"));
            return true;
        }

        var targetPlayer = this.GetPlayer(commandSender, arguments[0]);
        if (targetPlayer == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }

        if (this._playerInventoryNmsClass == null)
            try {
                this._playerInventoryNmsClass =
                        Class.forName("net.minecraft.server." + this._plugin.GetVersionManager().GetNMSVersion() + ".PlayerInventory");
            } catch (ClassNotFoundException exception) {
                if (this._playerInventoryNmsClass == null)
                    try {
                        this._playerInventoryNmsClass = Class.forName("net.minecraft.world.entity.player.PlayerInventory");
                    } catch (ClassNotFoundException exception1) {
                        exception1.addSuppressed(exception);
                        exception1.printStackTrace();
                        this._onceFired = true;
                        return this.OnCommandInternal(commandSender, command, commandLabel, arguments);
                    }
            }

        if (this._getInventoryMethod == null)
            try {
                this._getInventoryMethod =
                        Class.forName("org.bukkit.craftbukkit." + this._plugin.GetVersionManager().GetNMSVersion() + ".inventory.CraftInventoryPlayer")
                             .getDeclaredMethod("getInventory");
            } catch (NoSuchMethodException | ClassNotFoundException exception) {
                exception.printStackTrace();
                this._onceFired = true;
                return this.OnCommandInternal(commandSender, command, commandLabel, arguments);
            }

        if (this._getHandleMethod == null)
            try {
                this._getHandleMethod = Class.forName("org.bukkit.craftbukkit." + this._plugin.GetVersionManager().GetNMSVersion() + ".entity.CraftPlayer")
                                             .getDeclaredMethod("getHandle");
            } catch (NoSuchMethodException | ClassNotFoundException exception) {
                exception.printStackTrace();
                this._onceFired = true;
                return this.OnCommandInternal(commandSender, command, commandLabel, arguments);
            }

        if (this._craftInventoryPlayerConstructor == null)
            try {
                this._craftInventoryPlayerConstructor =
                        Class.forName("org.bukkit.craftbukkit." + this._plugin.GetVersionManager().GetNMSVersion() + ".inventory.CraftInventoryPlayer")
                             .getConstructors()[0];
            } catch (ClassNotFoundException exception) {
                exception.printStackTrace();
                this._onceFired = true;
                return this.OnCommandInternal(commandSender, command, commandLabel, arguments);
            }

        PlayerInventory playerInventory;
        if (this._cachedCustomInventories.containsKey(targetPlayer))
            playerInventory = this._cachedCustomInventories.get(targetPlayer);
        else
            playerInventory = this.CreateCustomInventory(targetPlayer, commandSender);

        if (playerInventory == null)
            return this.OnCommandInternal(commandSender, command, commandLabel, arguments);

        ((Player) commandSender).openInventory(playerInventory);

        var finalPlayerInventory = playerInventory;
        var taskId = new AtomicInteger(0);
        taskId.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(this._plugin, () -> {
            if (((Player) commandSender).getOpenInventory().getTopInventory() == finalPlayerInventory)
                ((Player) commandSender).updateInventory();
            else
                Bukkit.getScheduler().cancelTask(taskId.get());
        }, 10L, 10L));
        return true;
    }

    private boolean OnCommandInternal(CommandSender commandSender, Command command, String commandLabel, String... arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "invsee.use")) {
            var permission = this._plugin.GetPermissions().GetPermission("invsee.use");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }
        if (arguments.length == 0) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetSyntax(commandLabel, command.getName(), commandSender, null, "Invsee"));
            return true;
        }
        var targetPlayer = this.GetPlayer(commandSender, arguments[0]);
        if (targetPlayer == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }
        ((Player) commandSender).openInventory(targetPlayer.getInventory());
        return true;
    }

    public PlayerInventory CreateCustomInventory(Player targetPlayer, CommandSender commandSender) {
        Object handle;
        try {
            handle = this._getHandleMethod.invoke(commandSender);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
            return null;
        }


        Object customPlayerInventory = null;

        try {
            this._playerInventoryNmsClass.getDeclaredMethod("getItem", int.class);
        } catch (NoSuchMethodException | NoSuchMethodError exception) {
            Method getSizeMethod = null;
            for (var method : this._playerInventoryNmsClass.getDeclaredMethods())
                if (method.getReturnType().getName().equalsIgnoreCase(int.class.getName()))
                    if (method.getParameters().length == 0)
                        try {
                            if ((int) method.invoke(this._getInventoryMethod.invoke(targetPlayer.getInventory())) == 41) {
                                getSizeMethod = method;
                                break;
                            }
                        } catch (IllegalAccessException | InvocationTargetException exception1) {
                            exception1.printStackTrace();
                        }


            Method getItemMethod = null;
            for (var method : this._playerInventoryNmsClass.getDeclaredMethods())
                if (method.getParameters().length == 1)
                    if (method.getParameters()[0].getType().getName().equalsIgnoreCase(int.class.getName()))
                        if (method.getReturnType().getName().toLowerCase(Locale.ROOT).contains("itemstack"))
                            if (method.getName().equalsIgnoreCase("a")) {
                                getItemMethod = method;
                                break;
                            }

            Method setItemMethod = null;
            for (var method : this._playerInventoryNmsClass.getDeclaredMethods())
                if (method.getReturnType().getName().equalsIgnoreCase(void.class.getName()))
                    if (method.getParameters().length == 2)
                        if (method.getParameters()[0].getType().getName().equalsIgnoreCase(int.class.getName()))
                            if (method.getParameters()[1].getType().getName().toLowerCase(Locale.ROOT).contains("itemstack")) {
                                setItemMethod = method;
                                break;
                            }

            customPlayerInventory = this.CreateNew(handle, setItemMethod, getItemMethod, getSizeMethod, targetPlayer, commandSender);
        }

        if (customPlayerInventory == null)
            customPlayerInventory = this.CreateNormal(handle, targetPlayer, commandSender);


        PlayerInventory playerInventory;
        try {
            playerInventory = (PlayerInventory) this._craftInventoryPlayerConstructor.newInstance(customPlayerInventory);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
            return null;
        }

        return playerInventory;
    }

    private Object CreateNew(Object handle, Method setItemMethod, Method getItemMethod, Method getSizeMethod, Player targetPlayer, CommandSender commandSender) {
        Object customPlayerInventory;
        try {
            customPlayerInventory = new ByteBuddy().subclass(this._playerInventoryNmsClass)

                                                   .method(ElementMatchers.isDeclaredBy(this._playerInventoryNmsClass)
                                                                          .and(ElementMatchers.named("setItem"))
                                                                          .or(ElementMatchers.is(setItemMethod)))
                                                   .intercept(MethodDelegation.withDefaultConfiguration()
                                                                              .withBinders(Morph.Binder.install(IMorpher.class))
                                                                              .to(new InvseeSetItemInterceptor(this._plugin, targetPlayer, (Player) commandSender)))

                                                   .method((ElementMatchers.isDeclaredBy(this._playerInventoryNmsClass)
                                                                           .and(ElementMatchers.named("getItem"))).or(ElementMatchers.is(getItemMethod)))
                                                   .intercept(MethodDelegation.withDefaultConfiguration()
                                                                              .withBinders(Morph.Binder.install(IMorpher.class))
                                                                              .to(new InvseeGetItemInterceptor(this._plugin, targetPlayer)))

                                                   .method(ElementMatchers.isDeclaredBy(this._playerInventoryNmsClass)
                                                                          .and(ElementMatchers.named("getOwner")))
                                                   .intercept(FixedValue.value(commandSender))

                                                   .method((ElementMatchers.isDeclaredBy(this._playerInventoryNmsClass)).and(
                                                           ElementMatchers.named("getSize")))
                                                   .intercept(FixedValue.value(45))

                                                   .method(ElementMatchers.is(getSizeMethod))
                                                   .intercept(FixedValue.value(45))

                                                   .method(ElementMatchers.isDeclaredBy(this._playerInventoryNmsClass)

                                                                          .and(ElementMatchers.not(ElementMatchers.named("getSize")))
                                                                          .and(ElementMatchers.not(ElementMatchers.is(getSizeMethod)))
                                                                          .and(ElementMatchers.not(ElementMatchers.named("getOwner")))

                                                                          .and(ElementMatchers.not(ElementMatchers.named("setItem")))
                                                                          .and(ElementMatchers.not(ElementMatchers.is(setItemMethod)))
                                                                          .and(ElementMatchers.not(ElementMatchers.named("getItem")))
                                                                          .and(ElementMatchers.not(ElementMatchers.is(getItemMethod))))
                                                   .intercept(MethodDelegation.to(this._getInventoryMethod.invoke(targetPlayer.getInventory())))

                                                   .make()
                                                   .load(this.getClass().getClassLoader())
                                                   .getLoaded()
                                                   .getConstructors()[0].newInstance(handle);

            return customPlayerInventory;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException exception) {
            exception.printStackTrace();
            this._onceFired = true;
            return null;
        }
    }

    private Object CreateNormal(Object handle, Player targetPlayer, CommandSender commandSender) {
        Object customPlayerInventory;
        try {
            customPlayerInventory = new ByteBuddy().subclass(this._playerInventoryNmsClass)

                                                   .method(ElementMatchers.isDeclaredBy(this._playerInventoryNmsClass)
                                                                          .and(ElementMatchers.named("setItem")))
                                                   .intercept(MethodDelegation.withDefaultConfiguration()
                                                                              .withBinders(Morph.Binder.install(IMorpher.class))
                                                                              .to(new InvseeSetItemInterceptor(this._plugin, targetPlayer, (Player) commandSender)))

                                                   .method((ElementMatchers.isDeclaredBy(this._playerInventoryNmsClass)
                                                                           .and(ElementMatchers.named("getItem"))))
                                                   .intercept(MethodDelegation.withDefaultConfiguration()
                                                                              .withBinders(Morph.Binder.install(IMorpher.class))
                                                                              .to(new InvseeGetItemInterceptor(this._plugin, targetPlayer)))

                                                   .method(ElementMatchers.isDeclaredBy(this._playerInventoryNmsClass)
                                                                          .and(ElementMatchers.named("getOwner")))
                                                   .intercept(FixedValue.value(commandSender))

                                                   .method((ElementMatchers.isDeclaredBy(this._playerInventoryNmsClass)).and(
                                                           ElementMatchers.named("getSize")))
                                                   .intercept(FixedValue.value(45))

                                                   .method(ElementMatchers.isDeclaredBy(this._playerInventoryNmsClass)

                                                                          .and(ElementMatchers.not(ElementMatchers.named("getSize")))
                                                                          .and(ElementMatchers.not(ElementMatchers.named("getOwner")))

                                                                          .and(ElementMatchers.not(ElementMatchers.named("setItem")))
                                                                          .and(ElementMatchers.not(ElementMatchers.named("getItem"))))
                                                   .intercept(MethodDelegation.to(this._getInventoryMethod.invoke(targetPlayer.getInventory())))

                                                   .make()
                                                   .load(this.getClass().getClassLoader())
                                                   .getLoaded()
                                                   .getConstructors()[0].newInstance(handle);

            return customPlayerInventory;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException exception) {
            exception.printStackTrace();
            this._onceFired = true;
            return null;
        }
    }

    @EventHandler
    public void OnQuit(PlayerQuitEvent event) {
        Inventory inventory = null;

        if (this._cachedCustomInventories.containsKey(event.getPlayer())) {
            inventory = this._cachedCustomInventories.get(event.getPlayer());

            this._cachedCustomInventories.remove(event.getPlayer());
        }

        if (inventory == null)
            inventory = event.getPlayer().getInventory();

        var tryOfflineCommand = this._plugin.GetConfigReader().GetBoolean("invseeAndEndechest.tryOfflineCommandOnPlayerQuit");

        if (tryOfflineCommand)
            this.TryOfflineInventorySee(inventory, event.getPlayer());
    }

    private void TryOfflineInventorySee(Inventory inventory, Player target) {
        for (var human : new ArrayList<>(inventory.getViewers())) {
            if (human.getUniqueId().toString().equalsIgnoreCase(target.getUniqueId().toString()))
                continue;

            var cursorStack = human.getItemOnCursor();

            human.setItemOnCursor(null);

            human.closeInventory();

            Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
                Bukkit.getScheduler().runTaskLater(this._plugin, () -> human.setItemOnCursor(cursorStack), 2L);

                if (!(human instanceof Player player))
                    return;

                if (!this._plugin.GetPermissions().HasPermission(player, "offlineinvsee", true))
                    return;

                player.chat("/offlineinvsee " + target.getName());
            }, 2L);

            human.sendMessage(this._plugin.GetMessages().GetPrefix() +
                              this._plugin.GetMessages().GetMessage("invsee", "invsee", human, target, "InvSee.PlayerWentOffline"));
        }
    }
}