package me.testaccount666.serversystem.commands;

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for marking classes as command executors in ServerSystem.
 * The CommandManager will automatically register classes annotated with this as command executors.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ServerSystemCommand {
    /**
     * The name of the command.
     *
     * @return The primary name of the command
     */
    String name();

    /**
     * Optional versions for a command.
     * Can be used to group commands like /gamemode, /gmc, /gma, ...
     * <p>
     * Only for commands that do the same but require slightly different arguments
     *
     * @return An array of alternate versions for the command
     */
    String[] variants() default {};

    /**
     * The tab completer class to use for this command.
     *
     * @return The class that will handle tab completion for this command
     */
    Class<? extends ServerSystemTabCompleter> tabCompleter() default DefaultTabCompleter.class;
}
