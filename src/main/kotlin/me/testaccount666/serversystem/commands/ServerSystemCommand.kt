package me.testaccount666.serversystem.commands

import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import kotlin.reflect.KClass

/**
 * Annotation for marking classes as command executors in ServerSystem.
 * The CommandManager will automatically register classes annotated with this as command executors.
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class ServerSystemCommand(
    /**
     * The name of the command.
     * 
     * @return The primary name of the command
     */
    val name: String,
    /**
     * Optional versions for a command.
     * Can be used to group commands like /gamemode, /gmc, /gma, ...
     * 
     * 
     * Only for commands that do the same but require slightly different arguments
     * 
     * @return An array of alternate versions for the command
     */
    val variants: Array<String> = [],
    /**
     * The tab completer class to use for this command.
     * 
     * @return The class that will handle tab completion for this command
     */
    val tabCompleter: KClass<out ServerSystemTabCompleter> = DefaultTabCompleter::class
)
