package me.entity303.serversystem.vault;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import static org.bukkit.Bukkit.getServer;

public class Vault {
    private Chat _chat = null;
    private Permission _permission = null;

    public Vault() {
        this.SetupChat();
        this.SetupPermissions();
    }

    private void SetupChat() {
        var chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null)
            this._chat = chatProvider.getProvider();
    }

    private void SetupPermissions() {
        var permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null)
            this._permission = permissionProvider.getProvider();
    }

    public Permission GetPermission() {
        return this._permission;
    }

    public Chat GetChat() {
        return this._chat;
    }
}
