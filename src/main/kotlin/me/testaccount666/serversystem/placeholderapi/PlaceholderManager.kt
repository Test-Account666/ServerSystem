package me.testaccount666.serversystem.placeholderapi

import io.github.classgraph.ClassGraph
import me.testaccount666.serversystem.ServerSystem.Companion.log
import java.util.Locale.getDefault
import java.util.logging.Level

class PlaceholderManager {
    private val _registeredPlaceholders: MutableMap<String, Placeholder> = HashMap()

    fun registerPlaceholders() {
        ClassGraph()
            .enableAllInfo()
            .acceptPackages("me.testaccount666.serversystem.placeholderapi.executables")
            .scan().use { scanResult ->
                val placeholderClasses = scanResult.getClassesImplementing(Placeholder::class.java)
                for (placeholderClass in placeholderClasses) try {
                    val placeholder = placeholderClass.loadClass().getDeclaredConstructor().newInstance() as Placeholder
                    placeholder.identifiers.forEach { identifier ->
                        _registeredPlaceholders[identifier.lowercase(getDefault())] = placeholder
                    }
                } catch (exception: Exception) {
                    log.log(Level.SEVERE, "Error registering placeholder '${placeholderClass.getName()}'", exception)
                }
            }
    }

    fun getPlaceholder(identifier: String) = _registeredPlaceholders[identifier.lowercase(getDefault())]
}
