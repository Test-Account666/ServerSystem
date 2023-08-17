package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class EnderChestCommand extends MessageUtils implements CommandExecutor, Listener {

    public EnderChestCommand(ServerSystem plugin) {
        super(plugin);

        this.plugin.getEventManager().re(this);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }

        if (args.length == 0) {
            if (!this.isAllowed(cs, "enderchest.self")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("enderchest.self")));
                return true;
            }
            ((Player) cs).openInventory(((Player) cs).getEnderChest());
            return true;
        }

        if (!this.isAllowed(cs, "enderchest.others")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("enderchest.others")));
            return true;
        }

        Player targetPlayer = this.getPlayer(cs, args[0]);
        if (targetPlayer == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }
        if (this.isAllowed(targetPlayer, "enderchest.exempt", true))
            this.plugin.getEnderchest().put(((Player) cs), targetPlayer);
        ((Player) cs).openInventory(targetPlayer.getEnderChest());
        return true;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Inventory inventory = e.getPlayer().getEnderChest();

        boolean tryOfflineCommand = this.plugin.getConfigReader().getBoolean("invseeAndEndechest.tryOfflineCommandOnPlayerQuit");

        if (tryOfflineCommand)
            this.tryOfflineEnderChest(inventory, e.getPlayer());
    }

    private void tryOfflineEnderChest(Inventory inventory, Player target) {
        for (HumanEntity human : new ArrayList<>(inventory.getViewers())) {
            ItemStack cursorStack = human.getItemOnCursor();

            human.setItemOnCursor(null);

            human.closeInventory();

            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    human.setItemOnCursor(cursorStack);
                }, 2L);

                if (!(human instanceof Player))
                    return;

                Player player = (Player) human;

                if (!this.isAllowed(player, "offlineenderchest", true))
                    return;

                player.chat("/offlineenderchest " + target.getName());
            }, 2L);

            human.sendMessage(this.getPrefix() + this.getMessage("EnderChest.PlayerWentOffline", "enderchest", "enderchest", human, target));
        }
    }
}
