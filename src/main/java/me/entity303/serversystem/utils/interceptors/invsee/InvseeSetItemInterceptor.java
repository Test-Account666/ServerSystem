package me.entity303.serversystem.utils.interceptors.invsee;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.Morpher;
import net.bytebuddy.implementation.bind.annotation.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class InvseeSetItemInterceptor {

    private final ServerSystem plugin;
    private final Player victim;
    private final Player player;
    private final PlayerInventory master;
    private Method asCraftMirrorMethod = null;
    private Method setCountMethod = null;
    private Method getInventoryMethod = null;
    private Method setItemMethod = null;
    private Object masterNms = null;

    public InvseeSetItemInterceptor(ServerSystem plugin, Player victim, Player player) {
        this.plugin = plugin;
        this.victim = victim;
        this.player = player;
        this.master = victim.getInventory();
    }

    @RuntimeType
    public void intercept(@This Object obj,
                          @AllArguments Object[] allArguments,
                          @Morph Morpher morpher,
                          @SuperMethod Method method) {
        if (this.asCraftMirrorMethod == null) try {
            this.asCraftMirrorMethod = Arrays.stream(Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".inventory.CraftItemStack").getDeclaredMethods()).filter(method1 -> method1.getName().equalsIgnoreCase("asCraftMirror")).findFirst().orElse(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (this.masterNms == null) {
            if (this.getInventoryMethod == null) try {
                this.getInventoryMethod = Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".inventory.CraftInventoryPlayer").getDeclaredMethod("getInventory");
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }

            try {
                this.masterNms = this.getInventoryMethod.invoke(this.master);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return;
            }
        }

        if (allArguments[0] instanceof Integer) {
            int i = (int) allArguments[0];

            if (i > 45 - 5) if (allArguments[1] != null) {

                if (this.setCountMethod == null) try {
                    this.setCountMethod = allArguments[1].getClass().getDeclaredMethod("setCount", int.class);
                } catch (NoSuchMethodException ignored) {
                }


                try {
                    ItemStack itemStack = (ItemStack) this.asCraftMirrorMethod.invoke(null, allArguments[1]);
                    if (!itemStack.getType().name().contains("AIR"))
                        this.victim.getWorld().dropItem(this.victim.getEyeLocation().add(0, -0.33, 0), itemStack).setVelocity(new Vector(0.0, 0.0, 0.0).add(this.victim.getLocation().getDirection().multiply(0.35)));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    return;
                }

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (InvseeSetItemInterceptor.this.setCountMethod != null)
                            try {
                                InvseeSetItemInterceptor.this.setCountMethod.invoke(allArguments[1], 0);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                if (e instanceof InvocationTargetException) {
                                    if (!(e.getCause() instanceof AssertionError))
                                        e.printStackTrace();
                                } else
                                    e.printStackTrace();
                            }

                        try {
                            ItemStack itemStack = (ItemStack) InvseeSetItemInterceptor.this.asCraftMirrorMethod.invoke(null, allArguments[1]);
                            itemStack.setAmount(0);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }

                        InvseeSetItemInterceptor.this.player.getItemOnCursor().setAmount(0);

                        InvseeSetItemInterceptor.this.player.updateInventory();
                    }
                }, 1000L / 20);
                return;
            }

            if (this.setItemMethod == null)
                this.setItemMethod = Arrays.stream(this.masterNms.getClass().getDeclaredMethods()).filter(method1 -> method1.getName().equalsIgnoreCase("setItem")).findFirst().orElse(null);

            if (this.setItemMethod == null)
                for (Method m : this.masterNms.getClass().getDeclaredMethods())
                    if (m.getReturnType().getName().equalsIgnoreCase(void.class.getName()))
                        if (m.getParameters().length == 2)
                            if (m.getParameters()[0].getType().getName().equalsIgnoreCase(int.class.getName()))
                                if (m.getParameters()[1].getType().getName().toLowerCase(Locale.ROOT).contains("itemstack")) {
                                    this.setItemMethod = m;
                                    break;
                                }

            try {
                this.setItemMethod.invoke(this.masterNms, allArguments[0], allArguments[1]);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
