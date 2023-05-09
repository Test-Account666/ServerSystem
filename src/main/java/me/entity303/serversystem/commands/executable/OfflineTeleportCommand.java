package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.DummyCommandSender;
import me.entity303.serversystem.utils.MessageUtils;
import me.entity303.serversystem.utils.Teleport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class OfflineTeleportCommand extends MessageUtils implements TabExecutor {

    public OfflineTeleportCommand(ServerSystem plugin) {
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

                Teleport.teleport((Player) cs, offlineTarget.getPlayer());

                cs.sendMessage(this.getPrefix() + this.getMessage("OfflineTeleport.Success", label, cmd.getName(), cs, offlineTarget.getPlayer()));
                return true;
            }
            cs.sendMessage(this.getPrefix() + this.getMessage("OfflineTeleport.PlayerIsOnline", label, cmd.getName(), cs, offlineTarget.getPlayer()));
            return true;
        }

        Player player = this.getHookedPlayer(offlineTarget);

        Location location = player.getLocation();

        Teleport.teleport((Player) cs, location);

        cs.sendMessage(this.getPrefix() + this.getMessage("OfflineTeleport.Success", label, cmd.getName(), cs, player));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!this.isAllowed(sender, "offlineteleport", true))
            return Collections.singletonList("");

        List<String> players = Arrays.stream(Bukkit.getOfflinePlayers()).filter(offlinePlayer -> !offlinePlayer.isOnline()).map(OfflinePlayer::getName).collect(Collectors.toList());

        List<String> possiblePlayers = new ArrayList<>();

        for (String player : players)
            if (player.toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT)))
                possiblePlayers.add(player);

        return !possiblePlayers.isEmpty() ? possiblePlayers : players;
    }
}
