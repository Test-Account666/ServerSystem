package me.testaccount666.serversystem.userdata.persistence

import me.testaccount666.serversystem.commands.executables.back.CommandBack
import me.testaccount666.serversystem.userdata.vanish.VanishData
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

/**
 * Manages the persistence of object fields to and from configuration files.
 * This class uses Kotlin reflection and the [SaveableField] annotation to automatically
 * save and load fields.
 */
object PersistenceManager {
    private val TYPE_HANDLERS: MutableMap<Class<*>, FieldHandler<*>> = HashMap()
    private val CLASS_FIELD_CACHE: MutableMap<Class<*>, Map<String, FieldInfo>> = ConcurrentHashMap()

    init {
        // Register default handlers for common types
        registerHandler(Boolean::class.javaObjectType, PrimitiveFieldHandler(Boolean::class.javaObjectType))
        registerHandler(Boolean::class.javaPrimitiveType!!, PrimitiveFieldHandler(Boolean::class.javaPrimitiveType!!))
        registerHandler(Int::class.javaObjectType, PrimitiveFieldHandler(Int::class.javaObjectType))
        registerHandler(Int::class.javaPrimitiveType!!, PrimitiveFieldHandler(Int::class.javaPrimitiveType!!))
        registerHandler(Long::class.javaObjectType, PrimitiveFieldHandler(Long::class.javaObjectType))
        registerHandler(Long::class.javaPrimitiveType!!, PrimitiveFieldHandler(Long::class.javaPrimitiveType!!))
        registerHandler(Double::class.javaObjectType, PrimitiveFieldHandler(Double::class.javaObjectType))
        registerHandler(Double::class.javaPrimitiveType!!, PrimitiveFieldHandler(Double::class.javaPrimitiveType!!))
        registerHandler(String::class.java, PrimitiveFieldHandler(String::class.java))
        registerHandler(Location::class.java, LocationFieldHandler())
        @Suppress("UNCHECKED_CAST")
        registerHandler(HashSet::class.java, UuidSetFieldHandler() as FieldHandler<HashSet<*>>)
        @Suppress("UNCHECKED_CAST")
        registerHandler(Map::class.java, KitMapFieldHandler() as FieldHandler<Map<*, *>>)
        registerHandler(VanishData::class.java, VanishDataFieldHandler())
        registerHandler(CommandBack.BackType::class.java, EnumFieldHandler())
    }

    /**
     * Registers a handler for a specific type.
     *
     * @param type    The type to register the handler for
     * @param handler The handler to register
     * @param <T>     The type of data the handler can process
     */
    @JvmStatic
    fun <T> registerHandler(type: Class<T>, handler: FieldHandler<T>) {
        TYPE_HANDLERS[type] = handler
    }

    /**
     * Saves all fields marked with SaveableField from an object to a configuration.
     *
     * @param object The object to save fields from
     * @param config The configuration to save to
     */
    @JvmStatic
    fun saveFields(`object`: Any, config: FileConfiguration) {
        val clazz = `object`.javaClass
        val fieldInfoMap = getFieldInfoMap(clazz)

        for (fieldInfo in fieldInfoMap.values) {
            val value = fieldInfo.getter(`object`)
            fieldInfo.handler.save(config, fieldInfo.path, value)
        }
    }

    /**
     * Loads all fields marked with SaveableField from a configuration to an object.
     *
     * @param object The object to load fields into
     * @param config The configuration to load from
     */
    @JvmStatic
    fun loadFields(`object`: Any, config: FileConfiguration) {
        val clazz = `object`.javaClass
        val fieldInfoMap = getFieldInfoMap(clazz)

        for (fieldInfo in fieldInfoMap.values) {
            val defaultValue = fieldInfo.getter(`object`)
            val value = fieldInfo.handler.load(config, fieldInfo.path, defaultValue)
            fieldInfo.setter(`object`, value)
        }
    }

    /**
     * Gets the field info map for a class, caching it for future use.
     *
     * @param clazz The class to get field info for
     * @return A map of field names to field info
     */
    @Suppress("UNCHECKED_CAST")
    private fun getFieldInfoMap(clazz: Class<*>): Map<String, FieldInfo> {
        return CLASS_FIELD_CACHE.computeIfAbsent(clazz) {
            val fieldInfoMap = HashMap<String, FieldInfo>()
            val kClass = it.kotlin

            // Get all properties from the class and its superclasses
            for (prop in kClass.memberProperties) {
                val annotation = prop.findAnnotation<SaveableField>()
                    ?: prop.javaField?.getAnnotation(SaveableField::class.java)
                    ?: prop.javaGetter?.getAnnotation(SaveableField::class.java)
                    ?: continue

                prop.isAccessible = true

                val fieldName = prop.name
                val path = annotation.path.ifEmpty { "User.$fieldName" }

                val getter: (Any) -> Any? = { instance -> (prop as KProperty1<Any, *>).get(instance) }
                val setter: (Any, Any?) -> Any? = { instance, value ->
                    if (prop is KMutableProperty1<*, *>) {
                        (prop as KMutableProperty1<Any, Any?>).set(instance, value)
                    } else {
                        // Fallback to java field if property is not mutable
                        prop.javaField?.let { field ->
                            field.isAccessible = true
                            field.set(instance, value)
                        }
                    }
                    value
                }


                // Use the type handler based on the property type
                val type = prop.returnType.classifier as? KClass<*>
                    ?: throw IllegalArgumentException("Unknown type for $fieldName")

                val handler = TYPE_HANDLERS[type.java]
                    ?: TYPE_HANDLERS[type.javaObjectType]
                    ?: throw IllegalArgumentException("No handler for type ${type.qualifiedName}")

                fieldInfoMap[fieldName] = FieldInfo(path, getter, setter, handler as FieldHandler<Any>)
            }

            fieldInfoMap
        }
    }

    /**
     * Information about a field that can be saved and loaded.
     */
    private data class FieldInfo(
        val path: String,
        val getter: (Any) -> Any?,
        val setter: (Any, Any?) -> Any?,
        val handler: FieldHandler<Any>
    )
}
