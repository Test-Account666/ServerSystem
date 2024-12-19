package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ITabExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@ServerSystemCommand(name = "OfflineEnderChest")
public class OfflineEnderChestCommand implements ITabExecutorOverload, Listener {
    protected final ServerSystem _plugin;
    private final Map<Player, Inventory> _cachedInventories = new HashMap<>();

    public OfflineEnderChestCommand(ServerSystem plugin) {
        this._plugin = plugin;

        plugin.GetEventManager().RegisterEvent(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "offlineenderchest")) {
            var permission = this._plugin.GetPermissions().GetPermission("offlineenderchest");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "OfflineEnderChest"));
            return true;
        }

        var offlineTarget = Bukkit.getOfflinePlayer(arguments[0]);

        if (!offlineTarget.hasPlayedBefore()) {
            var name = offlineTarget.getName();
            if (name == null) name = arguments[0];
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessageWithStringTarget(commandLabel, command, commandSender, name, "OfflineEnderChest.NeverPlayed"));
            return true;
        }

        if (offlineTarget.isOnline()) {
            if (CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]) == null) {
                ((HumanEntity) commandSender).openInventory(offlineTarget.getPlayer().getEnderChest());
                return true;
            }
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command, commandSender, offlineTarget.getPlayer(),
                                                                                                       "OfflineEnderChest.PlayerIsOnline"));
            return true;
        }

        var player = CommandUtils.GetHookedPlayer(this._plugin, offlineTarget);

        this._cachedInventories.put(player, player.getEnderChest());

        ((Player) commandSender).openInventory(player.getEnderChest());

        var taskId = new AtomicInteger(-1);

        taskId.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(this._plugin, () -> {
            if (((Player) commandSender).getOpenInventory().getTopInventory() != player.getEnderChest()) {
                if (!player.isOnline()) player.saveData();
                Bukkit.getScheduler().cancelTask(taskId.get());
            }
        }, 10L, 10L));
        return true;
    }

    @EventHandler
    public void OnAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        var tryOnlineCommand = this._plugin.GetConfigReader().GetBoolean("invseeAndEndechest.tryOnlineCommandOnPlayerJoin");

        var target = this._cachedInventories.keySet()
                                            .stream()
                                            .filter(player -> player.getUniqueId().toString().equalsIgnoreCase(event.getUniqueId().toString()))
                                            .findFirst()
                                            .orElse(null);
        if (target != null) {
            target.saveData();
            Bukkit.getScheduler().runTask(this._plugin, () -> {
                for (var human : new ArrayList<>(target.getEnderChest().getViewers())) {
                    var cursorStack = human.getItemOnCursor();

                    if (tryOnlineCommand) human.setItemOnCursor(null);

                    human.closeInventory();

                    if (tryOnlineCommand) {
                        Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
                            Bukkit.getScheduler().runTaskLater(this._plugin, () -> human.setItemOnCursor(cursorStack), 2L);

                            if (!(human instanceof Player player)) return;

                            if (!this._plugin.GetPermissions().HasPermission(player, "enderchest.others", true)) return;

                            player.chat("/enderchest " + target);
                        }, 2L);
                    }

                    human.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage("offlineenderchest", "offlineenderchest", human, target,
                                                                                                       "OfflineEnderChest.PlayerCameOnline"));
                }

                this._cachedInventories.remove(target);
            });
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "offlineenderchest", true)) return Collections.singletonList("");

        return GetOfflinePlayers(arguments);
    }

    static List<String> GetOfflinePlayers(String... arguments) {
        var players = new ArrayList<String>();
        for (var offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.isOnline()) continue;

            var name = offlinePlayer.getName();
            players.add(name);
        }

        var possiblePlayers = new ArrayList<String>();

        for (var player : players) {
            if (!player.toLowerCase(Locale.ROOT).startsWith(arguments[0].toLowerCase(Locale.ROOT))) continue;

            possiblePlayers.add(player);
        }

        return !possiblePlayers.isEmpty()? possiblePlayers : players;
    }
}
