package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ITabExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
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
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static me.entity303.serversystem.commands.executable.OfflineEnderChestCommand.GetOfflinePlayers;

@ServerSystemCommand(name = "OfflineInvSee")
public class OfflineInvseeCommand implements ITabExecutorOverload, Listener {
    protected final ServerSystem _plugin;
    private final HashMap<Player, PlayerInventory> _cachedCustomInventories = new HashMap<>();
    private Class _playerInventoryNmsClass = null;
    private Constructor _craftInventoryPlayerConstructor = null;
    private boolean _onceFired = false;
    private Method _getInventoryMethod = null;
    private Method _getHandleMethod = null;

    public OfflineInvseeCommand(ServerSystem plugin) {
        this._plugin = plugin;

        this._plugin.GetEventManager().RegisterEvent(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this._onceFired || !this._plugin.IsAdvancedInvsee()) return this.OnCommandInternal(commandSender, command, commandLabel, arguments);

        if (!this._plugin.GetPermissions().HasPermission(commandSender, "offlineinvsee")) {
            var permission = this._plugin.GetPermissions().GetPermission("offlineinvsee");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "OfflineInvsee"));
            return true;
        }

        var offlineTarget = Bukkit.getOfflinePlayer(arguments[0]);

        if (!offlineTarget.hasPlayedBefore()) {
            var name = offlineTarget.getName();
            if (name == null) name = arguments[0];
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessageWithStringTarget(commandLabel, command, commandSender, name, "OfflineInvsee.NeverPlayed"));
            return true;
        }

        Player targetPlayer = null;
        if (offlineTarget.isOnline()) {
            targetPlayer = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]);
            if (targetPlayer != null) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, targetPlayer, "OfflineInvsee.PlayerIsOnline"));
                return true;
            } else {
                targetPlayer = Bukkit.getPlayer(arguments[0]);
            }
        }

        if (targetPlayer == null) targetPlayer = CommandUtils.GetHookedPlayer(this._plugin, Bukkit.getOfflinePlayer(arguments[0]));

        if (this._playerInventoryNmsClass == null) {
            try {
                this._playerInventoryNmsClass = Class.forName("net.minecraft.server." + this._plugin.GetVersionManager().GetNMSVersion() + "PlayerInventory");
            } catch (ClassNotFoundException exception) {
                if (this._playerInventoryNmsClass == null) {
                    try {
                        this._playerInventoryNmsClass = Class.forName("net.minecraft.world.entity.player.PlayerInventory");
                    } catch (ClassNotFoundException exception1) {
                        exception1.addSuppressed(exception);
                        exception1.printStackTrace();
                        this._onceFired = true;
                        return this.OnCommandInternal(commandSender, command, commandLabel, arguments);
                    }
                }
            }
        }

        if (this._getInventoryMethod == null) {
            try {
                this._getInventoryMethod = Class.forName("org.bukkit.craftbukkit." + this._plugin.GetVersionManager().GetNMSVersion() + "inventory.CraftInventoryPlayer")
                                                .getDeclaredMethod("getInventory");
            } catch (NoSuchMethodException | ClassNotFoundException exception) {
                exception.printStackTrace();
                this._onceFired = true;
                return this.OnCommandInternal(commandSender, command, commandLabel, arguments);
            }
        }

        if (this._getHandleMethod == null) {
            try {
                this._getHandleMethod = commandSender.getClass().getDeclaredMethod("getHandle");
            } catch (NoSuchMethodException exception) {
                exception.printStackTrace();
                this._onceFired = true;
                return this.OnCommandInternal(commandSender, command, commandLabel, arguments);
            }
        }

        if (this._craftInventoryPlayerConstructor == null) {
            try {
                this._craftInventoryPlayerConstructor =
                        Class.forName("org.bukkit.craftbukkit." + this._plugin.GetVersionManager().GetNMSVersion() + "inventory.CraftInventoryPlayer")
                             .getConstructors()[0];
            } catch (ClassNotFoundException exception) {
                exception.printStackTrace();
                this._onceFired = true;
                return this.OnCommandInternal(commandSender, command, commandLabel, arguments);
            }
        }

        PlayerInventory playerInventory;
        if (this._cachedCustomInventories.containsKey(targetPlayer)) {
            playerInventory = this._cachedCustomInventories.get(targetPlayer);
        } else {
            playerInventory = this.CreateCustomInventory(targetPlayer, commandSender);
        }

        if (playerInventory == null) return this.OnCommandInternal(commandSender, command, commandLabel, arguments);

        this._cachedCustomInventories.put(targetPlayer, playerInventory);

        ((Player) commandSender).openInventory(playerInventory);

        var finalPlayerInventory = playerInventory;
        var taskId = new AtomicInteger(0);
        var finalTargetPlayer = targetPlayer;
        taskId.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(this._plugin, () -> {
            if (((Player) commandSender).getOpenInventory().getTopInventory() == finalPlayerInventory) {
                ((Player) commandSender).updateInventory();
            } else {
                if (!finalTargetPlayer.isOnline()) {
                    this._cachedCustomInventories.remove(finalTargetPlayer);
                    finalTargetPlayer.saveData();
                }
                Bukkit.getScheduler().cancelTask(taskId.get());
            }
        }, 10L, 10L));
        return true;
    }

    private boolean OnCommandInternal(CommandSender commandSender, Command command, String commandLabel, String... arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "offlineinvsee")) {
            var permission = this._plugin.GetPermissions().GetPermission("offlineinvsee");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }
        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "OfflineInvsee"));
            return true;
        }

        Player targetPlayer = null;
        if (Bukkit.getPlayer(arguments[0]) != null) {
            targetPlayer = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]);
            if (targetPlayer != null) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, targetPlayer, "OfflineInvsee.PlayerIsOnline"));
                return true;
            } else {
                targetPlayer = Bukkit.getPlayer(arguments[0]);
            }
        }

        if (targetPlayer == null) targetPlayer = CommandUtils.GetHookedPlayer(this._plugin, Bukkit.getOfflinePlayer(arguments[0]));

        ((HumanEntity) commandSender).openInventory(targetPlayer.getInventory());

        var finalPlayerInventory = targetPlayer.getInventory();
        var taskId = new AtomicInteger(0);
        var finalTargetPlayer = targetPlayer;
        taskId.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(this._plugin, () -> {
            if (((Player) commandSender).getOpenInventory().getTopInventory() != finalPlayerInventory) {
                if (!finalTargetPlayer.isOnline()) {
                    this._cachedCustomInventories.remove(finalTargetPlayer);
                    finalTargetPlayer.saveData();
                }
                Bukkit.getScheduler().cancelTask(taskId.get());
            }
        }, 10L, 10L));
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
                if (method.getReturnType().getName().equalsIgnoreCase(int.class.getName())) {
                    if (method.getParameters().length == 0) {
                        try {
                            if ((int) method.invoke(this._getInventoryMethod.invoke(targetPlayer.getInventory())) == 41) {
                                getSizeMethod = method;
                                break;
                            }
                        } catch (IllegalAccessException | InvocationTargetException exception1) {
                            exception1.printStackTrace();
                        }
                    }
                }


            Method getItemMethod = null;
            for (var method : this._playerInventoryNmsClass.getDeclaredMethods())
                if (method.getParameters().length == 1) {
                    if (method.getParameters()[0].getType().getName().equalsIgnoreCase(int.class.getName())) {
                        if (method.getReturnType().getName().toLowerCase(Locale.ROOT).contains("itemstack")) {
                            if (method.getName().equalsIgnoreCase("a")) {
                                getItemMethod = method;
                                break;
                            }
                        }
                    }
                }

            Method setItemMethod = null;
            for (var method : this._playerInventoryNmsClass.getDeclaredMethods())
                if (method.getReturnType().getName().equalsIgnoreCase(void.class.getName())) {
                    if (method.getParameters().length == 2) {
                        if (method.getParameters()[0].getType().getName().equalsIgnoreCase(int.class.getName())) {
                            if (method.getParameters()[1].getType().getName().toLowerCase(Locale.ROOT).contains("itemstack")) {
                                setItemMethod = method;
                                break;
                            }
                        }
                    }
                }

            customPlayerInventory = this.CreateNew(handle, setItemMethod, getItemMethod, getSizeMethod, targetPlayer, commandSender);
        }

        if (customPlayerInventory == null) customPlayerInventory = this.CreateNormal(handle, targetPlayer, commandSender);


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

                                                   .method((ElementMatchers.isDeclaredBy(this._playerInventoryNmsClass).and(ElementMatchers.named("getItem"))).or(
                                                           ElementMatchers.is(getItemMethod)))
                                                   .intercept(MethodDelegation.withDefaultConfiguration()
                                                                              .withBinders(Morph.Binder.install(IMorpher.class))
                                                                              .to(new InvseeGetItemInterceptor(this._plugin, targetPlayer)))

                                                   .method(ElementMatchers.isDeclaredBy(this._playerInventoryNmsClass).and(ElementMatchers.named("getOwner")))
                                                   .intercept(FixedValue.value(commandSender))

                                                   .method((ElementMatchers.isDeclaredBy(this._playerInventoryNmsClass)).and(ElementMatchers.named("getSize")))
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

                                                   .method(ElementMatchers.isDeclaredBy(this._playerInventoryNmsClass).and(ElementMatchers.named("setItem")))
                                                   .intercept(MethodDelegation.withDefaultConfiguration()
                                                                              .withBinders(Morph.Binder.install(IMorpher.class))
                                                                              .to(new InvseeSetItemInterceptor(this._plugin, targetPlayer, (Player) commandSender)))

                                                   .method((ElementMatchers.isDeclaredBy(this._playerInventoryNmsClass).and(ElementMatchers.named("getItem"))))
                                                   .intercept(MethodDelegation.withDefaultConfiguration()
                                                                              .withBinders(Morph.Binder.install(IMorpher.class))
                                                                              .to(new InvseeGetItemInterceptor(this._plugin, targetPlayer)))

                                                   .method(ElementMatchers.isDeclaredBy(this._playerInventoryNmsClass).and(ElementMatchers.named("getOwner")))
                                                   .intercept(FixedValue.value(commandSender))

                                                   .method((ElementMatchers.isDeclaredBy(this._playerInventoryNmsClass)).and(ElementMatchers.named("getSize")))
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
    public void OnAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        var tryOnlineCommand = this._plugin.GetConfigReader().GetBoolean("invseeAndEndechest.tryOnlineCommandOnPlayerJoin");

        var target = this._cachedCustomInventories.keySet()
                                                  .stream()
                                                  .filter(player -> player.getUniqueId().toString().equalsIgnoreCase(event.getUniqueId().toString()))
                                                  .findFirst()
                                                  .orElse(null);
        if (target != null) {
            target.saveData();
            Bukkit.getScheduler().runTask(this._plugin, () -> {
                for (var human : new ArrayList<>(this._cachedCustomInventories.get(target).getViewers())) {
                    if (human.getUniqueId().toString().equalsIgnoreCase(target.getUniqueId().toString())) continue;

                    var cursorStack = human.getItemOnCursor();

                    if (tryOnlineCommand) human.setItemOnCursor(null);

                    human.closeInventory();

                    if (tryOnlineCommand) {
                        Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
                            Bukkit.getScheduler().runTaskLater(this._plugin, () -> human.setItemOnCursor(cursorStack), 2L);

                            if (!(human instanceof Player player)) return;

                            if (!this._plugin.GetPermissions().HasPermission(player, "invsee.use", true)) return;

                            player.chat("/invsee " + target);
                        }, 2L);
                    }

                    human.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage("offlineinvsee", "offlineinvsee", human, target, "OfflineInvsee.PlayerCameOnline"));
                }

                this._cachedCustomInventories.remove(target);
            });
        }
    }

    @EventHandler
    public void OnQuit(PlayerQuitEvent event) {
        this._cachedCustomInventories.remove(event.getPlayer());
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "offlineinvsee", true)) return Collections.singletonList("");

        return GetOfflinePlayers(arguments);
    }
}
