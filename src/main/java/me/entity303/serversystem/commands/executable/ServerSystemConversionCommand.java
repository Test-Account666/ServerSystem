package me.entity303.serversystem.commands.executable;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Warps;
import me.entity303.serversystem.databasemanager.HomeManager;
import me.entity303.serversystem.databasemanager.WarpManager;
import me.entity303.serversystem.economy.ManagerEconomy;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.FileUtils;
import me.entity303.serversystem.utils.MessageUtils;
import net.ess3.api.MaxMoneyException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ServerSystemConversionCommand extends MessageUtils implements CommandExecutor {
    private boolean starting = false;

    public ServerSystemConversionCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "converttoessentials")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("converttoessentials")));
            return true;
        }

        if (!this.starting) {
            cs.sendMessage(this.getPrefix() + this.getMessage("ConvertToEssentials.WarnNotTested", label, cmd.getName(), cs, null));
            this.starting = true;
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.starting = false, 20 * 10);
            return true;
        }

        cs.sendMessage(this.getPrefix() + this.getMessage("ConvertToEssentials.Start", label, cmd.getName(), cs, null));

        File essentialsDirectory = new File("plugins//Essentials");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String backupDate = dtf.format(now);

        try {
            FileUtils.copyFile(essentialsDirectory, new File("plugins//Essentials-Backups//Essentials-Backup-" + backupDate));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Essentials essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");

        boolean error = false;

        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            ManagerEconomy economy = this.plugin.getEconomyManager();

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


            HomeManager homeManager = this.plugin.getHomeManager();

            for (Map.Entry<String, Location> home : homeManager.getHomes(offlinePlayer).entrySet()) {
                String name = home.getKey();
                Location location = home.getValue();

                user.setHome(name, location);
            }

            if (this.plugin.getVanish().isVanish(offlinePlayer))
                user.setVanished(true);

            if (!this.plugin.getWantsTeleport().wantsTeleport(offlinePlayer))
                user.setTeleportEnabled(false);

            //TODO: Convert bans and mutes

            user.save();
        }

        Warps warps = essentials.getWarps();

        if (warps != null) {
            WarpManager warpManager = this.plugin.getWarpManager();

            for (String warp : warpManager.getWarps())
                try {
                    warps.setWarp(warp, warpManager.getWarp(warp));
                } catch (Exception e) {
                    e.printStackTrace();
                    error = true;
                }
        }

        //TODO: Convert kits

        if (error) {
            cs.sendMessage(this.getPrefix() + this.getMessage("ConvertToEssentials.FinishedWithErrors", label, cmd.getName(), cs, null));
            return true;
        }

        cs.sendMessage(this.getPrefix() + this.getMessage("ConvertToEssentials.Finished", label, cmd.getName(), cs, null));
        return true;
    }
}
