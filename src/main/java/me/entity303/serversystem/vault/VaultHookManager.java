package me.entity303.serversystem.vault;

import me.entity303.serversystem.main.ServerSystem;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

public class VaultHookManager {
    private final ServerSystem _plugin;
    private AbstractServerSystemEconomy _economy;

    public VaultHookManager(ServerSystem plugin) {
        this._plugin = plugin;
    }

    public void Hook(boolean force) {
        if (!force) {
            this.Hook();
            return;
        }
        var serviceManager = Bukkit.getServicesManager();
        if (!this._plugin.GetConfigReader().GetBoolean("economy.enabled"))
            return;

        if (!this._plugin.GetConfigReader().GetBoolean("economy.hookIntoVault")) {
            this._plugin.Info("ServerSystem will not hook! Hooking is disabled!");
            return;
        }
        try {
            if (this._economy == null)
                this._economy = new AbstractServerSystemEconomy(this._plugin);
            serviceManager.register(Economy.class, this._economy, this._plugin, ServicePriority.High);
            if (this._economy != null)
                serviceManager.register(Economy.class, this._economy, Bukkit.getPluginManager().getPlugin("Essentials"), ServicePriority.High);
        } catch (Exception exception) {
            this._plugin.Error("Error while trying to hook into Vault!");
            exception.printStackTrace();
        }
    }

    public void Hook() {
        if (!this._plugin.GetConfigReader().GetBoolean("economy.enabled"))
            return;

        if (this._plugin.getServer().getPluginManager().getPlugin("Essentials") != null) {
            this._plugin.Warn("ServerSystem will not hook into vault! Essentials is installed!");
            return;
        }

        if (!this._plugin.GetConfigReader().GetBoolean("economy.hookIntoVault")) {
            this._plugin.Info("ServerSystem will not hook into Vault! Hooking is disabled!");
            return;
        }
        try {
            if (this._economy == null)
                this._economy = new AbstractServerSystemEconomy(this._plugin);
            var serviceManager = this._plugin.getServer().getServicesManager();
            serviceManager.register(Economy.class, this._economy, this._plugin, ServicePriority.High);
        } catch (Exception exception) {
            this._plugin.Error("Error while trying to hook into Vault!");
            exception.printStackTrace();
        }
    }

    public boolean IsHooked() {
        var serviceManager = this._plugin.getServer().getServicesManager();
        serviceManager.getRegistrations(this._plugin);
        if (!serviceManager.getRegistrations(this._plugin).isEmpty())
            return true;
        if (serviceManager.getRegistrations(this._plugin).isEmpty())
            return false;
        return serviceManager.getRegistration(Economy.class).getPlugin().getName().equalsIgnoreCase("ServerSystem");
    }

    public void Unhook() {
        if (!this._plugin.GetConfigReader().GetBoolean("economy.enabled"))
            return;

        if (!this._plugin.GetConfigReader().GetBoolean("economy.hookIntoVault"))
            return;
        var serviceManager = this._plugin.getServer().getServicesManager();
        if (this._economy != null) {
            serviceManager.unregister(Economy.class, this._economy);
            try {
                serviceManager.unregisterAll(this._plugin);
            } catch (NumberFormatException ignored) {
            }
            this._economy = null;
        }
    }
}
