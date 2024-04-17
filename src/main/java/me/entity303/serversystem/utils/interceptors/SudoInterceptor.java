package me.entity303.serversystem.utils.interceptors;

import me.entity303.serversystem.commands.executable.SudoCommand;
import me.entity303.serversystem.utils.Morpher;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import org.bukkit.command.CommandSender;

public class SudoInterceptor {
    private final CommandSender commandSender;

    public SudoInterceptor(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    @RuntimeType
    public void intercept(@This Object obj, @AllArguments Object[] allArguments, @Morph Morpher morpher) {
        SudoCommand.sendMessage(this.commandSender, allArguments);
    }
}
