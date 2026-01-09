package me.testaccount666.serversystem.utils

import java.util.function.BiFunction
import java.util.function.Function

/*
 Kinda limited, but is suitable for ServerSystem's current needs.

 Made this for future shenanigans
*/
//TODO: Make this look better
object ConstructorAccessor {
    fun <T> createConstructor(targetClass: Class<T?>): Function<Void?, T?> {
        try {
            val constructor = targetClass.getDeclaredConstructor()
            constructor.isAccessible = true

            return Function { _: Void? ->
                try {
                    return@Function constructor.newInstance()
                } catch (exception: ReflectiveOperationException) {
                    throw RuntimeException("Failed to create instance of ${targetClass.name}", exception)
                }
            }
        } catch (exception: NoSuchMethodException) {
            throw RuntimeException("Failed to create constructor accessor for ${targetClass.name}", exception)
        }
    }

    fun <T, P> createConstructor(targetClass: Class<T?>, paramType: Class<P?>?): Function<P?, T?> {
        try {
            val constructor = targetClass.getDeclaredConstructor(paramType)
            constructor.isAccessible = true

            return Function { param: P? ->
                try {
                    return@Function constructor.newInstance(param)
                } catch (exception: ReflectiveOperationException) {
                    throw RuntimeException("Failed to create instance of ${targetClass.name}", exception)
                }
            }
        } catch (exception: NoSuchMethodException) {
            throw RuntimeException("Failed to create constructor accessor for ${targetClass.name}", exception)
        }
    }

    @JvmStatic
    fun <T, P1, P2> createConstructor(
        targetClass: Class<T?>,
        param1Type: Class<P1?>?,
        param2Type: Class<P2?>?
    ): BiFunction<P1?, P2?, T?> {
        try {
            val constructor = targetClass.getDeclaredConstructor(param1Type, param2Type)
            constructor.isAccessible = true

            return BiFunction { param1: P1?, param2: P2? ->
                try {
                    return@BiFunction constructor.newInstance(param1, param2)
                } catch (exception: ReflectiveOperationException) {
                    throw RuntimeException("Failed to create instance of ${targetClass.name}", exception)
                }
            }
        } catch (exception: NoSuchMethodException) {
            throw RuntimeException("Failed to create constructor accessor for ${targetClass.name}", exception)
        }
    }
}