package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ServerSystemCommand(name = "SetHome")
public class SetHomeCommand implements ICommandExecutorOverload {
    private final ServerSystem _plugin;

    public SetHomeCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }
        if (this._plugin.GetPermissions().GetConfiguration().GetBoolean("Permissions.sethome.general.required")) {
            if (!this._plugin.GetPermissions().HasPermission(commandSender, "sethome.general.permission")) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                          this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("sethome.general" + ".permission")));
                return true;
            }
        }
        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command.getName(), commandSender, null, "SetHome"));
            return true;
        }
        var homeFile = new File("plugins//ServerSystem//Homes", ((Player) commandSender).getUniqueId() + ".yml");
        var homeCfg = YamlConfiguration.loadConfiguration(homeFile);
        List<String> homes = new ArrayList<>();

        if (homeFile.exists() && homeCfg.getConfigurationSection("Homes") != null) homes.addAll(homeCfg.getConfigurationSection("Homes").getKeys(false));

        if (!this.AllowMoreHomes(((Player) commandSender), homes.size())) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command.getName(), commandSender, null, "SetHome.MaxReached"));
            return true;
        }
        homeCfg.set("Homes." + arguments[0].toUpperCase(), ((Player) commandSender).getLocation());
        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                       .GetMessage(commandLabel, command.getName(), commandSender, null,
                                                                                                   "SetHome.Success")
                                                                                       .replace("<HOME>", arguments[0].toUpperCase()));
        try {
            homeCfg.save(homeFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return true;
    }

    private boolean AllowMoreHomes(Player player, Integer homes) {
        if (this._plugin.GetPermissions().HasPermission(player, "sethome.bypassmax", true)) return true;

        String permissions;
        for (var amount : this._plugin.GetPermissions().GetConfiguration().GetConfigurationSection("Permissions.sethome.maxhomes").getKeys(false)) {
            if (amount.equalsIgnoreCase("default")) continue;
            if (Integer.parseInt(amount) > homes) {
                permissions = this._plugin.GetPermissions().GetConfiguration().GetString("Permissions.sethome.maxhomes." + amount);
                if (permissions == null) {
                    this._plugin.Error("Sethome Permission " + amount + " cannot be null!");
                    continue;
                }
                if (player.hasPermission(permissions)) return true;
            }
        }
        return false;
    }
}
