package me.testaccount666.serversystem.userdata.money.vault

import me.testaccount666.serversystem.ServerSystem
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority

object EconomyVaultAPI {
    val isVaultInstalled: Boolean
        get() = Bukkit.getServer().pluginManager.getPlugin("Vault") != null

    fun initialize() {
        Bukkit.getServer().servicesManager.register(
            Economy::class.java, VaultEconomyProvider(),
            ServerSystem.instance, ServicePriority.High
        )
    }
}