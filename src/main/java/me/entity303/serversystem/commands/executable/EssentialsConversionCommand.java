package me.entity303.serversystem.commands.executable;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Warps;
import com.earth2me.essentials.commands.WarpNotFoundException;
import me.entity303.serversystem.bansystem.Mute;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import me.entity303.serversystem.utils.FileUtils;
import net.ess3.api.InvalidWorldException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public class EssentialsConversionCommand extends CommandUtils implements CommandExecutorOverload {
    private boolean starting = false;

    public EssentialsConversionCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "convertfromessentials")) {
            var permission = this.plugin.getPermissions().getPermission("convertfromessentials");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (!this.starting) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "ConvertFromEssentials.WarnNotTested"));
            this.starting = true;
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.starting = false, 20 * 10);
            return true;
        }

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                  this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "ConvertFromEssentials.Start"));

        if (!this.CreateBackup())
            return true;

        var essentialsDirectory = new File("plugins" + File.separator + "Essentials");

        if (!essentialsDirectory.exists()) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessage(commandLabel, command, commandSender, null,
                                                                                                     "ConvertFromEssentials.Failed.NoDirectory"));
            return true;
        }

        var essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
        assert essentials != null;

        var userDirectory = new File("plugins" + File.separator + "Essentials" + File.separator + "userdata");
        if (userDirectory.exists())
            this.ConvertUserData(userDirectory, essentials, commandSender, command, commandLabel);

        var warps = essentials.getWarps();

        if (warps != null && !warps.isEmpty())
            this.ConvertWarps(warps, commandSender, command, commandLabel);

        //TODO: Convert kits

        if (this.plugin.getVaultHookManager() != null)
            this.plugin.getVaultHookManager().hook(true);


        commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                  this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "ConvertFromEssentials.Finished"));
        return true;
    }

    private boolean CreateBackup() {
        var serverSystemDirectory = new File("plugins" + File.separator + "ServerSystem");
        var dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm-ss");
        var now = LocalDateTime.now();
        var backupDate = dtf.format(now);

        try {
            FileUtils.copyDirectory(serverSystemDirectory,
                                    new File("plugins" + File.separator + "ServerSystem-Backups" + File.separator + "ServerSystem-Backup-" + backupDate));
            return true;
        } catch (IOException e) {
            e.printStackTrace();

            this.plugin.error("Exists: " + serverSystemDirectory.exists());

            this.plugin.error("Read: " + serverSystemDirectory.canRead());
            this.plugin.error("Write: " + serverSystemDirectory.canWrite());
            this.plugin.error("Execute: " + serverSystemDirectory.canExecute());

            this.plugin.error("Directory: " + serverSystemDirectory.isDirectory());
            this.plugin.error("File: " + serverSystemDirectory.isFile());
        }

        return false;
    }

    private void ConvertUserData(File userDirectory, Essentials essentials, CommandSender commandSender, Command command, String commandLabel) {
        if (!userDirectory.isDirectory())
            return;

        for (var userData : Objects.requireNonNull(userDirectory.listFiles()))
            try {
                var offlineUser = essentials.getUser(UUID.fromString(userData.getName().split("\\.")[0]));

                if (offlineUser == null) {
                    this.plugin.error("User '" + userData.getName().split("\\.")[0] + "' is null?!");
                    continue;
                }

                this.ConvertMute(offlineUser, commandSender, command, commandLabel);

                this.plugin.getEconomyManager().setMoney(offlineUser.getBase(), offlineUser.getMoney().doubleValue());

                this.ConvertHomes(offlineUser, commandSender, command, commandLabel, userData);

                this.plugin.getVanish().setVanish(offlineUser.isVanished(), offlineUser.getConfigUUID());

                this.plugin.getWantsTeleport().setWantsTeleport(Bukkit.getOfflinePlayer(offlineUser.getUUID()), offlineUser.isTeleportEnabled());
            } catch (Exception e) {
                this.plugin.error("Failed to process userdata '" + userData.getName() + "'!");
            }
    }

    private void ConvertWarps(Warps warps, CommandSender commandSender, Command command, String commandLabel) {
        for (var warp : warps.getList())
            try {
                this.plugin.getWarpManager().addWarp(warp, warps.getWarp(warp));
            } catch (WarpNotFoundException | InvalidWorldException e) {
                if (e instanceof WarpNotFoundException) {
                    this.plugin.warn("Warp '" + warp + "' does not exist?!");
                    continue;
                }

                e.printStackTrace();

                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                             .getMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                         "warpSetting;" + warp,
                                                                                                                         "ConvertFromEssentials.Failed.Unknown"));
                return;
            }
    }

    private void ConvertMute(User offlineUser, CommandSender commandSender, Command command, String commandLabel) {
        if (!offlineUser.isMuted())
            return;

        var target = offlineUser.getName();
        var reason = this.plugin.getMessages().getMessageWithStringTarget(commandLabel, command, commandSender, target, "Mute.DefaultReason");

        if (offlineUser.hasMuteReason())
            reason = offlineUser.getMuteReason();

        var unMute = offlineUser.getMuteTimeout();

        var date = this.plugin.getMuteManager().convertLongToDate(unMute);

        var mute =
                new Mute(offlineUser.getUUID().toString(), this.plugin.getMessages().getCfg().getString("Messages.Misc.BanSystem." + "ConsoleName"), unMute, date,
                         reason);

        this.plugin.getMuteManager().addMute(mute);
    }

    private void ConvertHomes(User offlineUser, CommandSender commandSender, Command command, String commandLabel, File userData) {
        var homeFile = new File("plugins" + File.separator + "ServerSystem" + File.separator + "Homes", offlineUser.getConfigUUID().toString() + ".yml");
        FileConfiguration homeCfg = YamlConfiguration.loadConfiguration(homeFile);

        var setHomes = false;

        for (var home : offlineUser.getHomes())
            try {
                setHomes = true;
                homeCfg.set("Homes." + home.toUpperCase(), offlineUser.getHome(home));
            } catch (Exception e) {
                e.printStackTrace();

                var target = "homeSetting;" + offlineUser.getName();
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                             .getMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                         target,
                                                                                                                         "ConvertFromEssentials.Failed.Unknown") +
                                          ";" + home);
                return;
            }

        if (!setHomes)
            return;

        try {
            homeCfg.save(homeFile);
        } catch (IOException e) {
            e.printStackTrace();

            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                         .getMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                     "homeSaving;" + userData.getName(),
                                                                                                                     "ConvertFromEssentials.Failed.Unknown"));
        }
    }
}
