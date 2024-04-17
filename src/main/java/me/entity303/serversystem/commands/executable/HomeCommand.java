package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.Teleport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeCommand implements CommandExecutorOverload {
    private final ServerSystem plugin;

    public HomeCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }
        if (this.plugin.getPermissions().getConfiguration().getBoolean("Permissions.home.required"))
            if (!this.plugin.getPermissions().hasPermission(commandSender, "home.permission")) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("home.permission")));
                return true;
            }

        var homesFile = new File("plugins//ServerSystem//Homes", ((Player) commandSender).getUniqueId() + ".yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(homesFile);

        if (!homesFile.exists()) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "Home.NoHomes"));
            return true;
        }

        if (arguments.length == 0) {
            this.SendHomeList(cfg, commandSender, command, commandLabel);
            return true;
        }

        if (cfg.get("Homes." + arguments[0].toUpperCase()) == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage(commandLabel, command.getName(), commandSender, null,
                                                                                                     "Home.HomeDoesntExist")
                                                                                         .replace("<HOME>", arguments[0].toUpperCase()));
            return true;
        }

        if (this.plugin.getConfigReader().getBoolean("teleportation.home.enableDelay") &&
            !this.plugin.getPermissions().hasPermission(commandSender, "home.bypassdelay", true)) {
            this.plugin.getTeleportMap().put(((Player) commandSender), Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                OfflinePlayer player = ((OfflinePlayer) commandSender).getPlayer();
                assert player != null;
                if (!player.isOnline())
                    return;

                var location = (Location) cfg.get("Homes." + arguments[0].toUpperCase());

                Teleport.teleport(Objects.requireNonNull(player.getPlayer()), location);

                commandSender.sendMessage(HomeCommand.this.plugin.getMessages().getPrefix() + ChatColor.translateAlternateColorCodes('&',
                                                                                                                                     HomeCommand.this.plugin.getMessages()
                                                                                                                                                            .getCfg()
                                                                                                                                                            .getString(
                                                                                                                                                                    "Messages.Misc.Teleportation.Success")));
                HomeCommand.this.plugin.getTeleportMap().remove(player);
            }, 20L * this.plugin.getConfigReader().getInt("teleportation.home.delay")));
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage(commandLabel, command.getName(), commandSender, null,
                                                                                                     "Home.Teleporting")
                                                                                         .replace("<HOME>", arguments[0].toUpperCase()));
            return true;
        }

        var location = (Location) cfg.get("Homes." + arguments[0].toUpperCase());

        Teleport.teleport((Player) commandSender, location);

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                     .getMessage(commandLabel, command.getName(), commandSender, null,
                                                                                                 "Home.InstantTeleporting")
                                                                                     .replace("<HOME>", arguments[0].toUpperCase()));
        return true;
    }

    private void SendHomeList(FileConfiguration cfg, CommandSender commandSender, Command command, String commandLabel) {
        try {
            var homeBuilder = new StringBuilder();
            var separator = this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "Home.HomeFormat.Separator");
            var homeFormat = this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "Home.HomeFormat.Format");

            if (cfg.getConfigurationSection("Homes") == null) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "Home.NoHomes"));
                return;
            }

            List<String> homes = new ArrayList<>(cfg.getConfigurationSection("Homes").getKeys(false));

            for (var home : homes)
                homeBuilder.append(homeFormat.replace("<SEPERATOR>", separator).replace("<HOME>", home));

            if (homeBuilder.toString().toLowerCase().startsWith(separator))
                homeBuilder.delete(0, separator.length());

            var homeMessage = this.plugin.getMessages()
                                         .getMessage(commandLabel, command.getName(), commandSender, null, "Home.HomeFormat.Message")
                                         .replace("<AMOUNT>", String.valueOf(homes.size()))
                                         .replace("<HOMES>", homeBuilder.toString());

            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + homeMessage);
        } catch (ArrayIndexOutOfBoundsException ignored) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "Home.NoHomes"));
        }
    }
}
