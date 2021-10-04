package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class COMMAND_sethome implements CommandExecutor {
    private final ss plugin;

    public COMMAND_sethome(ss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }
        if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.sethome.general.required"))
            if (!this.plugin.getPermissions().hasPerm(cs, "sethome.general.permission")) {
                cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("sethome.general.permission")));
                return true;
            }
        if (args.length <= 0) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(label, cmd.getName(), cs, null, "SetHome"));
            return true;
        }
        File homeFile = new File("plugins//ServerSystem//Homes", ((Player) cs).getUniqueId() + ".yml");
        FileConfiguration homeCfg = YamlConfiguration.loadConfiguration(homeFile);
        List<String> homes = new ArrayList<>();

        if (homeFile.exists() && homeCfg.getConfigurationSection("Homes") != null)
            homes.addAll(homeCfg.getConfigurationSection("Homes").getKeys(false));

        if (!this.allowMoreHomes(((Player) cs), homes.size())) {
            cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "SetHome.MaxReached"));
            return true;
        }
        homeCfg.set("Homes." + args[0].toUpperCase(), ((Player) cs).getLocation());
        cs.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(label, cmd.getName(), cs, null, "SetHome.Success").replace("<HOME>", args[0].toUpperCase()));
        try {
            homeCfg.save(homeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean allowMoreHomes(Player player, Integer homes) {
        if (this.plugin.getPermissions().hasPerm(player, "sethome.bypassmax", true)) return true;

        String permissions = null;
        for (String amount : this.plugin.getPermissions().getCfg().getConfigurationSection("Permissions.sethome.maxhomes").getKeys(false)) {
            if (amount.equalsIgnoreCase("default")) continue;
            if (Integer.parseInt(amount) > homes) {
                permissions = this.plugin.getPermissions().getCfg().getString("Permissions.sethome.maxhomes." + amount);
                if (permissions == null) {
                    this.plugin.error("Sethome Permission " + amount + " cannot be null!");
                    continue;
                }
                if (player.hasPermission(permissions)) return true;
            }
        }
        return false;
    }
}
