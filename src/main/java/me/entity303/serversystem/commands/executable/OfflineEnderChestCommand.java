package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.DummyCommandSender;
import me.entity303.serversystem.utils.MessageUtils;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class OfflineEnderChestCommand extends MessageUtils implements TabExecutor, Listener {
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
        boolean tryOnlineCommand = this.plugin.getConfigReader().getBoolean("invseeAndEndechest.tryOnlineCommandOnPlayerJoin");

        Player target = this.cachedInventories.keySet().stream().filter(player -> player.getUniqueId().toString().equalsIgnoreCase(e.getUniqueId().toString())).findFirst().orElse(null);
        if (target != null) {
            target.saveData();
            Bukkit.getScheduler().runTask(this.plugin, () -> {
                for (HumanEntity human : new ArrayList<>(target.getEnderChest().getViewers())) {
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

                            if (!this.isAllowed(player, "enderchest.others", true))
                                return;

                            player.chat("/enderchest " + target.getName());
                        }, 2L);

                    human.sendMessage(this.getPrefix() + this.getMessage("OfflineEnderChest.PlayerCameOnline", "offlineenderchest", "offlineenderchest", human, target));
                }

                this.cachedInventories.remove(target);
            });
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!this.isAllowed(sender, "offlineenderchest", true))
            return Collections.singletonList("");

        List<String> players = Arrays.stream(Bukkit.getOfflinePlayers()).filter(offlinePlayer -> !offlinePlayer.isOnline()).map(OfflinePlayer::getName).collect(Collectors.toList());

        List<String> possiblePlayers = new ArrayList<>();

        for (String player : players)
            if (player.toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT)))
                possiblePlayers.add(player);

        return !possiblePlayers.isEmpty() ? possiblePlayers : players;
    }
}
