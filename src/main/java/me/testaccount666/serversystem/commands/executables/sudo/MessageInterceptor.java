package me.testaccount666.serversystem.commands.executables.sudo;

import me.testaccount666.serversystem.ServerSystem;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.stream.Stream;

public class MessageInterceptor {
    private final CommandSender _commandSender;

    public MessageInterceptor(CommandSender commandSender) {
        _commandSender = commandSender;
    }

    @RuntimeType
    public void intercept(@This Object obj, @AllArguments Object[] allArguments, @Morph IMorpher morpher) {
        var sendMessageMethod = findMatchingMethod(allArguments);
        if (sendMessageMethod == null) return;

        invokeSendMessage(sendMessageMethod, allArguments);
    }

    private Method findMatchingMethod(Object[] allArguments) {
        return Stream.of(CommandSender.class.getDeclaredMethods())
                .filter(method -> hasMatchingParameters(method, allArguments))
                .findFirst()
                .orElse(null);
    }

    private boolean hasMatchingParameters(Method method, Object[] arguments) {
        var parameters = method.getParameterTypes();

        if (parameters.length != arguments.length) return false;

        for (var index = 0; index < parameters.length; index++) {
            if (parameters[index].getCanonicalName().equals(arguments[index].getClass().getCanonicalName())) continue;
            return false;
        }

        return true;
    }

    private void invokeSendMessage(Method method, Object[] arguments) {
        try {
            method.invoke(_commandSender, arguments);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            ServerSystem.getLog().log(Level.WARNING, "(MessageInterceptor) Couldn't invoke sendMessage method!", exception);
        }
    }
}
