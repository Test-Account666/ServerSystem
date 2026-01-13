@file:Suppress("UNCHECKED_CAST")

package me.testaccount666.serversystem.utils

import java.lang.reflect.Field
import java.util.function.BiFunction
import java.util.function.Function

/*
 Made this for future shenanigans
*/
//TODO: Make this look better
object FieldAccessor {
    @JvmStatic
    fun <T, R> createGetter(targetClass: Class<T>, fieldName: String): Function<T?, R?> {
        try {
            val field = findField(targetClass, fieldName)
            field.isAccessible = true

            return Function { instance: T? ->
                try {
                    return@Function field.get(instance) as R?
                } catch (exception: IllegalAccessException) {
                    throw RuntimeException("Failed to get field value for ${targetClass.name}.${fieldName}", exception)
                }
            }
        } catch (exception: NoSuchFieldException) {
            throw RuntimeException("Failed to create field accessor for ${targetClass.name}.${fieldName}", exception)
        }
    }

    /**
     * Finds a field in a class or any of its superclasses.
     * 
     * @param clazz     The class to search in
     * @param fieldName The name of the field to find
     * @return The field if found
     * @throws NoSuchFieldException If the field is not found in the class hierarchy
     */
    @Throws(NoSuchFieldException::class)
    private fun findField(clazz: Class<*>, fieldName: String): Field {
        var currentClass = clazz as Class<*>?
        while (currentClass != null) try {
            return currentClass.getDeclaredField(fieldName)
        } catch (exception: NoSuchFieldException) {
            // Field not found in this class, try the superclass
            currentClass = currentClass.getSuperclass()
        }
        throw NoSuchFieldException("Field '${fieldName}' not found in class ${clazz.name} or any of its superclasses")
    }

    fun <T, R> createGetter(targetClass: Class<T>, fieldName: String, fieldType: Class<R>): Function<T?, R?> {
        try {
            val field = findField(targetClass, fieldName)
            field.isAccessible = true

            require(fieldType.isAssignableFrom(field.type)) { "Field '${fieldName}' of type ${field.type.name} is not assignable to provided type ${fieldType.name}" }

            return Function { instance: T? ->
                try {
                    val value = field.get(instance) as R?
                    return@Function value
                } catch (exception: IllegalAccessException) {
                    throw RuntimeException("Failed to get field value for ${targetClass.name}.${fieldName}", exception)
                }
            }
        } catch (exception: NoSuchFieldException) {
            throw RuntimeException("Failed to create field accessor for ${targetClass.name}.${fieldName}", exception)
        }
    }


    @JvmStatic
    fun <T, R> createSetter(targetClass: Class<T>, fieldName: String): BiFunction<T?, R?, R?> {
        try {
            val field = findField(targetClass, fieldName)
            field.isAccessible = true

            return BiFunction { instance: T?, value: R? ->
                try {
                    field.set(instance, value)
                    return@BiFunction value
                } catch (exception: IllegalAccessException) {
                    throw RuntimeException("Failed to set field value for ${targetClass.name}.${fieldName}", exception)
                }
            }
        } catch (exception: NoSuchFieldException) {
            throw RuntimeException("Failed to create field accessor for ${targetClass.name}.${fieldName}", exception)
        }
    }

    fun <T, R> createSetter(targetClass: Class<T>, fieldName: String, fieldType: Class<R>): BiFunction<T?, R?, R?> {
        try {
            val field = findField(targetClass, fieldName)
            field.isAccessible = true

            require(fieldType.isAssignableFrom(field.type)) { "Field '${fieldName}' of type ${field.type.name} is not assignable to provided type ${fieldType.name}" }

            return BiFunction { instance: T?, value: R? ->
                try {
                    field.set(instance, value)
                    return@BiFunction value
                } catch (exception: IllegalAccessException) {
                    throw RuntimeException("Failed to set field value for ${targetClass.name}.${fieldName}", exception)
                }
            }
        } catch (exception: NoSuchFieldException) {
            throw RuntimeException("Failed to create field accessor for ${targetClass.name}.${fieldName}", exception)
        }
    }
}
