package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Listener.Join.JoinUpdateListener;
import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ChatColor;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import me.Entity303.ServerSystem.Utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class COMMAND_serversystem extends MessageUtils implements CommandExecutor {

    public COMMAND_serversystem(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "serversystem.use")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("serversystem.use")));
            return true;
        }
        if (args.length == 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("ServerSystem", label, cmd.getName(), cs, null));
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            if (!this.isAllowed(cs, "serversystem.reload")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("serversystem.reload")));
                return true;
            }
            this.plugin.onDisable();
            this.plugin.onEnable();
            cs.sendMessage(this.getPrefix() + this.getMessage("ServerSystem.Reload", label, cmd.getName(), cs, null));
            return true;
        }
        if (args[0].equalsIgnoreCase("version")) {
            if (!this.isAllowed(cs, "serversystem.version")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("serversystem.version")));
                return true;
            }
            cs.sendMessage(this.getPrefix() + this.getMessage("ServerSystem.Version", label, cmd.getName(), cs, null).replace("<PLUGINVERSION>", this.plugin.getDescription().getVersion()).replace("<CONFIGVERSION>", this.plugin.getConfigVersion()));
            return true;
        }
        if (args[0].equalsIgnoreCase("update")) {
            if (!this.isAllowed(cs, "serversystem.update")) {
                cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("serversystem.update")));
                return true;
            }
            cs.sendMessage(this.getPrefix() + this.getMessage("ServerSystem.Update.Checking", label, cmd.getName(), cs, null));
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                String version = this.plugin.getDescription().getVersion();

                Document doc = null;
                try {
                    doc = Jsoup.connect("http://pluginsupport.zapto.org:80/PluginSupport/ServerSystem").referrer("ServerSystem").timeout(30000).get();
                } catch (IOException e) {
                    cs.sendMessage(this.getPrefix() + ChatColor.RED + "Error while trying to check for updates!");
                    cs.sendMessage(this.getPrefix() + ChatColor.DARK_GREEN + "Please ignore this error. The update server is currently down. Please be patient");
                    this.plugin.error("Error while trying to check for updates!");
                    e.printStackTrace();
                    this.plugin.log("Please ignore this error. The update server is currently down. Please be patient");
                }

                if (doc != null) {
                    for (Element f : doc.getElementsContainingOwnText(".jar")) {
                        String s = f.attr("href");
                        s = s.substring(0, s.lastIndexOf("."));
                        version = s;
                    }

                    if (!this.plugin.getDescription().getVersion().equalsIgnoreCase(version)) {
                        cs.sendMessage(this.getPrefix() + this.getMessage("ServerSystem.Update.NewVersion", label, cmd.getName(), cs, null).replace("<VERSION>", version));
                        try {
                            Connection.Response resultImageResponse = Jsoup.connect("http://pluginsupport.zapto.org:80/PluginSupport/ServerSystem/" + version + ".jar").referrer("ServerSystem").timeout(30000).ignoreContentType(true).execute();

                            FileOutputStream out = (new FileOutputStream(new File("plugins/update", this.plugin.JAR_NAME)));
                            out.write(resultImageResponse.bodyAsBytes());
                            out.close();
                        } catch (IOException e) {
                            cs.sendMessage(this.getPrefix() + ChatColor.RED + "Error while trying to download the update!");
                            this.plugin.error("Error while trying to download the update!");
                            e.printStackTrace();
                        }
                    } else
                        cs.sendMessage(this.getPrefix() + this.getMessage("ServerSystem.Update.LatestVersion", label, cmd.getName(), cs, null));
                } else {
                    cs.sendMessage(this.getPrefix() + ChatColor.RED + "Switching to backup updater!");
                    String finalVersion = version;
                    new UpdateChecker(this.plugin, "78974").getVersion(checkedVersion -> {
                        if (checkedVersion.equalsIgnoreCase(finalVersion) || checkedVersion.equalsIgnoreCase("1.6.7"))
                            cs.sendMessage(this.getPrefix() + this.getMessage("ServerSystem.Update.LatestVersion", label, cmd.getName(), cs, null));
                        else {
                            cs.sendMessage(this.getPrefix() + this.getMessage("ServerSystem.Update.NewVersion", label, cmd.getName(), cs, null).replace("<VERSION>", checkedVersion));


                            try (BufferedInputStream in = new BufferedInputStream(new URL("https://api.spiget.org/v2/resources/78974/download").openStream());
                                 FileOutputStream fileOutputStream = new FileOutputStream(new File("plugins/update", this.plugin.JAR_NAME))) {
                                byte[] dataBuffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1)
                                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                            } catch (IOException e) {
                                cs.sendMessage(this.getPrefix() + ChatColor.RED + "Error while trying to download the update!");
                                this.plugin.error("Error while trying to download the update!");
                                e.printStackTrace();
                                if (!this.plugin.isRegistered()) {
                                    this.plugin.setRegistered(true);
                                    this.plugin.getEventManager().re(new JoinUpdateListener(this.plugin));
                                }
                                if (!checkedVersion.equalsIgnoreCase(this.plugin.getNewVersion()))
                                    this.plugin.setNewVersion(checkedVersion);
                            }
                        }
                    });
                }
            }, 20L);
            return true;
        }
        return true;
    }
}
