package me.Entity303.ServerSystem.Utils;

import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConfigUpdater extends MessageUtils {

    public ConfigUpdater(ss plugin) {
        super(plugin);
    }

    public void updateConfig(String version) {
        if (version.equalsIgnoreCase(this.plugin.CONFIG_VERSION)) return;

        this.plugin.log("Updating configs!");

        File serverSystemFolder = new File("plugins//ServerSystem");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);

        try {
            this.copyFolder(serverSystemFolder.toPath(), new File("plugins//ServerSystem-Backups//ServerSystem-Backup-" + date).toPath(), date);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (version.equalsIgnoreCase("2.5")) {
            this.plugin.log("Updating config version 2.5 to 2.6...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);

            this.plugin.getConfig().set("version", "2.6");

            msgENConfig.set("Messages.Syntax.CreateKit", "Syntax: &8/<LABEL> <Name>");
            msgENConfig.set("Messages.Syntax.DeleteKit", "Syntax: &8/<LABEL> <Name>");
            msgENConfig.set("Messages.Syntax.Kit", "Syntax: &8/<LABEL> <Name> <Player>");

            msgENConfig.set("Messages.Normal.DeleteKit.DoesntExist", "&cThe kit &4<KIT> &cdoes not exist!");
            msgENConfig.set("Messages.Normal.DeleteKit.Success", "You deleted the kit &8<KIT>&7!");

            msgENConfig.set("Messages.Normal.CreateKit.AlreadyExist", "&cThe kit &4<KIT> &calready exist!");
            msgENConfig.set("Messages.Normal.CreateKit.Success", "You created the kit &8<KIT>&7!");

            msgENConfig.set("Messages.Normal.Kit.Success.Self", "You gave yourself the kit &8<KIT>&7!");
            msgENConfig.set("Messages.Normal.Kit.Success.Others.Sender", "You gave &8<TARGET> &7the kit &8<KIT>&7!");
            msgENConfig.set("Messages.Normal.Kit.Success.Others.Target", "You got the kit &8<KIT>&7!");


            msgDEConfig.set("Messages.Syntax.CreateKit", "Syntax: &8/<LABEL> <Name>");
            msgDEConfig.set("Messages.Syntax.DeleteKit", "Syntax: &8/<LABEL> <Name>");
            msgDEConfig.set("Messages.Syntax.Kit", "Syntax: &8/<LABEL> <Name> <Spieler>");

            msgDEConfig.set("Messages.Normal.DeleteKit.DoesntExist", "&cDas Kit &4<KIT> &cexistiert nicht!");
            msgDEConfig.set("Messages.Normal.DeleteKit.Success", "Du hast das Kit &8<KIT> &7gelöscht!");

            msgDEConfig.set("Messages.Normal.CreateKit.AlreadyExist", "&cDas Kit &4<KIT> &cexistiert bereits!");
            msgDEConfig.set("Messages.Normal.CreateKit.Success", "Du hast das Kit &8<KIT> &7erstellt!");

            msgDEConfig.set("Messages.Normal.Kit.Success.Self", "Du hast dir das Kit &8<KIT> &7gegeben!");
            msgDEConfig.set("Messages.Normal.Kit.Success.Others.Sender", "Du hast &8<TARGET> &7das Kit &8<KIT> &7gegeben!");
            msgDEConfig.set("Messages.Normal.Kit.Success.Others.Target", "Du hast nun das Kit &8<KIT>&7!");

            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Syntax.CreateKit", "Syntax: &8/<LABEL> <Name>");
                msgConfig.set("Messages.Syntax.DeleteKit", "Syntax: &8/<LABEL> <Name>");
                msgConfig.set("Messages.Syntax.Kit", "Syntax: &8/<LABEL> <Name> <Spieler>");

                msgConfig.set("Messages.Normal.DeleteKit.DoesntExist", "&cDas Kit &4<KIT> &cexistiert nicht!");
                msgConfig.set("Messages.Normal.DeleteKit.Success", "Du hast das Kit &8<KIT> &7gelöscht!");

                msgConfig.set("Messages.Normal.CreateKit.AlreadyExist", "&cDas Kit &4<KIT> &cexistiert bereits!");
                msgConfig.set("Messages.Normal.CreateKit.Success", "Du hast das Kit &8<KIT> &7erstellt!");

                msgConfig.set("Messages.Normal.Kit.Success.Self", "Du hast dir das Kit &8<KIT> &7gegeben!");
                msgConfig.set("Messages.Normal.Kit.Success.Others.Sender", "Du hast &8<TARGET> &7das Kit &8<KIT> &7gegeben!");
                msgConfig.set("Messages.Normal.Kit.Success.Others.Target", "Du hast nun das Kit &8<KIT>&7!");
            } else {
                msgConfig.set("Messages.Syntax.CreateKit", "Syntax: &8/<LABEL> <Name>");
                msgConfig.set("Messages.Syntax.DeleteKit", "Syntax: &8/<LABEL> <Name>");
                msgConfig.set("Messages.Syntax.Kit", "Syntax: &8/<LABEL> <Name> <Player>");

                msgConfig.set("Messages.Normal.DeleteKit.DoesntExist", "&cThe kit &4<KIT> &cdoes not exist!");
                msgConfig.set("Messages.Normal.DeleteKit.Success", "You deleted the kit &8<KIT>&7!");

                msgConfig.set("Messages.Normal.CreateKit.AlreadyExist", "&cThe kit &4<KIT> &calready exist!");
                msgConfig.set("Messages.Normal.CreateKit.Success", "You created the kit &8<KIT>&7!");

                msgConfig.set("Messages.Normal.Kit.Success.Self", "You gave yourself the kit &8<KIT>&7!");
                msgConfig.set("Messages.Normal.Kit.Success.Others.Sender", "You gave &8<TARGET> &7the kit &8<KIT>&7!");
                msgConfig.set("Messages.Normal.Kit.Success.Others.Target", "You got the kit &8<KIT>&7!");
            }

            permissionConfig.set("Permissions.kit.self", "server.kit.<KIT>.self");
            permissionConfig.set("Permissions.kit.others", "server.kit.<KIT>.others");

            permissionConfig.set("Permissions.deletekit", "server.deletekit");

            permissionConfig.set("Permissions.createkit", "server.createkit");


            aliasConfig.set("Aliases.createkit.aliases", "No Aliases");

            aliasConfig.set("Aliases.deletekit.aliases", "No Aliases");

            aliasConfig.set("Aliases.kit.aliases", "No Aliases");

            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();

            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();

            this.plugin.log("Updated config version 2.5 to 2.6!");

            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("2.6")) {
            this.plugin.log("Updating config version 2.6 to 2.7...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);

            this.plugin.getConfig().set("version", "2.7");

            permissionConfig.set("Permissions.economy.general", "server.economy.use");
            permissionConfig.set("Permissions.economy.set", "server.economy.set");
            permissionConfig.set("Permissions.economy.give", "server.economy.give");
            permissionConfig.set("Permissions.economy.revoke", "server.economy.revoke");


            msgDEConfig.set("Messages.Normal.Economy.Error.NotANumber", "&4<NUMBER> &cist kein gültiger Betrag!");

            msgDEConfig.set("Messages.Normal.Economy.Success.Set.Sender", "Du hast den Kontostand von &8<TARGET> &7auf &8<AMOUNT> &7gesetzt!");
            msgDEConfig.set("Messages.Normal.Economy.Success.Set.Target", "Dein Kontostand wurde auf &8<AMOUNT> &7gesetzt!");

            msgDEConfig.set("Messages.Normal.Economy.Success.Give.Sender", "Du hast &8<AMOUNT> &7auf das Konto von &8<TARGET> &7überwiesen!");
            msgDEConfig.set("Messages.Normal.Economy.Success.Give.Target", "Dir wurden &8<AMOUNT> &7gegeben!");

            msgDEConfig.set("Messages.Normal.Economy.Success.Revoke.Sender", "Du hast &8<AMOUNT> &7vom Konto von &8<TARGET> &7genommen!");
            msgDEConfig.set("Messages.Normal.Economy.Success.Revoke.Target", "Dir wurden &8<AMOUNT> &7genommen!");


            msgENConfig.set("Messages.Normal.Economy.Error.NotANumber", "&4<NUMBER> &cis not a valid amount!");

            msgENConfig.set("Messages.Normal.Economy.Success.Set.Sender", "You set the balance of &8<TARGET> &7to &8<AMOUNT>&7!");
            msgENConfig.set("Messages.Normal.Economy.Success.Set.Target", "Your balance was set to &8<AMOUNT>&7!");

            msgENConfig.set("Messages.Normal.Economy.Success.Give.Sender", "You gave &8<AMOUNT> &7to &8<TARGET>&7!");
            msgENConfig.set("Messages.Normal.Economy.Success.Give.Target", "You received &8<AMOUNT>&7!");

            msgENConfig.set("Messages.Normal.Economy.Success.Revoke.Sender", "You took &8<AMOUNT> &7from &8<TARGET>&7!");
            msgENConfig.set("Messages.Normal.Economy.Success.Revoke.Target", "&8<AMOUNT> &7were taken from you!");


            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Normal.Economy.Error.NotANumber", "&4<NUMBER> &cist kein gültiger Betrag!");

                msgConfig.set("Messages.Normal.Economy.Success.Set.Sender", "Du hast den Kontostand von &8<TARGET> &7auf &8<AMOUNT> &7gesetzt!");
                msgConfig.set("Messages.Normal.Economy.Success.Set.Target", "Dein Kontostand wurde auf &8<AMOUNT> &7gesetzt!");

                msgConfig.set("Messages.Normal.Economy.Success.Give.Sender", "Du hast &8<AMOUNT> &7auf das Konto von &8<TARGET> &7überwiesen!");
                msgConfig.set("Messages.Normal.Economy.Success.Give.Target", "Dir wurden &8<AMOUNT> &7gegeben!");

                msgConfig.set("Messages.Normal.Economy.Success.Revoke.Sender", "Du hast &8<AMOUNT> &7vom Konto von &8<TARGET> &7genommen!");
                msgConfig.set("Messages.Normal.Economy.Success.Revoke.Target", "Dir wurden &8<AMOUNT> &7genommen!");
            } else {
                msgConfig.set("Messages.Normal.Economy.Error.NotANumber", "&4<NUMBER> &cis not a valid amount!");

                msgConfig.set("Messages.Normal.Economy.Success.Set.Sender", "You set the balance of &8<TARGET> &7to &8<AMOUNT>&7!");
                msgConfig.set("Messages.Normal.Economy.Success.Set.Target", "Your balance was set to &8<AMOUNT>&7!");

                msgConfig.set("Messages.Normal.Economy.Success.Give.Sender", "You gave &8<AMOUNT> &7to &8<TARGET>&7!");
                msgConfig.set("Messages.Normal.Economy.Success.Give.Target", "You received &8<AMOUNT>&7!");

                msgConfig.set("Messages.Normal.Economy.Success.Revoke.Sender", "You took &8<AMOUNT> &7from &8<TARGET>&7!");
                msgConfig.set("Messages.Normal.Economy.Success.Revoke.Target", "&8<AMOUNT> &7were taken from you!");
            }


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 2.6 to 2.7!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("2.7")) {
            this.plugin.log("Updating config version 2.7 to 2.8...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);

            this.plugin.getConfig().set("version", "2.8");


            permissionConfig.set("Permissions.tp.self", "server.tp.self");
            permissionConfig.set("Permissions.tp.others", "server.tp.others");

            permissionConfig.set("Permissions.tphere", "server.tphere");

            permissionConfig.set("Permissions.tpo.self", "server.tpo.self");
            permissionConfig.set("Permissions.tpo.others", "server.tpo.others");

            permissionConfig.set("Permissions.tpohere", "server.tpohere");

            permissionConfig.set("Permissions.tpall.self", "server.tpall.self");
            permissionConfig.set("Permissions.tpall.others", "server.tpall.others");


            msgDEConfig.set("Messages.Normal.Tp.NoTeleportations", "&cDer Spieler &4<TARGET> &chat seine Teleportationen deaktiviert!");
            msgDEConfig.set("Messages.Normal.Tp.Self", "Du hast dich zu &8<TARGET> &7teleportiert!");
            msgDEConfig.set("Messages.Normal.Tp.Others", "Du hast &8<TARGET> &7zu &8<TARGET2> &7teleportiert!");

            msgDEConfig.set("Messages.Normal.Tphere.NoTeleportations", "&cDer Spieler &4<TARGET> &chat seine Teleportationen deaktiviert!");
            msgDEConfig.set("Messages.Normal.Tphere.Success", "Du hast den Spieler &8<TARGET> &7zu dir teleportiert!");

            msgDEConfig.set("Messages.Normal.Tpo.Self", "Du hast dich zu &8<TARGET> &7teleportiert!");
            msgDEConfig.set("Messages.Normal.Tpo.Others", "Du hast &8<TARGET> &7zu &8<TARGET2> &7teleportiert!");

            msgDEConfig.set("Messages.Normal.TpoHere", "Du hast den Spieler &8<TARGET> &7zu dir teleportiert!");

            msgDEConfig.set("Messages.Normal.TpAll.Self", "Du hast alle Spieler zu dir teleportiert!");
            msgDEConfig.set("Messages.Normal.TpAll.Others", "Du hast alle Spieler zu &8<TARGET> &7teleportiert!");


            msgENConfig.set("Messages.Normal.Tp.NoTeleportations", "&4<TARGET> &cdisabled his teleportations!");
            msgENConfig.set("Messages.Normal.Tp.Self", "You teleported yourself to &8<TARGET>&7!");
            msgENConfig.set("Messages.Normal.Tp.Others", "You teleported &8<TARGET> &7to &8<TARGET2>&7!");

            msgENConfig.set("Messages.Normal.Tphere.NoTeleportations", "&4<TARGET> &cdisabled his teleportations!");
            msgENConfig.set("Messages.Normal.Tphere.Success", "You teleported to &8<TARGET> &7yourself!");

            msgENConfig.set("Messages.Normal.Tpo.Self", "You teleported yourself to &8<TARGET>&7!");
            msgENConfig.set("Messages.Normal.Tpo.Others", "You teleported &8<TARGET> &7to &8<TARGET2>&7!");

            msgENConfig.set("Messages.Normal.TpoHere", "You teleported to &8<TARGET> &7yourself!");

            msgENConfig.set("Messages.Normal.TpAll.Self", "You teleported all players to you!");
            msgENConfig.set("Messages.Normal.TpAll.Others", "You teleported all players to &8<TARGET>&7!");


            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Normal.Tp.NoTeleportations", "&cDer Spieler &4<TARGET> &chat seine Teleportationen deaktiviert!");
                msgConfig.set("Messages.Normal.Tp.Self", "Du hast dich zu &8<TARGET> &7teleportiert!");
                msgConfig.set("Messages.Normal.Tp.Others", "Du hast &8<TARGET> &7zu &8<TARGET2> &7teleportiert!");

                msgConfig.set("Messages.Normal.Tphere.NoTeleportations", "&cDer Spieler &4<TARGET> &chat seine Teleportationen deaktiviert!");
                msgConfig.set("Messages.Normal.Tphere.Success", "Du hast den Spieler &8<TARGET> &7zu dir teleportiert!");

                msgConfig.set("Messages.Normal.Tpo.Self", "Du hast dich zu &8<TARGET> &7teleportiert!");
                msgConfig.set("Messages.Normal.Tpo.Others", "Du hast &8<TARGET> &7zu &8<TARGET2> &7teleportiert!");

                msgConfig.set("Messages.Normal.TpoHere", "Du hast den Spieler &8<TARGET> &7zu dir teleportiert!");

                msgConfig.set("Messages.Normal.TpAll.Self", "Du hast alle Spieler zu dir teleportiert!");
                msgConfig.set("Messages.Normal.TpAll.Others", "Du hast alle Spieler zu &8<TARGET> &7teleportiert!");
            } else {
                msgConfig.set("Messages.Normal.Tp.NoTeleportations", "&4<TARGET> &cdisabled his teleportations!");
                msgConfig.set("Messages.Normal.Tp.Self", "You teleported yourself to &8<TARGET>&7!");
                msgConfig.set("Messages.Normal.Tp.Others", "You teleported &8<TARGET> &7to &8<TARGET2>&7!");

                msgConfig.set("Messages.Normal.Tphere.NoTeleportations", "&4<TARGET> &cdisabled his teleportations!");
                msgConfig.set("Messages.Normal.Tphere.Success", "You teleported to &8<TARGET> &7yourself!");

                msgConfig.set("Messages.Normal.Tpo.Self", "You teleported yourself to &8<TARGET>&7!");
                msgConfig.set("Messages.Normal.Tpo.Others", "You teleported &8<TARGET> &7to &8<TARGET2>&7!");

                msgConfig.set("Messages.Normal.TpoHere", "You teleported to &8<TARGET> &7yourself!");

                msgConfig.set("Messages.Normal.TpAll.Self", "You teleported all players to you!");
                msgConfig.set("Messages.Normal.TpAll.Others", "You teleported all players to &8<TARGET>&7!");
            }


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 2.7 to 2.8!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("2.8")) {
            this.plugin.log("Updating config version 2.8 to 2.9...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);

            this.plugin.getConfig().set("version", "2.9");


            permissionConfig.set("Permissions.warp.bypassdelay", "server.warp.bypassdelay");
            permissionConfig.set("Permissions.workbench", "server.workbench");
            permissionConfig.set("Permissions.fly.self", "server.fly.self");
            permissionConfig.set("Permissions.fly.others", "server.fly.others");


            msgDEConfig.set("Messages.Normal.Fly.Activated.Self", "Du kannst nun fliegen!");
            msgDEConfig.set("Messages.Normal.Fly.Activated.Others.Sender", "Der Spieler &8<TARGET> &7kann nun fliegen!");
            msgDEConfig.set("Messages.Normal.Fly.Activated.Others.Target", "Du kannst nun fliegen!");

            msgDEConfig.set("Messages.Normal.Fly.DeActivated.Self", "Du kannst nun nicht mehr fliegen!");
            msgDEConfig.set("Messages.Normal.Fly.DeActivated.Others.Sender", "Der Spieler &8<TARGET> &7kann nun nicht mehr fliegen!");
            msgDEConfig.set("Messages.Normal.Fly.DeActivated.Others.Target", "Du kannst nun nicht mehr fliegen!");

            msgDEConfig.set("Messages.Normal.Smelt.NoItem", "&cDafür musst du ein Item in der Hand halten!");


            msgENConfig.set("Messages.Normal.Fly.Activated.Self", "You can fly now!");
            msgENConfig.set("Messages.Normal.Fly.Activated.Others.Sender", "&8<TARGET> &7can fly now!");
            msgENConfig.set("Messages.Normal.Fly.Activated.Others.Target", "You can fly now!");

            msgENConfig.set("Messages.Normal.Fly.DeActivated.Self", "You can no longer fly!");
            msgENConfig.set("Messages.Normal.Fly.DeActivated.Others.Sender", "&8<TARGET> &7can no longer fly!");
            msgENConfig.set("Messages.Normal.Fly.DeActivated.Others.Target", "You cann no longer fly!");

            msgENConfig.set("Messages.Normal.Smelt.NoItem", "&cYou have to hold an item!");


            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Normal.Fly.Activated.Self", "Du kannst nun fliegen!");
                msgConfig.set("Messages.Normal.Fly.Activated.Others.Sender", "Der Spieler &8<TARGET> &7kann nun fliegen!");
                msgConfig.set("Messages.Normal.Fly.Activated.Others.Target", "Du kannst nun fliegen!");

                msgConfig.set("Messages.Normal.Fly.DeActivated.Self", "Du kannst nun nicht mehr fliegen!");
                msgConfig.set("Messages.Normal.Fly.DeActivated.Others.Sender", "Der Spieler &8<TARGET> &7kann nun nicht mehr fliegen!");
                msgConfig.set("Messages.Normal.Fly.DeActivated.Others.Target", "Du kannst nun nicht mehr fliegen!");
            } else {
                msgConfig.set("Messages.Normal.Fly.Activated.Self", "You can fly now!");
                msgConfig.set("Messages.Normal.Fly.Activated.Others.Sender", "&8<TARGET> &7can fly now!");
                msgConfig.set("Messages.Normal.Fly.Activated.Others.Target", "You can fly now!");

                msgConfig.set("Messages.Normal.Fly.DeActivated.Self", "You can no longer fly!");
                msgConfig.set("Messages.Normal.Fly.DeActivated.Others.Sender", "&8<TARGET> &7can no longer fly!");
                msgConfig.set("Messages.Normal.Fly.DeActivated.Others.Target", "You cann no longer fly!");
            }


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 2.8 to 2.9!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("2.9")) {
            this.plugin.log("Updating config version 2.9 to 3.0...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);

            this.plugin.getConfig().set("version", "3.0");


            permissionConfig.set("Permissions.msg.required", false);
            permissionConfig.set("Permissions.msg.permission", "server.msg");


            msgDEConfig.set("Messages.Normal.Kit.DoesntExist", "&cDas Kit &4<KIT> &cexistiert nicht!");

            msgDEConfig.set("Messages.Normal.Msg.Sender", "&8[&7MSG&8] &3Du &7-> &5<TARGET> &7>> &2<MESSAGE>");
            msgDEConfig.set("Messages.Normal.Msg.Target", "&8[&7MSG&8] &3<SENDER> &7-> &5Dir &7>> &2<MESSAGE>");


            msgENConfig.set("Messages.Normal.DoesntExist", "&The kit &4<KIT> &cdoes not exist!");

            msgENConfig.set("Messages.Normal.Msg.Sender", "&8[&7MSG&8] &3You &7-> &5<TARGET> &7>> &2<MESSAGE>");
            msgENConfig.set("Messages.Normal.Msg.Target", "&8[&7MSG&8] &3<SENDER> &7-> &5You &7>> &2<MESSAGE>");


            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Normal.Kit.DoesntExist", "&cDas Kit &4<KIT> &cexistiert nicht!");

                msgConfig.set("Messages.Normal.Msg.Sender", "&8[&7MSG&8] &3Du &7-> &5<TARGET> &7>> &2<MESSAGE>");
                msgConfig.set("Messages.Normal.Msg.Target", "&8[&7MSG&8] &3<SENDER> &7-> &5Dir &7>> &2<MESSAGE>");
            } else {
                msgConfig.set("Messages.Normal.DoesntExist", "&The kit &4<KIT> &cdoes not exist!");

                msgConfig.set("Messages.Normal.Msg.Sender", "&8[&7MSG&8] &3You &7-> &5<TARGET> &7>> &2<MESSAGE>");
                msgConfig.set("Messages.Normal.Msg.Target", "&8[&7MSG&8] &3<SENDER> &7-> &5You &7>> &2<MESSAGE>");
            }


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 2.9 to 3.0!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("3.0")) {
            this.plugin.log("Updating config version 3.0 to 3.1...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);

            this.plugin.getConfig().set("version", "3.1");


            permissionConfig.set("Permissions.convertfromessentials", "server.convertfromessentials");


            msgDEConfig.set("Messages.Normal.ConvertFromEssentials.WarnNotTested", "&cWarnung!! &cDieses Feature wurde &4nicht getestet&c! &4Erwarte Bugs/Fehler/Sonstige Probleme! &cWarnung!!<BREAK>&7Solltest du dieses Feature dennoch nutzen wollen, gib den Befehl einfach noch einmal ein!");
            msgDEConfig.set("Messages.Normal.ConvertFromEssentials.Start", "Starte Konvertierung von Essentials...");
            msgDEConfig.set("Messages.Normal.ConvertFromEssentials.Failed.NoDirectory", "&cFehler bei der Konvertierung! Es scheint als gäbe es keine Essentials Daten!");
            msgDEConfig.set("Messages.Normal.ConvertFromEssentials.Failed.Unknown", "&cFehler bei der Konvertierung! Siehe Konsole für mehr Daten! Oder schicke folgenden Fehler: &4<TARGET> &cund die Fehlermeldung der Konsole in den Support Discord (&4https://discord.gg/TbnyUrJ)");
            msgDEConfig.set("Messages.Normal.ConvertFromEssentials.Finished", "Die Konvertierung wurde beendet!");


            msgENConfig.set("Messages.Normal.ConvertFromEssentials.WarnNotTested", "&cWarning!! &cThis feature is &4not tested&c! &4Expect Bugs/Errors/Misc problems! &cWarning!!<BREAK>&7If you still want to use this feature, just type this command again!");
            msgENConfig.set("Messages.Normal.ConvertFromEssentials.Start", "Starting convertion from Essentials...");
            msgENConfig.set("Messages.Normal.ConvertFromEssentials.Failed.NoDirectory", "&cError while converting! It seems like there is now data from Essentials!");
            msgENConfig.set("Messages.Normal.ConvertFromEssentials.Failed.Unknown", "&cError while converting! Look console for more information! Or send this error: &4<TARGET> &cwith the console report in the support discord (&4https://discord.gg/TbnyUrJ)");
            msgENConfig.set("Messages.Normal.ConvertFromEssentials.Finished", "Convertion finished!");


            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Normal.ConvertFromEssentials.WarnNotTested", "&cWarnung!! &cDieses Feature wurde &4nicht getestet&c! &4Erwarte Bugs/Fehler/Sonstige Probleme! &cWarnung!!<BREAK>&7Solltest du dieses Feature dennoch nutzen wollen, gib den Befehl einfach noch einmal ein!");
                msgConfig.set("Messages.Normal.ConvertFromEssentials.Start", "Starte Konvertierung von Essentials...");
                msgConfig.set("Messages.Normal.ConvertFromEssentials.Failed.NoDirectory", "&cFehler bei der Konvertierung! Es scheint als gäbe es keine Essentials Daten!");
                msgConfig.set("Messages.Normal.ConvertFromEssentials.Failed.Unknown", "&cFehler bei der Konvertierung! Siehe Konsole für mehr Daten! Oder schicke folgenden Fehler: &4<TARGET> &cund die Fehlermeldung der Konsole in den Support Discord (&4https://discord.gg/TbnyUrJ)");
                msgConfig.set("Messages.Normal.ConvertFromEssentials.Finished", "Die Konvertierung wurde beendet!");
            } else {
                msgConfig.set("Messages.Normal.ConvertFromEssentials.WarnNotTested", "&cWarning!! &cThis feature is &4not tested&c! &4Expect Bugs/Errors/Misc problems! &cWarning!!<BREAK>&7If you still want to use this feature, just type this command again!");
                msgConfig.set("Messages.Normal.ConvertFromEssentials.Start", "Starting convertion from Essentials...");
                msgConfig.set("Messages.Normal.ConvertFromEssentials.Failed.NoDirectory", "&cError while converting! It seems like there is now data from Essentials!");
                msgConfig.set("Messages.Normal.ConvertFromEssentials.Failed.Unknown", "&cError while converting! Look console for more information! Or send this error: &4<TARGET> &cwith the console report in the support discord (&4https://discord.gg/TbnyUrJ)");
                msgConfig.set("Messages.Normal.ConvertFromEssentials.Finished", "Convertion finished!");
            }


            aliasConfig.set("Aliases.convertfromessentials.aliases", "No Aliases");


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 3.0 to 3.1!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("3.1")) {
            this.plugin.log("Updating config version 3.1 to 3.2...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);

            this.plugin.getConfig().set("version", "3.2");


            permissionConfig.set("Permissions.skull.self", "server.skull.self");
            permissionConfig.set("Permissions.skull.others", "server.skull.others");


            aliasConfig.set("Aliases.convertfromessentials.aliases", "No Aliases");


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 3.1 to 3.2!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("3.2")) {
            this.plugin.log("Updating config version 3.2 to 3.3...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);

            this.plugin.getConfig().set("version", "3.3");


            permissionConfig.set("Permissions.mute.use", "server.mute.use");
            permissionConfig.set("Permissions.mute.temporary", "server.mute.temporary");
            permissionConfig.set("Permissions.mute.permanent", "server.mute.permanent");
            permissionConfig.set("Permissions.mute.exempt", "server.mute.exempt");
            permissionConfig.set("Permissions.mute.shadow.permanent", "server.mute.permanent.shadow");
            permissionConfig.set("Permissions.mute.shadow.temporary", "server.mute.temporary.shadow");

            permissionConfig.set("Permissions.unmute", "server.unmute");


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 3.2 to 3.3!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("3.3")) {
            this.plugin.log("Updating config version 3.3 to 3.4...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);

            this.plugin.getConfig().set("version", "3.4");


            //permissionConfig.set("Permissions.", "server.");


            msgDEConfig.set("Messages.Normal.TPPos.NotANumber", "&4<NUMBER> &cist keine Zahl!");
            msgDEConfig.set("Messages.Normal.TPPos.Success.Self", "Du hast dich zu den Koordinaten &8X: <X> Y: <Y> Z: <Z> &7teleportiert!");
            msgDEConfig.set("Messages.Normal.TPPos.Success.Others", "Du hast &8<TARGET> &7zu den Koordinaten &8X: <X> Y: <Y> Z: <Z> &7teleportiert!");

            msgENConfig.set("Messages.Normal.TPPos.NotANumber", "&4<NUMBER> &cis not a valid number!");
            msgENConfig.set("Messages.Normal.TPPos.Success.Self", "You teleported yourself to the coordinates &8X: <X> Y: <Y> Z: <Z>&7!");
            msgENConfig.set("Messages.Normal.TPPos.Success.Others", "You teleported &8<TARGET> &7to the coordinates &8X: <X> Y: <Y> Z: <Z>&7!");


            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Normal.TPPos.NotANumber", "&4<NUMBER> &cist keine Zahl!");
                msgConfig.set("Messages.Normal.TPPos.Success.Self", "Du hast dich zu den Koordinaten &8X: <X> Y: <Y> Z: <Z> &7teleportiert!");
                msgConfig.set("Messages.Normal.TPPos.Success.Others", "Du hast &8<TARGET> &7zu den Koordinaten &8X: <X> Y: <Y> Z: <Z> &7teleportiert!");
            } else {
                msgConfig.set("Messages.Normal.TPPos.NotANumber", "&4<NUMBER> &cis not a valid number!");
                msgConfig.set("Messages.Normal.TPPos.Success.Self", "You teleported yourself to the coordinates &8X: <X> Y: <Y> Z: <Z>&7!");
                msgConfig.set("Messages.Normal.TPPos.Success.Others", "You teleported &8<TARGET> &7to the coordinates &8X: <X> Y: <Y> Z: <Z>&7!");
            }


            //aliasConfig.set("Aliases.command.aliases", "No Aliases");


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 3.3 to 3.4!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("3.4")) {
            this.plugin.log("Updating config version 3.4 to 3.5...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);

            this.plugin.getConfig().set("version", "3.5");


            permissionConfig.set("Permissions.unnick.required", false);
            permissionConfig.set("Permissions.unnick.permission", "server.unnick.self");
            permissionConfig.set("Permissions.unnick.others", "server.unnick.others");

            permissionConfig.set("Permissions.nick.self.use", "server.nick.self.use");
            permissionConfig.set("Permissions.nick.self.colored", "server.nick.self.colored");
            permissionConfig.set("Permissions.nick.others.use", "server.nick.others.use");
            permissionConfig.set("Permissions.nick.others.colored", "server.nick.others.colored");


            msgDEConfig.set("Messages.Syntax.Unnick", "Syntax: &8/<LABEL> <Spieler>");

            msgDEConfig.set("Messages.Syntax.Nick", "Syntax: &8/<LABEL> <Spieler> <Nickname>");

            msgDEConfig.set("Messages.Normal.Unnick.Self", "Du dich entnickt!");
            msgDEConfig.set("Messages.Normal.Unnick.Others", "Du hast &8<TARGET> &7entnickt!");

            msgDEConfig.set("Messages.Normal.Nick.NickTooLong", "&cDer Nick &4<NICK> &cist zu lang (max. 16 Zeichen)!");
            msgDEConfig.set("Messages.Normal.Nick.Success.Self", "Du hast deinen Nick zu &8<NICK> &7geändert!");
            msgDEConfig.set("Messages.Normal.Nick.Success.Others", "Du hast den Nick von &8<TARGET> &7zu &8<NICK> &7geändert!");

            msgDEConfig.set("Messages.Normal.DelHome.Success", "Du hast den Home &8<HOME> &7gelöscht!");
            msgDEConfig.set("Messages.Normal.DelHome.NoHomes", "&cDu hast keine Homes!");

            msgDEConfig.set("Messages.Normal.Hat.NoItem", "&cDu musst ein Item dafür in der Hand halten!");
            msgDEConfig.set("Messages.Normal.Hat.Success.HatRemoved", "Dein Hut &8[<TYPE>] &7wurde entfernt!");
            msgDEConfig.set("Messages.Normal.Hat.Success.NewHat", "Genieße deinen neuen Hut &8[<TYPE>]&7!");


            msgENConfig.set("Messages.Syntax.Unnick", "Syntax: &8/<LABEL> <Player>");

            msgENConfig.set("Messages.Syntax.Nick", "Syntax: &8/<LABEL> <Player> <Nickname>");

            msgENConfig.set("Messages.Normal.Unnick.Self", "You unnicked yourself!");
            msgENConfig.set("Messages.Normal.Unnick.Others", "You unnicked &8<TARGET>&7!");

            msgENConfig.set("Messages.Normal.Nick.NickTooLong", "&4<NICK> &cis too long (max. 16 characters)!");
            msgENConfig.set("Messages.Normal.Nick.Success.Self", "You changed your nickname to &8<NICK>&7!");
            msgENConfig.set("Messages.Normal.Nick.Success.Others", "You changed the nickname of &8<TARGET> &7to &8<NICK>&7!");

            msgENConfig.set("Messages.Normal.DelHome.Success", "You deleted your home &8<HOME>&7!");
            msgENConfig.set("Messages.Normal.DelHome.NoHomes", "&cYou do not have any homes, yet!");

            msgENConfig.set("Messages.Normal.Hat.NoItem", "&cYou have to hold an item!");
            msgENConfig.set("Messages.Normal.Hat.Success.HatRemoved", "Your hat &8[<TYPE>] &7was removed!");
            msgENConfig.set("Messages.Normal.Hat.Success.NewHat", "Enjoy your new hat: &8[<TYPE>]&7!");


            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Syntax.Unnick", "Syntax: &8/<LABEL> <Spieler>");

                msgConfig.set("Messages.Syntax.Nick", "Syntax: &8/<LABEL> <Spieler> <Nickname>");

                msgConfig.set("Messages.Normal.Unnick.Self", "Du dich entnickt!");
                msgConfig.set("Messages.Normal.Unnick.Others", "Du hast &8<TARGET> &7entnickt!");

                msgConfig.set("Messages.Normal.Nick.NickTooLong", "&cDer Nick &4<NICK> &cist zu lang (max. 16 Zeichen)!");
                msgConfig.set("Messages.Normal.Nick.Success.Self", "Du hast deinen Nick zu &8<NICK> &7geändert!");
                msgConfig.set("Messages.Normal.Nick.Success.Others", "Du hast den Nick von &8<TARGET> &7zu &8<NICK> &7geändert!");

                msgConfig.set("Messages.Normal.DelHome.Success", "Du hast den Home &8<HOME> &7gelöscht!");
                msgConfig.set("Messages.Normal.DelHome.NoHomes", "&cDu hast keine Homes!");

                msgConfig.set("Messages.Normal.Hat.NoItem", "&cDu musst ein Item dafür in der Hand halten!");
                msgConfig.set("Messages.Normal.Hat.Success.HatRemoved", "Dein Hut &8[<TYPE>] &7wurde entfernt!");
                msgConfig.set("Messages.Normal.Hat.Success.NewHat", "Genieße deinen neuen Hut &8[<TYPE>]&7!");
            } else {
                msgConfig.set("Messages.Syntax.Unnick", "Syntax: &8/<LABEL> <Player>");

                msgConfig.set("Messages.Syntax.Nick", "Syntax: &8/<LABEL> <Player> <Nickname>");

                msgConfig.set("Messages.Normal.Unnick.Self", "You unnicked yourself!");
                msgConfig.set("Messages.Normal.Unnick.Others", "You unnicked &8<TARGET>&7!");

                msgConfig.set("Messages.Normal.Nick.NickTooLong", "&4<NICK> &cis too long (max. 16 characters)!");
                msgConfig.set("Messages.Normal.Nick.Success.Self", "You changed your nickname to &8<NICK>&7!");
                msgConfig.set("Messages.Normal.Nick.Success.Others", "You changed the nickname of &8<TARGET> &7to &8<NICK>&7!");

                msgConfig.set("Messages.Normal.DelHome.Success", "You deleted your home &8<HOME>&7!");
                msgConfig.set("Messages.Normal.DelHome.NoHomes", "&cYou do not have any homes, yet!");

                msgConfig.set("Messages.Normal.Hat.NoItem", "&cYou have to hold an item!");
                msgConfig.set("Messages.Normal.Hat.Success.HatRemoved", "Your hat &8[<TYPE>] &7was removed!");
                msgConfig.set("Messages.Normal.Hat.Success.NewHat", "Enjoy your new hat: &8[<TYPE>]&7!");
            }


            aliasConfig.set("Aliases.nick.aliases", "No Aliases");
            aliasConfig.set("Aliases.unnick.aliases", "No Aliases");


            commandsConfig.set("nick", true);
            commandsConfig.set("unnick", true);


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 3.4 to 3.5!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("3.5")) {
            this.plugin.log("Updating config version 3.5 to 3.6...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);

            this.plugin.getConfig().set("version", "3.6");


            permissionConfig.set("Permissions.joinfullserver", "server.joinfullserver");


            msgDEConfig.set("Messages.Normal.Home.InstantTeleporting", "Du wurdest zum Home &8<HOME> &7teleportiert!");

            msgDEConfig.set("Messages.Misc.ServerFull", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4Der Server ist voll!<BREAK>&cUm trotzdem beitreten zu können, benötigst du die Permission &4<PERMISSION>&c!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");


            msgENConfig.set("Messages.Normal.Home.InstantTeleporting", "You were teleported to your home &8<HOME>&7!");

            msgENConfig.set("Messages.Misc.ServerFull", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4The Server is full!<BREAK>&cTo join anyway, you need the permission &4<PERMISSION>&c!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");


            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Normal.Home.InstantTeleporting", "Du wurdest zum Home &8<HOME> &7teleportiert!");

                msgConfig.set("Messages.Misc.ServerFull", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4Der Server ist voll!<BREAK>&cUm trotzdem beitreten zu können, benötigst du die Permission &4<PERMISSION>&c!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
            } else if (msgConfig.getString("language").equalsIgnoreCase("cz")) {
            } else {
                msgConfig.set("Messages.Normal.Home.InstantTeleporting", "You were teleported to your home &8<HOME>&7!");

                msgConfig.set("Messages.Misc.ServerFull", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4The Server is full!<BREAK>&cTo join anyway, you need the permission &4<PERMISSION>&c!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
            }


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 3.5 to 3.6!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("3.6")) {
            this.plugin.log("Updating config version 3.6 to 3.7...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);

            this.plugin.getConfig().set("version", "3.7");


            //permissionConfig.set("Permissions.permission", "server.permission");


            msgDEConfig.set("Messages.Normal.UnBan.Success", "Du hast den Spieler &8<TARGET> &7entbannt!");
            msgDEConfig.set("Messages.Normal.UnBan.NotBanned", "&cDer Spieler &4<TARGET> &cist nicht gebannt!");

            msgDEConfig.set("Messages.Normal.Mute.CannotMute", "&cDu kannst den Spieler &4<TARGET>&c nicht muten!");
            msgDEConfig.set("Messages.Normal.Mute.DefaultReason", "&cEs wurde kein Grund angegeben");
            msgDEConfig.set("Messages.Normal.Mute.NotANumber", "&4<TIME> &cist keine gültige Zahl!");
            msgDEConfig.set("Messages.Normal.Mute.NotATimeUnit", "&4<TIMEUNIT> &cist keine gültige Einheit!");
            msgDEConfig.set("Messages.Normal.Mute.Success", "Du hast den Spieler &8<TARGET> &7gemuted! (Unmute: &8<UNMUTE_DATE>&7) (Grund: &8<REASON>&7)");
            msgDEConfig.set("Messages.Normal.Mute.Muted", "&cDu bist gemuted! (Bis: &4<UNMUTE_DATE>&c)");

            msgDEConfig.set("Messages.Normal.UnMute.NotMuted", "&cDer Spieler &4<TARGET> &cist nicht gemuted!");
            msgDEConfig.set("Messages.Normal.UnMute.Success", "Du hast den Spieler &8<TARGET> &7entmuted!");


            msgENConfig.set("Messages.Normal.Home.InstantTeleporting", "You were teleported to your home &8<HOME>&7!");

            msgENConfig.set("Messages.Normal.UnBan.Success", "You unbanned &8<TARGET>&7!");
            msgENConfig.set("Messages.Normal.UnBan.NotBanned", "&4<TARGET> &cis not banned!");

            msgENConfig.set("Messages.Normal.Mute.CannotMute", "&cYou cannot mute &4<TARGET>&c!");
            msgENConfig.set("Messages.Normal.Mute.DefaultReason", "&cNo reason was specified");
            msgENConfig.set("Messages.Normal.Mute.NotANumber", "&4<TIME> &cis not a valid number!");
            msgENConfig.set("Messages.Normal.Mute.NotATimeUnit", "&4<TIMEUNIT> &cis not a valid unit!");
            msgENConfig.set("Messages.Normal.Mute.Success", "You muted &8<TARGET>&7! (Unmute: &8<UNMUTE_DATE>&7) (Reason: &8<REASON>&7)");
            msgENConfig.set("Messages.Normal.Mute.Muted", "&cYou are muted! (Unmute: &4<UNMUTE_DATE>&c)");

            msgENConfig.set("Messages.Normal.UnMute.NotMuted", "&4<TARGET> &cis not muted!");
            msgENConfig.set("Messages.Normal.UnMute.Success", "You unmuted &8<TARGET>&7!");


            msgCZConfig.set("Messages.Normal.UnBan.Success", "Odbanoval si &8<TARGET>&7!");
            msgCZConfig.set("Messages.Normal.UnBan.NotBanned", "&4<TARGET> &cnení zabanovaný!");

            msgCZConfig.set("Messages.Normal.Mute.CannotMute", "&cNemůžeš umlčet &4<TARGET>&c!");
            msgCZConfig.set("Messages.Normal.Mute.DefaultReason", "&cNebyl specifikován Důvod");
            msgCZConfig.set("Messages.Normal.Mute.NotANumber", "&4<NUMBER> &cnení validní číslo!");
            msgCZConfig.set("Messages.Normal.Mute.NotATimeUnit", "&4<TIMEUNIT> &cnení platná jednotka!");
            msgCZConfig.set("Messages.Normal.Mute.Success", "Umlčel si &8<TARGET>&7! (Datum odmlčení: &8<UNMUTE_DATE>&7) (Důvod: &8<REASON>&7)");
            msgCZConfig.set("Messages.Normal.Mute.Muted", "&cByl si umlčen! (Datum odmlčení: &4<UNMUTE_DATE>&c)");

            msgCZConfig.set("Messages.Normal.UnMute.NotMuted", "&4<TARGET> &cnení umlčen!");
            msgCZConfig.set("Messages.Normal.UnMute.Success", "Odmlčel si &8<TARGET>&7!");


            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Normal.UnBan.Success", "Du hast den Spieler &8<TARGET> &7entbannt!");
                msgConfig.set("Messages.Normal.UnBan.NotBanned", "&cDer Spieler &4<TARGET> &cist nicht gebannt!");

                msgConfig.set("Messages.Normal.Mute.CannotMute", "&cDu kannst den Spieler &4<TARGET>&c nicht muten!");
                msgConfig.set("Messages.Normal.Mute.DefaultReason", "&cEs wurde kein Grund angegeben");
                msgConfig.set("Messages.Normal.Mute.NotANumber", "&4<TIME> &cist keine gültige Zahl!");
                msgConfig.set("Messages.Normal.Mute.NotATimeUnit", "&4<TIMEUNIT> &cist keine gültige Einheit!");
                msgConfig.set("Messages.Normal.Mute.Success", "Du hast den Spieler &8<TARGET> &7gemuted! (Unmute: &8<UNMUTE_DATE>&7) (Grund: &8<REASON>&7)");
                msgConfig.set("Messages.Normal.Mute.Muted", "&cDu bist gemuted! (Bis: &4<UNMUTE_DATE>&c)");

                msgConfig.set("Messages.Normal.UnMute.NotMuted", "&cDer Spieler &4<TARGET> &cist nicht gemuted!");
                msgConfig.set("Messages.Normal.UnMute.Success", "Du hast den Spieler &8<TARGET> &7entmuted!");
            } else if (msgConfig.getString("language").equalsIgnoreCase("cz")) {
                msgConfig.set("Messages.Normal.UnBan.Success", "Odbanoval si &8<TARGET>&7!");
                msgConfig.set("Messages.Normal.UnBan.NotBanned", "&4<TARGET> &cnení zabanovaný!");

                msgConfig.set("Messages.Normal.Mute.CannotMute", "&cNemůžeš umlčet &4<TARGET>&c!");
                msgConfig.set("Messages.Normal.Mute.DefaultReason", "&cNebyl specifikován Důvod");
                msgConfig.set("Messages.Normal.Mute.NotANumber", "&4<NUMBER> &cnení validní číslo!");
                msgConfig.set("Messages.Normal.Mute.NotATimeUnit", "&4<TIMEUNIT> &cnení platná jednotka!");
                msgConfig.set("Messages.Normal.Mute.Success", "Umlčel si &8<TARGET>&7! (Datum odmlčení: &8<UNMUTE_DATE>&7) (Důvod: &8<REASON>&7)");
                msgConfig.set("Messages.Normal.Mute.Muted", "&cByl si umlčen! (Datum odmlčení: &4<UNMUTE_DATE>&c)");

                msgConfig.set("Messages.Normal.UnMute.NotMuted", "&4<TARGET> &cnení umlčen!");
                msgConfig.set("Messages.Normal.UnMute.Success", "Odmlčel si &8<TARGET>&7!");
            } else {
                msgConfig.set("Messages.Normal.Home.InstantTeleporting", "You were teleported to your home &8<HOME>&7!");

                msgConfig.set("Messages.Normal.UnBan.Success", "You unbanned &8<TARGET>&7!");
                msgConfig.set("Messages.Normal.UnBan.NotBanned", "&4<TARGET> &cis not banned!");

                msgConfig.set("Messages.Normal.Mute.CannotMute", "&cYou cannot mute &4<TARGET>&c!");
                msgConfig.set("Messages.Normal.Mute.DefaultReason", "&cNo reason was specified");
                msgConfig.set("Messages.Normal.Mute.NotANumber", "&4<TIME> &cis not a valid number!");
                msgConfig.set("Messages.Normal.Mute.NotATimeUnit", "&4<TIMEUNIT> &cis not a valid unit!");
                msgConfig.set("Messages.Normal.Mute.Success", "You muted &8<TARGET>&7! (Unmute: &8<UNMUTE_DATE>&7) (Reason: &8<REASON>&7)");
                msgConfig.set("Messages.Normal.Mute.Muted", "&cYou are muted! (Unmute: &4<UNMUTE_DATE>&c)");

                msgConfig.set("Messages.Normal.UnMute.NotMuted", "&4<TARGET> &cis not muted!");
                msgConfig.set("Messages.Normal.UnMute.Success", "You unmuted &8<TARGET>&7!");
            }


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 3.6 to 3.7!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("3.7")) {
            this.plugin.log("Updating config version 3.7 to 3.8...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);


            this.plugin.getConfig().set("kit.giveonfirstspawn", false);
            this.plugin.getConfig().set("kit.givenkit", "starter");
            this.plugin.getConfig().set("version", "3.8");


            permissionConfig.set("Permissions.serversystem.update", "server.serversystem.update");
            permissionConfig.set("Permissions.burn", "server.burn");
            permissionConfig.set("Permissions.kick.exempt", "server.kick.exempt");


            msgDEConfig.set("Messages.Normal.ServerSystem.Update.Checking", "Suche nach Updates...");
            msgDEConfig.set("Messages.Normal.ServerSystem.Update.NewVersion", "Eine neue Version wurde gefunden (<VERSION>)! Starte den Server neu, um das Update zu installieren!");
            msgDEConfig.set("Messages.Normal.ServerSystem.Update.LatestVersion", "Du nutzt die neueste Version von ServerSystem!");


            msgENConfig.set("Messages.Normal.ServerSystem.Update.Checking", "Checking for updates...");
            msgENConfig.set("Messages.Normal.ServerSystem.Update.NewVersion", "A new version was found (<VERSION>)! Restart the server to install the update!");
            msgENConfig.set("Messages.Normal.ServerSystem.Update.LatestVersion", "You are using the latest version of serversystem!");


            msgCZConfig.set("Messages.Normal.ServerSystem.Update.Checking", "Hledám updaty...");
            msgCZConfig.set("Messages.Normal.ServerSystem.Update.NewVersion", "Nová verze nalezena (<VERSION>)! Restartuj server pro updatování!");
            msgCZConfig.set("Messages.Normal.ServerSystem.Update.LatestVersion", "Máš poslední verzi!");


            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Normal.ServerSystem.Update.Checking", "Suche nach Updates...");
                msgConfig.set("Messages.Normal.ServerSystem.Update.NewVersion", "Eine neue Version wurde gefunden (<VERSION>)! Starte den Server neu, um das Update zu installieren!");
                msgConfig.set("Messages.Normal.ServerSystem.Update.LatestVersion", "Du nutzt die neueste Version von ServerSystem!");
            } else if (msgConfig.getString("language").equalsIgnoreCase("cz")) {
                msgConfig.set("Messages.Normal.ServerSystem.Update.Checking", "Hledám updaty...");
                msgConfig.set("Messages.Normal.ServerSystem.Update.NewVersion", "Nová verze nalezena (<VERSION>)! Restartuj server pro updatování!");
                msgConfig.set("Messages.Normal.ServerSystem.Update.LatestVersion", "Máš poslední verzi!");
            } else {
                msgConfig.set("Messages.Normal.ServerSystem.Update.Checking", "Checking for updates...");
                msgConfig.set("Messages.Normal.ServerSystem.Update.NewVersion", "A new version was found (<VERSION>)! Restart the server to install the update!");
                msgConfig.set("Messages.Normal.ServerSystem.Update.LatestVersion", "You are using the latest version of serversystem!");
            }


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 3.7 to 3.8!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("3.8")) {
            this.plugin.log("Updating config version 3.8 to 3.9...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);


            this.plugin.getConfig().set("version", "3.9");


            permissionConfig.set("Permissions.joinfullserver.premium", "server.joinfullserver.premium");
            permissionConfig.set("Permissions.joinfullserver.admin", "server.joinfullserver.admin");

            permissionConfig.set("Permissions.god.self", "server.god.self");
            permissionConfig.set("Permissions.god.others", "server.god.others");

            permissionConfig.set("Permissions.money.self.required", false);
            permissionConfig.set("Permissions.money.self.permission", "server.money.self");
            permissionConfig.set("Permissions.money.others", "server.money.others");

            permissionConfig.set("Permissions.pay.required", false);
            permissionConfig.set("Permissions.pay.permission", "server.pay");

            permissionConfig.set("Permissions.reply.required", false);
            permissionConfig.set("Permissions.reply.permission", "server.reply");

            permissionConfig.set("Permissions.lag", "server.lag");


            msgDEConfig.set("Messages.Normal.God.Self.Activated", "Du bist nun unsterblich!");
            msgDEConfig.set("Messages.Normal.God.Self.Deactivated", "Du bist nun nicht mehr unsterblich!");
            msgDEConfig.set("Messages.Normal.God.Others.Activated.Sender", "Der Spieler &8<TARGET> &7ist nun unsterblich!");
            msgDEConfig.set("Messages.Normal.God.Others.Deactivated.Sender", "Der Spieler &8<TARGET> &7ist nun nicht mehr unsterblich!");
            msgDEConfig.set("Messages.Normal.God.Others.Activated.Target", "Du bist nun unsterblich!");
            msgDEConfig.set("Messages.Normal.God.Others.Deactivated.Target", "Du bist nun nicht mehr unsterblich!");

            msgDEConfig.set("Messages.Normal.Weather.RainStopped", "Du hast den Regen in der Welt &8<WORLD> &7gestoppt!");
            msgDEConfig.set("Messages.Normal.Weather.RainStarted", "Du hast den Regen in der Welt &8<WORLD> &7gestartet!");
            msgDEConfig.set("Messages.Normal.Weather.NoWorld", "&cDie Welt &4<WORLD> &cexistiert nicht!");

            msgDEConfig.set("Messages.Normal.KickedByHigher.Admin", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4Du wurdest gekickt!<BREAK>&6Grund: &cDu wurdest gekickt, um einem Admin Platz zu machen!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
            msgDEConfig.set("Messages.Normal.KickedByHigher.Premium", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4Du wurdest gekickt!<BREAK>&6Grund: &cDu wurdest gekickt, um einem Premium Spieler Platz zu machen!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");

            msgDEConfig.set("Messages.Normal.Reply", "&cDu hast bisher noch mit niemanden geschrieben!");


            msgENConfig.set("Messages.Normal.God.Self.Activated", "You are now invincible!");
            msgENConfig.set("Messages.Normal.God.Self.Deactivated", "You are no longer invincible!");
            msgENConfig.set("Messages.Normal.God.Others.Activated.Sender", "&8<TARGET> &7is now invincible!");
            msgENConfig.set("Messages.Normal.God.Others.Deactivated.Sender", "&8<TARGET> &7is no longer invincible!");
            msgENConfig.set("Messages.Normal.God.Others.Activated.Target", "You are now invincible!");
            msgENConfig.set("Messages.Normal.God.Others.Deactivated.Target", "You are no longer invincible!");

            msgENConfig.set("Messages.Normal.Weather.RainStopped", "You stopped the rain in world &8<WORLD>&7!");
            msgENConfig.set("Messages.Normal.Weather.RainStarted", "You started the rain in world &8<WORLD>&7!");
            msgENConfig.set("Messages.Normal.Weather.NoWorld", "&cThe world &4<WORLD> &cdoes not exist!");

            msgENConfig.set("Messages.Normal.KickedByHigher.Admin", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4You got kicked!<BREAK>&6Reason: &cBecause an admin needed to join, you were kicked!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
            msgENConfig.set("Messages.Normal.KickedByHigher.Premium", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4You got kicked!<BREAK>&6Reason: &cBecause a premium player needed to join, you were kicked!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");

            msgENConfig.set("Messages.Normal.Reply", "&cYou did not pm someone, yet");


            msgCZConfig.set("Messages.Normal.God.Self.Activated", "Nyní jsi nesmrtelný!");
            msgCZConfig.set("Messages.Normal.God.Self.Deactivated", "Nyní nejsi nesmrtelný!");
            msgCZConfig.set("Messages.Normal.God.Others.Activated.Sender", "&8<TARGET> &7je nesmrtelný!");
            msgCZConfig.set("Messages.Normal.God.Others.Deactivated.Sender", "&8<TARGET> &7není nesmrtelný!");
            msgCZConfig.set("Messages.Normal.God.Others.Activated.Target", "Jsi nesmrtelný!");
            msgCZConfig.set("Messages.Normal.God.Others.Deactivated.Target", "Už nejsi nesmrtelný!");

            msgCZConfig.set("Messages.Normal.Weather.RainStopped", "Vypnul si déšť ve světě &8<WORLD>&7!");
            msgCZConfig.set("Messages.Normal.Weather.RainStarted", "Zapnul si déšť ve světě &8<WORLD>&7!");
            msgCZConfig.set("Messages.Normal.Weather.NoWorld", "&cSvět &4<WORLD> &cneexistuje!");

            msgCZConfig.set("Messages.Normal.KickedByHigher.Admin", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&cByl jsi vyhozen ze serveru!<BREAK>&6Důvod: &cProtože se připojil admin, byl si vyhozen!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
            msgCZConfig.set("Messages.Normal.KickedByHigher.Premium", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&cByl jsi vyhozen ze serveru!<BREAK>&6Důvod: &cProtože se připojilo premium, byl si vyhozen!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");

            msgCZConfig.set("Messages.Normal.Reply", "&cJeště si nikoho neoznačil");


            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Normal.God.Self.Activated", "Du bist nun unsterblich!");
                msgConfig.set("Messages.Normal.God.Self.Deactivated", "Du bist nun nicht mehr unsterblich!");
                msgConfig.set("Messages.Normal.God.Others.Activated.Sender", "Der Spieler &8<TARGET> &7ist nun unsterblich!");
                msgConfig.set("Messages.Normal.God.Others.Deactivated.Sender", "Der Spieler &8<TARGET> &7ist nun nicht mehr unsterblich!");
                msgConfig.set("Messages.Normal.God.Others.Activated.Target", "Du bist nun unsterblich!");
                msgConfig.set("Messages.Normal.God.Others.Deactivated.Target", "Du bist nun nicht mehr unsterblich!");

                msgConfig.set("Messages.Normal.Weather.RainStopped", "Du hast den Regen in der Welt &8<WORLD> &7gestoppt!");
                msgConfig.set("Messages.Normal.Weather.RainStarted", "Du hast den Regen in der Welt &8<WORLD> &7gestartet!");
                msgConfig.set("Messages.Normal.Weather.NoWorld", "&cDie Welt &4<WORLD> &cexistiert nicht!");

                msgConfig.set("Messages.Normal.KickedByHigher.Admin", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4Du wurdest gekickt!<BREAK>&6Grund: &cDu wurdest gekickt, um einem Admin Platz zu machen!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
                msgConfig.set("Messages.Normal.KickedByHigher.Premium", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4Du wurdest gekickt!<BREAK>&6Grund: &cDu wurdest gekickt, um einem Premium Spieler Platz zu machen!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");

                msgConfig.set("Messages.Normal.Reply", "&cDu hast bisher noch mit niemanden geschrieben!");

            } else if (msgConfig.getString("language").equalsIgnoreCase("cz")) {
                msgConfig.set("Messages.Normal.God.Self.Activated", "Nyní jsi nesmrtelný!");
                msgConfig.set("Messages.Normal.God.Self.Deactivated", "Nyní nejsi nesmrtelný!");
                msgConfig.set("Messages.Normal.God.Others.Activated.Sender", "&8<TARGET> &7je nesmrtelný!");
                msgConfig.set("Messages.Normal.God.Others.Deactivated.Sender", "&8<TARGET> &7není nesmrtelný!");
                msgConfig.set("Messages.Normal.God.Others.Activated.Target", "Jsi nesmrtelný!");
                msgConfig.set("Messages.Normal.God.Others.Deactivated.Target", "Už nejsi nesmrtelný!");

                msgConfig.set("Messages.Normal.Weather.RainStopped", "Vypnul si déšť ve světě &8<WORLD>&7!");
                msgConfig.set("Messages.Normal.Weather.RainStarted", "Zapnul si déšť ve světě &8<WORLD>&7!");
                msgConfig.set("Messages.Normal.Weather.NoWorld", "&cSvět &4<WORLD> &cneexistuje!");

                msgConfig.set("Messages.Normal.KickedByHigher.Admin", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&cByl jsi vyhozen ze serveru!<BREAK>&6Důvod: &cProtože se připojil admin, byl si vyhozen!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
                msgConfig.set("Messages.Normal.KickedByHigher.Premium", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&cByl jsi vyhozen ze serveru!<BREAK>&6Důvod: &cProtože se připojilo premium, byl si vyhozen!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");

                msgConfig.set("Messages.Normal.Reply", "&cJeště si nikoho neoznačil");
            } else {
                msgConfig.set("Messages.Normal.God.Self.Activated", "You are now invincible!");
                msgConfig.set("Messages.Normal.God.Self.Deactivated", "You are no longer invincible!");
                msgConfig.set("Messages.Normal.God.Others.Activated.Sender", "&8<TARGET> &7is now invincible!");
                msgConfig.set("Messages.Normal.God.Others.Deactivated.Sender", "&8<TARGET> &7is no longer invincible!");
                msgConfig.set("Messages.Normal.God.Others.Activated.Target", "You are now invincible!");
                msgConfig.set("Messages.Normal.God.Others.Deactivated.Target", "You are no longer invincible!");

                msgConfig.set("Messages.Normal.Weather.RainStopped", "You stopped the rain in world &8<WORLD>&7!");
                msgConfig.set("Messages.Normal.Weather.RainStarted", "You started the rain in world &8<WORLD>&7!");
                msgConfig.set("Messages.Normal.Weather.NoWorld", "&cThe world &4<WORLD> &cdoes not exist!");

                msgConfig.set("Messages.Normal.KickedByHigher.Admin", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4You got kicked!<BREAK>&6Reason: &cBecause an admin needed to join, you were kicked!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
                msgConfig.set("Messages.Normal.KickedByHigher.Premium", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4You got kicked!<BREAK>&6Reason: &cBecause a premium player needed to join, you were kicked!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");

                msgConfig.set("Messages.Normal.Reply", "&cYou did not pm someone, yet");
            }


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 3.8 to 3.9!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("3.9")) {
            this.plugin.log("Updating config version 3.9 to 4.0...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);


            this.plugin.getConfig().set("version", "4.0");


            //permissionConfig.set("Permissions.joinfullserver.premium", "server.joinfullserver.premium");


            msgDEConfig.set("Messages.Misc.JoinMessage.Change", true);

            msgDEConfig.set("Messages.Misc.QuitMessage.Change", true);


            msgENConfig.set("Messages.Misc.JoinMessage.Change", true);

            msgENConfig.set("Messages.Misc.QuitMessage.Change", true);


            msgCZConfig.set("Messages.Misc.JoinMessage.Change", true);

            msgCZConfig.set("Messages.Misc.QuitMessage.Change", true);


            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Misc.JoinMessage.Change", true);

                msgConfig.set("Messages.Misc.QuitMessage.Change", true);
            } else if (msgConfig.getString("language").equalsIgnoreCase("cz")) {
                msgConfig.set("Messages.Misc.JoinMessage.Change", true);

                msgConfig.set("Messages.Misc.QuitMessage.Change", true);
            } else {
                msgConfig.set("Messages.Misc.JoinMessage.Change", true);

                msgConfig.set("Messages.Misc.QuitMessage.Change", true);
            }


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 3.9 to 4.0!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("4.0")) {
            this.plugin.log("Updating config version 4.0 to 4.1...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);


            this.plugin.getConfig().set("version", "4.1");


            permissionConfig.set("Permissions.baltop.required", false);
            permissionConfig.set("Permissions.baltop.permission", "server.baltop");


            msgDEConfig.set("Messages.Normal.BalTop", "&8[]&7--------------------------------&8[]<BREAK>&3Baltop<BREAK>&8[]&7--------------------------------&8[]<BREAK>&61.<FIRST><BREAK>&62.<SECOND><BREAK>&63.<THIRD><BREAK>&64.<FOURTH><BREAK>&65.<FIFTH><BREAK>&66.<SIXTH><BREAK>&67.<SEVENTH><BREAK>&68.<EIGHTH><BREAK>&69.<NINTH><BREAK>&610.<TENTH><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3Baltop<BREAK>&8[]&7--------------------------------&8[]");


            msgENConfig.set("Messages.Normal.BalTop", "&8[]&7--------------------------------&8[]<BREAK>&3Baltop<BREAK>&8[]&7--------------------------------&8[]<BREAK>&61.<FIRST><BREAK>&62.<SECOND><BREAK>&63.<THIRD><BREAK>&64.<FOURTH><BREAK>&65.<FIFTH><BREAK>&66.<SIXTH><BREAK>&67.<SEVENTH><BREAK>&68.<EIGHTH><BREAK>&69.<NINTH><BREAK>&610.<TENTH><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3Baltop<BREAK>&8[]&7--------------------------------&8[]");


            msgCZConfig.set("Messages.Normal.BalTop", "&8[]&7--------------------------------&8[]<BREAK>&3Baltop<BREAK>&8[]&7--------------------------------&8[]<BREAK>&61.<FIRST><BREAK>&62.<SECOND><BREAK>&63.<THIRD><BREAK>&64.<FOURTH><BREAK>&65.<FIFTH><BREAK>&66.<SIXTH><BREAK>&67.<SEVENTH><BREAK>&68.<EIGHTH><BREAK>&69.<NINTH><BREAK>&610.<TENTH><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3Baltop<BREAK>&8[]&7--------------------------------&8[]");


            if (msgConfig.getString("language").equalsIgnoreCase("de"))
                msgConfig.set("Messages.Normal.BalTop", "&8[]&7--------------------------------&8[]<BREAK>&3Baltop<BREAK>&8[]&7--------------------------------&8[]<BREAK>&61.<FIRST><BREAK>&62.<SECOND><BREAK>&63.<THIRD><BREAK>&64.<FOURTH><BREAK>&65.<FIFTH><BREAK>&66.<SIXTH><BREAK>&67.<SEVENTH><BREAK>&68.<EIGHTH><BREAK>&69.<NINTH><BREAK>&610.<TENTH><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3Baltop<BREAK>&8[]&7--------------------------------&8[]");
            else if (msgConfig.getString("language").equalsIgnoreCase("cz"))
                msgConfig.set("Messages.Normal.BalTop", "&8[]&7--------------------------------&8[]<BREAK>&3Baltop<BREAK>&8[]&7--------------------------------&8[]<BREAK>&61.<FIRST><BREAK>&62.<SECOND><BREAK>&63.<THIRD><BREAK>&64.<FOURTH><BREAK>&65.<FIFTH><BREAK>&66.<SIXTH><BREAK>&67.<SEVENTH><BREAK>&68.<EIGHTH><BREAK>&69.<NINTH><BREAK>&610.<TENTH><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3Baltop<BREAK>&8[]&7--------------------------------&8[]");
            else
                msgConfig.set("Messages.Normal.BalTop", "&8[]&7--------------------------------&8[]<BREAK>&3Baltop<BREAK>&8[]&7--------------------------------&8[]<BREAK>&61.<FIRST><BREAK>&62.<SECOND><BREAK>&63.<THIRD><BREAK>&64.<FOURTH><BREAK>&65.<FIFTH><BREAK>&66.<SIXTH><BREAK>&67.<SEVENTH><BREAK>&68.<EIGHTH><BREAK>&69.<NINTH><BREAK>&610.<TENTH><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3Baltop<BREAK>&8[]&7--------------------------------&8[]");


            aliasConfig.set("Aliases.baltop.aliases", "No Aliases");


            commandsConfig.set("baltop", true);


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 4.0 to 4.1!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("4.1")) {
            this.plugin.log("Updating config version 4.1 to 4.2...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);


            this.plugin.getConfig().set("version", "4.2");


            permissionConfig.set("Permissions.noafk", "server.noafk");


            msgDEConfig.set("Messages.Normal.AFK.Kick", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4Du wurdest gekickt!<BREAK>&6Grund: &cDu warst länger als 10 Minuten Inaktiv!<BREAK>&6Von: &c<SENDER><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
            msgDEConfig.set("Messages.Normal.AFK.Afk", "Du bist nun afk!");
            msgDEConfig.set("Messages.Normal.AFK.NotAfk", "Du bist nun nicht mehr afk!");


            msgENConfig.set("Messages.Normal.AFK.Kick", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4You got kicked!<BREAK>&6Reason: You were afk for more than 10 minutes!<BREAK>&6By: &c<SENDER><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
            msgENConfig.set("Messages.Normal.AFK.Afk", "You are now afk!");
            msgENConfig.set("Messages.Normal.AFK.NotAfk", "You are no longer afk!");


            msgCZConfig.set("Messages.Normal.AFK.Kick", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&cByl jsi vyhozen ze serveru!<BREAK>&6Důvod: &cSi AFK dýl jak 10 minut!<BREAK>&6Od: &c<SENDER><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
            msgCZConfig.set("Messages.Normal.AFK.Afk", "Nyní si AFK!");
            msgCZConfig.set("Messages.Normal.AFK.NotAfk", "Už nejsi AFK!");


            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Normal.AFK.Kick", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4Du wurdest gekickt!<BREAK>&6Grund: &cDu warst länger als 10 Minuten Inaktiv!<BREAK>&6Von: &c<SENDER><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
                msgConfig.set("Messages.Normal.AFK.Afk", "Du bist nun afk!");
                msgConfig.set("Messages.Normal.AFK.NotAfk", "Du bist nun nicht mehr afk!");
            } else if (msgConfig.getString("language").equalsIgnoreCase("cz")) {
                msgConfig.set("Messages.Normal.AFK.Kick", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&cByl jsi vyhozen ze serveru!<BREAK>&6Důvod: &cSi AFK dýl jak 10 minut!<BREAK>&6Od: &c<SENDER><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
                msgConfig.set("Messages.Normal.AFK.Afk", "Nyní si AFK!");
                msgConfig.set("Messages.Normal.AFK.NotAfk", "Už nejsi AFK!");
            } else {
                msgConfig.set("Messages.Normal.AFK.Kick", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4You got kicked!<BREAK>&6Reason: You were afk for more than 10 minutes!<BREAK>&6By: &c<SENDER><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
                msgConfig.set("Messages.Normal.AFK.Afk", "You are now afk!");
                msgConfig.set("Messages.Normal.AFK.NotAfk", "You are no longer afk!");
            }


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 4.1 to 4.2!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("4.2")) {
            this.plugin.log("Updating config version 4.2 to 4.3...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);

            this.plugin.getConfig().set("afksystem", true);
            this.plugin.getConfig().set("setplayerlistname", true);

            this.plugin.getConfig().set("economy.enabled", true);

            this.plugin.getConfig().set("bansystem.enabled", true);

            this.plugin.getConfig().set("version", "4.3");


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 4.2 to 4.3!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("4.3")) {
            this.plugin.log("Updating config version 4.3 to 4.4...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);

            this.plugin.getConfig().set("metrics", true);

            this.plugin.getConfig().set("version", "4.4");

            permissionConfig.set("Permissions.maintenance.toggle", "server.maintenance.toggle");
            permissionConfig.set("Permissions.maintenance.join", "server.maintenance.join");


            msgDEConfig.set("Messages.Normal.Maintenance.NoJoin", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4Du wurdest gekickt!<BREAK>&cDie Wartungen sind zurzeit aktiv!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
            msgDEConfig.set("Messages.Normal.Maintenance.Kick", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4Du wurdest gekickt!<BREAK>&6Grund: &cDie Wartungen wurden aktiviert!<BREAK>&6Von: &c<SENDER><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
            msgDEConfig.set("Messages.Normal.Maintenance.Activated", "Du hast die Wartungen aktiviert!");
            msgDEConfig.set("Messages.Normal.Maintenance.Deactivated", "Du hast die Wartungen deaktiviert!");


            msgENConfig.set("Messages.Normal.Maintenance.NoJoin", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4You got kicked!<BREAK>&cThe server is currently under maintenance!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
            msgENConfig.set("Messages.Normal.Maintenance.Kick", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4You got kicked!<BREAK>&6Reason: &cThe server is now under maintenance!<BREAK>&6By: &c<SENDER><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
            msgENConfig.set("Messages.Normal.Maintenance.Activated", "The server is now under maintenance!");
            msgENConfig.set("Messages.Normal.Maintenance.Deactivated", "The server is no longer under maintenance!");


            msgCZConfig.set("Messages.Normal.Maintenance.NoJoin", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4Byl jsi vyhozen ze serveru!<BREAK>&cServer je momentálně v údržbě!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
            msgCZConfig.set("Messages.Normal.Maintenance.Kick", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4Byl jsi vyhozen ze serveru!<BREAK>&6Důvod: &cServer je nyní v údržbě!<BREAK>&6By: &c<SENDER><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
            msgCZConfig.set("Messages.Normal.Maintenance.Activated", "Server je nyní v údržbě!");
            msgCZConfig.set("Messages.Normal.Maintenance.Deactivated", "Server již není v údržbě!");


            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Normal.Maintenance.NoJoin", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4Du wurdest gekickt!<BREAK>&cDie Wartungen sind zurzeit aktiv!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
                msgConfig.set("Messages.Normal.Maintenance.Kick", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4Du wurdest gekickt!<BREAK>&6Grund: &cDie Wartungen wurden aktiviert!<BREAK>&6Von: &c<SENDER><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
                msgConfig.set("Messages.Normal.Maintenance.Activated", "Du hast die Wartungen aktiviert!");
                msgConfig.set("Messages.Normal.Maintenance.Deactivated", "Du hast die Wartungen deaktiviert!");

            } else if (msgConfig.getString("language").equalsIgnoreCase("cz")) {
                msgConfig.set("Messages.Normal.Maintenance.NoJoin", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4Byl jsi vyhozen ze serveru!<BREAK>&cServer je momentálně v údržbě!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
                msgConfig.set("Messages.Normal.Maintenance.Kick", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4Byl jsi vyhozen ze serveru!<BREAK>&6Důvod: &cServer je nyní v údržbě!<BREAK>&6By: &c<SENDER><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
                msgConfig.set("Messages.Normal.Maintenance.Activated", "Server je nyní v údržbě!");
                msgConfig.set("Messages.Normal.Maintenance.Deactivated", "Server již není v údržbě!");
            } else {
                msgConfig.set("Messages.Normal.Maintenance.NoJoin", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4You got kicked!<BREAK>&cThe server is currently under maintenance!<BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
                msgConfig.set("Messages.Normal.Maintenance.Kick", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4You got kicked!<BREAK>&6Reason: &cThe server is now under maintenance!<BREAK>&6By: &c<SENDER><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
                msgConfig.set("Messages.Normal.Maintenance.Activated", "The server is now under maintenance!");
                msgConfig.set("Messages.Normal.Maintenance.Deactivated", "The server is no longer under maintenance!");

            }


            aliasConfig.set("Aliases.maintenance.aliases", "wartungen");


            commandsConfig.set("maintenance", true);


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 4.3 to 4.4!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("4.4")) {
            this.plugin.log("Updating config version 4.4 to 4.5...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);

            this.plugin.getConfig().set("afkkick", 5.0);
            this.plugin.getConfig().set("afktime", 2.5);

            this.plugin.getConfig().set("version", "4.5");

            //permissionConfig.set("Permissions.maintenance.toggle", "server.maintenance.toggle");

            msgDEConfig.set("Messages.Normal.AFK.Kick", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4Du wurdest gekickt!<BREAK>&6Grund: &cDu warst länger als <MINUTES> Minuten Inaktiv!<BREAK>&6Von: &c<SENDER><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");


            msgENConfig.set("Messages.Normal.AFK.Kick", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4You got kicked!<BREAK>&6Reason: You were afk for more than <MINUTES> minutes!<BREAK>&6By: &c<SENDER><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");


            msgCZConfig.set("Messages.Normal.AFK.Kick", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&cByl jsi vyhozen ze serveru!<BREAK>&6Důvod: &cSi AFK dýl jak <MINUTES> minut!<BREAK>&6Od: &c<SENDER><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");


            if (msgConfig.getString("language").equalsIgnoreCase("de"))
                msgConfig.set("Messages.Normal.AFK.Kick", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4Du wurdest gekickt!<BREAK>&6Grund: &cDu warst länger als <MINUTES> Minuten Inaktiv!<BREAK>&6Von: &c<SENDER><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
            else if (msgConfig.getString("language").equalsIgnoreCase("cz"))
                msgConfig.set("Messages.Normal.AFK.Kick", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&cByl jsi vyhozen ze serveru!<BREAK>&6Důvod: &cSi AFK dýl jak <MINUTES> minut!<BREAK>&6Od: &c<SENDER><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");
            else
                msgConfig.set("Messages.Normal.AFK.Kick", "&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]<BREAK>&4You got kicked!<BREAK>&6Reason: You were afk for more than <MINUTES> minutes!<BREAK>&6By: &c<SENDER><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3SERVER<BREAK>&8[]&7--------------------------------&8[]");


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 4.4 to 4.5!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("4.5")) {
            this.plugin.log("Updating config version 4.5 to 4.6...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);

            this.plugin.getConfig().set("afkoptions.blockspawners", false);
            this.plugin.getConfig().set("afkoptions.blockexpchange", true);
            this.plugin.getConfig().set("afkoptions.blockitempickupdrop", true);
            this.plugin.getConfig().set("afkoptions.blockfishing", false);
            this.plugin.getConfig().set("afkoptions.blockbreaking", false);

            this.plugin.getConfig().set("version", "4.6");


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 4.5 to 4.6!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("4.6")) {
            this.plugin.log("Updating config version 4.6 to 4.7...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);


            this.plugin.getConfig().set("version", "4.7");

            permissionConfig.set("Permissions.kit.bypassdelay", "server.kit.bypass.delay");


            msgDEConfig.set("Messages.Syntax.CreateKit", "Syntax: &8/<LABEL> <Name> <Delay>");
            msgDEConfig.set("Messages.Normal.Kit.OnDelay", "&cDu kannst dieses Kit erst wieder in &4<MINUTES> Minuten &cnutzen!");


            msgENConfig.set("Messages.Syntax.CreateKit", "Syntax: &8/<LABEL> <Name> <Delay>");
            msgENConfig.set("Messages.Normal.Kit.OnDelay", "&cYou can use this kit again in &4<MINUTES> minutes&c!");


            msgCZConfig.set("Messages.Syntax.CreateKit", "Použití: &8/<LABEL> <Jméno> <Delay>");
            msgCZConfig.set("Messages.Normal.Kit.OnDelay", "&cTento kit můžeš znovu použít až za &4<MINUTES> minuty&c!");


            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Syntax.CreateKit", "Syntax: &8/<LABEL> <Name> <Delay>");
                msgConfig.set("Messages.Normal.Kit.OnDelay", "&cDu kannst dieses Kit erst wieder in &4<MINUTES> Minuten &cnutzen!");
            } else if (msgConfig.getString("language").equalsIgnoreCase("cz")) {
                msgConfig.set("Messages.Syntax.CreateKit", "Použití: &8/<LABEL> <Jméno> <Delay>");
                msgConfig.set("Messages.Normal.Kit.OnDelay", "&cTento kit můžeš znovu použít až za &4<MINUTES> minuty&c!");
            } else {
                msgConfig.set("Messages.Syntax.CreateKit", "Syntax: &8/<LABEL> <Name> <Delay>");
                msgConfig.set("Messages.Normal.Kit.OnDelay", "&cYou can use this kit again in &4<MINUTES> minutes&c!");
            }


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 4.6 to 4.7!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("4.7")) {
            this.plugin.log("Updating config version 4.7 to 4.8...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);


            this.plugin.getConfig().set("version", "4.8");

            permissionConfig.set("Permissions.rules.required", false);
            permissionConfig.set("Permissions.rules.permission", "server.rules");


            msgDEConfig.set("Messages.Normal.Rules", "&8[]&7--------------------------------&8[]<BREAK>&3Regeln<BREAK>&8[]&7--------------------------------&8[]<BREAK>&6<RULES><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3Regeln<BREAK>&8[]&7--------------------------------&8[]");


            msgENConfig.set("Messages.Normal.Rules", "&8[]&7--------------------------------&8[]<BREAK>&3Rules<BREAK>&8[]&7--------------------------------&8[]<BREAK>&6<RULES><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3Rules<BREAK>&8[]&7--------------------------------&8[]");

            msgCZConfig.set("Messages.Normal.Rules", "&8[]&7--------------------------------&8[]<BREAK>&3Pravidla<BREAK>&8[]&7--------------------------------&8[]<BREAK>&6<RULES><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3Pravidla<BREAK>&8[]&7--------------------------------&8[]");

            if (msgConfig.getString("language").equalsIgnoreCase("de"))
                msgConfig.set("Messages.Normal.Rules", "&8[]&7--------------------------------&8[]<BREAK>&3Regeln<BREAK>&8[]&7--------------------------------&8[]<BREAK>&6<RULES><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3Regeln<BREAK>&8[]&7--------------------------------&8[]");
            else if (msgConfig.getString("language").equalsIgnoreCase("cz"))
                msgConfig.set("Messages.Normal.Rules", "&8[]&7--------------------------------&8[]<BREAK>&3Pravidla<BREAK>&8[]&7--------------------------------&8[]<BREAK>&6<RULES><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3Pravidla<BREAK>&8[]&7--------------------------------&8[]");
            else
                msgConfig.set("Messages.Normal.Rules", "&8[]&7--------------------------------&8[]<BREAK>&3Rules<BREAK>&8[]&7--------------------------------&8[]<BREAK>&6<RULES><BREAK>&8[]&7--------------------------------&8[]<BREAK>&3Rules<BREAK>&8[]&7--------------------------------&8[]");


            aliasConfig.set("Aliases.rules.aliases", "regeln");


            commandsConfig.set("rules", true);


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 4.7 to 4.8!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("4.8")) {
            this.plugin.log("Updating config version 4.8 to 4.9...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);


            this.plugin.getConfig().set("economy.createaccountonjoin", false);
            this.plugin.getConfig().set("version", "4.9");

            //permissionConfig.set("Permissions.rules.permission", "server.rules");
            permissionConfig.set("Permissions.rules.permission", "server.rules");


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 4.8 to 4.9!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("4.9")) {
            this.plugin.log("Updating config version 4.9 to 5.0...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);


            this.plugin.getConfig().set("fly.stopwhenhit", false);
            this.plugin.getConfig().set("fly.disablewhenhit", false);

            this.plugin.getConfig().set("version", "5.0");

            permissionConfig.set("Permissions.fly.bypassdamage", "server.fly.bypassdamage");
            permissionConfig.set("Permissions.rename", "server.rename");


            msgDEConfig.set("Messages.Syntax.Rename", "Syntax: &8/<LABEL> <Name>");
            msgDEConfig.set("Messages.Normal.Rename.Success", "Du hast das Item in deiner Hand zu &8\"<NAME>&8\" &7umbenannt!");
            msgDEConfig.set("Messages.Normal.Rename.NoItem", "&cDu musst ein Item in der Hand halten!");


            msgENConfig.set("Messages.Syntax.Rename", "Syntax: &8/<LABEL> <Name>");
            msgENConfig.set("Messages.Normal.Rename.Success", "You renamed the item in your hand to &8\"<NAME>&8\"&7!");
            msgENConfig.set("Messages.Normal.Rename.NoItem", "&cYou need to hold an item!");


            msgCZConfig.set("Messages.Syntax.Rename", "Použití: &8/<LABEL> <jméno>");
            msgCZConfig.set("Messages.Normal.Rename.Success", "Přejmenoval si item v ruce na &8\"<NAME>&8\"&7!");
            msgCZConfig.set("Messages.Normal.Rename.NoItem", "&cPotřebuješ mít v ruce item!");

            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Syntax.Rename", "Syntax: &8/<LABEL> <Name>");
                msgConfig.set("Messages.Normal.Rename.Success", "Du hast das Item in deiner Hand zu &8\"<NAME>&8\" &7umbenannt!");
            } else if (msgConfig.getString("language").equalsIgnoreCase("cz")) {
                msgConfig.set("Messages.Syntax.Rename", "Použití: &8/<LABEL> <jméno>");
                msgConfig.set("Messages.Normal.Rename.Success", "Přejmenoval si item v ruce na &8\"<NAME>&8\"&7!");
            } else {
                msgConfig.set("Messages.Syntax.Rename", "Syntax: &8/<LABEL> <Name>");
                msgConfig.set("Messages.Normal.Rename.Success", "You renamed the item in your hand to &8\"<NAME>&8\"&7!");
            }


            aliasConfig.set("Aliases.deletekit.aliases", null);
            aliasConfig.set("Aliases.delkit.aliases", "deletekit");
            aliasConfig.set("Aliases.rename.aliases", "No Aliases");


            boolean delKit = commandsConfig.getBoolean("deletekit");

            commandsConfig.set("deletekit", null);
            commandsConfig.set("delkit", delKit);
            commandsConfig.set("rename", true);


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 4.9 to 5.0!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("5.0")) {
            this.plugin.log("Updating config version 5.0 to 5.1...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);


            this.plugin.getConfig().set("version", "5.1");


            permissionConfig.set("Permissions.recipe.required", false);
            permissionConfig.set("Permissions.recipe.permission", "server.recipe");


            msgDEConfig.set("Messages.Normal.Recipe.NoItem", "&cDu musst ein Item in der Hand halten!");
            msgDEConfig.set("Messages.Normal.Recipe.NoRecipe", "&cEs gibt kein Crafting Rezept für &4<MATERIAL>&c!");
            msgDEConfig.set("Messages.Normal.Recipe.InvalidMaterial", "&cEs gibt kein Material namens &4<MATERIAL>&c!");


            msgENConfig.set("Messages.Normal.Recipe.NoItem", "&cYou need to hold an item!");
            msgENConfig.set("Messages.Normal.Recipe.NoRecipe", "&cThere is no crafting recipe for &4<MATERIAL>&c!");
            msgENConfig.set("Messages.Normal.Recipe.InvalidMaterial", "&cThere is no material named &4<MATERIAL>&c!");


            msgCZConfig.set("Messages.Normal.Recipe.NoItem", "&cNedržíš žádný item v ruce!");
            msgCZConfig.set("Messages.Normal.Recipe.NoRecipe", "&cNení zde žádný recept pro &4<MATERIAL>&c!");
            msgCZConfig.set("Messages.Normal.Recipe.InvalidMaterial", "&cMateriál &4<MATERIAL>&c neexistuje!");

            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Normal.Recipe.NoItem", "&cDu musst ein Item in der Hand halten!");
                msgConfig.set("Messages.Normal.Recipe.NoRecipe", "&cEs gibt kein Crafting Rezept für &4<MATERIAL>&c!");
                msgConfig.set("Messages.Normal.Recipe.InvalidMaterial", "&cEs gibt kein Material namens &4<MATERIAL>&c!");
            } else if (msgConfig.getString("language").equalsIgnoreCase("cz")) {
                msgConfig.set("Messages.Normal.Recipe.NoItem", "&cNedržíš žádný item v ruce!");
                msgConfig.set("Messages.Normal.Recipe.NoRecipe", "&cNení zde žádný recept pro &4<MATERIAL>&c!");
                msgConfig.set("Messages.Normal.Recipe.InvalidMaterial", "&cMateriál &4<MATERIAL>&c neexistuje!");
            } else {
                msgConfig.set("Messages.Normal.Recipe.NoItem", "&cYou need to hold an item!");
                msgConfig.set("Messages.Normal.Recipe.NoRecipe", "&cThere is no crafting recipe for &4<MATERIAL>&c!");
                msgConfig.set("Messages.Normal.Recipe.InvalidMaterial", "&cThere is no material named &4<MATERIAL>&c!");
            }


            aliasConfig.set("Aliases.recipe.aliases", "No Aliases");


            commandsConfig.set("recipe", true);


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 5.0 to 5.1!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("5.1")) {
            this.plugin.log("Updating config version 5.1 to 5.2...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);

            this.plugin.getConfig().set("economy.thousand", ".");
            this.plugin.getConfig().set("version", "5.2");


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 5.1 to 5.2!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("5.2")) {
            this.plugin.log("Updating config version 5.2 to 5.3...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);

            this.plugin.getConfig().set("version", "5.3");


            //permissionConfig.set("Permissions.recipe.required", false);
            permissionConfig.set("Permissions.warp.others", "server.warp.others");


            msgDEConfig.set("Messages.Syntax.Warp", "Syntax: &8/<LABEL> <Warp> <Spieler>");
            msgDEConfig.set("Messages.Normal.Warp.Others.Teleporting.Sender", "Du hast &8<TARGET> &7zu dem Warp &8<WARP> &7teleportiert!");
            msgDEConfig.set("Messages.Normal.Warp.Others.Teleporting.Target", "Du wurdest zu dem Warp &8<WARP> &7teleportiert!");


            msgENConfig.set("Messages.Syntax.Warp", "Syntax: &8/<LABEL> <Warp> <Player>");
            msgENConfig.set("Messages.Normal.Warp.Others.Teleporting.Sender", "You teleported &8<TARGET> &7to the warp &8<WARP>&7!");
            msgENConfig.set("Messages.Normal.Warp.Others.Teleporting.Target", "You were teleported to the warp &8<WARP>&7!");


            msgCZConfig.set("Messages.Syntax.Warp", "Použij: &8/<LABEL> <Warp> <Hráč>");
            msgCZConfig.set("Messages.Normal.Warp.Others.Teleporting.Sender", "Teleportoval jsi &8<TARGET> &7na warp &8<WARP>&7!");
            msgCZConfig.set("Messages.Normal.Warp.Others.Teleporting.Target", "Byl jsi teleportován na warp &8<WARP>&7!");


            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Syntax.Warp", "Syntax: &8/<LABEL> <Warp> <Spieler>");
                msgConfig.set("Messages.Normal.Warp.Others.Teleporting.Sender", "Du hast &8<TARGET> &7zu dem Warp &8<WARP> &7teleportiert!");
                msgConfig.set("Messages.Normal.Warp.Others.Teleporting.Target", "Du wurdest zu dem Warp &8<WARP> &7teleportiert!");
            } else if (msgConfig.getString("language").equalsIgnoreCase("cz")) {
                msgConfig.set("Messages.Syntax.Warp", "Použij: &8/<LABEL> <Warp> <Hráč>");
                msgConfig.set("Messages.Normal.Warp.Others.Teleporting.Sender", "Teleportoval jsi &8<TARGET> &7na warp &8<WARP>&7!");
                msgConfig.set("Messages.Normal.Warp.Others.Teleporting.Target", "Byl jsi teleportován na warp &8<WARP>&7!");
            } else {
                msgConfig.set("Messages.Syntax.Warp", "Syntax: &8/<LABEL> <Warp> <Player>");
                msgConfig.set("Messages.Normal.Warp.Others.Teleporting.Sender", "You teleported &8<TARGET> &7to the warp &8<WARP>&7!");
                msgConfig.set("Messages.Normal.Warp.Others.Teleporting.Target", "You were teleported to the warp &8<WARP>&7!");

            }


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 5.2 to 5.3!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("5.3")) {
            this.plugin.log("Updating config version 5.3 to 5.4...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);

            this.plugin.getConfig().set("version", "5.4");


            permissionConfig.set("Permissions.warps.required", false);
            permissionConfig.set("Permissions.warps.permission", "server.warps");


            msgDEConfig.set("Messages.Normal.Warps.Format.Separator", "&b, ");
            msgDEConfig.set("Messages.Normal.Warps.Format.Format", "<SEPERATOR>&7<Warp>");
            msgDEConfig.set("Messages.Normal.Warps.Format.Message", "Warps (&b<AMOUNT>&7): <WARPS>");


            msgENConfig.set("Messages.Normal.Warps.Format.Separator", "&b, ");
            msgENConfig.set("Messages.Normal.Warps.Format.Format", "<SEPERATOR>&7<Warp>");
            msgENConfig.set("Messages.Normal.Warps.Format.Message", "Warps (&b<AMOUNT>&7): <WARPS>");


            msgCZConfig.set("Messages.Normal.Warps.Format.Separator", "&b, ");
            msgCZConfig.set("Messages.Normal.Warps.Format.Format", "<SEPERATOR>&7<Warp>");
            msgCZConfig.set("Messages.Normal.Warps.Format.Message", "Warps (&b<AMOUNT>&7): <WARPS>");


            msgConfig.set("Messages.Normal.Warps.Format.Separator", "&b, ");
            msgConfig.set("Messages.Normal.Warps.Format.Format", "<SEPERATOR>&7<Warp>");
            msgConfig.set("Messages.Normal.Warps.Format.Message", "Warps (&b<AMOUNT>&7): <WARPS>");


            aliasConfig.set("Aliases.warps.aliases", "No Aliases");


            commandsConfig.set("warps", true);


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 5.3 to 5.4!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("5.4")) {
            this.plugin.log("Updating config version 5.4 to 5.5...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);

            this.plugin.getConfig().set("messagebyitembreak", false);
            this.plugin.getConfig().set("version", "5.5");


            msgDEConfig.set("Messages.Normal.TPPos.NotANumber", "&4<NUMBER> &cist keine Zahl!");
            msgDEConfig.set("Messages.Normal.TPPos.Success.Self", "Du hast dich zu den Koordinaten &8X: <X> Y: <Y> Z: <Z> &7teleportiert!");
            msgDEConfig.set("Messages.Normal.TPPos.Success.Others", "Du hast &8<TARGET> &7zu den Koordinaten &8X: <X> Y: <Y> Z: <Z> &7teleportiert!");
            msgDEConfig.set("Messages.Normal.ItemBreaking", "&cDein Item &4\"<ITEM>\" &cwird bald kaputt gehen! &8(&4<DURABILITY>&8/&2<MAXDURABILITY>&8)");

            msgENConfig.set("Messages.Normal.TPPos.NotANumber", "&4<NUMBER> &cis not a valid number!");
            msgENConfig.set("Messages.Normal.TPPos.Success.Self", "You teleported yourself to the coordinates &8X: <X> Y: <Y> Z: <Z>&7!");
            msgENConfig.set("Messages.Normal.TPPos.Success.Others", "You teleported &8<TARGET> &7to the coordinates &8X: <X> Y: <Y> Z: <Z>&7!");
            msgENConfig.set("Messages.Normal.ItemBreaking", "&cYour item &4\"<ITEM>\" &cis about to break! &8(&4<DURABILITY>&8/&2<MAXDURABILITY>&8)");

            msgCZConfig.set("Messages.Normal.TPPos.NotANumber", "&4<NUMBER> &cnení platné číslo!");
            msgCZConfig.set("Messages.Normal.TPPos.Success.Self", "Teleportoval jsi se na místo &aX: <X> Y: <Y> Z: <Z>&7!");
            msgCZConfig.set("Messages.Normal.TPPos.Success.Others", "Teleportoval jsi &8<TARGET> &7na místo &aX: <X> Y: <Y> Z: <Z>&7!");
            msgCZConfig.set("Messages.Normal.ItemBreaking", "&cTvůj item &4\"<ITEM>\" &cse brzo zničí &8(&4<DURABILITY>&8/&2<MAXDURABILITY>&8)");


            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Normal.TPPos.NotANumber", "&4<NUMBER> &cist keine Zahl!");
                msgConfig.set("Messages.Normal.TPPos.Success.Self", "Du hast dich zu den Koordinaten &8X: <X> Y: <Y> Z: <Z> &7teleportiert!");
                msgConfig.set("Messages.Normal.TPPos.Success.Others", "Du hast &8<TARGET> &7zu den Koordinaten &8X: <X> Y: <Y> Z: <Z> &7teleportiert!");
                msgConfig.set("Messages.Normal.ItemBreaking", "&cDein Item &4\"<ITEM>\" &cwird bald kaputt gehen! &8(&4<DURABILITY>&8/&2<MAXDURABILITY>&8)");
            } else if (msgConfig.getString("language").equalsIgnoreCase("cz")) {
                msgConfig.set("Messages.Normal.TPPos.NotANumber", "&4<NUMBER> &cnení platné číslo!");
                msgConfig.set("Messages.Normal.TPPos.Success.Self", "Teleportoval jsi se na místo &aX: <X> Y: <Y> Z: <Z>&7!");
                msgConfig.set("Messages.Normal.TPPos.Success.Others", "Teleportoval jsi &8<TARGET> &7na místo &aX: <X> Y: <Y> Z: <Z>&7!");
                msgConfig.set("Messages.Normal.ItemBreaking", "&cTvůj item &4\"<ITEM>\" &cse brzo zničí &8(&4<DURABILITY>&8/&2<MAXDURABILITY>&8)");
            } else {
                msgConfig.set("Messages.Normal.TPPos.NotANumber", "&4<NUMBER> &cis not a valid number!");
                msgConfig.set("Messages.Normal.TPPos.Success.Self", "You teleported yourself to the coordinates &8X: <X> Y: <Y> Z: <Z>&7!");
                msgConfig.set("Messages.Normal.TPPos.Success.Others", "You teleported &8<TARGET> &7to the coordinates &8X: <X> Y: <Y> Z: <Z>&7!");
                msgConfig.set("Messages.Normal.ItemBreaking", "&cYour item &4\"<ITEM>\" &cis about to break! &8(&4<DURABILITY>&8/&2<MAXDURABILITY>&8)");
            }


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 5.4 to 5.5!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("5.5")) {
            this.plugin.log("Updating config version 5.5 to 5.6...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);

            this.plugin.getConfig().set("betterafkscheduler", true);
            this.plugin.getConfig().set("deactivateentitycollision", true);
            this.plugin.getConfig().set("version", "5.6");


            msgDEConfig.set("Messages.Misc.JoinMessage.SendMessageToPlayer", false);
            msgDEConfig.set("Messages.Misc.JoinMessage.MessageToPlayer", "&cWillkommen auf dem Server!");

            msgENConfig.set("Messages.Misc.JoinMessage.SendMessageToPlayer", false);
            msgENConfig.set("Messages.Misc.JoinMessage.MessageToPlayer", "&cWelcome to the server!");

            msgCZConfig.set("Messages.Misc.JoinMessage.SendMessageToPlayer", false);
            msgCZConfig.set("Messages.Misc.JoinMessage.MessageToPlayer", "&cVítej na serveru!");


            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Misc.JoinMessage.SendMessageToPlayer", false);
                msgConfig.set("Messages.Misc.JoinMessage.MessageToPlayer", "&cWillkommen auf dem Server!");
            } else if (msgConfig.getString("language").equalsIgnoreCase("cz")) {
                msgConfig.set("Messages.Misc.JoinMessage.SendMessageToPlayer", false);
                msgConfig.set("Messages.Misc.JoinMessage.MessageToPlayer", "&cVítej na serveru!");
            } else {
                msgConfig.set("Messages.Misc.JoinMessage.SendMessageToPlayer", false);
                msgConfig.set("Messages.Misc.JoinMessage.MessageToPlayer", "&cWelcome to the server!");
            }


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 5.5 to 5.6!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("5.6")) {
            this.plugin.log("Updating config version 5.6 to 5.7...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);

            this.plugin.getConfig().set("version", "5.7");


            msgDEConfig.set("Messages.Syntax.CheckHealth", "Syntax: &8/<LABEL> <Spieler>");
            msgDEConfig.set("Messages.Normal.CheckHealth", "Leben: &2<HEALTH>&6/&2<MAXHEALTH> &7Essen: &2<FOOD>&6/&220");

            msgENConfig.set("Messages.Syntax.CheckHealth", "Syntax: &8/<LABEL> <Player>");
            msgENConfig.set("Messages.Normal.CheckHealth", "Health: &2<HEALTH>&6/&2<MAXHEALTH> &7Food: &2<FOOD>&6/&220");

            msgCZConfig.set("Messages.Syntax.CheckHealth", "Použij: &8/<LABEL> <Hráč>");
            msgCZConfig.set("Messages.Normal.CheckHealth", "Životy: &2<HEALTH>&6/&2<MAXHEALTH> &7Jídlo: &2<FOOD>&6/&220");


            if (msgConfig.getString("language").equalsIgnoreCase("de")) {
                msgConfig.set("Messages.Syntax.CheckHealth", "Syntax: &8/<LABEL> <Spieler>");
                msgConfig.set("Messages.Normal.CheckHealth", "Leben: &2<HEALTH>&6/&2<MAXHEALTH> &7Essen: &2<FOOD>&6/&220");
            } else if (msgConfig.getString("language").equalsIgnoreCase("cz")) {
                msgConfig.set("Messages.Syntax.CheckHealth", "Použij: &8/<LABEL> <Hráč>");
                msgConfig.set("Messages.Normal.CheckHealth", "Životy: &2<HEALTH>&6/&2<MAXHEALTH> &7Jídlo: &2<FOOD>&6/&220");
            } else {
                msgConfig.set("Messages.Syntax.CheckHealth", "Syntax: &8/<LABEL> <Player>");
                msgConfig.set("Messages.Normal.CheckHealth", "Health: &2<HEALTH>&6/&2<MAXHEALTH> &7Food: &2<FOOD>&6/&220");
            }


            aliasConfig.set("Aliases.checkhealth.aliases", "No Aliases");


            commandsConfig.set("checkhealth", true);


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 5.6 to 5.7!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("5.7")) {
            this.plugin.log("Updating config version 5.7 to 5.8...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);

            this.plugin.getConfig().set("version", "5.8");

            permissionConfig.set("Permissions.checkhealth", "server.checkhealth");


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 5.7 to 5.8!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("5.8")) {
            this.plugin.log("Updating config version 5.8 to 5.9...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);


            this.plugin.getConfig().set("worldChange.resetgamemode", false);
            this.plugin.getConfig().set("worldChange.resetfly", false);
            this.plugin.getConfig().set("worldChange.resetgod", false);

            this.plugin.getConfig().set("version", "5.9");


            permissionConfig.set("Permissions.lightning", "server.lightning");

            permissionConfig.set("Permissions.worldchange.bypassreset.gamemode", "server.bypassreset.gamemode");
            permissionConfig.set("Permissions.worldchange.bypassreset.god", "server.bypassreset.god");
            permissionConfig.set("Permissions.worldchange.bypassreset.fly", "server.bypassreset.fly");


            aliasConfig.set("Aliases.lightning.aliases", "No Aliases");


            commandsConfig.set("lightning", true);


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 5.8 to 5.9!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("5.9")) {
            this.plugin.log("Updating config version 5.9 to 6.0...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);


            this.plugin.getConfig().set("version", "6.0");

            permissionConfig.set("Permissions.rename", "server.rename");


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 5.9 to 6.0!");


            this.plugin.onEnable();
            return;
        } else if (version.equalsIgnoreCase("6.0")) {
            this.plugin.log("Updating config version 6.0 to 6.1...");

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File msgENFile = new File("plugins//ServerSystem", "messages_en.yml");
            File msgDEFile = new File("plugins//ServerSystem", "messages_de.yml");
            File msgCZFile = new File("plugins//ServerSystem", "messages_cz.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration msgENConfig = YamlConfiguration.loadConfiguration(msgENFile);
            FileConfiguration msgDEConfig = YamlConfiguration.loadConfiguration(msgDEFile);
            FileConfiguration msgCZConfig = YamlConfiguration.loadConfiguration(msgCZFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);


            this.plugin.getConfig().set("spawn.respawn", true);
            this.plugin.getConfig().set("spawn.forceRespawn", false);
            this.plugin.getConfig().set("version", "6.1");

            permissionConfig.set("Permissions.updatenotify", "server.updatenotify");


            try {
                permissionConfig.save(permissionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgConfig.save(msgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgDEConfig.save(msgDEFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgENConfig.save(msgENFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                msgCZConfig.save(msgCZFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                aliasConfig.save(aliasFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                commandsConfig.save(commandsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.plugin.saveConfig();


            try {
                msgConfig.load(msgFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            this.plugin.reloadConfig();


            this.plugin.log("Updated config version 6.0 to 6.1!");


            this.plugin.onEnable();
            return;
        }

        if (this.updateFromWeb(version)) return;

        this.plugin.warn("Unknown config version detected (" + version + ")!");
        this.plugin.warn("Deleting ServerSystem files...");
        try {
            FileUtils.deleteDirectory(serverSystemFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.plugin.warn("Creating new files...");
        this.plugin.onEnable();
    }

    public boolean configUpdateNeeded(String version) {
        return !version.equalsIgnoreCase(this.plugin.CONFIG_VERSION);
    }

    public void copyFolder(Path src, Path dest, String date) throws IOException {
        FileUtils.copyDirectory(src.toFile(), dest.toFile());
    }

    public boolean updateFromWeb(String version) {
        this.plugin.log("Checking for config updates...");
        Document doc = null;
        try {
            doc = Jsoup.connect("http://pluginsupport.zapto.org:80/PluginSupport/Config-Updates/ServerSystem").referrer("ServerSystem").timeout(30000).get();
        } catch (Exception e) {
            this.plugin.error("An error occurred while trying to connect to the config updater!");
            e.printStackTrace();
            return true;
        }

        if (doc == null) return false;

        try {
            doc = Jsoup.connect("http://pluginsupport.zapto.org:80/PluginSupport/Config-Updates/ServerSystem/" + version).referrer("ServerSystem").timeout(30000).get();
        } catch (IOException e) {
            return false;
        }

        if (doc != null) {
            File configUpdaterDir = new File("plugins/ServerSystem/ConfigUpdater");
            if (!configUpdaterDir.exists()) configUpdaterDir.mkdir();

            Element element;
            for (Element f : doc.getAllElements()) {
                String s = f.attr("href");
                if (s.equalsIgnoreCase("changes.yml") || s.equalsIgnoreCase("changes"))
                    try {
                        File downloadFile = new File(configUpdaterDir, s);
                        if (downloadFile.exists()) downloadFile.delete();
                        //Open a URL Stream
                        Connection.Response resultUpdateFile = Jsoup.connect("http://pluginsupport.zapto.org:80/PluginSupport/Config-Updates/ServerSystem/" + version + "/" + s).referrer("ServerSystem").timeout(30000).ignoreContentType(true).execute();
                        // output here
                        FileOutputStream out = (new FileOutputStream(new File(configUpdaterDir, s)));
                        out.write(resultUpdateFile.bodyAsBytes());
                        out.close();
                        break;
                    } catch (IOException e) {
                        this.plugin.error("Error while trying downloading the config update \"" + s + "\" !");
                        e.printStackTrace();
                    }
            }

            File permissionFile = new File("plugins//ServerSystem", "permissions.yml");
            File msgFile = new File("plugins//ServerSystem", "messages.yml");
            File aliasFile = new File("plugins//ServerSystem", "aliases.yml");
            File commandsFile = new File("plugins//ServerSystem", "commands.yml");

            FileConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(msgFile);
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFile);
            FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);

            File changesFile = new File(configUpdaterDir, "changes.yml");
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(changesFile);

            String newVersion = cfg.getString("Changes.Config.Change.version");

            this.plugin.log("Updating config version " + version + " to " + newVersion + "...");

            if (cfg.isConfigurationSection("Changes.Messages"))
                for (String language : cfg.getConfigurationSection("Changes.Messages").getKeys(false)) {
                    if (msgConfig.getString("language").equalsIgnoreCase(language)) {
                        boolean errored = false;
                        try {
                            for (String action : cfg.getConfigurationSection("Changes.Messages." + language).getKeys(false)) {
                                if (action.equalsIgnoreCase("remove")) {
                                    for (String key : cfg.getConfigurationSection("Changes.Messages." + language + "." + action).getKeys(false)) {
                                        key = key.replace(";", ".");
                                        msgConfig.set(key, null);
                                    }
                                    continue;
                                }

                                if (action.equalsIgnoreCase("change"))
                                    for (String key : cfg.getConfigurationSection("Changes.Messages." + language + "." + action).getKeys(false)) {
                                        String origin = key;
                                        key = key.replace(";", ".");
                                        msgConfig.set(key, cfg.get("Changes.Messages." + language + "." + action + "." + origin));
                                    }
                            }
                        } catch (Exception e) {
                            this.plugin.error("Error while writing to msg file!");
                            e.printStackTrace();
                            errored = true;
                        }

                        if (!errored)
                            try {
                                msgConfig.save(msgFile);
                            } catch (IOException e) {
                                this.plugin.error("Error while saving msg file!");
                                e.printStackTrace();
                            }
                    }

                    File toWriteFile = new File("plugins/ServerSystem", "messages_" + language + ".yml");
                    FileConfiguration toWriteConfig = YamlConfiguration.loadConfiguration(toWriteFile);

                    boolean errored = false;
                    try {
                        for (String action : cfg.getConfigurationSection("Changes.Messages." + language).getKeys(false)) {
                            if (action.equalsIgnoreCase("remove")) {
                                for (String key : cfg.getConfigurationSection("Changes.Messages." + language + "." + action).getKeys(false)) {
                                    key = key.replace(";", ".");
                                    toWriteConfig.set(key, null);
                                }
                                continue;
                            }

                            if (action.equalsIgnoreCase("change"))
                                for (String key : cfg.getConfigurationSection("Changes.Messages." + language + "." + action).getKeys(false)) {
                                    String origin = key;
                                    key = key.replace(";", ".");
                                    toWriteConfig.set(key, cfg.get("Changes.Messages." + language + "." + action + "." + origin));
                                }
                        }
                    } catch (Exception e) {
                        this.plugin.error("Error while writing to " + toWriteFile.getName() + " file!");
                        e.printStackTrace();
                        errored = true;
                    }

                    if (!errored)
                        try {
                            toWriteConfig.save(toWriteFile);
                        } catch (IOException e) {
                            this.plugin.error("Error while saving " + toWriteFile.getName() + " file!");
                            e.printStackTrace();
                        }
                }


            if (cfg.isConfigurationSection("Changes.Permissions")) {
                for (String action : cfg.getConfigurationSection("Changes.Permissions").getKeys(false))
                    try {
                        if (action.equalsIgnoreCase("remove")) {
                            for (String key : cfg.getConfigurationSection("Changes.Permissions." + action).getKeys(false)) {
                                key = key.replace(";", ".");
                                permissionConfig.set(key, null);
                            }
                            continue;
                        }

                        if (action.equalsIgnoreCase("change"))
                            for (String key : cfg.getConfigurationSection("Changes.Permissions." + action).getKeys(false)) {
                                String origin = key;
                                key = key.replace(";", ".");
                                permissionConfig.set(key, cfg.get("Changes.Permissions." + action + "." + origin));
                            }
                    } catch (Exception e) {
                        this.plugin.error("Error while writing to permissions file!");
                        e.printStackTrace();
                    }

                try {
                    permissionConfig.save(permissionFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            if (cfg.isConfigurationSection("Changes.Commands")) {
                for (String action : cfg.getConfigurationSection("Changes.Commands").getKeys(false))
                    try {
                        if (action.equalsIgnoreCase("remove")) {
                            for (String key : cfg.getConfigurationSection("Changes.Commands." + action).getKeys(false)) {
                                key = key.replace(";", ".");
                                commandsConfig.set(key, null);
                            }
                            continue;
                        }

                        if (action.equalsIgnoreCase("change"))
                            for (String key : cfg.getConfigurationSection("Changes.Commands." + action).getKeys(false)) {
                                String origin = key;
                                key = key.replace(";", ".");
                                commandsConfig.set(key, cfg.get("Changes.Commands." + action + "." + origin));
                            }
                    } catch (Exception e) {
                        this.plugin.error("Error while writing to Commands file!");
                        e.printStackTrace();
                    }

                try {
                    commandsConfig.save(commandsFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (cfg.isConfigurationSection("Changes.Aliases")) {
                for (String action : cfg.getConfigurationSection("Changes.Aliases").getKeys(false))
                    try {
                        if (action.equalsIgnoreCase("remove")) {
                            for (String key : cfg.getConfigurationSection("Changes.Aliases." + action).getKeys(false)) {
                                key = key.replace(";", ".");
                                aliasConfig.set(key, null);
                            }
                            continue;
                        }

                        if (action.equalsIgnoreCase("change"))
                            for (String key : cfg.getConfigurationSection("Changes.Aliases." + action).getKeys(false)) {
                                String origin = key;
                                key = key.replace(";", ".");
                                aliasConfig.set(key, cfg.get("Changes.Aliases." + action + "." + origin));
                            }
                    } catch (Exception e) {
                        this.plugin.error("Error while writing to Aliases file!");
                        e.printStackTrace();
                    }

                try {
                    aliasConfig.save(aliasFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (cfg.isConfigurationSection("Changes.Config")) {
                for (String action : cfg.getConfigurationSection("Changes.Config").getKeys(false))
                    try {
                        if (action.equalsIgnoreCase("remove")) {
                            for (String key : cfg.getConfigurationSection("Changes.Config." + action).getKeys(false)) {
                                key = key.replace(";", ".");
                                this.plugin.getConfig().set(key, null);
                            }
                            continue;
                        }

                        if (action.equalsIgnoreCase("change"))
                            for (String key : cfg.getConfigurationSection("Changes.Config." + action).getKeys(false)) {
                                String origin = key;
                                key = key.replace(";", ".");
                                this.plugin.getConfig().set(key, cfg.get("Changes.Config." + action + "." + origin));
                            }
                    } catch (Exception e) {
                        this.plugin.error("Error while writing to Config file!");
                        e.printStackTrace();
                    }

                try {
                    this.plugin.saveConfig();
                    this.plugin.reloadConfig();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.plugin.log("Updated config version " + version + " to " + newVersion + "!");
            this.plugin.onEnable();
            return true;
        }
        return false;
    }
}
