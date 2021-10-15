package me.Entity303.ServerSystem.Utils;

import me.Entity303.ServerSystem.Commands.executable.COMMAND_sudo;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import org.bukkit.command.CommandSender;

public class Interceptor {
    private CommandSender commandSender;

    public Interceptor(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    @RuntimeType
    public void intercept(@This Object obj,
                            @AllArguments Object[] allArguments,
                            @Morph Morpher morpher) {
        COMMAND_sudo.sendMessage(commandSender, allArguments);
    }
}
