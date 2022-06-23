package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.DummyCommandSender;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class OfflineEnderChestCommand extends MessageUtils implements CommandExecutor, Listener {
    private final HashMap<Player, Inventory> cachedInventories = new HashMap<>();

    public OfflineEnderChestCommand(ServerSystem plugin) {
        super(plugin);

        plugin.getEventManager().re(this);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "offlineenderchest")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("offlineenderchest")));
            return true;
        }

        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }

        if (args.length == 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("OfflineEnderChest", label, cmd.getName(), cs, null));
            return true;
        }

        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(args[0]);

        if (!offlineTarget.hasPlayedBefore()) {
            String name = offlineTarget.getName();
            if (name == null) name = args[0];
            cs.sendMessage(this.getPrefix() + this.getMessage("OfflineEnderChest.NeverPlayed", label, cmd.getName(), cs, new DummyCommandSender(name)));
            return true;
        }

        if (offlineTarget.isOnline()) {
            if (this.getPlayer(cs, args[0]) == null) {
                ((Player) cs).openInventory(offlineTarget.getPlayer().getEnderChest());
                return true;
            }
            cs.sendMessage(this.getPrefix() + this.getMessage("OfflineEnderChest.PlayerIsOnline", label, cmd.getName(), cs, offlineTarget.getPlayer()));
            return true;
        }

        Player player = this.getHookedPlayer(offlineTarget);

        this.cachedInventories.put(player, player.getEnderChest());

        ((Player) cs).openInventory(player.getEnderChest());

        AtomicInteger taskId = new AtomicInteger(-1);

        taskId.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            if (((Player) cs).getOpenInventory().getTopInventory() != player.getEnderChest()) {
                if (!player.isOnline())
                    player.saveData();
                Bukkit.getScheduler().cancelTask(taskId.get());
            }
        }, 10L, 10L));
        return true;
    }

    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent e) {
        Player target = this.cachedInventories.keySet().stream().filter(player -> player.getUniqueId().toString().equalsIgnoreCase(e.getUniqueId().toString())).findFirst().orElse(null);
        if (target != null) {
            target.saveData();
            Bukkit.getScheduler().runTask(this.plugin, () -> {
                for (HumanEntity human : new ArrayList<>(target.getEnderChest().getViewers())) {
                    human.closeInventory();
                    human.sendMessage(this.getPrefix() + this.getMessage("OfflineEnderChest.PlayerCameOnline", "invsee", "invsee", human, target));
                }

                this.cachedInventories.remove(target);
            });
        }
    }
}
