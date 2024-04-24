package me.entity303.serversystem.commands.executable;

import com.github.intellectualsites.plotsquared.plot.object.Plot;
import com.intellectualcrafters.plot.api.PlotAPI;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

//NOTE: I'll probably delete this at some point
@Deprecated(forRemoval = true) public class EditSignPlotSquaredCommand implements ICommandExecutorOverload {
    private final ServerSystem _plugin;

    public EditSignPlotSquaredCommand(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (this._plugin.GetVersionStuff().GetSignEdit() == null) {
            commandSender.sendMessage(this.GetPrefix() + this.GetMessage("EditSign.NotAvailable", commandLabel, command.getName(), commandSender, null));
            return true;
        }

        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(this.GetPrefix() + this._plugin.GetMessages().GetOnlyPlayer());
            return true;
        }

        if (this.IsLatestPlotSquared()) {
            if (!this._plugin.GetPermissions().HasPermission(player, "editschild.players")) {
                player.sendMessage(
                        this.GetPrefix() + this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("editschild.players")));
                return true;
            }

            var block = player.getTargetBlock(null, 5);
            if (!(block.getState() instanceof Sign sign)) {
                player.sendMessage(this.GetPrefix() + this.GetMessage("EditSign.SignNeeded", commandLabel, command.getName(), commandSender, null));
                return true;
            }

            if (!this.IsPlotWorldLatest(player)) {
                this._plugin.GetVersionStuff().GetSignEdit().EditSign(player, sign);
                return true;
            }


            if ((!this.IsPlayerInPlotLatest(player) && ((!this._plugin.GetPermissions().HasPermission(player, "editschild.admin"))))) {
                player.sendMessage(this.GetPrefix() + this.GetMessage("EditSign.NotInPlot", commandLabel, command.getName(), commandSender, null));
                return true;
            }

            if (((!this.IsPlayerInPlotLatest(player)) || (!this.GetLatestPlot(player.getLocation()).isOwner(player.getUniqueId()))) &&
                ((!this._plugin.GetPermissions().HasPermission(player, "editschild.admin")))) {
                player.sendMessage(this.GetPrefix() + this.GetMessage("EditSign.NotYourPlot", commandLabel, command.getName(), commandSender, null));
                return true;
            }

            this._plugin.GetVersionStuff().GetSignEdit().EditSign(player, sign);
            return true;
        }

        if (this.NewerPlotSquared()) {
            if (!this._plugin.GetPermissions().HasPermission(player, "editschild.players")) {
                player.sendMessage(
                        this.GetPrefix() + this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("editschild.players")));
                return true;
            }

            var block = player.getTargetBlock(null, 5);
            if (!(block.getState() instanceof Sign sign)) {
                player.sendMessage(this.GetPrefix() + this.GetMessage("EditSign.SignNeeded", commandLabel, command.getName(), commandSender, null));
                return true;
            }

            if (!this.IsPlotWorld(player)) {
                this._plugin.GetVersionStuff().GetSignEdit().EditSign(player, sign);
                return true;
            }


            if ((!this.IsPlayerInPlot(player) && ((!this._plugin.GetPermissions().HasPermission(player, "editschild.admin"))))) {
                player.sendMessage(this.GetPrefix() + this.GetMessage("EditSign.NotInPlot", commandLabel, command.getName(), commandSender, null));
                return true;
            }

            if (((!this.IsPlayerInPlot(player)) || (!this.GetPlot(player.getLocation()).isOwner(player.getUniqueId()))) &&
                ((!this._plugin.GetPermissions().HasPermission(player, "editschild.admin")))) {
                player.sendMessage(this.GetPrefix() + this.GetMessage("EditSign.NotYourPlot", commandLabel, command.getName(), commandSender, null));
                return true;
            }

            this._plugin.GetVersionStuff().GetSignEdit().EditSign(player, sign);
            return true;
        }

        var plotAPI = new PlotAPI();

        if (!this._plugin.GetPermissions().HasPermission(player, "editschild.players")) {
            player.sendMessage(this.GetPrefix() + this._plugin.GetMessages().GetNoPermission(this._plugin.GetPermissions().GetPermission("editschild.players")));
            return true;
        }

        var block = player.getTargetBlock(null, 5);
        if (!(block.getState() instanceof Sign sign)) {
            player.sendMessage(this.GetPrefix() + this.GetMessage("EditSign.SignNeeded", commandLabel, command.getName(), commandSender, null));
            return true;
        }

        if (!plotAPI.isPlotWorld(player.getWorld())) {
            this._plugin.GetVersionStuff().GetSignEdit().EditSign(player, sign);
            return true;
        }

        if ((plotAPI.getPlot(player.getTargetBlock(null, 5).getLocation()) == null) &&
            ((!this._plugin.GetPermissions().HasPermission(player, "editschild.admin")))) {
            player.sendMessage(this.GetPrefix() + this.GetMessage("EditSign.NotInPlot", commandLabel, command.getName(), commandSender, null));
            return true;
        } else if (((!plotAPI.isInPlot(player)) || (!plotAPI.getPlot(player.getLocation()).isOwner(player.getUniqueId()))) &&
                   ((!this._plugin.GetPermissions().HasPermission(player, "editschild.admin")))) {
            player.sendMessage(this.GetPrefix() + this.GetMessage("EditSign.NotYourPlot", commandLabel, command.getName(), commandSender, null));
            return true;
        }

        this._plugin.GetVersionStuff().GetSignEdit().EditSign(player, sign);

        return true;
    }

    public String GetPrefix() {
        return this._plugin.GetMessages().GetPrefix();
    }

    public String GetMessage(String action, String commandLabel, String command, CommandSender sender, CommandSender target) {
        return this._plugin.GetMessages().GetMessage(commandLabel, command, sender, target, action);
    }

    public boolean IsLatestPlotSquared() {
        try {
            Class.forName("com.plotsquared.bukkit.listener.PlayerEventListener");
        } catch (ClassNotFoundException exception) {
            return false;
        }
        return true;
    }

    public boolean IsPlotWorldLatest(Player player) {
        try {
            var getLocationMethod = Class.forName("com.plotsquared.bukkit.util.BukkitUtil").getDeclaredMethod("getLocation", Location.class);
            getLocationMethod.setAccessible(true);
            var plotLocation = getLocationMethod.invoke(null, player.getLocation());
            var getPlotAreaMethod = plotLocation.getClass().getDeclaredMethod("getPlotArea");
            var plotArea = getPlotAreaMethod.invoke(plotLocation);
            return plotArea != null;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean IsPlayerInPlotLatest(Player player) {
        return this.GetLatestPlot(player.getLocation()) != null;
    }

    public com.plotsquared.core.plot.Plot GetLatestPlot(Location location) {
        try {
            var getLocationMethod = Class.forName("com.plotsquared.bukkit.util.BukkitUtil").getDeclaredMethod("getLocation", Location.class);
            getLocationMethod.setAccessible(true);
            var plotLocation = getLocationMethod.invoke(null, location);
            var getPlotMethod = plotLocation.getClass().getDeclaredMethod("getPlot");
            var plot = getPlotMethod.invoke(plotLocation);
            return (com.plotsquared.core.plot.Plot) plot;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public boolean NewerPlotSquared() {
        try {
            Class.forName("com.intellectualcrafters.plot.api.PlotAPI");
        } catch (ClassNotFoundException exception) {
            return true;
        }
        return false;
    }

    public boolean IsPlotWorld(Player player) {
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

    public boolean IsPlayerInPlot(Entity player) {
        return this.GetPlot(player.getLocation()) != null;
    }

    public Plot GetPlot(Location location) {
        var plotLocation =
                new com.github.intellectualsites.plotsquared.plot.object.Location(location.getWorld().getName(), (int) location.getX(), (int) location.getY(),
                                                                                  (int) location.getZ());
        return plotLocation.getPlot();
    }

    public boolean IsAllowed(CommandSender commandSender, String action) {
        return this._plugin.GetPermissions().HasPermission(commandSender, action);
    }
}
