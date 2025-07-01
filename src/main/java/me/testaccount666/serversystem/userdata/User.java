package me.testaccount666.serversystem.userdata;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Optional;

public class User extends OfflineUser {
    protected Player player;

    protected User(File userFile) {
        super(userFile);
    }

    protected User(OfflineUser offlineUser) {
        this(offlineUser.userFile);
    }

    @Override
    protected void loadBasicData() {
        super.loadBasicData();

        userConfig.set("User.LastKnownName", getPlayer().getName());

        save();
    }

    @Override
    public Player getPlayer() {
        if (player == null) player = (Player) super.getPlayer();

        return player;
    }

    @Override
    public Optional<String> getName() {
        return Optional.of(getPlayer().getName());
    }

    public CommandSender getCommandSender() {
        return getPlayer();
    }
}
