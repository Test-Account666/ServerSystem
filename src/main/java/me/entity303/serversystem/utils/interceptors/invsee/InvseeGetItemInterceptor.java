package me.entity303.serversystem.utils.interceptors.invsee;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.IMorpher;
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
    private final ServerSystem _plugin;
    private final PlayerInventory _masterInventory;
    private final ItemStack _dropItemStack = new ItemStack(Material.DROPPER);
    private Method _asNMSCopyMethod = null;
    private Method _getInventoryMethod = null;
    private Method _getItemMethod = null;
    private Object _masterInventoryNms = null;

    public InvseeGetItemInterceptor(ServerSystem plugin, Player victim) {
        this._plugin = plugin;
        this._masterInventory = victim.getInventory();

        var meta = this._dropItemStack.getItemMeta();
        meta.setDisplayName("§cDrop Item");
        this._dropItemStack.setItemMeta(meta);
    }

    @SuppressWarnings("NewMethodNamingConvention")
    @RuntimeType
    public Object intercept(@This Object obj, @AllArguments Object[] allArguments, @Morph IMorpher morpher, @SuperMethod Method method) {
        if (this._asNMSCopyMethod == null)
            try {
                this._asNMSCopyMethod = Arrays.stream(
                        Class.forName("org.bukkit.craftbukkit." + this._plugin.GetVersionManager().GetNMSVersion() + ".inventory.CraftItemStack")
                             .getDeclaredMethods()).filter(method1 -> method1.getName().equalsIgnoreCase("asNMSCopy")).findFirst().orElse(null);
            } catch (ClassNotFoundException exception) {
                exception.printStackTrace();
                return null;
            }

        if (this._masterInventoryNms == null) {
            if (this._getInventoryMethod == null)
                try {
                    this._getInventoryMethod =
                            Class.forName("org.bukkit.craftbukkit." + this._plugin.GetVersionManager().GetNMSVersion() + ".inventory.CraftInventoryPlayer")
                                 .getDeclaredMethod("getInventory");
                } catch (NoSuchMethodException | ClassNotFoundException exception) {
                    exception.printStackTrace();
                    return null;
                }

            try {
                this._masterInventoryNms = this._getInventoryMethod.invoke(this._masterInventory);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                exception.printStackTrace();
                return null;
            }
        }

        if (allArguments[0] instanceof Integer) {
            var index = (int) allArguments[0];

            if (index > 45 - 5)
                try {
                    return this._asNMSCopyMethod.invoke(null, this._dropItemStack);
                } catch (IllegalAccessException | InvocationTargetException exception) {
                    exception.printStackTrace();
                    return null;
                }

            try {
                if (this._getItemMethod == null)
                    this._getItemMethod = Arrays.stream(this._masterInventoryNms.getClass().getDeclaredMethods())
                                                .filter(this::IsGetItem)
                                                .findFirst()
                                                .orElse(null);

                if (this._getItemMethod == null)
                    for (var declaredMethod : this._masterInventoryNms.getClass().getDeclaredMethods())
                        if (declaredMethod.getParameters().length == 1)
                            if (declaredMethod.getParameters()[0].getType().getName().equalsIgnoreCase(int.class.getName()))
                                if (declaredMethod.getReturnType().getName().toLowerCase(Locale.ROOT).contains("itemstack"))
                                    if (declaredMethod.getName().equalsIgnoreCase("a")) {
                                        this._getItemMethod = declaredMethod;
                                        break;
                                    }

                return this._getItemMethod.invoke(this._masterInventoryNms, allArguments[0]);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                exception.printStackTrace();
            }
        }

        return null;
    }

    private boolean IsGetItem(Method method1) {
        return method1.getName().equalsIgnoreCase("getItem") && method1.getParameters().length == 1 &&
               (method1.getParameters()[0].getType() == Integer.class || method1.getParameters()[0].getType() == int.class);
    }
}
