package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.Teleport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeCommand implements CommandExecutor {
    private final ServerSystem plugin;

    public HomeCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }
        if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.home.required"))
            if (!this.plugin.getPermissions().hasPerm(cs, "home.permission")) {
                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("home.permission")));
                return true;
            }
        File f = new File("plugins//ServerSystem//Homes", ((Player) cs).getUniqueId() + ".yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        if (!f.exists()) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "Home.NoHomes"));
            return true;
        }

        if (args.length <= 0) {
            try {
                StringBuilder homeBuilder = new StringBuilder();
                String seperator = this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "Home.HomeFormat.Separator");
                String homeFormat = this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "Home.HomeFormat.Format");

                if (cfg.getConfigurationSection("Homes") == null) {
                    cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "Home.NoHomes"));
                    return true;
                }

                List<String> homes = new ArrayList<>(cfg.getConfigurationSection("Homes").getKeys(false));

                for (String home : homes)
                    homeBuilder.append(homeFormat.replace("<SEPERATOR>", seperator).replace("<HOME>", home));

                if (homeBuilder.toString().toLowerCase().startsWith(seperator))
                    homeBuilder.delete(0, seperator.length());

                String homeMessage = this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "Home.HomeFormat.Message").replace("<AMOUNT>", String.valueOf(homes.size())).replace("<HOMES>", homeBuilder.toString());

                cs.sendMessage(this.plugin.getMessages().getPrefix() + homeMessage);
            } catch (ArrayIndexOutOfBoundsException ignored) {
                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "Home.NoHomes"));
                return true;
            }
            return true;
        }
        if (cfg.get("Homes." + args[0].toUpperCase()) == null) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "Home.HomeDoesntExist").replace("<HOME>", args[0].toUpperCase()));
            return true;
        }

        if (this.plugin.getConfigReader().getBoolean("teleportation.home.enableDelay") && !this.plugin.getPermissions().hasPerm(cs, "home.bypassdelay", true)) {
            this.plugin.getTeleportMap().put(((Player) cs), Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                OfflinePlayer player = ((OfflinePlayer) cs).getPlayer();
                if (player.isOnline()) {
                    Location location = (Location) cfg.get("Homes." + args[0].toUpperCase());

                    Teleport.teleport(player.getPlayer(), location);

                    cs.sendMessage(HomeCommand.this.plugin.getMessages().getPrefix() + ChatColor.translateAlternateColorCodes('&', HomeCommand.this.plugin.getMessages().getCfg().getString("Messages.Misc.Teleportation.Success")));
                    HomeCommand.this.plugin.getTeleportMap().remove(player);
                }
            }, 20L * this.plugin.getConfigReader().getInt("teleportation.home.delay")));
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "Home.Teleporting").replace("<HOME>", args[0].toUpperCase()));
            return true;
        }
        Location location = (Location) cfg.get("Homes." + args[0].toUpperCase());

        Teleport.teleport((Player) cs, location);

        cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "Home.InstantTeleporting").replace("<HOME>", args[0].toUpperCase()));
        return true;
    }
}
