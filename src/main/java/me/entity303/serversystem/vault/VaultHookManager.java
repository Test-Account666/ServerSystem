package me.entity303.serversystem.vault;

import me.entity303.serversystem.main.ServerSystem;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

public class VaultHookManager {
    private final ServerSystem plugin;
    private AbstractServerSystemEconomy economy;

    public VaultHookManager(ServerSystem plugin) {
        this.plugin = plugin;
    }

    public void hook() {
        if (!this.plugin.getConfigReader().getBoolean("economy.enabled")) return;

        if (this.plugin.getServer().getPluginManager().getPlugin("Essentials") != null) {
            this.plugin.warn("ServerSystem will not hook into vault! Essentials is installed!");
            return;
        }

        if (!this.plugin.getConfigReader().getBoolean("economy.hookIntoVault")) {
            this.plugin.log("ServerSystem will not hook into Vault! Hooking is disabled!");
            return;
        }
        try {
            if (this.economy == null) this.economy = new AbstractServerSystemEconomy(this.plugin);
            ServicesManager sm = this.plugin.getServer().getServicesManager();
            sm.register(Economy.class, this.economy, this.plugin, ServicePriority.High);
        } catch (Exception e) {
            this.plugin.error("Error while trying to hook into Vault!");
            e.printStackTrace();
        }
    }

    public void hook(boolean force) {
        if (!force) {
            this.hook();
            return;
        }
        ServicesManager sm = Bukkit.getServicesManager();
        if (!this.plugin.getConfigReader().getBoolean("economy.enabled")) return;

        if (!this.plugin.getConfigReader().getBoolean("economy.hookIntoVault")) {
            this.plugin.log("ServerSystem will not hook! Hooking is disabled!");
            return;
        }
        try {
            if (this.economy == null) this.economy = new AbstractServerSystemEconomy(this.plugin);
            sm.register(Economy.class, this.economy, this.plugin, ServicePriority.High);
            if (this.economy != null)
                sm.register(Economy.class, this.economy, Bukkit.getPluginManager().getPlugin("Essentials"), ServicePriority.High);
        } catch (Exception e) {
            this.plugin.error("Error while trying to hook into Vault!");
            e.printStackTrace();
        }
    }

    public boolean isHooked() {
        ServicesManager sm = this.plugin.getServer().getServicesManager();
        sm.getRegistrations(this.plugin);
        if (sm.getRegistrations(this.plugin).size() >= 1) return true;
        if (sm.getRegistrations(this.plugin).size() <= 0) return false;
        return sm.getRegistration(Economy.class).getPlugin().getName().equalsIgnoreCase("ServerSystem");
    }

    public void unhook() {
        if (!this.plugin.getConfigReader().getBoolean("economy.enabled")) return;

        if (!this.plugin.getConfigReader().getBoolean("economy.hookIntoVault")) return;
        ServicesManager sm = this.plugin.getServer().getServicesManager();
        if (this.economy != null) {
            sm.unregister(Economy.class, this.economy);
            try {
                sm.unregisterAll(this.plugin);
            } catch (NumberFormatException ignored) {
            }
            this.economy = null;
        }
    }
}
