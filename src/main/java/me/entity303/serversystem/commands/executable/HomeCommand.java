package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.tabcompleter.HomeTabCompleter;
import me.entity303.serversystem.utils.ChatColor;
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

@ServerSystemCommand(name = "Home", tabCompleter = HomeTabCompleter.class)
public class HomeCommand implements ICommandExecutorOverload {
    private final ServerSystem _plugin;

    public HomeCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }
        if (this._plugin.GetPermissions().GetConfiguration().GetBoolean("Permissions.home.required")) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "home.permission")) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("home.permission")));
                return true;
            }
        }

        var homesFile = new File("plugins//ServerSystem//Homes", ((Player) commandSender).getUniqueId() + ".yml");
        var cfg = YamlConfiguration.loadConfiguration(homesFile);

        if (!homesFile.exists()) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "Home.NoHomes"));
            return true;
        }

        if (arguments.length == 0) {
            this.SendHomeList(cfg, commandSender, command, commandLabel);
            return true;
        }

        if (cfg.get("Homes." + arguments[0].toUpperCase()) == null) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command.getName(), commandSender, null,
                                                                                                       "Home.HomeDoesntExist")
                                                                                           .replace("<HOME>", arguments[0].toUpperCase()));
            return true;
        }

        if (this._plugin.GetConfigReader().GetBoolean("teleportation.home.enableDelay") &&
            !this._plugin.GetPermissions().HasPermission(commandSender, "home.bypassdelay", true)) {
            this._plugin.GetTeleportMap().put(((Player) commandSender), Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
                OfflinePlayer player = ((OfflinePlayer) commandSender).getPlayer();
                assert player != null;
                if (!player.isOnline()) return;

                var location = (Location) cfg.get("Homes." + arguments[0].toUpperCase());

                var player1 = Objects.requireNonNull(player.getPlayer());
                player1.teleport(location);

                commandSender.sendMessage(HomeCommand.this._plugin.GetMessages().GetPrefix() + ChatColor.TranslateAlternateColorCodes('&',
                                                                                                                                      HomeCommand.this._plugin.GetMessages()
                                                                                                                                                              .GetConfiguration()
                                                                                                                                                              .GetString(
                                                                                                                                                                      "Messages.Misc.Teleportation.Success")));
                HomeCommand.this._plugin.GetTeleportMap().remove(player);
            }, 20L * this._plugin.GetConfigReader().GetInt("teleportation.home.delay")));
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessage(commandLabel, command.getName(), commandSender, null,
                                                                                                       "Home.Teleporting")
                                                                                           .replace("<HOME>", arguments[0].toUpperCase()));
            return true;
        }

        var location = (Location) cfg.get("Homes." + arguments[0].toUpperCase());

        ((Player) commandSender).teleport(location);

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                       .GetMessage(commandLabel, command.getName(), commandSender, null,
                                                                                                   "Home.InstantTeleporting")
                                                                                       .replace("<HOME>", arguments[0].toUpperCase()));
        return true;
    }

    private void SendHomeList(FileConfiguration cfg, CommandSender commandSender, Command command, String commandLabel) {
        try {
            var homeBuilder = new StringBuilder();
            var separator = this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "Home.HomeFormat.Separator");
            var homeFormat = this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "Home.HomeFormat.Format");

            if (cfg.getConfigurationSection("Homes") == null) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "Home.NoHomes"));
                return;
            }

            List<String> homes = new ArrayList<>(cfg.getConfigurationSection("Homes").getKeys(false));

            for (var home : homes)
                homeBuilder.append(homeFormat.replace("<SEPERATOR>", separator).replace("<HOME>", home));

            if (homeBuilder.toString().toLowerCase().startsWith(separator)) homeBuilder.delete(0, separator.length());

            var homeMessage = this._plugin.GetMessages()
                                          .GetMessage(commandLabel, command.getName(), commandSender, null, "Home.HomeFormat.Message")
                                          .replace("<AMOUNT>", String.valueOf(homes.size()))
                                          .replace("<HOMES>", homeBuilder.toString());

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + homeMessage);
        } catch (ArrayIndexOutOfBoundsException ignored) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "Home.NoHomes"));
        }
    }
}
