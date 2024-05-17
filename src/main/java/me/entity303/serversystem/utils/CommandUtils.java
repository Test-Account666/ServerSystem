package me.entity303.serversystem.utils;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.interceptors.SaveDataInterceptor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.matcher.ElementMatchers;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.Metadatable;

import java.lang.reflect.Method;
import java.util.UUID;

public final class CommandUtils {

    private static final String NMS_CRAFT_PLAYER_CLASS_NAME = "org.bukkit.craftbukkit.%s.entity.CraftPlayer";
    private static Method _loadDataMethod = null;

    public static boolean IsAwayFromKeyboard(Metadatable player) {
        var awayFromKeyboard = false;

        for (var metadataValue : player.getMetadata("afk")) {
            if (metadataValue == null)
                continue;

            if (metadataValue.getOwningPlugin() == null)
                continue;

            if (!metadataValue.getOwningPlugin().getName().equalsIgnoreCase("ServerSystem"))
                continue;

            awayFromKeyboard = metadataValue.asBoolean();
            break;
        }

        return awayFromKeyboard;
    }

    public static Player GetPlayer(ServerSystem plugin, CommandSender sender, String name) {
        return CommandUtils.GetPlayer(plugin, sender, name, null);
    }

    public static Player GetPlayer(ServerSystem plugin, CommandSender sender, String name, UUID uuid) {
        Player player = null;

        if (name != null)
            player = Bukkit.getPlayer(name);
        else if (uuid != null)
            player = Bukkit.getPlayer(uuid);

        if (player == null)
            return null;
        if (sender instanceof Player)
            if (!plugin.GetVanish().IsVanish(player) || plugin.GetPermissions().HasPermission(sender, "vanish.see", true))
                return player;
            else
                return null;
        return player;
    }

    public static Player GetPlayer(ServerSystem plugin, CommandSender sender, UUID uuid) {
        return CommandUtils.GetPlayer(plugin, sender, null, uuid);
    }

    public static Player GetHookedPlayer(ServerSystem plugin, OfflinePlayer offlineTarget) {
        try {
            // Create a subclass of CraftPlayer with overridden saveData method
            var hookedPlayerClass =
                    new ByteBuddy().subclass(Class.forName(String.format(NMS_CRAFT_PLAYER_CLASS_NAME, plugin.GetVersionManager().GetNMSVersion())))
                                   .method(ElementMatchers.named("saveData"))
                                   .intercept(MethodCall.invokeSuper()
                                                        .withAllArguments()
                                                        .andThen(MethodDelegation.withDefaultConfiguration()
                                                                                 .withBinders(Morph.Binder.install(IMorpher.class))
                                                                                 .to(new SaveDataInterceptor(plugin))))
                                   .make()
                                   .load(plugin.getClass().getClassLoader())
                                   .getLoaded();

            // Instantiate the hooked player
            var hookedPlayerConstructor = hookedPlayerClass.getConstructors()[0];
            var hookedPlayer =
                    hookedPlayerConstructor.newInstance(Bukkit.getServer(), plugin.GetVersionStuff().GetEntityPlayer().GetEntityPlayer(offlineTarget));

            // Load data method setup
            if (_loadDataMethod == null) {
                _loadDataMethod = Class.forName(String.format(NMS_CRAFT_PLAYER_CLASS_NAME, plugin.GetVersionManager().GetNMSVersion()))
                                       .getDeclaredMethod("loadData");
                _loadDataMethod.setAccessible(true);
            }

            // Invoke loadData method on the hooked player
            _loadDataMethod.invoke(hookedPlayer);

            return (Player) hookedPlayer;
        } catch (Exception e) {
            System.err.println("Failed to hook player: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
