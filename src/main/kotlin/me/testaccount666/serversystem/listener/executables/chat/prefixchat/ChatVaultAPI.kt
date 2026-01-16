package me.testaccount666.serversystem.listener.executables.chat.prefixchat

import net.milkbowl.vault.chat.Chat
import org.bukkit.Bukkit

class ChatVaultAPI {
    var chat: Chat? = null
        private set

    fun setupChat(): Boolean {
        if (!isVaultInstalled) return false

        val registeredProvider = Bukkit.getServer().servicesManager.getRegistration(Chat::class.java) ?: return false

        chat = registeredProvider.getProvider()
        return true
    }

    companion object {
        val isVaultInstalled by lazy {
            return@lazy Bukkit.getServer().pluginManager.getPlugin("Vault") != null
        }
    }
}
