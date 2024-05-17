package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

public class EnderChestCommand implements ICommandExecutorOverload, Listener {

    protected final ServerSystem _plugin;

    public EnderChestCommand(ServerSystem plugin) {
        this._plugin = plugin;

        this._plugin.GetEventManager().RegisterEvent(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (arguments.length == 0) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "enderchest.self")) {
                var permission = this._plugin.GetPermissions().GetPermission("enderchest.self");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return true;
            }
            ((Player) commandSender).openInventory(((Player) commandSender).getEnderChest());
            return true;
        }

        if (!this._plugin.GetPermissions().HasPermission(commandSender, "enderchest.others")) {
            var permission = this._plugin.GetPermissions().GetPermission("enderchest.others");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        var targetPlayer = CommandUtils.GetPlayer(this._plugin, commandSender, arguments[0]);
        if (targetPlayer == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoTarget(arguments[0]));
            return true;
        }

        if (this._plugin.GetPermissions().HasPermission(targetPlayer, "enderchest.exempt", true))
            this._plugin.GetEnderchest().put(((Player) commandSender), targetPlayer);

        ((Player) commandSender).openInventory(targetPlayer.getEnderChest());
        return true;
    }

    @EventHandler
    public void OnQuit(PlayerQuitEvent event) {
        var inventory = event.getPlayer().getEnderChest();

        var tryOfflineCommand = this._plugin.GetConfigReader().GetBoolean("invseeAndEndechest.tryOfflineCommandOnPlayerQuit");

        if (tryOfflineCommand)
            this.TryOfflineEnderChest(inventory, event.getPlayer());
    }

    private void TryOfflineEnderChest(Inventory inventory, Player target) {
        for (var human : new ArrayList<>(inventory.getViewers())) {
            var cursorStack = human.getItemOnCursor();

            human.setItemOnCursor(null);

            human.closeInventory();

            Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
                Bukkit.getScheduler().runTaskLater(this._plugin, () -> human.setItemOnCursor(cursorStack), 2L);

                if (!(human instanceof Player player))
                    return;

                if (!this._plugin.GetPermissions().HasPermission(player, "offlineenderchest", true))
                    return;

                player.chat("/offlineenderchest " + target);
            }, 2L);

            human.sendMessage(this._plugin.GetMessages().GetPrefix() +
                              this._plugin.GetMessages().GetMessage("enderchest", "enderchest", human, target, "EnderChest.PlayerWentOffline"));
        }
    }
}
