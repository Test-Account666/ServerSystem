package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.listener.join.JoinUpdateListener;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class ServerSystemCommand implements ICommandExecutorOverload {

    protected final ServerSystem _plugin;

    public ServerSystemCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "serversystem.use")) {
            var permission = this._plugin.GetPermissions().GetPermission("serversystem.use");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "ServerSystem"));
            return true;
        }

        if (arguments[0].equalsIgnoreCase("reload")) {
            this.ExecuteReloadCommand(commandSender, command, commandLabel);
            return true;
        }

        if (arguments[0].equalsIgnoreCase("version")) {
            this.ExecuteVersionCommand(commandSender, command, commandLabel);
            return true;
        }
        if (arguments[0].equalsIgnoreCase("update"))
            this.ExecuteUpdateCommand(commandSender, command, commandLabel);
        return true;
    }

    private void ExecuteReloadCommand(CommandSender commandSender, Command command, String commandLabel) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "serversystem.reload")) {
            var permission = this._plugin.GetPermissions().GetPermission("serversystem.reload");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return;
        }
        this._plugin.onDisable();

        this._plugin.ReloadConfigValidating();

        this._plugin.onEnable();

        commandSender.sendMessage(
                this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "ServerSystem.Reload"));
    }

    private void ExecuteVersionCommand(CommandSender commandSender, Command command, String commandLabel) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "serversystem.version")) {
            var permission = this._plugin.GetPermissions().GetPermission("serversystem.version");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return;
        }

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                     .GetMessage(commandLabel, command, commandSender, null,
                                                                                                 "ServerSystem.Version")
                                                                                     .replace("<PLUGINVERSION>", this._plugin.getDescription().getVersion())
                                                                                     .replace("<CONFIGVERSION>", this._plugin.GetConfigVersion()));
    }

    private void ExecuteUpdateCommand(CommandSender commandSender, Command command, String commandLabel) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "serversystem.update")) {
            var permission = this._plugin.GetPermissions().GetPermission("serversystem.update");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return;
        }

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                  this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "ServerSystem.Update.Checking"));

        Bukkit.getScheduler().runTaskLaterAsynchronously(this._plugin, () -> {
            var version = this._plugin.getDescription().getVersion();

            Document doc = null;
            try {
                doc = Jsoup.connect("http://pluginsupport.zapto.org:80/PluginSupport/ServerSystem").referrer("ServerSystem").timeout(30000).get();
            } catch (IOException exception) {
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + ChatColor.RED + "Error while trying to check for updates!");
                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + ChatColor.DARK_GREEN +
                                          "Please ignore this error. The update server is currently down. Please be patient");
                this._plugin.Error("Error while trying to check for updates!");
                //exception.printStackTrace();
                this._plugin.Info("Please ignore this error. The update server is currently down. Please be patient");
            }

            if (doc != null) {
                this.DownloadFromPrimaryServer(commandSender, command, commandLabel, doc, version);
                return;
            }

            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + ChatColor.RED + "Switching to backup updater!");
            new UpdateChecker(this._plugin, "78974").GetVersion(checkedVersion -> {
                if (checkedVersion.equalsIgnoreCase(version) || checkedVersion.equalsIgnoreCase("1.6.7")) {
                    commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                                 .GetMessage(commandLabel, command, commandSender, null,
                                                                                                             "ServerSystem.Update.LatestVersion"));
                    return;
                }

                this.DownloadFromBackupServer(commandSender, command, commandLabel, checkedVersion);
            });
        }, 20L);
    }

    private void DownloadFromPrimaryServer(CommandSender commandSender, Command command, String commandLabel, Document document, String version) {
        for (var remoteFile : document.getElementsContainingOwnText(".jar")) {
            var foundVersion = remoteFile.attr("href");
            foundVersion = foundVersion.substring(0, foundVersion.lastIndexOf('.'));
            version = foundVersion;
        }

        if (this._plugin.getDescription().getVersion().equalsIgnoreCase(version)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() +
                                      this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "ServerSystem.Update.LatestVersion"));
            return;
        }

        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                     .GetMessage(commandLabel, command, commandSender, null,
                                                                                                 "ServerSystem.Update.NewVersion")
                                                                                     .replace("<VERSION>", version));
        try {
            var resultImageResponse = Jsoup.connect("http://pluginsupport.zapto.org:80/PluginSupport/ServerSystem/" + version + ".jar")
                                           .referrer("ServerSystem")
                                           .timeout(30000)
                                           .ignoreContentType(true)
                                           .execute();

            var inputStream = new BufferedInputStream(new URL("http://pluginsupport.zapto.org:80/PluginSupport/ServerSystem/" + version + ".jar").openStream());
            var fileOutputStream = new FileOutputStream(new File("plugins/update", this._plugin._jarName));
            var dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(dataBuffer, 0, 1024)) != -1)
                fileOutputStream.write(dataBuffer, 0, bytesRead);

            inputStream.close();
            fileOutputStream.close();
        } catch (IOException exception) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + ChatColor.RED + "Error while trying to download the update!");
            this._plugin.Error("Error while trying to download the update!");
            exception.printStackTrace();
        }
    }

    private void DownloadFromBackupServer(CommandSender commandSender, Command command, String commandLabel, String checkedVersion) {
        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                     .GetMessage(commandLabel, command, commandSender, null,
                                                                                                 "ServerSystem.Update.NewVersion")
                                                                                     .replace("<VERSION>", checkedVersion));


        try (var inputStream = new BufferedInputStream(new URL("https://api.spiget.org/v2/resources/78974/download").openStream());
             var fileOutputStream = new FileOutputStream(new File("plugins/update", this._plugin._jarName))) {
            var dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(dataBuffer, 0, 1024)) != -1)
                fileOutputStream.write(dataBuffer, 0, bytesRead);
        } catch (IOException exception) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + ChatColor.RED + "Error while trying to download the update!");
            this._plugin.Error("Error while trying to download the update!");
            exception.printStackTrace();

            if (!this._plugin.IsRegistered()) {
                this._plugin.SetRegistered(true);
                this._plugin.GetEventManager().RegisterEvent(new JoinUpdateListener(this._plugin));
            }

            if (checkedVersion.equalsIgnoreCase(this._plugin.GetNewVersion()))
                return;

            this._plugin.SetNewVersion(checkedVersion);
        }
    }
}
