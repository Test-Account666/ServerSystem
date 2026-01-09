package me.testaccount666.serversystem.placeholderapi;

import me.testaccount666.serversystem.userdata.OfflineUser;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface Placeholder {
    String execute(@Nullable OfflineUser user, String identifier, String... arguments);

    Set<String> getIdentifiers();
}
