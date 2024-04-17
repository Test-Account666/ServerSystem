package me.entity303.serversystem.commands.executable;

import com.github.intellectualsites.plotsquared.plot.object.Plot;
import com.intellectualcrafters.plot.api.PlotAPI;
import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//NOTE: I'll probably delete this at some point
@Deprecated(forRemoval = true) public class EditSignPlotSquaredCommand implements CommandExecutorOverload {
    private final ServerSystem plugin;

    public EditSignPlotSquaredCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] arguments) {
        if (this.plugin.getVersionStuff().getSignEdit() == null) {
            sender.sendMessage(this.getPrefix() + this.getMessage("EditSign.NotAvailable", commandLabel, command.getName(), sender, null));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(this.getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }

        if (this.isLatestPlotSquared()) {
            if (!this.plugin.getPermissions().hasPermission(player, "editschild.players")) {
                player.sendMessage(
                        this.getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("editschild.players")));
                return true;
            }

            var block = player.getTargetBlock(null, 5);
            if (!(block.getState() instanceof Sign sign)) {
                player.sendMessage(this.getPrefix() + this.getMessage("EditSign.SignNeeded", commandLabel, command.getName(), sender, null));
                return true;
            }

            if (!this.isPlotWorldLatest(player)) {
                this.plugin.getVersionStuff().getSignEdit().editSign(player, sign);
                return true;
            }


            if ((!this.isPlayerInPlotLatest(player) && ((!this.plugin.getPermissions().hasPermission(player, "editschild.admin"))))) {
                player.sendMessage(this.getPrefix() + this.getMessage("EditSign.NotInPlot", commandLabel, command.getName(), sender, null));
                return true;
            }

            if (((!this.isPlayerInPlotLatest(player)) || (!this.getLatestPlot(player.getLocation()).isOwner(player.getUniqueId()))) &&
                ((!this.plugin.getPermissions().hasPermission(player, "editschild.admin")))) {
                player.sendMessage(this.getPrefix() + this.getMessage("EditSign.NotYourPlot", commandLabel, command.getName(), sender, null));
                return true;
            }

            this.plugin.getVersionStuff().getSignEdit().editSign(player, sign);
            return true;
        }

        if (this.newerPlotSquared()) {
            if (!this.plugin.getPermissions().hasPermission(player, "editschild.players")) {
                player.sendMessage(
                        this.getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("editschild.players")));
                return true;
            }

            var block = player.getTargetBlock(null, 5);
            if (!(block.getState() instanceof Sign sign)) {
                player.sendMessage(this.getPrefix() + this.getMessage("EditSign.SignNeeded", commandLabel, command.getName(), sender, null));
                return true;
            }

            if (!this.isPlotWorld(player)) {
                this.plugin.getVersionStuff().getSignEdit().editSign(player, sign);
                return true;
            }


            if ((!this.isPlayerInPlot(player) && ((!this.plugin.getPermissions().hasPermission(player, "editschild.admin"))))) {
                player.sendMessage(this.getPrefix() + this.getMessage("EditSign.NotInPlot", commandLabel, command.getName(), sender, null));
                return true;
            }

            if (((!this.isPlayerInPlot(player)) || (!this.getPlot(player.getLocation()).isOwner(player.getUniqueId()))) &&
                ((!this.plugin.getPermissions().hasPermission(player, "editschild.admin")))) {
                player.sendMessage(this.getPrefix() + this.getMessage("EditSign.NotYourPlot", commandLabel, command.getName(), sender, null));
                return true;
            }

            this.plugin.getVersionStuff().getSignEdit().editSign(player, sign);
            return true;
        }

        var plotAPI = new PlotAPI();

        if (!this.plugin.getPermissions().hasPermission(player, "editschild.players")) {
            player.sendMessage(this.getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().getPermission("editschild.players")));
            return true;
        }

        var block = player.getTargetBlock(null, 5);
        if (!(block.getState() instanceof Sign sign)) {
            player.sendMessage(this.getPrefix() + this.getMessage("EditSign.SignNeeded", commandLabel, command.getName(), sender, null));
            return true;
        }

        if (!plotAPI.isPlotWorld(player.getWorld())) {
            this.plugin.getVersionStuff().getSignEdit().editSign(player, sign);
            return true;
        }

        if ((plotAPI.getPlot(player.getTargetBlock(null, 5).getLocation()) == null) &&
            ((!this.plugin.getPermissions().hasPermission(player, "editschild.admin")))) {
            player.sendMessage(this.getPrefix() + this.getMessage("EditSign.NotInPlot", commandLabel, command.getName(), sender, null));
            return true;
        } else if (((!plotAPI.isInPlot(player)) || (!plotAPI.getPlot(player.getLocation()).isOwner(player.getUniqueId()))) &&
                   ((!this.plugin.getPermissions().hasPermission(player, "editschild.admin")))) {
            player.sendMessage(this.getPrefix() + this.getMessage("EditSign.NotYourPlot", commandLabel, command.getName(), sender, null));
            return true;
        }

        this.plugin.getVersionStuff().getSignEdit().editSign(player, sign);

        return true;
    }

    public String getPrefix() {
        return this.plugin.getMessages().getPrefix();
    }

    public String getMessage(String action, String label, String command, CommandSender sender, CommandSender target) {
        return this.plugin.getMessages().getMessage(label, command, sender, target, action);
    }

    public boolean isLatestPlotSquared() {
        try {
            Class.forName("com.plotsquared.bukkit.listener.PlayerEventListener");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public boolean isPlotWorldLatest(Player player) {
        try {
            var getLocationMethod = Class.forName("com.plotsquared.bukkit.util.BukkitUtil").getDeclaredMethod("getLocation", Location.class);
            getLocationMethod.setAccessible(true);
            var plotLocation = getLocationMethod.invoke(null, player.getLocation());
            var getPlotAreaMethod = plotLocation.getClass().getDeclaredMethod("getPlotArea");
            var plotArea = getPlotAreaMethod.invoke(plotLocation);
            return plotArea != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isPlayerInPlotLatest(Player player) {
        return this.getLatestPlot(player.getLocation()) != null;
    }

    public com.plotsquared.core.plot.Plot getLatestPlot(Location location) {
        try {
            var getLocationMethod = Class.forName("com.plotsquared.bukkit.util.BukkitUtil").getDeclaredMethod("getLocation", Location.class);
            getLocationMethod.setAccessible(true);
            var plotLocation = getLocationMethod.invoke(null, location);
            var getPlotMethod = plotLocation.getClass().getDeclaredMethod("getPlot");
            var plot = getPlotMethod.invoke(plotLocation);
            return (com.plotsquared.core.plot.Plot) plot;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean newerPlotSquared() {
        try {
            Class.forName("com.intellectualcrafters.plot.api.PlotAPI");
        } catch (ClassNotFoundException e) {
            return true;
        }
        return false;
    }

    public boolean isPlotWorld(Player player) {
        try {
            var location = player.getLocation();
            var plotLocation =
                    new com.github.intellectualsites.plotsquared.plot.object.Location(location.getWorld().getName(), (int) location.getX(), (int) location.getY(),
                                                                                      (int) location.getZ());
            if (!plotLocation.getPlotArea().getPlots().isEmpty())
                return true;
        } catch (Exception ignored) {
            return false;
        }
        return false;
    }

    public boolean isPlayerInPlot(Player player) {
        return this.getPlot(player.getLocation()) != null;
    }

    public Plot getPlot(Location location) {
        var plotLocation =
                new com.github.intellectualsites.plotsquared.plot.object.Location(location.getWorld().getName(), (int) location.getX(), (int) location.getY(),
                                                                                  (int) location.getZ());
        return plotLocation.getPlot();
    }

    public boolean isAllowed(CommandSender cs, String action) {
        return this.plugin.getPermissions().hasPermission(cs, action);
    }
}
