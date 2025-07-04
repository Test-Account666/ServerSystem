package me.testaccount666.serversystem.userdata.money.vault;

import me.testaccount666.serversystem.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

public class VaultAPI {

    public static boolean isVaultInstalled() {
        return Bukkit.getServer().getPluginManager().getPlugin("Vault") != null;
    }

    public static void initialize() {
        Bukkit.getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, new VaultProvider(), ServerSystem.Instance, ServicePriority.High);
    }
}
