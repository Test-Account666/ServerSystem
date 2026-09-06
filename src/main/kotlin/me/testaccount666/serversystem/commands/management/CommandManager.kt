package me.testaccount666.serversystem.commands.management

import io.github.classgraph.ClassGraph
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.common.tabcompleters.SimpleTabCompleter
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.commands.wrappers.CommandExecutorWrapper
import me.testaccount666.serversystem.commands.wrappers.TabCompleterWrapper
import me.testaccount666.serversystem.managers.config.ConfigReader
import me.testaccount666.serversystem.utils.*
import org.bukkit.Bukkit
import org.bukkit.command.*
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class CommandManager(private val _configReader: ConfigReader) {
    private val _commandMapAccessor =
        FieldAccessor.createGetter<SimpleCommandMap, MutableMap<String, Command>>(SimpleCommandMap::class.java, "knownCommands")
    private val _pluginCommandConstructor =
        ConstructorAccessor.createConstructor(
            PluginCommand::class.java,
            String::class.java, Plugin::class.java
        )
    private val _syncCommandsAccessor = MethodAccessor.createVoidAccessor(Bukkit.getServer().javaClass, "syncCommands")
    private val _registeredCommands = mutableSetOf<String>()
    private val _registeredCommandInstances = mutableSetOf<ServerSystemCommandExecutor>()

    fun getCommand(commandName: String) = commandMap[commandName]

    fun getServerSystemCommand(commandName: String): PluginCommand? {
        return commandMap.values
            .filterIsInstance<PluginCommand>()
            .find {
                it.name.equals(commandName, true) &&
                        it.plugin === instance &&
                        it.executor is ServerSystemCommandExecutor
            }
    }

    fun getServerSystemCommandExecutor(commandName: String): ServerSystemCommandExecutor? {
        return getServerSystemCommand(commandName)?.executor as? ServerSystemCommandExecutor
    }

    val commandMap: MutableMap<String, Command>
        get() {
            val commandMap = Bukkit.getCommandMap() as SimpleCommandMap
            return _commandMapAccessor.apply(commandMap) ?: HashMap()
        }

    private fun createCommand(name: String): PluginCommand {
        val command = _pluginCommandConstructor.apply(name, instance)
        requireNotNull(command) { "Error creating command '${name}'!" }

        return command
    }

    private fun registerCommand(
        command: ServerSystemCommandExecutor,
        completer: ServerSystemTabCompleter,
        variantAliasMap: Map<String, List<String>>,
    ) {
        val commandMap = commandMap

        variantAliasMap.forEach { (variant, aliases) ->
            // Make aliases List modifiable
            val aliases = aliases.toMutableList()
            aliases.forEach(commandMap::remove)

            if (command is AbstractServerSystemCommand) command.variantLabelMap[variant] = aliases

            val bukkitCommand = createCommand(variant).apply {
                setExecutor(CommandExecutorWrapper(command))
                tabCompleter = TabCompleterWrapper(completer)
            }

            for (alias in aliases) {
                commandMap[alias] = bukkitCommand
                commandMap["serversystem:${alias}"] = bukkitCommand

                _registeredCommands.add(alias)
                _registeredCommands.add("serversystem:${alias}")

                _registeredCommandInstances.add(command)
            }

            // Paper, for some reason, throws an error if the alias and command name are the same...
            aliases.remove(bukkitCommand.name)
            bukkitCommand.aliases = aliases
        }
    }

    fun registerCommands() {
        ClassGraph()
            .enableAllInfo()
            .acceptPackages("me.testaccount666.serversystem.commands.executables")
            .scan().use { scanResult ->
                val serverSystemCommands = scanResult.getClassesWithAnnotation(ServerSystemCommand::class.java)
                serverSystemCommands.forEach { processCommandExecutor(it.loadClass()) }
            }
        syncCommands()
    }

    fun unregisterCommands() {
        val commandMap = commandMap

        _registeredCommands.forEach { commandMap.remove(it) }
        _registeredCommands.clear()
        _registeredCommandInstances.clear()
    }

    val registeredCommands
        get() = _registeredCommands.toSet()

    val registeredCommandInstances
        get() = _registeredCommandInstances.toSet()

    @Suppress("UNCHECKED_CAST")
    private fun processCommandExecutor(clazz: Class<*>) {
        val commandExecutor = clazz as Class<ServerSystemCommandExecutor>

        if (!commandExecutor.isAnnotationPresent(ServerSystemCommand::class.java)) return

        val commandAnnotation = checkNotNull(commandExecutor.getAnnotation(ServerSystemCommand::class.java))
        val command = commandAnnotation.name

        if (!isCommandEnabled(command)) return

        val variantAliasMap = buildVariantAliasMap(command, commandAnnotation.variants)
        instantiateAndRegisterCommand(commandExecutor, commandAnnotation.tabCompleter.java, variantAliasMap, command, commandAnnotation)
    }

    private fun isCommandEnabled(command: String) = _configReader.getBoolean("Commands.${command}.Enabled")

    private fun isVariantEnabled(command: String, variant: String) = _configReader.getBoolean("Commands.${command}.Variants.${variant}.Enabled")

    private fun buildVariantAliasMap(command: String, variants: Array<String>): Map<String, List<String>> {
        val variantAliasMap = HashMap<String, List<String>>()

        val parentAliases = getAliases("Commands.${command}.Aliases")
        variantAliasMap[command] = parentAliases

        for (variant in variants) {
            if (!isVariantEnabled(command, variant)) continue

            val variantAliases = getAliases("Commands.${command}.Variants.${variant}.Aliases")
            variantAliasMap[variant] = variantAliases
        }

        return variantAliasMap
    }

    private fun getAliases(configPath: String): List<String> {
        return _configReader.getString(configPath, "")!!.split(",")
            .dropLastWhile(String::isEmpty)
            .map(String::trim)
            .filter(String::isNotEmpty)
    }

    private fun instantiateAndRegisterCommand(
        commandExecutor: Class<ServerSystemCommandExecutor>,
        tabCompleter: Class<out ServerSystemTabCompleter>,
        variantAliasMap: Map<String, List<String>>,
        command: String, commandAnnotation: ServerSystemCommand,
    ) {
        try {
            val completer = commandAnnotation.simpleCompletions.takeIf { it.isNotEmpty() }?.let { completions ->
                SimpleTabCompleter(completions.associate { (it.position + 1) to (if (it.isNull) null else it.values.toList()) })
            } ?: tabCompleter.getDeclaredConstructor().newInstance()

            registerCommand(commandExecutor.getDeclaredConstructor().newInstance(), completer, variantAliasMap)
        } catch (exception: Exception) {
            throw RuntimeException("Error registering command '${command}'!", exception)
        }
    }

    @Synchronized
    fun syncCommands() {
        _syncCommandsAccessor.accept(Bukkit.getServer())

        Bukkit.getOnlinePlayers().forEach(Player::updateCommands)
    }
}