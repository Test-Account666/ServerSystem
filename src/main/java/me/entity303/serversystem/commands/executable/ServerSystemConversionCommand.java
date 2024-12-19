package me.entity303.serversystem.commands.executable;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.commands.ServerSystemCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.FileUtils;
import net.ess3.api.MaxMoneyException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ServerSystemCommand(name = "ConvertToEssentials")
public class ServerSystemConversionCommand implements ICommandExecutorOverload {
    protected final ServerSystem _plugin;
    private boolean _starting = false;

    public ServerSystemConversionCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    public static boolean ShouldRegister(ServerSystem serverSystem) {
        return Bukkit.getPluginManager().getPlugin("Essentials") != null;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "converttoessentials")) {
            var permission = this._plugin.GetPermissions().GetPermission("converttoessentials");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (!this._starting) {

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "ConvertToEssentials.WarnNotTested"));
            this._starting = true;
            Bukkit.getScheduler().runTaskLater(this._plugin, () -> this._starting = false, 20 * 10);
            return true;
        }

        var command1 = command.getName();
        commandSender.sendMessage(
                this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command1, commandSender, null, "ConvertToEssentials.Start"));

        var essentialsDirectory = new File("plugins//Essentials");
        var dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm-ss");
        var now = LocalDateTime.now();
        var backupDate = dtf.format(now);

        try {
            FileUtils.CopyFile(essentialsDirectory, new File("plugins//Essentials-Backups//Essentials-Backup-" + backupDate));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        var essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");

        var error = false;

        for (var offlinePlayer : Bukkit.getOfflinePlayers()) {
            var economy = this._plugin.GetEconomyManager();

            double balance = economy.GetMoneyAsNumber(offlinePlayer);

            User user = null;

            if (offlinePlayer.getName() != null) {
                user = essentials.getOfflineUser(offlinePlayer.getName());
            } else if (offlinePlayer.getUniqueId() != null) essentials.getUser(offlinePlayer.getUniqueId());

            if (user == null) continue;

            try {
                user.setMoney(new BigDecimal(balance));
            } catch (MaxMoneyException exception) {
                exception.printStackTrace();
                error = true;
            }


            var homeManager = this._plugin.GetHomeManager();

            for (var home : homeManager.GetHomes(offlinePlayer).entrySet()) {
                var name = home.getKey();
                var location = home.getValue();

                user.setHome(name, location);
            }

            if (this._plugin.GetVanish().IsVanish(offlinePlayer)) user.setVanished(true);

            if (!this._plugin.GetWantsTeleport().DoesPlayerWantTeleport(offlinePlayer)) user.setTeleportEnabled(false);

            var muteManager = this._plugin.GetMuteManager();

            var mute = muteManager.GetMute(offlinePlayer);

            if (mute != null) {
                user.setMuted(true);
                user.setMuteReason(mute.GetReason());
                user.setMuteTimeout(mute.GetExpireTime());
            }

            //TODO: Convert bans

            user.save();
        }

        var warps = essentials.getWarps();

        if (warps != null) {
            var warpManager = this._plugin.GetWarpManager();

            for (var warp : warpManager.GetWarps())
                try {
                    warps.setWarp(warp, warpManager.GetWarp(warp));
                } catch (Exception exception) {
                    exception.printStackTrace();
                    error = true;
                }
        }

        //TODO: Convert kits

        if (error) {

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "ConvertToEssentials.FinishedWithErrors"));
            return true;
        }


        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                  this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "ConvertToEssentials.Finished"));
        return true;
    }
}
