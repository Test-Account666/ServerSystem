package me.entity303.serversystem.listener;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener extends CommandUtils implements Listener {

    public SignListener(ServerSystem plugin) {
        super(plugin);
    }

    @EventHandler
    public void OnSignColorCode(SignChangeEvent event) {
        if (event.getLine(0).equalsIgnoreCase("[warp]")) {
            if (!this._plugin.GetPermissions().HasPermission(event.getPlayer(), "signs.warp", true))
                return;
            event.setLine(0, ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "Warp" + ChatColor.GRAY + "]");
            var warp = event.getLine(1).toLowerCase();
            event.setLine(1, (this._plugin.GetWarpManager().DoesWarpExist(warp)? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + event.getLine(1));
            return;
        }

        if (event.getLine(0).equalsIgnoreCase("[disposal]")) {
            if (!this._plugin.GetPermissions().HasPermission(event.getPlayer(), "signs.disposal", true))
                return;
            event.setLine(0, ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "Disposal" + ChatColor.GRAY + "]");
            return;
        }
        if (!this._plugin.GetPermissions().HasPermission(event.getPlayer(), "signs.color", true))
            return;
        for (var index = 0; index < event.getLines().length; index++)
            event.setLine(index, ChatColor.TranslateAlternateColorCodes('&', event.getLine(index)));
    }

    @EventHandler
    public void OnWarpSignInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
            if (event.getClickedBlock() != null)
                if (event.getClickedBlock().getType().name().endsWith("_SIGN"))
                    try {
                        var sign = (Sign) event.getClickedBlock().getState();
                        if (sign.getLine(0).equalsIgnoreCase(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "Warp" + ChatColor.GRAY + "]")) {
                            if (sign.getLine(1).startsWith("§4")) {
                                var str = sign.getLine(1).substring(2).toLowerCase();
                                if (!this._plugin.GetWarpManager().DoesWarpExist(str)) {
                                    var sender = event.getPlayer().getName();
                                    event.getPlayer()
                                     .sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                                     .GetMessage("warp", "warp", sender, null,
                                                                                                                 "Warp.NoWarp")
                                                                                                     .replace("<WARP>",
                                                                                                              sign.getLine(1).substring(2).toUpperCase()));
                                    return;
                                }
                                sign.setLine(1, "§2" + sign.getLine(1).substring(2));
                                event.getPlayer().chat("/warp " + sign.getLine(1).substring(2).toUpperCase());
                                sign.update();
                                return;
                            }
                            event.getPlayer().chat("/warp " + sign.getLine(1).substring(2).toUpperCase());
                            return;
                        }
                        if (sign.getLine(0).equalsIgnoreCase(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "Disposal" + ChatColor.GRAY + "]"))
                            event.getPlayer().chat("/disposal");
                    } catch (ClassCastException exception) {
                        exception.printStackTrace();
                        this._plugin.Error("Found block " + event.getClickedBlock().getType().name() + " which isn't a sign!");
                        this._plugin.Error("Please report this to the plugin author!");
                    }
    }
}
