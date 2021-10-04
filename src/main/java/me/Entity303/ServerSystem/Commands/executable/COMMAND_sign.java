package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ChatColor;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class COMMAND_sign extends ServerSystemCommand implements CommandExecutor {

    public COMMAND_sign(ss plugin) {
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
        String loreString = Arrays.stream(args).map(arg -> ChatColor.translateAlternateColorCodes('&', arg) + " §9").collect(Collectors.joining("", "§9", ""));
        loreList.add("§c-------------------------------------");
        loreList.add(loreString);
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
        Date localDate = new Date();
        localDate.setTime(System.currentTimeMillis());
        TemporalAccessor accessor = localDate.toInstant();
        loreList.add("§c" + this.getMessage("Sign.Translation.Von", label, cmd.getName(), cs, null) + ": §2" + cs.getName());
        loreList.add("§c" + this.getMessage("Sign.Translation.Am", label, cmd.getName(), cs, null) + ": §2" + dateFormat2.format(localDate).replace("/", "."));
        loreList.add("§c" + this.getMessage("Sign.Translation.Um", label, cmd.getName(), cs, null) + ": §2" + dateFormat.format(localDate));
        loreList.add("§4§c-------------------------------------");
        meta.setLore(loreList);
        ((Player) cs).getInventory().getItemInHand().setItemMeta(meta);
        ((Player) cs).updateInventory();
        cs.sendMessage(this.getPrefix() + this.getMessage("Sign.Success", label, cmd.getName(), cs, null));
        return true;
    }
}
