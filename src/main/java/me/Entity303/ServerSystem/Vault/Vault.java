package me.Entity303.ServerSystem.Vault;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;

import static org.bukkit.Bukkit.getServer;

public class Vault {
    private Chat chat = null;
    private Permission permission = null;

    public Vault() {
        this.setupChat();
        this.setupPermissions();
    }

    private void setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) this.chat = chatProvider.getProvider();
    }

    private void setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) this.permission = permissionProvider.getProvider();
    }

    public Permission getPermission() {
        return this.permission;
    }

    public Chat getChat() {
        return this.chat;
    }
}
