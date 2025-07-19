package me.testaccount666.serversystem.clickablesigns;

import me.testaccount666.serversystem.userdata.User;
import org.bukkit.block.Sign;

public interface SignConfigurator {
    void execute(User user, Sign sign);
}
