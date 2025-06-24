package me.testaccount666.serversystem.commands;

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ServerSystemCommand {
    String name();

    String[] variants() default {};

    Class<? extends ServerSystemTabCompleter> tabCompleter() default DefaultTabCompleter.class;
}
