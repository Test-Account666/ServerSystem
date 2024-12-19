package me.entity303.serversystem.commands.executable;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Warps;
import com.earth2me.essentials.commands.WarpNotFoundException;
import me.entity303.serversystem.bansystem.moderation.MuteModeration;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.FileUtils;
import net.ess3.api.InvalidWorldException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@ServerSystemCommand(name = "ConvertFromEssentials")
public class EssentialsConversionCommand implements ICommandExecutorOverload {
    protected final ServerSystem _plugin;
    private boolean _starting = false;

    public EssentialsConversionCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    public static boolean ShouldRegister(ServerSystem serverSystem) {
        return Bukkit.getPluginManager().getPlugin("Essentials") != null;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "convertfromessentials")) {
            var permission = this._plugin.GetPermissions().GetPermission("convertfromessentials");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (!this._starting) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "ConvertFromEssentials.WarnNotTested"));
            this._starting = true;
            Bukkit.getScheduler().runTaskLater(this._plugin, () -> this._starting = false, 20 * 10);
            return true;
        }

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                  this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "ConvertFromEssentials.Start"));

        if (!this.CreateBackup()) return true;

        var essentialsDirectory = new File("plugins" + File.separator + "Essentials");

        if (!essentialsDirectory.exists()) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "ConvertFromEssentials.Failed.NoDirectory"));
            return true;
        }

        var essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
        assert essentials != null;

        var userDirectory = new File("plugins" + File.separator + "Essentials" + File.separator + "userdata");
        if (userDirectory.exists()) this.ConvertUserData(userDirectory, essentials, commandSender, command, commandLabel);

        var warps = essentials.getWarps();

        if (warps != null && !warps.isEmpty()) this.ConvertWarps(warps, commandSender, command, commandLabel);

        //TODO: Convert kits

        if (this._plugin.GetVaultHookManager() != null) this._plugin.GetVaultHookManager().Hook(true);


        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                  this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "ConvertFromEssentials.Finished"));
        return true;
    }

    private boolean CreateBackup() {
        var serverSystemDirectory = new File("plugins" + File.separator + "ServerSystem");
        var dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm-ss");
        var now = LocalDateTime.now();
        var backupDate = dtf.format(now);

        try {
            FileUtils.CopyDirectory(serverSystemDirectory,
                                    new File("plugins" + File.separator + "ServerSystem-Backups" + File.separator + "ServerSystem-Backup-" + backupDate));
            return true;
        } catch (IOException exception) {
            exception.printStackTrace();

            this._plugin.Error("Exists: " + serverSystemDirectory.exists());

            this._plugin.Error("Read: " + serverSystemDirectory.canRead());
            this._plugin.Error("Write: " + serverSystemDirectory.canWrite());
            this._plugin.Error("Execute: " + serverSystemDirectory.canExecute());

            this._plugin.Error("Directory: " + serverSystemDirectory.isDirectory());
            this._plugin.Error("File: " + serverSystemDirectory.isFile());
        }

        return false;
    }

    private void ConvertUserData(File userDirectory, Essentials essentials, CommandSender commandSender, Command command, String commandLabel) {
        if (!userDirectory.isDirectory()) return;

        for (var userData : Objects.requireNonNull(userDirectory.listFiles()))
            try {
                var offlineUser = essentials.getUser(UUID.fromString(userData.getName().split("\\.")[0]));

                if (offlineUser == null) {
                    this._plugin.Error("User '" + userData.getName().split("\\.")[0] + "' is null?!");
                    continue;
                }

                this.ConvertMute(offlineUser, commandSender, command, commandLabel);

                this._plugin.GetEconomyManager().SetMoney(offlineUser.getBase(), offlineUser.getMoney().doubleValue());

                this.ConvertHomes(offlineUser, commandSender, command, commandLabel, userData);

                this._plugin.GetVanish().SetVanish(offlineUser.isVanished(), offlineUser.getConfigUUID());

                this._plugin.GetWantsTeleport().SetWantsTeleport(Bukkit.getOfflinePlayer(offlineUser.getUUID()), offlineUser.isTeleportEnabled());
            } catch (Exception exception) {
                this._plugin.Error("Failed to process userdata '" + userData.getName() + "'!");
            }
    }

    private void ConvertWarps(Warps warps, CommandSender commandSender, Command command, String commandLabel) {
        for (var warp : warps.getList())
            try {
                this._plugin.GetWarpManager().AddWarp(warp, warps.getWarp(warp));
            } catch (WarpNotFoundException | InvalidWorldException exception) {
                if (exception instanceof WarpNotFoundException) {
                    this._plugin.Warn("Warp '" + warp + "' does not exist?!");
                    continue;
                }

                exception.printStackTrace();

                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                           "warpSetting;" + warp,
                                                                                                                           "ConvertFromEssentials.Failed" + ".Unknown"));
                return;
            }
    }

    private void ConvertMute(User offlineUser, CommandSender commandSender, Command command, String commandLabel) {
        if (!offlineUser.isMuted()) return;

        var target = offlineUser.getName();
        var reason = this._plugin.GetMessages().GetMessageWithStringTarget(commandLabel, command, commandSender, target, "Mute.DefaultReason");

        if (offlineUser.hasMuteReason()) reason = offlineUser.getMuteReason();

        var unMute = offlineUser.getMuteTimeout();

        var date = this._plugin.GetMuteManager().ConvertLongToDate(unMute);

        var mute = new MuteModeration(UUID.fromString(offlineUser.getUUID().toString()),
                                      this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.BanSystem." + "ConsoleName"), unMute, date, reason, false);

        this._plugin.GetMuteManager().CreateMute(mute);
    }

    private void ConvertHomes(User offlineUser, CommandSender commandSender, Command command, String commandLabel, File userData) {
        var homeFile = new File("plugins" + File.separator + "ServerSystem" + File.separator + "Homes", offlineUser.getConfigUUID().toString() + ".yml");
        var homeCfg = YamlConfiguration.loadConfiguration(homeFile);

        var setHomes = false;

        for (var home : offlineUser.getHomes())
            try {
                setHomes = true;
                homeCfg.set("Homes." + home.toUpperCase(), offlineUser.getHome(home));
            } catch (Exception exception) {
                exception.printStackTrace();

                var target = "homeSetting;" + offlineUser.getName();
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                               .GetMessageWithStringTarget(commandLabel, command, commandSender, target,
                                                                                                                           "ConvertFromEssentials.Failed" + ".Unknown") +
                                          ";" + home);
                return;
            }

        if (!setHomes) return;

        try {
            homeCfg.save(homeFile);
        } catch (IOException exception) {
            exception.printStackTrace();

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                           .GetMessageWithStringTarget(commandLabel, command, commandSender,
                                                                                                                       "homeSaving;" + userData.getName(),
                                                                                                                       "ConvertFromEssentials.Failed" + ".Unknown"));
        }
    }
}
