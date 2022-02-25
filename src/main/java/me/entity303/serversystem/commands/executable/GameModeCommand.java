package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameModeCommand extends MessageUtils implements CommandExecutor {
    private final ServerSystem plugin;

    public GameModeCommand(ServerSystem plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "gamemode.self.creative", true) && !this.isAllowed(cs, "gamemode.self.survival", true) && !this.isAllowed(cs, "gamemode.self.spectator", true) && !this.isAllowed(cs, "gamemode.self.adventure", true) && !this.isAllowed(cs, "gamemode.others.creative", true) && !this.isAllowed(cs, "gamemode.others.survival", true) && !this.isAllowed(cs, "gamemode.others.spectator", true) && !this.isAllowed(cs, "gamemode.others.adventure", true)) {
            this.plugin.log(ChatColor.translateAlternateColorCodes('&', this.plugin.getMessages().getCfg().getString("Messages.Misc.NoPermissionInfo")).replace("<SENDER>", cs.getName()));
            cs.sendMessage(this.getPrefix() + this.getNoPermission("gamemode"));
            return true;
        }
        if (args.length == 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("GameMode", label, cmd.getName(), cs, null));
            return true;
        } else if (args.length == 1) {
            if (!(cs instanceof Player)) {
                cs.sendMessage(this.getPrefix() + this.getSyntax("GameMode", label, cmd.getName(), cs, null));
                return true;
            }
            Player p = (Player) cs;
            if ("1".equalsIgnoreCase(args[0]) || "c".equalsIgnoreCase(args[0]) || "creative".equalsIgnoreCase(args[0]) || "k".equalsIgnoreCase(args[0]) || "kreativ".equalsIgnoreCase(args[0]))
                if (this.isAllowed(cs, "gamemode.self.creative")) {
                    p.setGameMode(GameMode.CREATIVE);
                    p.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Self", label, cmd.getName(), cs, null).replace("<MODE>", this.getMode(((Player) cs).getGameMode())));
                } else cs.sendMessage(this.getNoPermission(this.Perm("gamemode.self.creative")));
            else if ("2".equalsIgnoreCase(args[0]) || "a".equalsIgnoreCase(args[0]) || "adventure".equalsIgnoreCase(args[0]) || "abenteuer".equalsIgnoreCase(args[0]))
                if (this.isAllowed(cs, "gamemode.self.adventure")) {
                    p.setGameMode(GameMode.ADVENTURE);
                    p.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Self", label, cmd.getName(), cs, null).replace("<MODE>", this.getMode(((Player) cs).getGameMode())));
                } else cs.sendMessage(this.getNoPermission(this.Perm("gamemode.self.adventure")));
            else if ("3".equalsIgnoreCase(args[0]) || "sp".equalsIgnoreCase(args[0]) || "spectator".equalsIgnoreCase(args[0]) || "z".equalsIgnoreCase(args[0]) || "zuschauer".equalsIgnoreCase(args[0]))
                if (this.isAllowed(cs, "gamemode.self.spectator")) {
                    p.setGameMode(GameMode.SPECTATOR);
                    p.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Self", label, cmd.getName(), cs, null).replace("<MODE>", this.getMode(((Player) cs).getGameMode())));
                } else cs.sendMessage(this.getNoPermission(this.Perm("gamemode.self.spectator")));
            else if ("0".equalsIgnoreCase(args[0]) || "s".equalsIgnoreCase(args[0]) || "survival".equalsIgnoreCase(args[0]) || "ü".equalsIgnoreCase(args[0]) || "überleben".equalsIgnoreCase(args[0]))
                if (this.isAllowed(cs, "gamemode.self.survival")) {
                    p.setGameMode(GameMode.SURVIVAL);
                    p.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Self", label, cmd.getName(), cs, null).replace("<MODE>", this.getMode(((Player) cs).getGameMode())));
                } else cs.sendMessage(this.getNoPermission(this.Perm("gamemode.self.survival")));
            else
                cs.sendMessage(this.getPrefix() + this.getMessage("GameMode.NotGameMode", label, cmd.getName(), cs, null).replace("<MODE>", args[0].toUpperCase()));
            return true;
        } else {
            Player target = this.getPlayer(cs, args[1]);
            if (target != null)
                if ("1".equalsIgnoreCase(args[0]) || "c".equalsIgnoreCase(args[0]) || "creative".equalsIgnoreCase(args[0]) || "k".equalsIgnoreCase(args[0]) || "kreativ".equalsIgnoreCase(args[0]))
                    if (this.isAllowed(cs, "gamemode.others.creative")) {
                        target.setGameMode(GameMode.CREATIVE);
                        target.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Others.Target", label, cmd.getName(), cs, target).replace("<MODE>", this.getMode((target.getGameMode()))));
                        cs.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Others.Sender", label, cmd.getName(), cs, target).replace("<MODE>", this.getMode((target.getGameMode()))));
                    } else cs.sendMessage(this.getNoPermission(this.Perm("gamemode.others.creative")));
                else if ("2".equalsIgnoreCase(args[0]) || "a".equalsIgnoreCase(args[0]) || "adventure".equalsIgnoreCase(args[0]) || "abenteuer".equalsIgnoreCase(args[0]))
                    if (this.isAllowed(cs, "gamemode.others.adventure")) {
                        target.setGameMode(GameMode.ADVENTURE);
                        target.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Others.Target", label, cmd.getName(), cs, target).replace("<MODE>", this.getMode((target.getGameMode()))));
                        cs.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Others.Sender", label, cmd.getName(), cs, target).replace("<MODE>", this.getMode((target.getGameMode()))));
                    } else cs.sendMessage(this.getNoPermission(this.Perm("gamemode.others.adventure")));
                else if ("3".equalsIgnoreCase(args[0]) || "sp".equalsIgnoreCase(args[0]) || "spectator".equalsIgnoreCase(args[0]) || "z".equalsIgnoreCase(args[0]) || "zuschauer".equalsIgnoreCase(args[0]))
                    if (this.isAllowed(cs, "gamemode.others.spectator")) {
                        target.setGameMode(GameMode.SPECTATOR);
                        target.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Others.Target", label, cmd.getName(), cs, target).replace("<MODE>", this.getMode((target.getGameMode()))));
                        cs.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Others.Sender", label, cmd.getName(), cs, target).replace("<MODE>", this.getMode((target.getGameMode()))));
                    } else cs.sendMessage(this.getNoPermission(this.Perm("gamemode.others.spectator")));
                else if ("0".equalsIgnoreCase(args[0]) || "s".equalsIgnoreCase(args[0]) || "survival".equalsIgnoreCase(args[0]) || "ü".equalsIgnoreCase(args[0]) || "überleben".equalsIgnoreCase(args[0]))
                    if (this.isAllowed(cs, "gamemode.others.survival")) {
                        target.setGameMode(GameMode.SURVIVAL);
                        target.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Others.Target", label, cmd.getName(), cs, target).replace("<MODE>", this.getMode((target.getGameMode()))));
                        cs.sendMessage(this.getPrefix() + this.getMessage("GameMode.Changed.Others.Sender", label, cmd.getName(), cs, target).replace("<MODE>", this.getMode((target.getGameMode()))));
                    } else cs.sendMessage(this.getNoPermission(this.Perm("gamemode.others.survival")));
                else
                    cs.sendMessage(this.getPrefix() + this.getMessage("GameMode.NotGameMode", label, cmd.getName(), cs, target).replace("<MODE>", args[0].toUpperCase()));
            else
                cs.sendMessage(this.getPrefix() + this.getNoTarget(args[1]));
        }
        return true;
    }

    private String getMode(GameMode gamemode) {
        return this.plugin.getMessages().getCfg().getString("Messages.Misc.GameModes." + gamemode.name());
    }
}
