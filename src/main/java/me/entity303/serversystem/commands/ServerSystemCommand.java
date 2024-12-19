package me.entity303.serversystem.commands;

import me.entity303.serversystem.tabcompleter.DefaultTabCompleter;
import org.bukkit.command.TabCompleter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServerSystemCommand {
    String name();

    Class<? extends TabCompleter> tabCompleter() default DefaultTabCompleter.class;
}
