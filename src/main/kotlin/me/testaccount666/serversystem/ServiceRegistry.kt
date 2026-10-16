package me.testaccount666.serversystem

import java.util.concurrent.ConcurrentHashMap

class ServiceRegistry {
    private val _services = ConcurrentHashMap<Class<*>, Any>()

    fun <T : Any> registerService(type: Class<T>, service: T): T {
        check(!hasService(type)) { "Service already registered: ${type.name}" }
        return service.also { _services[type] = it }
    }

    fun <T : Any> getService(type: Class<T>) = getServiceOrNull(type) ?: error("Service not registered: ${type.name}")
    fun <T : Any> getServiceOrNull(type: Class<T>) = _services[type]?.let(type::cast)
    fun <T : Any> hasService(type: Class<T>) = _services.containsKey(type)

    fun clearServices() = _services.clear()

    inline fun <reified T : Any> registerService(service: T): T = registerService(T::class.java, service)
    inline fun <reified T : Any> getService(): T = getService(T::class.java)
    inline fun <reified T : Any> getServiceOrNull(): T? = getServiceOrNull(T::class.java)
    inline fun <reified T : Any> hasService(): Boolean = hasService(T::class.java)

}