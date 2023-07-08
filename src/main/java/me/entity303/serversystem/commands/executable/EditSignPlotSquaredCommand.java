package me.entity303.serversystem.commands.executable;

import com.github.intellectualsites.plotsquared.plot.object.Plot;
import com.intellectualcrafters.plot.api.PlotAPI;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class EditSignPlotSquaredCommand implements CommandExecutor {
    private final ServerSystem plugin;

    public EditSignPlotSquaredCommand(ServerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (this.plugin.getVersionStuff().getSignEdit() == null) {
            sender.sendMessage(this.getPrefix() + this.getMessage("EditSign.NotAvailable", label, cmd.getName(), sender, null));
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.getPrefix() + this.plugin.getMessages().getOnlyPlayer());
            return true;
        }
        Player p = (Player) sender;
        if (this.isLatestPlotSquared()) {
            if (!this.isAllowed(p, "editschild.players")) {
                p.sendMessage(this.getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("editschild.players")));
                return true;
            }

            Block block = p.getTargetBlock(null, 5);
            if (block.getState() instanceof Sign) {
                if (!this.isPlotWorldLatest(p)) {
                    Sign sign = (Sign) block.getState();
                    this.plugin.getVersionStuff().getSignEdit().editSign(p, sign);
                    return true;
                }


                if ((this.isPlayerInPlotLatest(p) || ((this.isAllowed(p, "editschild.admin")))))
                    if (((this.isPlayerInPlotLatest(p)) && (this.getLatestPlot(p.getLocation()).isOwner(p.getUniqueId()))) || ((this.isAllowed(p, "editschild.admin")))) {
                        Sign sign = (Sign) block.getState();
                        this.plugin.getVersionStuff().getSignEdit().editSign(p, sign);
                    } else
                        p.sendMessage(this.getPrefix() + this.getMessage("EditSign.NotYourPlot", label, cmd.getName(), sender, null));
                else
                    p.sendMessage(this.getPrefix() + this.getMessage("EditSign.NotInPlot", label, cmd.getName(), sender, null));
            } else
                p.sendMessage(this.getPrefix() + this.getMessage("EditSign.SignNeeded", label, cmd.getName(), sender, null));

        } else if (this.newerPlotSquared()) {
            if (!this.isAllowed(p, "editschild.players")) {
                p.sendMessage(this.getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("editschild.players")));
                return true;
            }

            Block block = p.getTargetBlock(null, 5);
            if (block.getState() instanceof Sign) {
                if (!this.isPlotWorld(p)) {
                    Sign sign = (Sign) block.getState();
                    this.plugin.getVersionStuff().getSignEdit().editSign(p, sign);
                    return true;
                }


                if ((this.isPlayerInPlot(p) || ((this.isAllowed(p, "editschild.admin")))))
                    if (((this.isPlayerInPlot(p)) && (this.getPlot(p.getLocation()).isOwner(p.getUniqueId()))) || ((this.isAllowed(p, "editschild.admin")))) {
                        Sign sign = (Sign) block.getState();
                        this.plugin.getVersionStuff().getSignEdit().editSign(p, sign);
                    } else
                        p.sendMessage(this.getPrefix() + this.getMessage("EditSign.NotYourPlot", label, cmd.getName(), sender, null));
                else
                    p.sendMessage(this.getPrefix() + this.getMessage("EditSign.NotInPlot", label, cmd.getName(), sender, null));
            } else
                p.sendMessage(this.getPrefix() + this.getMessage("EditSign.SignNeeded", label, cmd.getName(), sender, null));

        } else {
            PlotAPI papi = new PlotAPI();
            if (!this.isAllowed(p, "editschild.players")) {
                p.sendMessage(this.getPrefix() + this.plugin.getMessages().getNoPermission(this.plugin.getPermissions().Perm("editschild.players")));
                return true;
            }
            Block block = p.getTargetBlock(null, 5);
            if (block.getState() instanceof Sign) {
                if (!papi.isPlotWorld(p.getWorld())) {
                    Sign sign = (Sign) block.getState();
                    this.plugin.getVersionStuff().getSignEdit().editSign(p, sign);
                    return true;
                }
                if ((papi.getPlot(p.getTargetBlock(null, 5).getLocation()) != null) || ((this.isAllowed(p, "editschild.admin"))))
                    if (((papi.isInPlot(p)) && (papi.getPlot(p.getLocation()).isOwner(p.getUniqueId()))) || ((this.isAllowed(p, "editschild.admin")))) {
                        Sign sign = (Sign) block.getState();
                        this.plugin.getVersionStuff().getSignEdit().editSign(p, sign);
                    } else
                        p.sendMessage(this.getPrefix() + this.getMessage("EditSign.NotYourPlot", label, cmd.getName(), sender, null));
                else
                    p.sendMessage(this.getPrefix() + this.getMessage("EditSign.NotInPlot", label, cmd.getName(), sender, null));
            } else
                p.sendMessage(this.getPrefix() + this.getMessage("EditSign.SignNeeded", label, cmd.getName(), sender, null));

        }
        return true;
    }

    public com.plotsquared.core.plot.Plot getLatestPlot(Location location) {
        try {
            Method getLocationMethod = Class.forName("com.plotsquared.bukkit.util.BukkitUtil").getDeclaredMethod("getLocation", Location.class);
            getLocationMethod.setAccessible(true);
            Object plotLocation = getLocationMethod.invoke(null, location);
            Method getPlotMethod = plotLocation.getClass().getDeclaredMethod("getPlot");
            Object plot = getPlotMethod.invoke(plotLocation);
            return (com.plotsquared.core.plot.Plot) plot;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isPlotWorldLatest(Player player) {
        try {
            Method getLocationMethod = Class.forName("com.plotsquared.bukkit.util.BukkitUtil").getDeclaredMethod("getLocation", Location.class);
            getLocationMethod.setAccessible(true);
            Object plotLocation = getLocationMethod.invoke(null, player.getLocation());
            Method getPlotAreaMethod = plotLocation.getClass().getDeclaredMethod("getPlotArea");
            Object plotArea = getPlotAreaMethod.invoke(plotLocation);
            return plotArea != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isPlotWorld(Player player) {
        try {
            Location location = player.getLocation();
            com.github.intellectualsites.plotsquared.plot.object.Location plotLocation = new com.github.intellectualsites.plotsquared.plot.object.Location(location.getWorld().getName(), (int) location.getX(), (int) location.getY(), (int) location.getZ());
            if (plotLocation.getPlotArea().getPlots().size() >= 1) return true;
        } catch (Exception ignored) {
            return false;
        }
        return false;
    }

    public boolean isLatestPlotSquared() {
        try {
            Class.forName("com.plotsquared.bukkit.listener.PlayerEventListener");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public boolean newerPlotSquared() {
        try {
            Class.forName("com.intellectualcrafters.plot.api.PlotAPI");
        } catch (ClassNotFoundException e) {
            return true;
        }
        return false;
    }

    public Plot getPlot(Location location) {
        com.github.intellectualsites.plotsquared.plot.object.Location plotLocation = new com.github.intellectualsites.plotsquared.plot.object.Location(location.getWorld().getName(), (int) location.getX(), (int) location.getY(), (int) location.getZ());
        return plotLocation.getPlot();
    }

    public boolean isPlayerInPlotLatest(Player player) {
        return this.getLatestPlot(player.getLocation()) != null;
    }

    public boolean isPlayerInPlot(Player player) {
        return this.getPlot(player.getLocation()) != null;
    }

    public String getPrefix() {
        return this.plugin.getMessages().getPrefix();
    }

    public String getMessage(String action, String label, String command, CommandSender sender, CommandSender target) {
        return this.plugin.getMessages().getMessage(label, command, sender, target, action);
    }

    public boolean isAllowed(CommandSender cs, String action) {
        return this.plugin.getPermissions().hasPerm(cs, action);
    }
}
