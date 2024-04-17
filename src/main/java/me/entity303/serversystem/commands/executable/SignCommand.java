package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SignCommand extends CommandUtils implements CommandExecutorOverload {

    public SignCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "sign")) {
            var permission = this.plugin.getPermissions().getPermission("sign");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }
        ((Player) commandSender).getInventory().getItemInMainHand();
        if (((Player) commandSender).getInventory().getItemInMainHand().getType() == Material.AIR) {
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Sign.NoItem"));
            return true;
        }
        var meta = ((Player) commandSender).getInventory().getItemInMainHand().getItemMeta();
        if (meta.hasLore()) {
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Sign.AlreadySigned"));
            return true;
        }
        if (arguments.length == 0) {
            
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Sign"));
            return true;
        }
        List<String> loreList = new ArrayList<>();

        var command4 = command.getName();
        var signFormat = this.plugin.getMessages().getMessage(commandLabel, command4, commandSender, null, "Sign.Format");

        var command3 = command.getName();
        var dateFormat = this.plugin.getMessages().getMessage(commandLabel, command3, commandSender, null, "Sign.DateFormat");

        var command2 = command.getName();
        var timeFormat = this.plugin.getMessages().getMessage(commandLabel, command2, commandSender, null, "Sign.TimeFormat");

        var timeFormatter = DateTimeFormatter.ofPattern(timeFormat);
        var dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
        var localDate = LocalDateTime.now();

        String message;

        var command1 = command.getName();
        var textColor = this.plugin.getMessages().getMessage(commandLabel, command1, commandSender, null, "Sign.TextColor");

        message = Arrays.stream(arguments).map(arg -> textColor + ChatColor.translateAlternateColorCodes('&', arg) + " ").collect(Collectors.joining());

        for (var lore : signFormat.split("\\n"))
            loreList.add(lore.replace("<DATE>", localDate.format(dateFormatter))
                             .replace("<TIME>", localDate.format(timeFormatter))
                             .replace("<SENDER>", commandSender.getName())
                             .replace("<MESSAGE>", message));

        meta.setLore(loreList);

        ((Player) commandSender).getInventory().getItemInMainHand().setItemMeta(meta);
        ((Player) commandSender).updateInventory();
        
        commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, null, "Sign.Success"));
        return true;
    }
}
