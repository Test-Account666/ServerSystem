package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
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

public class EnderChestCommand extends CommandUtils implements CommandExecutorOverload, Listener {

    public EnderChestCommand(ServerSystem plugin) {
        super(plugin);

        this.plugin.getEventManager().registerEvent(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (arguments.length == 0) {
            if (!this.plugin.getPermissions().hasPermission(commandSender, "enderchest.self")) {
                var permission = this.plugin.getPermissions().getPermission("enderchest.self");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
                return true;
            }
            ((Player) commandSender).openInventory(((Player) commandSender).getEnderChest());
            return true;
        }

        if (!this.plugin.getPermissions().hasPermission(commandSender, "enderchest.others")) {
            var permission = this.plugin.getPermissions().getPermission("enderchest.others");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        var targetPlayer = this.getPlayer(commandSender, arguments[0]);
        if (targetPlayer == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }

        if (this.plugin.getPermissions().hasPermission(targetPlayer, "enderchest.exempt", true))
            this.plugin.getEnderchest().put(((Player) commandSender), targetPlayer);

        ((Player) commandSender).openInventory(targetPlayer.getEnderChest());
        return true;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        var inventory = e.getPlayer().getEnderChest();

        var tryOfflineCommand = this.plugin.getConfigReader().getBoolean("invseeAndEndechest.tryOfflineCommandOnPlayerQuit");

        if (tryOfflineCommand)
            this.tryOfflineEnderChest(inventory, e.getPlayer());
    }

    private void tryOfflineEnderChest(Inventory inventory, Player target) {
        for (var human : new ArrayList<>(inventory.getViewers())) {
            var cursorStack = human.getItemOnCursor();

            human.setItemOnCursor(null);

            human.closeInventory();

            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                Bukkit.getScheduler().runTaskLater(this.plugin, () -> human.setItemOnCursor(cursorStack), 2L);

                if (!(human instanceof Player player))
                    return;

                if (!this.plugin.getPermissions().hasPermission(player, "offlineenderchest", true))
                    return;

                player.chat("/offlineenderchest " + target);
            }, 2L);

            human.sendMessage(this.plugin.getMessages().getPrefix() +
                              this.plugin.getMessages().getMessage("enderchest", "enderchest", human, target, "EnderChest.PlayerWentOffline"));
        }
    }
}
