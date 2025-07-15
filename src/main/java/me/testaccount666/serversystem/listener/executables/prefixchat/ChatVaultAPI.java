package me.testaccount666.serversystem.listener.executables.prefixchat;

import lombok.Getter;
import org.bukkit.Bukkit;

public class ChatVaultAPI {
    @Getter
    private net.milkbowl.vault.chat.Chat _chat;

    public static boolean isVaultInstalled() {
        return Bukkit.getServer().getPluginManager().getPlugin("Vault") != null;
    }

    public boolean setupChat() {
        if (!isVaultInstalled()) return false;

        var registeredProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (registeredProvider == null) return false;

        _chat = registeredProvider.getProvider();
        return true;
    }
}
