package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.listener.join.JoinUpdateListener;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
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

public class ServerSystemCommand extends CommandUtils implements CommandExecutorOverload {

    public ServerSystemCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "serversystem.use")) {
            var permission = this.plugin.getPermissions().getPermission("serversystem.use");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (arguments.length == 0) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "ServerSystem"));
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
        if (!this.plugin.getPermissions().hasPermission(commandSender, "serversystem.reload")) {
            var permission = this.plugin.getPermissions().getPermission("serversystem.reload");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return;
        }
        this.plugin.onDisable();

        this.plugin.reloadConfigValidating();

        this.plugin.onEnable();

        commandSender.sendMessage(
                this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "ServerSystem.Reload"));
    }

    private void ExecuteVersionCommand(CommandSender commandSender, Command command, String commandLabel) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "serversystem.version")) {
            var permission = this.plugin.getPermissions().getPermission("serversystem.version");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return;
        }

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                     .getMessage(commandLabel, command, commandSender, null,
                                                                                                 "ServerSystem.Version")
                                                                                     .replace("<PLUGINVERSION>", this.plugin.getDescription().getVersion())
                                                                                     .replace("<CONFIGVERSION>", this.plugin.getConfigVersion()));
    }

    private void ExecuteUpdateCommand(CommandSender commandSender, Command command, String commandLabel) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "serversystem.update")) {
            var permission = this.plugin.getPermissions().getPermission("serversystem.update");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return;
        }

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                  this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "ServerSystem.Update.Checking"));

        Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            var version = this.plugin.getDescription().getVersion();

            Document doc = null;
            try {
                doc = Jsoup.connect("http://pluginsupport.zapto.org:80/PluginSupport/ServerSystem").referrer("ServerSystem").timeout(30000).get();
            } catch (IOException e) {
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + ChatColor.RED + "Error while trying to check for updates!");
                commandSender.sendMessage(this.plugin.getMessages().getPrefix() + ChatColor.DARK_GREEN +
                                          "Please ignore this error. The update server is currently down. Please be patient");
                this.plugin.error("Error while trying to check for updates!");
                //e.printStackTrace();
                this.plugin.log("Please ignore this error. The update server is currently down. Please be patient");
            }

            if (doc != null) {
                this.DownloadFromPrimaryServer(commandSender, command, commandLabel, doc, version);
                return;
            }

            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + ChatColor.RED + "Switching to backup updater!");
            new UpdateChecker(this.plugin, "78974").getVersion(checkedVersion -> {
                if (checkedVersion.equalsIgnoreCase(version) || checkedVersion.equalsIgnoreCase("1.6.7")) {
                    commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                                 .getMessage(commandLabel, command, commandSender, null,
                                                                                                             "ServerSystem.Update.LatestVersion"));
                    return;
                }

                this.DownloadFromBackupServer(commandSender, command, commandLabel, checkedVersion);
            });
        }, 20L);
    }

    private void DownloadFromPrimaryServer(CommandSender commandSender, Command command, String commandLabel, Document document, String version) {
        for (var f : document.getElementsContainingOwnText(".jar")) {
            var foundVersion = f.attr("href");
            foundVersion = foundVersion.substring(0, foundVersion.lastIndexOf('.'));
            version = foundVersion;
        }

        if (this.plugin.getDescription().getVersion().equalsIgnoreCase(version)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() +
                                      this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "ServerSystem.Update.LatestVersion"));
            return;
        }

        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                     .getMessage(commandLabel, command, commandSender, null,
                                                                                                 "ServerSystem.Update.NewVersion")
                                                                                     .replace("<VERSION>", version));
        try {
            var resultImageResponse = Jsoup.connect("http://pluginsupport.zapto.org:80/PluginSupport/ServerSystem/" + version + ".jar")
                                           .referrer("ServerSystem")
                                           .timeout(30000)
                                           .ignoreContentType(true)
                                           .execute();

            var in = new BufferedInputStream(new URL("http://pluginsupport.zapto.org:80/PluginSupport/ServerSystem/" + version + ".jar").openStream());
            var fileOutputStream = new FileOutputStream(new File("plugins/update", this.plugin.JAR_NAME));
            var dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1)
                fileOutputStream.write(dataBuffer, 0, bytesRead);

            in.close();
            fileOutputStream.close();
        } catch (IOException e) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + ChatColor.RED + "Error while trying to download the update!");
            this.plugin.error("Error while trying to download the update!");
            e.printStackTrace();
        }
    }

    private void DownloadFromBackupServer(CommandSender commandSender, Command command, String commandLabel, String checkedVersion) {
        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                     .getMessage(commandLabel, command, commandSender, null,
                                                                                                 "ServerSystem.Update.NewVersion")
                                                                                     .replace("<VERSION>", checkedVersion));


        try (var in = new BufferedInputStream(new URL("https://api.spiget.org/v2/resources/78974/download").openStream());
             var fileOutputStream = new FileOutputStream(new File("plugins/update", this.plugin.JAR_NAME))) {
            var dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1)
                fileOutputStream.write(dataBuffer, 0, bytesRead);
        } catch (IOException e) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + ChatColor.RED + "Error while trying to download the update!");
            this.plugin.error("Error while trying to download the update!");
            e.printStackTrace();

            if (!this.plugin.isRegistered()) {
                this.plugin.setRegistered(true);
                this.plugin.getEventManager().registerEvent(new JoinUpdateListener(this.plugin));
            }

            if (checkedVersion.equalsIgnoreCase(this.plugin.getNewVersion()))
                return;

            this.plugin.setNewVersion(checkedVersion);
        }
    }
}
