package me.testaccount666.serversystem.userdata;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class User extends OfflineUser {
    protected Player player;

    protected User(File userFile) {
        super(userFile);

        player = getPlayer();
    }

    @Override
    public Player getPlayer() {
        if (player == null) player = (Player) super.getPlayer();

        return player;
    }

    public CommandSender getCommandSender() {
        return getPlayer();
    }
}
