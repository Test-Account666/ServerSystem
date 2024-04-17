package me.entity303.serversystem.utils;

import me.entity303.serversystem.main.ServerSystem;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class CommandUtils {
    protected final ServerSystem plugin;

    private Method loadDataMethod = null;

    public CommandUtils(ServerSystem plugin) {
        this.plugin = plugin;
    }

    public static boolean isAwayFromKeyboard(Player player) {
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

    public Player getHookedPlayer(OfflinePlayer offlineTarget) {
        try {
            var player = new AtomicReference<Player>(null);
            var cPlayer =
                    new ByteBuddy().subclass(Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".entity.CraftPlayer"))
                                   .method(ElementMatchers.named("saveData"))
                                   .intercept(MethodCall.invokeSuper()
                                                        .andThen(MethodCall.run(() -> this.plugin.getVersionStuff().getSaveData().saveData(player.get()))))
                                   .make()
                                   .load(this.plugin.getClass().getClassLoader())
                                   .getLoaded()
                                   .getConstructors()[0].newInstance(Bukkit.getServer(),
                                                                     this.plugin.getVersionStuff().getEntityPlayer().getEntityPlayer(offlineTarget));

            if (this.loadDataMethod == null) {
                this.loadDataMethod = Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".entity.CraftPlayer")
                                           .getDeclaredMethod("loadData");
                this.loadDataMethod.setAccessible(true);
            }

            this.loadDataMethod.invoke(cPlayer);

            player.set((Player) cPlayer);
            return player.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Player getPlayer(CommandSender sender, String name) {
        return this.getPlayer(sender, name, null);
    }

    public Player getPlayer(CommandSender sender, String name, UUID uuid) {
        Player player = null;

        if (name != null)
            player = Bukkit.getPlayer(name);
        else if (uuid != null)
            player = Bukkit.getPlayer(uuid);

        if (player == null)
            return null;
        if (sender instanceof Player)
            if (!this.plugin.getVanish().isVanish(player) || this.plugin.getPermissions().hasPermission(sender, "vanish.see", true))
                return player;
            else
                return null;
        return player;
    }

    public Player getPlayer(CommandSender sender, UUID uuid) {
        return this.getPlayer(sender, null, uuid);
    }
}
