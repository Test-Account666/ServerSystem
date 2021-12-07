package me.Entity303.ServerSystem.Utils.interceptors;

import me.Entity303.ServerSystem.Commands.executable.COMMAND_sudo;
import me.Entity303.ServerSystem.Utils.Morpher;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import org.bukkit.command.CommandSender;

public class SudoInterceptor {
    private CommandSender commandSender;

    public SudoInterceptor(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    @RuntimeType
    public void intercept(@This Object obj,
                          @AllArguments Object[] allArguments,
                          @Morph Morpher morpher) {
        COMMAND_sudo.sendMessage(this.commandSender, allArguments);
    }
}
