package me.entity303.serversystem.utils.interceptors;

import me.entity303.serversystem.commands.executable.SudoCommand;
import me.entity303.serversystem.utils.IMorpher;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import org.bukkit.command.CommandSender;

public class SudoInterceptor {
    private final CommandSender _commandSender;

    public SudoInterceptor(CommandSender commandSender) {
        this._commandSender = commandSender;
    }

    @SuppressWarnings("NewMethodNamingConvention")
    @RuntimeType
    public void intercept(@This Object obj, @AllArguments Object[] allArguments, @Morph IMorpher morpher) {
        SudoCommand.SendMessage(this._commandSender, allArguments);
    }
}
