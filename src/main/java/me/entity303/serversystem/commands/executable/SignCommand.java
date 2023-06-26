package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SignCommand extends MessageUtils implements CommandExecutor {

    public SignCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "sign")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("sign")));
            return true;
        }
        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }
        ((Player) cs).getInventory().getItemInHand();
        if (((Player) cs).getInventory().getItemInHand().getType() == Material.AIR) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Sign.NoItem", label, cmd.getName(), cs, null));
            return true;
        }
        ItemMeta meta = ((Player) cs).getInventory().getItemInHand().getItemMeta();
        if (meta.hasLore()) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Sign.AlreadySigned", label, cmd.getName(), cs, null));
            return true;
        }
        if (args.length <= 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("Sign", label, cmd.getName(), cs, null));
            return true;
        }
        List<String> loreList = new ArrayList<>();

        String signFormat = this.getMessage("Sign.Format", label, cmd.getName(), cs, null);

        String dateFormat = this.getMessage("Sign.DateFormat", label, cmd.getName(), cs, null);

        String timeFormat = this.getMessage("Sign.TimeFormat", label, cmd.getName(), cs, null);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(timeFormat);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
        LocalDateTime localDate = LocalDateTime.now();

        String message;

        String textColor = this.getMessage("Sign.TextColor", label, cmd.getName(), cs, null);

        message = Arrays.stream(args).map(arg -> textColor + ChatColor.translateAlternateColorCodes('&', arg) + " ").collect(Collectors.joining());

        System.out.println(signFormat);

        for (String lore : signFormat.split("\\n")) {
            System.out.println(lore.replace("<DATE>", localDate.format(dateFormatter))
                    .replace("<TIME>", localDate.format(timeFormatter))
                    .replace("<SENDER>", cs.getName())
                    .replace("<MESSAGE>", message));

            loreList.add(lore.replace("<DATE>", localDate.format(dateFormatter))
                    .replace("<TIME>", localDate.format(timeFormatter))
                    .replace("<SENDER>", cs.getName())
                    .replace("<MESSAGE>", message));
        }

        meta.setLore(loreList);

        ((Player) cs).getInventory().getItemInHand().setItemMeta(meta);
        ((Player) cs).updateInventory();
        cs.sendMessage(this.getPrefix() + this.getMessage("Sign.Success", label, cmd.getName(), cs, null));
        return true;
    }
}
