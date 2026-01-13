package me.testaccount666.serversystem.commands.management

import io.github.classgraph.ClassGraph
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.commands.interfaces.ServerSystemTabCompleter
import me.testaccount666.serversystem.commands.wrappers.CommandExecutorWrapper
import me.testaccount666.serversystem.commands.wrappers.TabCompleterWrapper
import me.testaccount666.serversystem.managers.config.ConfigReader
import me.testaccount666.serversystem.utils.ConstructorAccessor
import me.testaccount666.serversystem.utils.FieldAccessor
import me.testaccount666.serversystem.utils.MethodAccessor
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.command.Command
import org.bukkit.command.PluginCommand
import org.bukkit.command.SimpleCommandMap
import org.bukkit.plugin.Plugin
import java.util.*
import java.util.function.Consumer

class CommandManager(private val _configReader: ConfigReader) {
    private val _commandMapAccessor =
        FieldAccessor.createGetter<SimpleCommandMap, MutableMap<String, Command>>(SimpleCommandMap::class.java, "knownCommands")
    private val _pluginCommandConstructor =
        ConstructorAccessor.createConstructor(
            PluginCommand::class.java,
            String::class.java, Plugin::class.java
        )
    private val _syncCommandsAccessor: Consumer<Server?> = MethodAccessor.createVoidAccessor(Bukkit.getServer().javaClass, "syncCommands")
    private val _registeredCommands = HashSet<String>()
    private val _registeredCommandInstances = HashSet<ServerSystemCommandExecutor>()

    fun getCommand(commandName: String): Command? {
        return commandMap[commandName]
    }

    fun getServerSystemCommand(commandName: String): PluginCommand? {
        return commandMap.values
            .filterIsInstance<PluginCommand>()
            .find { pluginCommand ->
                pluginCommand.name.equals(commandName, ignoreCase = true) &&
                        pluginCommand.plugin === instance &&
                        pluginCommand.executor is ServerSystemCommandExecutor
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
        variantAliasMap: Map<String, List<String>>
    ) {
        val commandMap = commandMap

        variantAliasMap.forEach { (variant, aliases) ->
            var aliases = aliases
            aliases.forEach { commandMap.remove(it) }

            val bukkitCommand = createCommand(variant)
            bukkitCommand.setExecutor(CommandExecutorWrapper(command))
            bukkitCommand.tabCompleter = TabCompleterWrapper(completer)

            for (alias in aliases) {
                commandMap[alias] = bukkitCommand
                commandMap["serversystem:${alias}"] = bukkitCommand

                _registeredCommands.add(alias)
                _registeredCommands.add("serversystem:${alias}")

                _registeredCommandInstances.add(command)
            }

            // Make aliases List modifiable
            aliases = ArrayList(aliases)

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

    val registeredCommands: Set<String>
        get() = _registeredCommands.toSet()

    val registeredCommandInstances: Set<ServerSystemCommandExecutor>
        get() = _registeredCommandInstances.toSet()

    @Suppress("UNCHECKED_CAST")
    private fun processCommandExecutor(clazz: Class<*>) {
        val commandExecutor = clazz as Class<ServerSystemCommandExecutor>

        if (!commandExecutor.isAnnotationPresent(ServerSystemCommand::class.java)) return

        val commandAnnotation = checkNotNull(commandExecutor.getAnnotation(ServerSystemCommand::class.java))
        val command = commandAnnotation.name

        if (!isCommandEnabled(command)) return

        val variantAliasMap = buildVariantAliasMap(command, commandAnnotation.variants)
        instantiateAndRegisterCommand(commandExecutor, commandAnnotation.tabCompleter.java, variantAliasMap, command)
    }

    private fun isCommandEnabled(command: String): Boolean = _configReader.getBoolean("Commands.${command}.Enabled")

    private fun isVariantEnabled(command: String, variant: String): Boolean {
        return _configReader.getBoolean("Commands.${command}.Variants.${variant}.Enabled")
    }

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
        return Arrays.stream(
            _configReader.getString(configPath, "")!!.split(",".toRegex())
                .dropLastWhile { it.isEmpty() }.toTypedArray()
        )
            .map { obj -> obj.trim { it <= ' ' } }
            .filter { alias -> alias.isNotEmpty() }
            .toList()
    }

    private fun instantiateAndRegisterCommand(
        commandExecutor: Class<ServerSystemCommandExecutor>,
        tabCompleter: Class<out ServerSystemTabCompleter>,
        variantAliasMap: Map<String, List<String>>,
        command: String
    ) {
        try {
            registerCommand(
                commandExecutor.getDeclaredConstructor().newInstance(),
                tabCompleter.getDeclaredConstructor().newInstance(),
                variantAliasMap
            )
        } catch (exception: Exception) {
            throw RuntimeException("Error registering command '${command}'!", exception)
        }
    }

    @Synchronized
    fun syncCommands() {
        _syncCommandsAccessor.accept(Bukkit.getServer())

        Bukkit.getOnlinePlayers().forEach { it.updateCommands() }
    }
}