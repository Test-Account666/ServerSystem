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
    protected final ServerSystem _plugin;

    private Method _loadDataMethod = null;

    public CommandUtils(ServerSystem plugin) {
        this._plugin = plugin;
    }

    public static boolean IsAwayFromKeyboard(Player player) {
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

    public Player GetHookedPlayer(OfflinePlayer offlineTarget) {
        try {
            var player = new AtomicReference<Player>(null);
            var cPlayer =
                    new ByteBuddy().subclass(Class.forName("org.bukkit.craftbukkit." + this._plugin.GetVersionManager().GetNMSVersion() + ".entity.CraftPlayer"))
                                   .method(ElementMatchers.named("saveData"))
                                   .intercept(MethodCall.invokeSuper()
                                                        .andThen(MethodCall.run(() -> this._plugin.GetVersionStuff().GetSaveData().SaveData(player.get()))))
                                   .make()
                                   .load(this._plugin.getClass().getClassLoader())
                                   .getLoaded()
                                   .getConstructors()[0].newInstance(Bukkit.getServer(),
                                                                     this._plugin.GetVersionStuff().GetEntityPlayer().GetEntityPlayer(offlineTarget));

            if (this._loadDataMethod == null) {
                this._loadDataMethod = Class.forName("org.bukkit.craftbukkit." + this._plugin.GetVersionManager().GetNMSVersion() + ".entity.CraftPlayer")
                                            .getDeclaredMethod("loadData");
                this._loadDataMethod.setAccessible(true);
            }

            this._loadDataMethod.invoke(cPlayer);

            player.set((Player) cPlayer);
            return player.get();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public Player GetPlayer(CommandSender sender, String name) {
        return this.GetPlayer(sender, name, null);
    }

    public Player GetPlayer(CommandSender sender, String name, UUID uuid) {
        Player player = null;

        if (name != null)
            player = Bukkit.getPlayer(name);
        else if (uuid != null)
            player = Bukkit.getPlayer(uuid);

        if (player == null)
            return null;
        if (sender instanceof Player)
            if (!this._plugin.GetVanish().IsVanish(player) || this._plugin.GetPermissions().HasPermission(sender, "vanish.see", true))
                return player;
            else
                return null;
        return player;
    }

    public Player GetPlayer(CommandSender sender, UUID uuid) {
        return this.GetPlayer(sender, null, uuid);
    }
}
