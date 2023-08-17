package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.DummyCommandSender;
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
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class OfflineInvseeCommand extends MessageUtils implements TabExecutor, Listener {
    private final HashMap<Player, PlayerInventory> cachedCustomInventories = new HashMap<>();
    private Class playerInventoryNmsClass = null;
    private Constructor craftInventoryPlayerConstructor = null;
    private boolean onceFired = false;
    private Method getInventoryMethod = null;
    private Method getHandleMethod = null;

    public OfflineInvseeCommand(ServerSystem plugin) {
        super(plugin);

        this.plugin.getEventManager().re(this);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (this.onceFired || !this.plugin.isAdvancedInvsee()) return this.onCommand0(cs, cmd, label, args);

        if (!this.isAllowed(cs, "offlineinvsee")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("offlineinvsee")));
            return true;
        }

        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }

        if (args.length == 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("OfflineInvsee", label, cmd.getName(), cs, null));
            return true;
        }

        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(args[0]);

        if (!offlineTarget.hasPlayedBefore()) {
            String name = offlineTarget.getName();
            if (name == null) name = args[0];
            cs.sendMessage(this.getPrefix() + this.getMessage("OfflineInvsee.NeverPlayed", label, cmd.getName(), cs, new DummyCommandSender(name)));
            return true;
        }

        Player targetPlayer = null;
        if (offlineTarget.isOnline()) {
            targetPlayer = this.getPlayer(cs, args[0]);
            if (targetPlayer != null) {
                cs.sendMessage(this.getPrefix() + this.getMessage("OfflineInvsee.PlayerIsOnline", label, cmd.getName(), cs, targetPlayer));
                return true;
            } else targetPlayer = Bukkit.getPlayer(args[0]);
        }

        if (targetPlayer == null)
            targetPlayer = this.getHookedPlayer(Bukkit.getOfflinePlayer(args[0]));

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

        this.cachedCustomInventories.put(targetPlayer, playerInventory);

        ((Player) cs).openInventory(playerInventory);

        PlayerInventory finalPlayerInventory = playerInventory;
        AtomicInteger taskId = new AtomicInteger(0);
        Player finalTargetPlayer = targetPlayer;
        taskId.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            if (((Player) cs).getOpenInventory().getTopInventory() == finalPlayerInventory)
                ((Player) cs).updateInventory();
            else {
                if (!finalTargetPlayer.isOnline()) {
                    this.cachedCustomInventories.remove(finalTargetPlayer);
                    finalTargetPlayer.saveData();
                }
                Bukkit.getScheduler().cancelTask(taskId.get());
            }
        }, 10L, 10L));
        return true;
    }

    private boolean onCommand0(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "offlineinvsee")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("offlineinvsee")));
            return true;
        }
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }
        if (args.length == 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("OfflineInvsee", label, cmd.getName(), cs, null));
            return true;
        }

        Player targetPlayer = null;
        if (Bukkit.getPlayer(args[0]) != null) {
            targetPlayer = this.getPlayer(cs, args[0]);
            if (targetPlayer != null) {
                cs.sendMessage(this.getPrefix() + this.getMessage("OfflineInvsee.PlayerIsOnline", label, cmd.getName(), cs, targetPlayer));
                return true;
            } else targetPlayer = Bukkit.getPlayer(args[0]);
        }

        if (targetPlayer == null)
            targetPlayer = this.getHookedPlayer(Bukkit.getOfflinePlayer(args[0]));

        ((Player) cs).openInventory(targetPlayer.getInventory());

        PlayerInventory finalPlayerInventory = targetPlayer.getInventory();
        AtomicInteger taskId = new AtomicInteger(0);
        Player finalTargetPlayer = targetPlayer;
        taskId.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            if (((Player) cs).getOpenInventory().getTopInventory() != finalPlayerInventory) {
                if (!finalTargetPlayer.isOnline()) {
                    this.cachedCustomInventories.remove(finalTargetPlayer);
                    finalTargetPlayer.saveData();
                }
                Bukkit.getScheduler().cancelTask(taskId.get());
            }
        }, 10L, 10L));
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
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent e) {
        boolean tryOnlineCommand = this.plugin.getConfigReader().getBoolean("invseeAndEndechest.tryOnlineCommandOnPlayerJoin");

        Player target = this.cachedCustomInventories.keySet().stream().filter(player -> player.getUniqueId().toString().equalsIgnoreCase(e.getUniqueId().toString())).findFirst().orElse(null);
        if (target != null) {
            target.saveData();
            Bukkit.getScheduler().runTask(this.plugin, () -> {
                for (HumanEntity human : new ArrayList<>(this.cachedCustomInventories.get(target).getViewers())) {
                    ItemStack cursorStack = human.getItemOnCursor();

                    if (tryOnlineCommand)
                        human.setItemOnCursor(null);

                    human.closeInventory();

                    if (tryOnlineCommand)
                        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                                human.setItemOnCursor(cursorStack);
                            }, 2L);

                            if (!(human instanceof Player))
                                return;

                            Player player = (Player) human;

                            if (!this.isAllowed(player, "invsee.use", true))
                                return;

                            player.chat("/invsee " + target.getName());
                        }, 2L);

                    human.sendMessage(this.getPrefix() + this.getMessage("OfflineInvsee.PlayerCameOnline", "offlineinvsee", "offlineinvsee", human, target));
                }

                this.cachedCustomInventories.remove(target);
            });
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        this.cachedCustomInventories.remove(e.getPlayer());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!this.isAllowed(sender, "offlineinvsee", true))
            return Collections.singletonList("");

        List<String> players = Arrays.stream(Bukkit.getOfflinePlayers()).filter(offlinePlayer -> !offlinePlayer.isOnline()).map(OfflinePlayer::getName).collect(Collectors.toList());

        List<String> possiblePlayers = new ArrayList<>();

        for (String player : players)
            if (player.toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT)))
                possiblePlayers.add(player);

        return !possiblePlayers.isEmpty() ? possiblePlayers : players;
    }
}
