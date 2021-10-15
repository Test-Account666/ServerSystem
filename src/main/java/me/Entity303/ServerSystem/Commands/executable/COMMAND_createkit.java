package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class COMMAND_createkit extends MessageUtils implements CommandExecutor {

    public COMMAND_createkit(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "createkit")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("createkit")));
            return true;
        }

        if (!(cs instanceof Player)) {
            cs.sendMessage(this.getPrefix() + this.getOnlyPlayer());
            return true;
        }

        if (args.length <= 0) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("CreateKit", label, cmd.getName(), cs, null));
            return true;
        }

        if (this.plugin.getKitsManager().doesKitExist(args[0])) {
            cs.sendMessage(this.getPrefix() + this.getMessage("CreateKit.AlreadyExist", label, cmd.getName(), cs, null).replace("<KIT>", args[0].toUpperCase()));
            return true;
        }

        Player player = ((Player) cs);

        Map<Integer, ItemStack> kit = new HashMap<>();
        for (int i = 0; i < 41; i++) {
            if (i <= 35) {
                kit.put(i, player.getInventory().getItem(i));
                continue;
            }

            if (i == 36) {
                kit.put(i, player.getInventory().getHelmet());
                continue;
            }

            if (i == 37) {
                kit.put(i, player.getInventory().getChestplate());
                continue;
            }
            if (i == 38) {
                kit.put(i, player.getInventory().getLeggings());
                continue;
            }
            if (i == 39) {
                kit.put(i, player.getInventory().getBoots());
                continue;
            }
            try {
                if (!this.plugin.getVersionManager().is188()) kit.put(i, player.getInventory().getItemInOffHand());
                else kit.put(i, null);
                break;
            } catch (Exception ignored) {
                break;
            }
        }

        long delay = 0;

        if (args.length > 1) try {
            delay = Long.parseLong(args[1]);
            delay = (delay * 60) * 1000;
        } catch (NumberFormatException ignored) {
        }

        this.plugin.getKitsManager().addKit(args[0].toLowerCase(), kit, delay);
        cs.sendMessage(this.getPrefix() + this.getMessage("CreateKit.Success", label, cmd.getName(), cs, null).replace("<KIT>", args[0].toUpperCase()));
        return true;
    }
}
