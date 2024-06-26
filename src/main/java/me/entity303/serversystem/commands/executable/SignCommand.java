package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SignCommand implements ICommandExecutorOverload {

    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\n");
    protected final ServerSystem _plugin;

    public SignCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "sign")) {
            var permission = this._plugin.GetPermissions().GetPermission("sign");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }
        ((Player) commandSender).getInventory().getItemInMainHand();
        if (((Player) commandSender).getInventory().getItemInMainHand().getType() == Material.AIR) {
            
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Sign.NoItem"));
            return true;
        }
        var meta = ((Player) commandSender).getInventory().getItemInMainHand().getItemMeta();
        if (meta.hasLore()) {
            
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Sign.AlreadySigned"));
            return true;
        }
        if (arguments.length == 0) {
            
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetSyntax(commandLabel, command, commandSender, null, "Sign"));
            return true;
        }
        List<String> loreList = new ArrayList<>();

        var command4 = command.getName();
        var signFormat = this._plugin.GetMessages().GetMessage(commandLabel, command4, commandSender, null, "Sign.Format");

        var command3 = command.getName();
        var dateFormat = this._plugin.GetMessages().GetMessage(commandLabel, command3, commandSender, null, "Sign.DateFormat");

        var command2 = command.getName();
        var timeFormat = this._plugin.GetMessages().GetMessage(commandLabel, command2, commandSender, null, "Sign.TimeFormat");

        var timeFormatter = DateTimeFormatter.ofPattern(timeFormat);
        var dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
        var localDate = LocalDateTime.now();

        String message;

        var command1 = command.getName();
        var textColor = this._plugin.GetMessages().GetMessage(commandLabel, command1, commandSender, null, "Sign.TextColor");

        message = Arrays.stream(arguments).map(arg -> textColor + ChatColor.TranslateAlternateColorCodes('&', arg) + " ").collect(Collectors.joining());

        for (var lore : SPLIT_PATTERN.split(signFormat))
            loreList.add(lore.replace("<DATE>", localDate.format(dateFormatter))
                             .replace("<TIME>", localDate.format(timeFormatter))
                             .replace("<SENDER>", commandSender.getName())
                             .replace("<MESSAGE>", message));

        meta.setLore(loreList);

        ((Player) commandSender).getInventory().getItemInMainHand().setItemMeta(meta);
        ((Player) commandSender).updateInventory();
        
        commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(commandLabel, command, commandSender, null, "Sign.Success"));
        return true;
    }
}
