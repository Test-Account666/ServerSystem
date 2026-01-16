package me.testaccount666.serversystem.listener.management

import io.github.classgraph.ClassGraph
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.annotations.RequiredCommands
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import me.testaccount666.serversystem.commands.management.CommandManager
import me.testaccount666.serversystem.utils.MethodAccessor
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import java.util.function.Consumer
import java.util.logging.Level

class ListenerManager(private val _commandManager: CommandManager) {
    private val _registeredListeners = HashSet<Listener>()

    fun registerListeners() {
        ClassGraph().enableAllInfo().scan().use { scan ->
            val listeners = scan.getClassesImplementing(Listener::class.java)
            listeners.forEach(Consumer { listenerClass ->
                try {
                    val loadedClass = listenerClass.loadClass()
                    val listener = loadedClass.getDeclaredConstructor().newInstance() as Listener

                    if (loadedClass.isAnnotationPresent(RequiredCommands::class.java)) {
                        val shouldRegister = shouldRegister(listener)

                        if (!shouldRegister) return@Consumer
                    }

                    Bukkit.getPluginManager().registerEvents(listener, instance)
                    _registeredListeners.add(listener)
                } catch (exception: Exception) {
                    throw RuntimeException("Failed to register listener '${listenerClass?.getName()}'", exception)
                }
            })
        }
    }

    fun unregisterListeners() {
        _registeredListeners.forEach(Consumer { listener: Listener? -> HandlerList.unregisterAll(listener!!) })
        _registeredListeners.clear()
    }

    private fun findInstance(commandExecutor: Class<out ServerSystemCommandExecutor>): ServerSystemCommandExecutor? {
        return _commandManager.registeredCommandInstances.firstOrNull { commandExecutor.isAssignableFrom(it.javaClass) }
    }

    private fun shouldRegister(listener: Listener): Boolean {
        val requiredCommands = listener.javaClass.getAnnotation(RequiredCommands::class.java)

        val instances = HashSet<ServerSystemCommandExecutor>()

        for (command in requiredCommands.requiredCommands) {
            val commandInstance = findInstance(command.java) ?: continue

            instances.add(commandInstance)
        }

        try {
            val methodAccessor =
                MethodAccessor.createAccessor(
                    listener.javaClass, "canRegister",
                    Set::class.java, Boolean::class.java
                )

            return methodAccessor.apply(listener, instances) ?: false
        } catch (exception: RuntimeException) {
            log.log(Level.SEVERE, "Listener '${listener.javaClass.name}' requires a 'canRegister' method!", exception)
            return false
        } catch (exception: NoSuchMethodError) {
            log.log(Level.SEVERE, "Listener '${listener.javaClass.name}' requires a 'canRegister' method!", exception)
            return false
        }
    }
}
