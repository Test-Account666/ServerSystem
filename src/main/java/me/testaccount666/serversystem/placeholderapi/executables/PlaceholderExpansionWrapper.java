package me.testaccount666.serversystem.placeholderapi.executables;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.testaccount666.serversystem.ServerSystem;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderExpansionWrapper extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "serversystem";
    }

    @Override
    public @NotNull String getAuthor() {
        return ServerSystem.Instance.getPluginMeta().getAuthors().getFirst();
    }

    @Override
    public @NotNull String getVersion() {
        return ServerSystem.Instance.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.isBlank()) {
            ServerSystem.getLog().warning("An invalid placeholder was requested!");
            return null;
        }

        var split = params.split("_");
        var identifier = split[0].toLowerCase();

        var placeholderManager = ServerSystem.Instance.getPlaceholderManager();
        var placeholderOptional = placeholderManager.getPlaceholder(identifier);
        if (placeholderOptional.isEmpty()) {
            ServerSystem.getLog().warning("An unknown placeholder was requested! '${params}'");
            return null;
        }

        var placeholder = placeholderOptional.get();

        var arguments = new String[0];

        if (split.length > 1) {
            arguments = new String[split.length - 1];
            System.arraycopy(split, 1, arguments, 0, arguments.length);
        }

        if (player == null) return placeholder.execute(null, identifier, arguments);

        var userOptional = ServerSystem.Instance.getUserManager().getUser(player.getUniqueId());
        if (userOptional.isEmpty()) return null;
        var cachedUser = userOptional.get();
        var offlineUser = cachedUser.getOfflineUser();

        return placeholder.execute(offlineUser, identifier, arguments);
    }
}
