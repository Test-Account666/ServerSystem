package me.entity303.serversystem.commands.executable;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.FileUtils;
import me.entity303.serversystem.utils.CommandUtils;
import net.ess3.api.MaxMoneyException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerSystemConversionCommand extends CommandUtils implements CommandExecutorOverload {
    private boolean starting = false;

    public ServerSystemConversionCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "converttoessentials")) {
            var permission = this.plugin.getPermissions().getPermission("converttoessentials");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (!this.starting) {
            
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "ConvertToEssentials.WarnNotTested"));
            this.starting = true;
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.starting = false, 20 * 10);
            return true;
        }

        var command1 = command.getName();
        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command1, commandSender, null, "ConvertToEssentials.Start"));

        var essentialsDirectory = new File("plugins//Essentials");
        var dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm-ss");
        var now = LocalDateTime.now();
        var backupDate = dtf.format(now);

        try {
            FileUtils.copyFile(essentialsDirectory, new File("plugins//Essentials-Backups//Essentials-Backup-" + backupDate));
        } catch (IOException e) {
            e.printStackTrace();
        }

        var essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");

        var error = false;

        for (var offlinePlayer : Bukkit.getOfflinePlayers()) {
            var economy = this.plugin.getEconomyManager();

            double balance = economy.getMoneyAsNumber(offlinePlayer);

            User user = null;

            if (offlinePlayer.getName() != null)
                user = essentials.getOfflineUser(offlinePlayer.getName());
            else if (offlinePlayer.getUniqueId() != null)
                essentials.getUser(offlinePlayer.getUniqueId());

            if (user == null)
                continue;

            try {
                user.setMoney(new BigDecimal(balance));
            } catch (MaxMoneyException e) {
                e.printStackTrace();
                error = true;
            }


            var homeManager = this.plugin.getHomeManager();

            for (var home : homeManager.getHomes(offlinePlayer).entrySet()) {
                var name = home.getKey();
                var location = home.getValue();

                user.setHome(name, location);
            }

            if (this.plugin.getVanish().isVanish(offlinePlayer))
                user.setVanished(true);

            if (!this.plugin.getWantsTeleport().wantsTeleport(offlinePlayer))
                user.setTeleportEnabled(false);

            //TODO: Convert bans and mutes

            user.save();
        }

        var warps = essentials.getWarps();

        if (warps != null) {
            var warpManager = this.plugin.getWarpManager();

            for (var warp : warpManager.getWarps())
                try {
                    warps.setWarp(warp, warpManager.getWarp(warp));
                } catch (Exception e) {
                    e.printStackTrace();
                    error = true;
                }
        }

        //TODO: Convert kits

        if (error) {
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "ConvertToEssentials.FinishedWithErrors"));
            return true;
        }

        
        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "ConvertToEssentials.Finished"));
        return true;
    }
}
