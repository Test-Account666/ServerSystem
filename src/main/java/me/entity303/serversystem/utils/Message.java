package me.entity303.serversystem.utils;

import me.entity303.serversystem.config.DefaultConfigReader;
import me.entity303.serversystem.config.IConfigReader;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class Message {
    private final File _messagesFile;
    private final IConfigReader _configuration;
    private final boolean _placeholderAPI;

    public Message(ServerSystem plugin) {
        this._messagesFile = new File("plugins//ServerSystem", "messages.yml");
        this._configuration = DefaultConfigReader.LoadConfiguration(this._messagesFile, plugin);
        this._placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    public String GetMessageWithStringTarget(String commandLabel, Command command, CommandSender sender, String target, String action) {
        return this.GetMessageWithStringTarget(commandLabel, command.getName(), sender, target, action);
    }

    public String GetMessageWithStringTarget(String commandLabel, String command, CommandSender sender, String target, String action) {
        if (sender == null) throw new IllegalArgumentException("Sender cannot be null!");

        var senderName = sender.getName();
        String senderDisplayName;

        if (sender instanceof Player) {
            senderDisplayName = ((Player) sender).getDisplayName();
        } else {
            senderDisplayName = senderName;
        }

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
            senderName = this._configuration.GetString("Messages.Misc.BanSystem.ConsoleName");
            senderDisplayName = senderName;
        }

        if (targetName.equalsIgnoreCase("console") || targetName.equalsIgnoreCase("konsole")) {
            targetName = this._configuration.GetString("Messages.Misc.BanSystem.ConsoleName");
            targetDisplayName = targetName;
        }
        try {
            var message = ChatColor.TranslateAlternateColorCodes('&', this._configuration.GetString("Messages.Normal." + action))
                                   .replace("<BREAK>", "\n")
                                   .replace("https://discord.gg/TbnyUrJ", "https://discord.gg/dBhfCzdZxq")
                                   .replace("<LABEL>", commandLabel)
                                   .replace("<COMMAND>", command)
                                   .replace("<SENDER>", senderName)
                                   .replace("<TARGET>", targetName)
                                   .replace("<SENDERDISPLAY>", senderDisplayName)
                                   .replace("<TARGETDISPLAY>", targetDisplayName);

            return sender instanceof Player && this._placeholderAPI? me.clip.placeholderapi.PlaceholderAPI.setPlaceholders((Player) sender, message) : message;
        } catch (NullPointerException ignored) {
            return "Error! Path: Normal." + action;
        }
    }

    public String GetMessage(String commandLabel, Command command, CommandSender sender, CommandSender target, String action) {
        return this.GetMessage(commandLabel, command.getName(), sender, target, action);
    }

    public String GetMessage(String commandLabel, String command, CommandSender sender, CommandSender target, String action) {
        if (sender == null) throw new IllegalArgumentException("Sender cannot be null!");
        if (target == null) target = sender;

        var senderName = sender.getName();
        String senderDisplayName;

        if (sender instanceof Player) {
            senderDisplayName = ((Player) sender).getDisplayName();
        } else {
            senderDisplayName = senderName;
        }

        var targetName = target.getName();
        String targetDisplayName;

        if (target instanceof Player) {
            targetDisplayName = ((Player) target).getDisplayName();
        } else {
            targetDisplayName = targetName;
        }

        if (senderName.equalsIgnoreCase("console") || senderName.equalsIgnoreCase("konsole")) {
            senderName = this._configuration.GetString("Messages.Misc.BanSystem.ConsoleName");
            senderDisplayName = senderName;
        }

        if (targetName.equalsIgnoreCase("console") || targetName.equalsIgnoreCase("konsole")) {
            targetName = this._configuration.GetString("Messages.Misc.BanSystem.ConsoleName");
            targetDisplayName = targetName;
        }

        try {
            var message = ChatColor.TranslateAlternateColorCodes('&', this._configuration.GetString("Messages.Normal." + action)
                                                                                         .replace("<LABEL>", commandLabel)
                                                                                         .replace("<COMMAND>", command)
                                                                                         .replace("<SENDER>", senderName)
                                                                                         .replace("<TARGET>", targetName)
                                                                                         .replace("<SENDERDISPLAY>", senderDisplayName)
                                                                                         .replace("<TARGETDISPLAY>", targetDisplayName)
                                                                                         .replace("<BREAK>", "\n")
                                                                                         .replace("https://discord.gg/TbnyUrJ", "https://discord.gg/dBhfCzdZxq"));
            return sender instanceof Player && this._placeholderAPI? me.clip.placeholderapi.PlaceholderAPI.setPlaceholders((Player) sender, message) : message;
        } catch (NullPointerException ignored) {
            return "Error! Path: Normal." + action;
        }
    }

    public String GetMessage(String commandLabel, String command, String sender, CommandSender target, String action) {
        if (sender == null) throw new IllegalArgumentException("Sender cannot be null!");

        var senderName = sender;
        var senderDisplayName = senderName;

        String targetName;
        String targetDisplayName;

        if (target != null) {
            targetName = target.getName();
            if (target instanceof Player) {
                targetDisplayName = ((Player) target).getDisplayName();
            } else {
                targetDisplayName = targetName;
            }
        } else {
            targetName = senderName;
            targetDisplayName = senderDisplayName;
        }

        if (senderName.equalsIgnoreCase("console") || senderName.equalsIgnoreCase("konsole")) {
            senderName = this._configuration.GetString("Messages.Misc.BanSystem.ConsoleName");
            senderDisplayName = senderName;
        }

        if (targetName.equalsIgnoreCase("console") || targetName.equalsIgnoreCase("konsole")) {
            targetName = this._configuration.GetString("Messages.Misc.BanSystem.ConsoleName");
            targetDisplayName = targetName;
        }

        try {
            return ChatColor.TranslateAlternateColorCodes('&', this._configuration.GetString("Messages.Normal." + action)
                                                                                  .replace("<LABEL>", commandLabel)
                                                                                  .replace("<COMMAND>", command)
                                                                                  .replace("<SENDER>", senderName)
                                                                                  .replace("<TARGET>", targetName)
                                                                                  .replace("<SENDERDISPLAY>", senderDisplayName)
                                                                                  .replace("<TARGETDISPLAY>", targetDisplayName)
                                                                                  .replace("<BREAK>", "\n")
                                                                                  .replace("https://discord.gg/TbnyUrJ", "https://discord.gg/dBhfCzdZxq"));
        } catch (NullPointerException ignored) {
            return "Error! Path: Normal." + action;
        }
    }

    public String GetMiscMessage(String commandLabel, String command, CommandSender sender, CommandSender target, String action) {
        if (sender == null) throw new IllegalArgumentException("Sender cannot be null!");
        if (target == null) target = sender;

        var senderName = sender.getName();
        String senderDisplayName;

        if (sender instanceof Player) {
            senderDisplayName = ((Player) sender).getDisplayName();
        } else {
            senderDisplayName = senderName;
        }

        var targetName = target.getName();
        String targetDisplayName;

        if (target instanceof Player) {
            targetDisplayName = ((Player) target).getDisplayName();
        } else {
            targetDisplayName = targetName;
        }

        if (senderName.equalsIgnoreCase("console") || senderName.equalsIgnoreCase("konsole")) {
            senderName = this._configuration.GetString("Messages.Misc.BanSystem.ConsoleName");
            senderDisplayName = senderName;
        }

        if (targetName.equalsIgnoreCase("console") || targetName.equalsIgnoreCase("konsole")) {
            targetName = this._configuration.GetString("Messages.Misc.BanSystem.ConsoleName");
            targetDisplayName = targetName;
        }
        try {
            var message = ChatColor.TranslateAlternateColorCodes('&', this._configuration.GetString("Messages.Misc." + action)
                                                                                         .replace("<LABEL>", commandLabel)
                                                                                         .replace("<COMMAND>", command)
                                                                                         .replace("<SENDER>", senderName)
                                                                                         .replace("<TARGET>", targetName)
                                                                                         .replace("<SENDERDISPLAY>", senderDisplayName)
                                                                                         .replace("<TARGETDISPLAY>", targetDisplayName)
                                                                                         .replace("<BREAK>", "\n")
                                                                                         .replace("https://discord.gg/TbnyUrJ", "https://discord.gg/dBhfCzdZxq"));
            return sender instanceof Player && this._placeholderAPI? me.clip.placeholderapi.PlaceholderAPI.setPlaceholders((Player) sender, message) : message;
        } catch (NullPointerException ignored) {
            return "Error! Path: Misc." + action;
        }
    }

    public String GetMessage(String commandLabel, String command, CommandSender sender, CommandSender target, String action, boolean colorless) {
        if (sender == null) throw new IllegalArgumentException("Sender cannot be null!");
        if (target == null) target = sender;

        var senderName = sender.getName();
        String senderDisplayName;

        if (sender instanceof Player) {
            senderDisplayName = ((Player) sender).getDisplayName();
        } else {
            senderDisplayName = senderName;
        }

        var targetName = target.getName();
        String targetDisplayName;

        if (target instanceof Player) {
            targetDisplayName = ((Player) target).getDisplayName();
        } else {
            targetDisplayName = targetName;
        }

        if (senderName.equalsIgnoreCase("console") || senderName.equalsIgnoreCase("konsole")) {
            senderName = this._configuration.GetString("Messages.Misc.BanSystem.ConsoleName");
            senderDisplayName = senderName;
        }

        if (targetName.equalsIgnoreCase("console") || targetName.equalsIgnoreCase("konsole")) {
            targetName = this._configuration.GetString("Messages.Misc.BanSystem.ConsoleName");
            targetDisplayName = targetName;
        }
        if (colorless) {
            try {
                return this._configuration.GetString("Messages.Normal." + action)
                                          .replace("<LABEL>", commandLabel)
                                          .replace("<COMMAND>", command)
                                          .replace("<SENDER>", senderName)
                                          .replace("<TARGET>", targetName)
                                          .replace("<SENDERDISPLAY>", senderDisplayName)
                                          .replace("<TARGETDISPLAY>", targetDisplayName)
                                          .replace("<BREAK>", "\n")
                                          .replace("https://discord.gg/TbnyUrJ", "https://discord.gg/dBhfCzdZxq");
            } catch (NullPointerException ignored) {
                return "Error! Path: " + action;
            }
        } else {
            try {
                var message = ChatColor.TranslateAlternateColorCodes('&', this._configuration.GetString("Messages.Normal." + action)
                                                                                             .replace("<LABEL>", commandLabel)
                                                                                             .replace("<COMMAND>", command)
                                                                                             .replace("<SENDER>", senderName)
                                                                                             .replace("<TARGET>", targetName)
                                                                                             .replace("<BREAK>", "\n")
                                                                                             .replace("https://discord.gg/TbnyUrJ", "https://discord.gg/dBhfCzdZxq"));
                return sender instanceof Player && this._placeholderAPI? me.clip.placeholderapi.PlaceholderAPI.setPlaceholders((Player) sender, message) : message;
            } catch (NullPointerException ignored) {
                return "Error! Path: Normal." + action;
            }
        }
    }

    public String GetSyntax(String commandLabel, Command command, CommandSender sender, CommandSender target, String action) {
        return this.GetSyntax(commandLabel, command.getName(), sender, target, action);
    }

    public String GetSyntax(String commandLabel, String command, CommandSender sender, CommandSender target, String action) {
        if (sender == null) throw new IllegalArgumentException("Sender cannot be null!");
        if (target == null) target = sender;

        var senderName = sender.getName();
        String senderDisplayName;

        if (sender instanceof Player) {
            senderDisplayName = ((Player) sender).getDisplayName();
        } else {
            senderDisplayName = senderName;
        }

        var targetName = target.getName();
        String targetDisplayName;

        if (target instanceof Player) {
            targetDisplayName = ((Player) target).getDisplayName();
        } else {
            targetDisplayName = targetName;
        }

        if (senderName.equalsIgnoreCase("console") || senderName.equalsIgnoreCase("konsole")) {
            senderName = this._configuration.GetString("Messages.Misc.BanSystem.ConsoleName");
            senderDisplayName = senderName;
        }

        if (targetName.equalsIgnoreCase("console") || targetName.equalsIgnoreCase("konsole")) {
            targetName = this._configuration.GetString("Messages.Misc.BanSystem.ConsoleName");
            targetDisplayName = targetName;
        }
        try {
            var message = ChatColor.TranslateAlternateColorCodes('&', this._configuration.GetString("Messages.Syntax." + action))
                                   .replace("<BREAK>", "\n")
                                   .replace("https://discord.gg/TbnyUrJ", "https://discord.gg/dBhfCzdZxq")
                                   .replace("<LABEL>", commandLabel)
                                   .replace("<COMMAND>", command)
                                   .replace("<SENDER>", senderName)
                                   .replace("<TARGET>", targetName)
                                   .replace("<SENDERDISPLAY>", senderDisplayName)
                                   .replace("<TARGETDISPLAY>", targetDisplayName);
            return sender instanceof Player && this._placeholderAPI? me.clip.placeholderapi.PlaceholderAPI.setPlaceholders((Player) sender, message) : message;
        } catch (NullPointerException ignored) {
            return "Error! Path: Syntax." + action;
        }
    }

    public String GetSyntaxWithStringTarget(String commandLabel, Command command, CommandSender sender, String target, String action) {
        return this.GetSyntaxWithStringTarget(commandLabel, command.getName(), sender, target, action);
    }

    public String GetSyntaxWithStringTarget(String commandLabel, String command, CommandSender sender, String target, String action) {
        if (sender == null) throw new IllegalArgumentException("Sender cannot be null!");

        var senderName = sender.getName();
        String senderDisplayName;

        if (sender instanceof Player) {
            senderDisplayName = ((Player) sender).getDisplayName();
        } else {
            senderDisplayName = senderName;
        }

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
            senderName = this._configuration.GetString("Messages.Misc.BanSystem.ConsoleName");
            senderDisplayName = senderName;
        }

        if (targetName.equalsIgnoreCase("console") || targetName.equalsIgnoreCase("konsole")) {
            targetName = this._configuration.GetString("Messages.Misc.BanSystem.ConsoleName");
            targetDisplayName = targetName;
        }
        try {
            var message = ChatColor.TranslateAlternateColorCodes('&', this._configuration.GetString("Messages.Syntax." + action))
                                   .replace("<BREAK>", "\n")
                                   .replace("https://discord.gg/TbnyUrJ", "https://discord.gg/dBhfCzdZxq")
                                   .replace("<LABEL>", commandLabel)
                                   .replace("<COMMAND>", command)
                                   .replace("<SENDER>", senderName)
                                   .replace("<TARGET>", targetName)
                                   .replace("<SENDERDISPLAY>", senderDisplayName)
                                   .replace("<TARGETDISPLAY>", targetDisplayName);
            return sender instanceof Player && this._placeholderAPI? me.clip.placeholderapi.PlaceholderAPI.setPlaceholders((Player) sender, message) : message;
        } catch (NullPointerException ignored) {
            return "Error! Path: Syntax." + action;
        }
    }

    public IConfigReader GetConfiguration() {
        return this._configuration;
    }

    public String GetPrefix() {
        try {
            return ChatColor.TranslateAlternateColorCodes('&', this._configuration.GetString("Messages.Misc.Prefix")
                                                                                  .replace("<BREAK>", "\n")
                                                                                  .replace("https://discord.gg/TbnyUrJ", "https://discord.gg/dBhfCzdZxq"));
        } catch (NullPointerException ignored) {
            return "Error! Path: Misc.Prefix";
        }
    }

    public String GetNoPermission(String permission) {
        if (permission == null) permission = "No permission found";
        try {
            return ChatColor.TranslateAlternateColorCodes('&', this._configuration.GetString("Messages.Misc.NoPermissions")
                                                                                  .replace("<PERMISSION>", permission)
                                                                                  .replace("<BREAK>", "\n")
                                                                                  .replace("https://discord.gg/TbnyUrJ", "https://discord.gg/dBhfCzdZxq"));
        } catch (NullPointerException ignored) {
            return "Error! Path: Misc.NoPermissions";
        }
    }

    public String GetOnlyPlayer() {
        try {
            return ChatColor.TranslateAlternateColorCodes('&', this._configuration.GetString("Messages.Misc.OnlyPlayer"))
                            .replace("<BREAK>", "\n")
                            .replace("https://discord.gg/TbnyUrJ", "https://discord.gg/dBhfCzdZxq");
        } catch (NullPointerException ignored) {
            return "Error! Path: Misc.OnlyPlayer";
        }
    }

    public String GetNoTarget(String targetName) {
        try {
            return ChatColor.TranslateAlternateColorCodes('&', this._configuration.GetString("Messages.Misc.NoTarget")
                                                                                  .replace("<TARGET>", targetName != null? targetName : "null")
                                                                                  .replace("<TARGETDISPLAY>", targetName != null? targetName : "null"))
                            .replace("<BREAK>", "\n")
                            .replace("https://discord.gg/TbnyUrJ", "https://discord.gg/dBhfCzdZxq");
        } catch (NullPointerException ignored) {
            return "Error! Path: Misc.NoTarget";
        }
    }

    public void ReloadMessages() {
        this._configuration.Load(this._messagesFile);
    }

    public boolean GetBoolean(String fullAction) {
        return this._configuration.GetBoolean(fullAction);
    }
}
