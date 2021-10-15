package me.Entity303.ServerSystem.Utils.versions.commands;

import org.bukkit.command.Command;

public abstract class BukkitCommandWrap {

    public abstract void wrap(Command command, String alias);

    public abstract void unwrap(String command);

}
