package me.entity303.serversystem.utils.interceptors.invsee;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.IMorpher;
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

    private final ServerSystem _plugin;
    private final Player _victim;
    private final Player _player;
    private final PlayerInventory _masterInventory;
    private Method _asCraftMirrorMethod = null;
    private Method _setCountMethod = null;
    private Method _getInventoryMethod = null;
    private Method _setItemMethod = null;
    private Object _masterInventoryNms = null;

    public InvseeSetItemInterceptor(ServerSystem plugin, Player victim, Player player) {
        this._plugin = plugin;
        this._victim = victim;
        this._player = player;
        this._masterInventory = victim.getInventory();
    }

    @SuppressWarnings("NewMethodNamingConvention")
    @RuntimeType
    public void intercept(@This Object obj, @AllArguments Object[] allArguments, @Morph IMorpher morpher, @SuperMethod Method method) {
        if (this._asCraftMirrorMethod == null)
            try {
                this._asCraftMirrorMethod = Arrays.stream(
                        Class.forName("org.bukkit.craftbukkit." + this._plugin.GetVersionManager().GetNMSVersion() + ".inventory.CraftItemStack")
                             .getDeclaredMethods()).filter(method1 -> method1.getName().equalsIgnoreCase("asCraftMirror")).findFirst().orElse(null);
            } catch (ClassNotFoundException exception) {
                exception.printStackTrace();
                return;
            }

        if (this._masterInventoryNms == null) {
            if (this._getInventoryMethod == null)
                try {
                    this._getInventoryMethod =
                            Class.forName("org.bukkit.craftbukkit." + this._plugin.GetVersionManager().GetNMSVersion() + ".inventory.CraftInventoryPlayer")
                                 .getDeclaredMethod("getInventory");
                } catch (NoSuchMethodException | ClassNotFoundException exception) {
                    exception.printStackTrace();
                    return;
                }

            try {
                this._masterInventoryNms = this._getInventoryMethod.invoke(this._masterInventory);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                exception.printStackTrace();
                return;
            }
        }

        if (!(allArguments[0] instanceof Integer))
            return;

        var index = (int) allArguments[0];

        if (index > 45 - 5)
            if (allArguments[1] != null) {
                if (this._setCountMethod == null)
                    try {
                        this._setCountMethod = allArguments[1].getClass().getDeclaredMethod("setCount", int.class);
                    } catch (NoSuchMethodException ignored) {
                    }


                try {
                    var itemStack = (ItemStack) this._asCraftMirrorMethod.invoke(null, allArguments[1]);
                    if (!itemStack.getType().name().contains("AIR"))
                        this._victim.getWorld()
                                    .dropItem(this._victim.getEyeLocation().add(0, -0.33, 0), itemStack)
                                    .setVelocity(new Vector(0.0, 0.0, 0.0).add(this._victim.getLocation().getDirection().multiply(0.35)));
                } catch (IllegalAccessException | InvocationTargetException exception) {
                    exception.printStackTrace();
                    return;
                }

                var timer = new Timer();
                timer.schedule(new MyTimerTask(allArguments), 1000L / 20);
                return;
            }

        if (this._setItemMethod == null)
            this._setItemMethod = Arrays.stream(this._masterInventoryNms.getClass().getDeclaredMethods())
                                        .filter(method1 -> method1.getName().equalsIgnoreCase("setItem"))
                                        .findFirst()
                                        .orElse(null);

        if (this._setItemMethod == null)
            for (var declaredMethod : this._masterInventoryNms.getClass().getDeclaredMethods())
                if (declaredMethod.getReturnType().getName().equalsIgnoreCase(void.class.getName()))
                    if (declaredMethod.getParameters().length == 2)
                        if (declaredMethod.getParameters()[0].getType().getName().equalsIgnoreCase(int.class.getName()))
                            if (declaredMethod.getParameters()[1].getType().getName().toLowerCase(Locale.ROOT).contains("itemstack")) {
                                this._setItemMethod = declaredMethod;
                                break;
                            }

        try {
            this._setItemMethod.invoke(this._masterInventoryNms, allArguments[0], allArguments[1]);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
        }
    }

    private class MyTimerTask extends TimerTask {
        private final Object[] _allArguments;

        public MyTimerTask(Object... allArguments) {
            this._allArguments = allArguments;
        }

        @Override
        public void run() {
            if (InvseeSetItemInterceptor.this._setCountMethod != null)
                try {
                    InvseeSetItemInterceptor.this._setCountMethod.invoke(this._allArguments[1], 0);
                } catch (IllegalAccessException | InvocationTargetException exception) {
                    if (exception instanceof InvocationTargetException) {
                        if (!(exception.getCause() instanceof AssertionError))
                            exception.printStackTrace();
                    } else
                        exception.printStackTrace();
                }

            try {
                var itemStack = (ItemStack) InvseeSetItemInterceptor.this._asCraftMirrorMethod.invoke(null, this._allArguments[1]);
                itemStack.setAmount(0);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                exception.printStackTrace();
            }

            InvseeSetItemInterceptor.this._player.getItemOnCursor().setAmount(0);

            InvseeSetItemInterceptor.this._player.updateInventory();
        }
    }
}
