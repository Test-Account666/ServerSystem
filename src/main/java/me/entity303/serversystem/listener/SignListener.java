package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener extends MessageUtils implements Listener {

    public SignListener(ServerSystem plugin) {
        super(plugin);
    }

    @EventHandler
    public void onSignColorCode(SignChangeEvent e) {
        if (e.getLine(0).equalsIgnoreCase("[warp]")) {
            if (!this.isAllowed(e.getPlayer(), "signs.warp", true)) return;
            e.setLine(0, ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "Warp" + ChatColor.GRAY + "]");
            String warp = e.getLine(1).toLowerCase();
            e.setLine(1, this.plugin.getWarpManager().doesWarpExist(warp) ? ChatColor.DARK_GREEN + e.getLine(1) : ChatColor.DARK_RED + e.getLine(1));
            return;
        }

        if (e.getLine(0).equalsIgnoreCase("[disposal]")) {
            if (!this.isAllowed(e.getPlayer(), "signs.disposal", true)) return;
            e.setLine(0, ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "Disposal" + ChatColor.GRAY + "]");
            return;
        }
        if (!this.isAllowed(e.getPlayer(), "signs.color", true)) return;
        for (int i = 0; i < e.getLines().length; i++)
            e.setLine(i, ChatColor.translateAlternateColorCodes('&', e.getLine(i)));
    }

    @EventHandler
    public void onWarpSignInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
            if (e.getClickedBlock() != null)
                if (e.getClickedBlock().getType().name().endsWith("_SIGN")) try {
                    Sign sign = (Sign) e.getClickedBlock().getState();
                    if (sign.getLine(0).equalsIgnoreCase(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "Warp" + ChatColor.GRAY + "]")) {
                        if (sign.getLine(1).startsWith("§4")) {
                            String str = sign.getLine(1).substring(2).toLowerCase();
                            if (!this.plugin.getWarpManager().doesWarpExist(str)) {
                                e.getPlayer().sendMessage(this.getPrefix() + this.getMessage("Warp.NoWarp", "warp", "warp", e.getPlayer().getName(), null).replace("<WARP>", sign.getLine(1).substring(2).toUpperCase()));
                                return;
                            }
                            sign.setLine(1, "§2" + sign.getLine(1).substring(2));
                            e.getPlayer().chat("/warp " + sign.getLine(1).substring(2).toUpperCase());
                            sign.update();
                            return;
                        }
                        e.getPlayer().chat("/warp " + sign.getLine(1).substring(2).toUpperCase());
                        return;
                    }
                    if (sign.getLine(0).equalsIgnoreCase(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "Disposal" + ChatColor.GRAY + "]"))
                        e.getPlayer().chat("/disposal");
                } catch (ClassCastException exception) {
                    exception.printStackTrace();
                    this.plugin.error("Found block " + e.getClickedBlock().getType().name() + " which isn't a sign!");
                    this.plugin.error("Please report this to the plugin author!");
                }
    }
}
