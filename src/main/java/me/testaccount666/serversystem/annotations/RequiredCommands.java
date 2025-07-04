package me.testaccount666.serversystem.annotations;

import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequiredCommands {
    Class<? extends ServerSystemCommandExecutor>[] requiredCommands();
}
