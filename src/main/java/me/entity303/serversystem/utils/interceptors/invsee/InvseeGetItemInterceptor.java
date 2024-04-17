package me.entity303.serversystem.utils.interceptors.invsee;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.Morpher;
import net.bytebuddy.implementation.bind.annotation.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;

public class InvseeGetItemInterceptor {
    private final ServerSystem plugin;
    private final PlayerInventory master;
    private final ItemStack dropItemStack = new ItemStack(Material.DROPPER);
    private Method asNMSCopyMethod = null;
    private Method getInventoryMethod = null;
    private Method getItemMethod = null;
    private Object masterNms = null;

    public InvseeGetItemInterceptor(ServerSystem plugin, Player victim) {
        this.plugin = plugin;
        this.master = victim.getInventory();

        var meta = this.dropItemStack.getItemMeta();
        meta.setDisplayName("§cDrop Item");
        this.dropItemStack.setItemMeta(meta);
    }

    @RuntimeType
    public Object intercept(@This Object obj, @AllArguments Object[] allArguments, @Morph Morpher morpher, @SuperMethod Method method) {
        if (this.asNMSCopyMethod == null)
            try {
                this.asNMSCopyMethod = Arrays.stream(
                        Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".inventory.CraftItemStack")
                             .getDeclaredMethods()).filter(method1 -> method1.getName().equalsIgnoreCase("asNMSCopy")).findFirst().orElse(null);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }

        if (this.masterNms == null) {
            if (this.getInventoryMethod == null)
                try {
                    this.getInventoryMethod =
                            Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".inventory.CraftInventoryPlayer")
                                 .getDeclaredMethod("getInventory");
                } catch (NoSuchMethodException | ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }

            try {
                this.masterNms = this.getInventoryMethod.invoke(this.master);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }

        if (allArguments[0] instanceof Integer) {
            var i = (int) allArguments[0];

            if (i > 45 - 5)
                try {
                    return this.asNMSCopyMethod.invoke(null, this.dropItemStack);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    return null;
                }

            try {
                if (this.getItemMethod == null)
                    this.getItemMethod = Arrays.stream(this.masterNms.getClass().getDeclaredMethods())
                                               .filter(method1 -> method1.getName().equalsIgnoreCase("getItem") && method1.getParameters().length == 1 &&
                                                                  (method1.getParameters()[0].getType() == Integer.class ||
                                                                   method1.getParameters()[0].getType() == int.class))
                                               .findFirst()
                                               .orElse(null);

                if (this.getItemMethod == null)
                    for (var m : this.masterNms.getClass().getDeclaredMethods())
                        if (m.getParameters().length == 1)
                            if (m.getParameters()[0].getType().getName().equalsIgnoreCase(int.class.getName()))
                                if (m.getReturnType().getName().toLowerCase(Locale.ROOT).contains("itemstack"))
                                    if (m.getName().equalsIgnoreCase("a")) {
                                        this.getItemMethod = m;
                                        break;
                                    }

                return this.getItemMethod.invoke(this.masterNms, allArguments[0]);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
