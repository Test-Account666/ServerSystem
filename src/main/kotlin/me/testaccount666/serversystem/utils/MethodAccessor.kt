package me.testaccount666.serversystem.utils

import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Function

/*
 Made this for future shenanigans
*/
//TODO: Make this look better
class MethodAccessor private constructor() {
    init {
        throw IllegalStateException("This class cannot be instantiated")
    }

    companion object {
        @JvmStatic
        fun <T> createVoidAccessor(targetClass: Class<T>, methodName: String): Consumer<T?> {
            try {
                val method = targetClass.getDeclaredMethod(methodName)
                method.isAccessible = true

                return Consumer { instance: T? ->
                    try {
                        method.invoke(instance)
                    } catch (exception: ReflectiveOperationException) {
                        throw RuntimeException("Failed to invoke method ${targetClass.name}.${methodName}", exception)
                    }
                }
            } catch (exception: NoSuchMethodException) {
                throw RuntimeException("Failed to create void method accessor for ${targetClass.name}.${methodName}", exception)
            }
        }

        @JvmStatic
        fun <T, P> createVoidAccessor(
            targetClass: Class<T?>,
            methodName: String,
            paramType: Class<P?>?
        ): BiConsumer<T?, P?> {
            try {
                val method = targetClass.getDeclaredMethod(methodName, paramType)
                method.isAccessible = true

                return BiConsumer { instance: T?, param: P? ->
                    try {
                        method.invoke(instance, param)
                    } catch (exception: ReflectiveOperationException) {
                        throw RuntimeException("Failed to invoke method ${targetClass.name}.${methodName}", exception)
                    }
                }
            } catch (exception: NoSuchMethodException) {
                throw RuntimeException("Failed to create void method accessor for ${targetClass.name}.${methodName}", exception)
            }
        }

        @JvmStatic
        fun <T> createVoidAccessor(
            targetClass: Class<T?>,
            methodName: String,
            vararg paramTypes: Class<*>?
        ): BiConsumer<T?, Array<Any?>?> {
            try {
                val method = targetClass.getDeclaredMethod(methodName, *paramTypes)
                method.isAccessible = true

                return BiConsumer { instance: T?, params: Array<Any?>? ->
                    try {
                        method.invoke(instance, params)
                    } catch (exception: ReflectiveOperationException) {
                        throw RuntimeException("Failed to invoke method ${targetClass.name}.${methodName}", exception)
                    }
                }
            } catch (exception: NoSuchMethodException) {
                throw RuntimeException("Failed to create void method accessor for ${targetClass.name}.${methodName}", exception)
            }
        }

        @JvmStatic
        fun <T, R> createAccessor(targetClass: Class<T?>, methodName: String, returnType: Class<R?>?): Function<T?, R?> {
            try {
                val method = targetClass.getDeclaredMethod(methodName)
                method.isAccessible = true

                require(method.returnType != Void.TYPE) { "Method returns void. Use createVoidAccessor instead." }

                return Function { instance: T? ->
                    try {
                        val result = method.invoke(instance) as R?
                        return@Function result
                    } catch (exception: ReflectiveOperationException) {
                        throw RuntimeException("Failed to invoke method ${targetClass.name}.${methodName}", exception)
                    }
                }
            } catch (exception: NoSuchMethodException) {
                throw RuntimeException("Failed to create method accessor for ${targetClass.name}.${methodName}", exception)
            }
        }

        @JvmStatic
        fun <T, P, R> createAccessor(
            targetClass: Class<T>,
            methodName: String,
            paramType: Class<P>,
            returnType: Class<R>
        ): BiFunction<T?, P?, R?> {
            try {
                val method = targetClass.getDeclaredMethod(methodName, paramType)
                method.isAccessible = true

                require(method.returnType != Void.TYPE) { "Method returns void. Use createVoidAccessor instead." }

                return BiFunction { instance: T?, param: P? ->
                    try {
                        val result = method.invoke(instance, param) as R?
                        return@BiFunction result
                    } catch (exception: ReflectiveOperationException) {
                        throw RuntimeException("Failed to invoke method ${targetClass.name}.${methodName}", exception)
                    }
                }
            } catch (exception: NoSuchMethodException) {
                throw RuntimeException("Failed to create method accessor for ${targetClass.name}.${methodName}", exception)
            }
        }

        @JvmStatic
        fun <T, R> createAccessor(
            targetClass: Class<T?>,
            methodName: String,
            returnType: Class<R?>?,
            vararg paramTypes: Class<*>?
        ): BiFunction<T?, Array<Any?>?, R?> {
            try {
                val method = targetClass.getDeclaredMethod(methodName, *paramTypes)
                method.isAccessible = true

                require(method.returnType != Void.TYPE) { "Method returns void. Use createVoidAccessor instead." }

                return BiFunction { instance: T?, params: Array<Any?>? ->
                    try {
                        val result = method.invoke(instance, params) as R?
                        return@BiFunction result
                    } catch (exception: ReflectiveOperationException) {
                        throw RuntimeException("Failed to invoke method ${targetClass.name}.${methodName}", exception)
                    }
                }
            } catch (exception: NoSuchMethodException) {
                throw RuntimeException("Failed to create method accessor for ${targetClass.name}.${methodName}", exception)
            }
        }
    }
}