package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SetHomeCommand implements CommandExecutorOverload {
    private final ServerSystem plugin;

    public SetHomeCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }
        if (this.plugin.getPermissions().getConfiguration().getBoolean("Permissions.sethome.general.required"))
            if (!this.plugin.getPermissions().hasPermission(commandSender, "sethome.general.permission")) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                          this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("sethome.general.permission")));
                return true;
            }
        if (arguments.length == 0) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command.getName(), commandSender, null, "SetHome"));
            return true;
        }
        var homeFile = new File("plugins//ServerSystem//Homes", ((Player) commandSender).getUniqueId() + ".yml");
        FileConfiguration homeCfg = YamlConfiguration.loadConfiguration(homeFile);
        List<String> homes = new ArrayList<>();

        if (homeFile.exists() && homeCfg.getConfigurationSection("Homes") != null)
            homes.addAll(homeCfg.getConfigurationSection("Homes").getKeys(false));

        if (!this.allowMoreHomes(((Player) commandSender), homes.size())) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "SetHome.MaxReached"));
            return true;
        }
        homeCfg.set("Homes." + arguments[0].toUpperCase(), ((Player) commandSender).getLocation());
        commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                  this.plugin.getMessages().getMessage(commandLabel, command.getName(), commandSender, null, "SetHome.Success").replace("<HOME>", arguments[0].toUpperCase()));
        try {
            homeCfg.save(homeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean allowMoreHomes(Player player, Integer homes) {
        if (this.plugin.getPermissions().hasPermission(player, "sethome.bypassmax", true))
            return true;

        String permissions;
        for (var amount : this.plugin.getPermissions().getConfiguration().getConfigurationSection("Permissions.sethome.maxhomes").getKeys(false)) {
            if (amount.equalsIgnoreCase("default"))
                continue;
            if (Integer.parseInt(amount) > homes) {
                permissions = this.plugin.getPermissions().getConfiguration().getString("Permissions.sethome.maxhomes." + amount);
                if (permissions == null) {
                    this.plugin.error("Sethome Permission " + amount + " cannot be null!");
                    continue;
                }
                if (player.hasPermission(permissions))
                    return true;
            }
        }
        return false;
    }
}
