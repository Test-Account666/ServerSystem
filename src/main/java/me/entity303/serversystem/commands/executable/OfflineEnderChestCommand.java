package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import me.entity303.serversystem.utils.DummyCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class OfflineEnderChestCommand extends CommandUtils implements CommandExecutorOverload, TabCompleter, Listener {
    private final HashMap<Player, Inventory> cachedInventories = new HashMap<>();

    public OfflineEnderChestCommand(ServerSystem plugin) {
        super(plugin);

        plugin.getEventManager().registerEvent(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] argument) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "offlineenderchest")) {
            var permission = this.plugin.getPermissions().getPermission("offlineenderchest");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (argument.length == 0) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "OfflineEnderChest"));
            return true;
        }

        var offlineTarget = Bukkit.getOfflinePlayer(argument[0]);

        if (!offlineTarget.hasPlayedBefore()) {
            var name = offlineTarget.getName();
            if (name == null)
                name = argument[0];
            CommandSender target = new DummyCommandSender(name);
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "OfflineEnderChest.NeverPlayed"));
            return true;
        }

        if (offlineTarget.isOnline()) {
            if (this.getPlayer(commandSender, argument[0]) == null) {
                ((Player) commandSender).openInventory(offlineTarget.getPlayer().getEnderChest());
                return true;
            }
            CommandSender target = offlineTarget.getPlayer();
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "OfflineEnderChest.PlayerIsOnline"));
            return true;
        }

        var player = this.getHookedPlayer(offlineTarget);

        this.cachedInventories.put(player, player.getEnderChest());

        ((Player) commandSender).openInventory(player.getEnderChest());

        var taskId = new AtomicInteger(-1);

        taskId.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            if (((Player) commandSender).getOpenInventory().getTopInventory() != player.getEnderChest()) {
                if (!player.isOnline())
                    player.saveData();
                Bukkit.getScheduler().cancelTask(taskId.get());
            }
        }, 10L, 10L));
        return true;
    }

    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent e) {
        var tryOnlineCommand = this.plugin.getConfigReader().getBoolean("invseeAndEndechest.tryOnlineCommandOnPlayerJoin");

        var target = this.cachedInventories.keySet()
                                           .stream()
                                           .filter(player -> player.getUniqueId().toString().equalsIgnoreCase(e.getUniqueId().toString()))
                                           .findFirst()
                                           .orElse(null);
        if (target != null) {
            target.saveData();
            Bukkit.getScheduler().runTask(this.plugin, () -> {
                for (var human : new ArrayList<>(target.getEnderChest().getViewers())) {
                    var cursorStack = human.getItemOnCursor();

                    if (tryOnlineCommand)
                        human.setItemOnCursor(null);

                    human.closeInventory();

                    if (tryOnlineCommand)
                        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                            Bukkit.getScheduler().runTaskLater(this.plugin, () -> human.setItemOnCursor(cursorStack), 2L);

                            if (!(human instanceof Player player))
                                return;

                            if (!this.plugin.getPermissions().hasPermission(player, "enderchest.others", true))
                                return;

                            player.chat("/enderchest " + target);
                        }, 2L);

                    human.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage("offlineenderchest", "offlineenderchest", human, target,
                                                                                                     "OfflineEnderChest.PlayerCameOnline"));
                }

                this.cachedInventories.remove(target);
            });
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!this.plugin.getPermissions().hasPermission(sender, "offlineenderchest", true))
            return Collections.singletonList("");

        return GetOfflinePlayers(args);
    }

    static List<String> GetOfflinePlayers(String... args) {
        var players = Arrays.stream(Bukkit.getOfflinePlayers())
                            .filter(offlinePlayer -> !offlinePlayer.isOnline())
                            .map(OfflinePlayer::getName)
                            .collect(Collectors.toList());

        List<String> possiblePlayers = new ArrayList<>();

        for (var player : players)
            if (player.toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT)))
                possiblePlayers.add(player);

        return !possiblePlayers.isEmpty()? possiblePlayers : players;
    }
}
