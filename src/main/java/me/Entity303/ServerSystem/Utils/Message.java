package me.Entity303.ServerSystem.Utils;

import me.Entity303.ServerSystem.Main.ss;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class Message {
    private final ss plugin;
    private final File messagesFile;
    private final FileConfiguration cfg;

    public Message(ss plugin) {
        this.plugin = plugin;
        this.messagesFile = new File("plugins//ServerSystem", "messages.yml");
        this.cfg = YamlConfiguration.loadConfiguration(this.messagesFile);
    }

    public String getMessage(String label, String command, CommandSender sender, CommandSender target, String action) {
        if (sender == null) throw new IllegalArgumentException("Sender cannot be null!");
        if (target == null) target = sender;

        String senderName = sender.getName();
        String senderDisplayName;

        if (sender instanceof Player) senderDisplayName = ((Player) sender).getDisplayName();
        else senderDisplayName = senderName;

        String targetName = target.getName();
        String targetDisplayName;

        if (target instanceof Player) targetDisplayName = ((Player) target).getDisplayName();
        else targetDisplayName = targetName;

        if (senderName.equalsIgnoreCase("console") || senderName.equalsIgnoreCase("konsole")) {
            senderName = this.cfg.getString("Messages.Misc.BanSystem.ConsoleName");
            senderDisplayName = senderName;
        }

        if (targetName.equalsIgnoreCase("console") || targetName.equalsIgnoreCase("konsole")) {
            targetName = this.cfg.getString("Messages.Misc.BanSystem.ConsoleName");
            targetDisplayName = targetName;
        }

        try {
            String s = ChatColor.translateAlternateColorCodes('&', this.cfg.getString("Messages.Normal." + action).replace("<LABEL>", label).replace("<COMMAND>", command).replace("<SENDER>", senderName).replace("<TARGET>", targetName).replace("<SENDERDISPLAY>", senderDisplayName).replace("<TARGETDISPLAY>", targetDisplayName).replace("<BREAK>", "\n"));
            return s;
        } catch (NullPointerException ignored) {
            return "Error! Path: Normal." + action;
        }
    }

    public String getMessageWithStringTarget(String label, String command, CommandSender sender, String target, String action) {
        if (sender == null) throw new IllegalArgumentException("Sender cannot be null!");

        String senderName = sender.getName();
        String senderDisplayName;

        if (sender instanceof Player) senderDisplayName = ((Player) sender).getDisplayName();
        else senderDisplayName = senderName;

        String targetName;
        String targetDisplayName;

        if (target == null) {
            targetName = senderName;
            targetDisplayName = senderDisplayName;
        } else {
            targetName = target;
            targetDisplayName = target;
        }

        if (senderName.equalsIgnoreCase("console") || senderName.equalsIgnoreCase("konsole")) {
            senderName = this.cfg.getString("Messages.Misc.BanSystem.ConsoleName");
            senderDisplayName = senderName;
        }

        if (targetName.equalsIgnoreCase("console") || targetName.equalsIgnoreCase("konsole")) {
            targetName = this.cfg.getString("Messages.Misc.BanSystem.ConsoleName");
            targetDisplayName = targetName;
        }
        try {
            String s = ChatColor.translateAlternateColorCodes('&', this.cfg.getString("Messages.Normal." + action)).replace("<BREAK>", "\n").replace("<LABEL>", label).replace("<COMMAND>", command).replace("<SENDER>", senderName).replace("<TARGET>", targetName).replace("<SENDERDISPLAY>", senderDisplayName).replace("<TARGETDISPLAY>", targetDisplayName);
            return s;
        } catch (NullPointerException ignored) {
            return "Error! Path: Normal." + action;
        }
    }

    public String getMessage(String label, String command, String sender, CommandSender target, String action) {
        if (sender == null) throw new IllegalArgumentException("Sender cannot be null!");

        String senderName = sender;
        String senderDisplayName = senderName;

        String targetName;
        String targetDisplayName;

        if (target != null) {
            targetName = target.getName();
            if (target instanceof Player) targetDisplayName = ((Player) target).getDisplayName();
            else targetDisplayName = targetName;
        } else {
            targetName = senderName;
            targetDisplayName = senderDisplayName;
        }

        if (senderName.equalsIgnoreCase("console") || senderName.equalsIgnoreCase("konsole")) {
            senderName = this.cfg.getString("Messages.Misc.BanSystem.ConsoleName");
            senderDisplayName = senderName;
        }

        if (targetName.equalsIgnoreCase("console") || targetName.equalsIgnoreCase("konsole")) {
            targetName = this.cfg.getString("Messages.Misc.BanSystem.ConsoleName");
            targetDisplayName = targetName;
        }

        try {
            String s = ChatColor.translateAlternateColorCodes('&', this.cfg.getString("Messages.Normal." + action).replace("<LABEL>", label).replace("<COMMAND>", command).replace("<SENDER>", senderName).replace("<TARGET>", targetName).replace("<SENDERDISPLAY>", senderDisplayName).replace("<TARGETDISPLAY>", targetDisplayName).replace("<BREAK>", "\n"));
            return s;
        } catch (NullPointerException ignored) {
            return "Error! Path: Normal." + action;
        }
    }

    public String getMiscMessage(String label, String command, CommandSender sender, CommandSender target, String action) {
        if (sender == null) throw new IllegalArgumentException("Sender cannot be null!");
        if (target == null) target = sender;

        String senderName = sender.getName();
        String senderDisplayName;

        if (sender instanceof Player) senderDisplayName = ((Player) sender).getDisplayName();
        else senderDisplayName = senderName;

        String targetName = target.getName();
        String targetDisplayName;

        if (target instanceof Player) targetDisplayName = ((Player) target).getDisplayName();
        else targetDisplayName = targetName;

        if (senderName.equalsIgnoreCase("console") || senderName.equalsIgnoreCase("konsole")) {
            senderName = this.cfg.getString("Messages.Misc.BanSystem.ConsoleName");
            senderDisplayName = senderName;
        }

        if (targetName.equalsIgnoreCase("console") || targetName.equalsIgnoreCase("konsole")) {
            targetName = this.cfg.getString("Messages.Misc.BanSystem.ConsoleName");
            targetDisplayName = targetName;
        }
        try {
            String s = ChatColor.translateAlternateColorCodes('&', this.cfg.getString("Messages.Misc." + action).replace("<LABEL>", label).replace("<COMMAND>", command).replace("<SENDER>", senderName).replace("<TARGET>", targetName).replace("<SENDERDISPLAY>", senderDisplayName).replace("<TARGETDISPLAY>", targetDisplayName).replace("<BREAK>", "\n"));
            return s;
        } catch (NullPointerException ignored) {
            return "Error! Path: Misc." + action;
        }
    }

    public String getMessage(String label, String command, CommandSender sender, CommandSender target, String action, boolean colorless) {
        if (sender == null) throw new IllegalArgumentException("Sender cannot be null!");
        if (target == null) target = sender;

        String senderName = sender.getName();
        String senderDisplayName;

        if (sender instanceof Player) senderDisplayName = ((Player) sender).getDisplayName();
        else senderDisplayName = senderName;

        String targetName = target.getName();
        String targetDisplayName;

        if (target instanceof Player) targetDisplayName = ((Player) target).getDisplayName();
        else targetDisplayName = targetName;

        if (senderName.equalsIgnoreCase("console") || senderName.equalsIgnoreCase("konsole")) {
            senderName = this.cfg.getString("Messages.Misc.BanSystem.ConsoleName");
            senderDisplayName = senderName;
        }

        if (targetName.equalsIgnoreCase("console") || targetName.equalsIgnoreCase("konsole")) {
            targetName = this.cfg.getString("Messages.Misc.BanSystem.ConsoleName");
            targetDisplayName = targetName;
        }
        if (colorless) try {
            String s = this.cfg.getString("Messages.Normal." + action).replace("<LABEL>", label).replace("<COMMAND>", command).replace("<SENDER>", senderName).replace("<TARGET>", targetName).replace("<SENDERDISPLAY>", senderDisplayName).replace("<TARGETDISPLAY>", targetDisplayName).replace("<BREAK>", "\n");
            return s;
        } catch (NullPointerException ignored) {
            return "Error! Path: " + action;
        }
        else try {
            String s = ChatColor.translateAlternateColorCodes('&', this.cfg.getString("Messages.Normal." + action).replace("<LABEL>", label).replace("<COMMAND>", command).replace("<SENDER>", senderName).replace("<TARGET>", targetName).replace("<BREAK>", "\n"));
            return s;
        } catch (NullPointerException ignored) {
            return "Error! Path: Normal." + action;
        }
    }

    public String getSyntax(String label, String command, CommandSender sender, CommandSender target, String action) {
        if (sender == null) throw new IllegalArgumentException("Sender cannot be null!");
        if (target == null) target = sender;

        String senderName = sender.getName();
        String senderDisplayName;

        if (sender instanceof Player) senderDisplayName = ((Player) sender).getDisplayName();
        else senderDisplayName = senderName;

        String targetName = target.getName();
        String targetDisplayName;

        if (target instanceof Player) targetDisplayName = ((Player) target).getDisplayName();
        else targetDisplayName = targetName;

        if (senderName.equalsIgnoreCase("console") || senderName.equalsIgnoreCase("konsole")) {
            senderName = this.cfg.getString("Messages.Misc.BanSystem.ConsoleName");
            senderDisplayName = senderName;
        }

        if (targetName.equalsIgnoreCase("console") || targetName.equalsIgnoreCase("konsole")) {
            targetName = this.cfg.getString("Messages.Misc.BanSystem.ConsoleName");
            targetDisplayName = targetName;
        }
        try {
            return ChatColor.translateAlternateColorCodes('&', this.cfg.getString("Messages.Syntax." + action)).replace("<BREAK>", "\n").replace("<LABEL>", label).replace("<COMMAND>", command).replace("<SENDER>", senderName).replace("<TARGET>", targetName).replace("<SENDERDISPLAY>", senderDisplayName).replace("<TARGETDISPLAY>", targetDisplayName);
        } catch (NullPointerException ignored) {
            return "Error! Path: Syntax." + action;
        }
    }

    public String getSyntaxWithStringTarget(String label, String command, CommandSender sender, String target, String action) {
        if (sender == null) throw new IllegalArgumentException("Sender cannot be null!");

        String senderName = sender.getName();
        String senderDisplayName;

        if (sender instanceof Player) senderDisplayName = ((Player) sender).getDisplayName();
        else senderDisplayName = senderName;

        String targetName;
        String targetDisplayName;

        if (target == null) {
            targetName = senderName;
            targetDisplayName = senderDisplayName;
        } else {
            targetName = target;
            targetDisplayName = target;
        }

        if (senderName.equalsIgnoreCase("console") || senderName.equalsIgnoreCase("konsole")) {
            senderName = this.cfg.getString("Messages.Misc.BanSystem.ConsoleName");
            senderDisplayName = senderName;
        }

        if (targetName.equalsIgnoreCase("console") || targetName.equalsIgnoreCase("konsole")) {
            targetName = this.cfg.getString("Messages.Misc.BanSystem.ConsoleName");
            targetDisplayName = targetName;
        }
        try {
            return ChatColor.translateAlternateColorCodes('&', this.cfg.getString("Messages.Syntax." + action)).replace("<BREAK>", "\n").replace("<LABEL>", label).replace("<COMMAND>", command).replace("<SENDER>", senderName).replace("<TARGET>", targetName).replace("<SENDERDISPLAY>", senderDisplayName).replace("<TARGETDISPLAY>", targetDisplayName);
        } catch (NullPointerException ignored) {
            return "Error! Path: Syntax." + action;
        }
    }

    public FileConfiguration getCfg() {
        return this.cfg;
    }

    public String getPrefix() {
        try {
            String s = ChatColor.translateAlternateColorCodes('&', this.cfg.getString("Messages.Misc.Prefix").replace("<BREAK>", "\n"));
            return s;
        } catch (NullPointerException ignored) {
            return "Error! Path: Misc.Prefix";
        }
    }

    public String getNoPermission(String permission) {
        if (permission == null) permission = "No permission found";
        try {
            String s = ChatColor.translateAlternateColorCodes('&', this.cfg.getString("Messages.Misc.NoPermissions").replace("<PERMISSION>", permission).replace("<BREAK>", "\n"));
            return s;
        } catch (NullPointerException ignored) {
            return "Error! Path: Misc.NoPermissions";
        }
    }

    public String getOnlyPlayer() {
        try {
            String s = ChatColor.translateAlternateColorCodes('&', this.cfg.getString("Messages.Misc.OnlyPlayer")).replace("<BREAK>", "\n");
            return s;
        } catch (NullPointerException ignored) {
            return "Error! Path: Misc.OnlyPlayer";
        }
    }

    public String getNoTarget(String targetName) {
        try {
            String s = ChatColor.translateAlternateColorCodes('&', this.cfg.getString("Messages.Misc.NoTarget").replace("<TARGET>", targetName != null ? targetName : "null").replace("<TARGETDISPLAY>", targetName != null ? targetName : "null")).replace("<BREAK>", "\n");
            return s;
        } catch (NullPointerException ignored) {
            return "Error! Path: Misc.NoTarget";
        }
    }

    public void reloadMessages() throws IOException, InvalidConfigurationException {
        this.cfg.load(this.messagesFile);
    }

    public boolean getBoolean(String fullAction) {
        return this.cfg.getBoolean(fullAction);
    }
}
