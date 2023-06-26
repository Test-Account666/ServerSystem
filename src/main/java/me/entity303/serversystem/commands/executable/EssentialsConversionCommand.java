package me.entity303.serversystem.commands.executable;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Warps;
import com.earth2me.essentials.commands.WarpNotFoundException;
import me.entity303.serversystem.bansystem.Mute;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.FileUtils;
import me.entity303.serversystem.utils.MessageUtils;
import net.ess3.api.InvalidWorldException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class EssentialsConversionCommand extends MessageUtils implements CommandExecutor {
    private boolean starting = false;

    public EssentialsConversionCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "convertfromessentials")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("convertfromessentials")));
            return true;
        }

        if (!this.starting) {
            cs.sendMessage(this.getPrefix() + this.getMessage("ConvertFromEssentials.WarnNotTested", label, cmd.getName(), cs, null));
            this.starting = true;
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.starting = false, 20 * 10);
            return true;
        }

        cs.sendMessage(this.getPrefix() + this.getMessage("ConvertFromEssentials.Start", label, cmd.getName(), cs, null));

        File serverSystemDirectory = new File("plugins//ServerSystem");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String backupDate = dtf.format(now);

        try {
            FileUtils.copyFile(serverSystemDirectory, new File("plugins//ServerSystem-Backups//ServerSystem-Backup-" + backupDate));
        } catch (IOException e) {
            e.printStackTrace();
        }

        File essentialsDirectory = new File("plugins//Essentials");

        if (!essentialsDirectory.exists()) {
            cs.sendMessage(this.getPrefix() + this.getMessage("ConvertFromEssentials.Failed.NoDirectory", label, cmd.getName(), cs, null));
            return true;
        }

        Essentials essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");

        File userDirectory = new File("plugins//Essentials//userdata");
        if (userDirectory.exists())
            for (File userData : userDirectory.listFiles())
                try {
                    User offlineUser = essentials.getUser(UUID.fromString(userData.getName().split("\\.")[0]));

                    if (offlineUser == null) {
                        this.plugin.error("User '" + userData.getName().split("\\.")[0] + "' is null?!");
                        continue;
                    }

                    if (offlineUser.isMuted()) {
                        String reason = this.getMessageWithStringTarget("Mute.DefaultReason", label, cmd.getName(), cs, offlineUser.getName());

                        if (offlineUser.hasMuteReason())
                            reason = offlineUser.getMuteReason();

                        long unMute = offlineUser.getMuteTimeout();

                        String date = this.plugin.getMuteManager().convertLongToDate(unMute);

                        Mute mute = new Mute(offlineUser.getUUID().toString(), this.getBanSystem("ConsoleName"), unMute, date, reason);

                        this.plugin.getMuteManager().addMute(mute);
                    }

                    this.plugin.getEconomyManager().setMoney(offlineUser.getBase(), offlineUser.getMoney().doubleValue());

                    File homeFile = new File("plugins//ServerSystem//Homes", offlineUser.getConfigUUID().toString() + ".yml");
                    FileConfiguration homeCfg = YamlConfiguration.loadConfiguration(homeFile);

                    boolean setHomes = false;

                    for (String home : offlineUser.getHomes())
                        try {
                            setHomes = true;
                            homeCfg.set("Homes." + home.toUpperCase(), offlineUser.getHome(home));
                        } catch (Exception e) {
                            e.printStackTrace();
                            cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("ConvertFromEssentials.Failed.Unknown", label, cmd.getName(), cs, "homeSetting;" + offlineUser.getName()) + ";" + home);
                            return true;
                        }

                    if (setHomes) try {
                        homeCfg.save(homeFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                        cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("ConvertFromEssentials.Failed.Unknown", label, cmd.getName(), cs, "homeSaving;" + userData.getName()));
                        return true;
                    }

                    this.plugin.getVanish().setVanish(offlineUser.isVanished(), offlineUser.getConfigUUID());

                    this.plugin.setWantsTP(Bukkit.getOfflinePlayer(offlineUser.getUUID()), offlineUser.isTeleportEnabled());
                } catch (Exception e) {
                    this.plugin.error("Failed to process userdata '" + userData.getName() + "'!");
                }

        Warps warps = essentials.getWarps();

        if (warps != null)
            if (!warps.isEmpty())
                for (String warp : warps.getList())
                    try {
                        this.plugin.getWarpManager().addWarp(warp, warps.getWarp(warp));
                    } catch (WarpNotFoundException | InvalidWorldException e) {
                        if (e instanceof WarpNotFoundException) {
                            this.plugin.warn("Warp '" + warp + "' does not exist?!");
                            continue;
                        }

                        e.printStackTrace();
                        cs.sendMessage(this.getPrefix() + this.getMessageWithStringTarget("ConvertFromEssentials.Failed.Unknown", label, cmd.getName(), cs, "warpSetting;" + warp));
                        return true;
                    }

        //TODO: Convert kits

        if (this.plugin != null)
            if (this.plugin.getVaultHookManager() != null)
                this.plugin.getVaultHookManager().hook(true);

        cs.sendMessage(this.getPrefix() + this.getMessage("ConvertFromEssentials.Finished", label, cmd.getName(), cs, null));
        return true;
    }
}
