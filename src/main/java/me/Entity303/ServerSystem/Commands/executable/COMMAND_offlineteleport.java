package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.DummyCommandSender;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

public class COMMAND_offlineteleport extends MessageUtils implements CommandExecutor {

    private Method loadDataMethod = null;

    public COMMAND_offlineteleport(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "offlineteleport")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("offlineteleport")));
            return true;
        }

        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }

        if (args.length == 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("OfflineTeleport", label, cmd.getName(), cs, null));
            return true;
        }

        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(args[0]);

        if (!offlineTarget.hasPlayedBefore()) {
            String name = offlineTarget.getName();
            if (name == null) name = args[0];
            cs.sendMessage(this.getPrefix() + this.getMessage("OfflineTeleport.NeverPlayed", label, cmd.getName(), cs, new DummyCommandSender(name)));
            return true;
        }

        if (offlineTarget.isOnline()) {
            if (this.getPlayer(cs, offlineTarget.getUniqueId()) == null) {
                ((Player) cs).teleport(offlineTarget.getPlayer());
                cs.sendMessage(this.getPrefix() + this.getMessage("OfflineTeleport.Success", label, cmd.getName(), cs, offlineTarget.getPlayer()));
                return true;
            }
            cs.sendMessage(this.getPrefix() + this.getMessage("OfflineTeleport.PlayerIsOnline", label, cmd.getName(), cs, offlineTarget.getPlayer()));
            return true;
        }

        try {
            AtomicReference<Player> player = new AtomicReference<>(null);
            Object cPlayer = new ByteBuddy()
                    .subclass(Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".entity.CraftPlayer"))
                    .method(ElementMatchers.named("saveData"))
                    .intercept(MethodCall.invokeSuper().
                            andThen(MethodCall.run(() -> this.plugin.getVersionStuff().getSaveData().saveData(player.get()))))
                    .make()
                    .load(this.plugin.getClass().getClassLoader())
                    .getLoaded()
                    .getConstructors()[0]
                    .newInstance(Bukkit.getServer(), this.plugin.getVersionStuff().getEntityPlayer().getEntityPlayer(offlineTarget));

            if (this.loadDataMethod == null) {
                this.loadDataMethod = Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".entity.CraftPlayer").getDeclaredMethod("loadData");
                this.loadDataMethod.setAccessible(true);
            }

            this.loadDataMethod.invoke(cPlayer);

            player.set((Player) cPlayer);

            Location location = player.get().getLocation();
            ((Player) cs).teleport(location);
            cs.sendMessage(this.getPrefix() + this.getMessage("OfflineTeleport.Success", label, cmd.getName(), cs, player.get()));
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return true;
    }
}
